package business.impl;

import business.AssignmentService;
import dao.entity.*;
import dao.repository.*;
import expose.dto.AssignmentDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.AssignmentMapper;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final EventRepository eventRepository;
    private final EventOccurrenceRepository eventOccurrenceRepository;
    private final EventPartRepository eventPartRepository;
    private final EventPersonRepository eventPersonRepository;
    private final PersonRepository personRepository;
    private final PersonUnavailabilityRepository personUnavailabilityRepository;
    private final AssignmentMapper assignmentMapper;

    private final SecureRandom random = new SecureRandom();

    // Tope de asignaciones (cualquier parte) por persona dentro de una ventana de 7 días, para
    // repartir mejor la carga semanal. Se relaja automáticamente si deja a todos los candidatos
    // afuera (ej. pool de personas muy chico para la cantidad de partes del evento).
    private static final int MAX_ASSIGNMENTS_PER_WEEK = 2;

    @Override
    public List<AssignmentDto.Response> findAllByEventOccurrenceId(UUID eventOccurrenceId) {
        return assignmentMapper.toResponseList(assignmentRepository.findAllByEventOccurrenceId(eventOccurrenceId));
    }

    @Override
    @Transactional
    public AssignmentDto.Response assignManually(AssignmentDto.Request request) {
        AssignmentEntity entity = assignmentRepository
                .findByEventOccurrenceIdAndEventPartId(request.getEventOccurrenceId(), request.getEventPartId())
                .orElseGet(AssignmentEntity::new);

        boolean isNew = entity.getAssignmentId() == null;
        entity.setEventOccurrenceId(request.getEventOccurrenceId());
        entity.setEventPartId(request.getEventPartId());
        entity.setPersonId(request.getPersonId());
        entity.setOrigin(AssignmentOrigin.MANUAL);

        if (isNew) {
            entity.setCreatedDate(LocalDateTime.now());
            entity.setIsActive(true);
            assignmentRepository.persist(entity);
        } else {
            entity.setLastModifiedDate(LocalDateTime.now());
            entity.setLastModifiedBy(request.getLastModifiedBy());
        }
        return assignmentMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public List<AssignmentDto.Response> draw(AssignmentDto.DrawRequest request) {
        EventOccurrenceEntity occurrence = eventOccurrenceRepository.findById(request.getEventOccurrenceId())
                .orElseThrow(() -> new NotFoundException("Event occurrence not found: " + request.getEventOccurrenceId()));

        if (Boolean.TRUE.equals(occurrence.getCancelled())) {
            throw new BadRequestException("The occurrence is cancelled, people cannot be drawn");
        }

        List<EventPartEntity> parts = eventPartRepository.findAllByEventId(occurrence.getEventId());
        if (parts.isEmpty()) {
            throw new BadRequestException("The event has no configured parts");
        }

        EventEntity event = eventRepository.findById(occurrence.getEventId())
                .orElseThrow(() -> new NotFoundException("Event not found: " + occurrence.getEventId()));
        List<PersonEntity> pool = resolvePool(event, request.getCompanyId());
        if (pool.isEmpty()) {
            throw new BadRequestException("There are no eligible people to draw from for this event");
        }

        List<UUID> poolIds = pool.stream().map(PersonEntity::getPersonId).toList();
        List<PersonUnavailabilityEntity> restrictions = personUnavailabilityRepository.findAllByPersonIds(poolIds);

        Map<UUID, List<PersonUnavailabilityEntity>> restrictionsByPerson = restrictions.stream()
                .collect(Collectors.groupingBy(PersonUnavailabilityEntity::getPersonId));

        // Solo fechas vigentes (no canceladas): una fecha cancelada no debe pesar en el reparto
        // ni en el enfriamiento de 7 días, aunque haya quedado con asignaciones guardadas de
        // antes de cancelarla — ese día ya no va a ocurrir.
        List<EventOccurrenceEntity> eventOccurrences = eventOccurrenceRepository.findAllByEventId(occurrence.getEventId())
                .stream()
                .filter(o -> !Boolean.TRUE.equals(o.getCancelled()))
                .toList();
        List<UUID> eventOccurrenceIds = eventOccurrences.stream().map(EventOccurrenceEntity::getEventOccurrenceId).toList();
        Map<UUID, java.time.LocalDate> dateByOccurrenceId = eventOccurrences.stream()
                .collect(Collectors.toMap(EventOccurrenceEntity::getEventOccurrenceId, EventOccurrenceEntity::getOccurrenceDate));

        // Historial de DÍAS DISTINTOS en que cada persona ya fue asignada en este evento (no de
        // filas de asignación: a alguien le pueden tocar 3 partes vinculadas el mismo día y eso
        // debe pesar como "1 día", no como "3 veces" — si no, alguien con partes vinculadas
        // parecería más cargado que alguien a quien le tocaron partes sueltas en muchos días
        // distintos, cuando en realidad es al revés). Se usa para repartir más parejo: entre más
        // días ya le hayan tocado a alguien, menos probabilidad relativa tiene de volver a salir
        // sorteado, para no saturar siempre a las mismas personas cuando hay más gente registrada.
        List<AssignmentEntity> eventHistory = assignmentRepository.findAllByEventOccurrenceIds(eventOccurrenceIds);
        Map<UUID, Set<UUID>> daysByPerson = new HashMap<>();
        for (AssignmentEntity a : eventHistory) {
            daysByPerson.computeIfAbsent(a.getPersonId(), k -> new HashSet<>()).add(a.getEventOccurrenceId());
        }
        Map<UUID, Long> assignmentCountByPerson = new HashMap<>();
        daysByPerson.forEach((personId, days) -> assignmentCountByPerson.put(personId, (long) days.size()));

        // Partes con el mismo linkGroupId se sortean como un solo bloque: se elige UNA persona
        // para todo el grupo, así que si le toca el grupo, le tocan todas sus partes (no algunas sí
        // y otras no). Entre un día y otro sigue siendo al azar quién sale elegido para el grupo.
        Map<UUID, List<EventPartEntity>> partsByGroup = parts.stream()
                .filter(p -> p.getLinkGroupId() != null)
                .collect(Collectors.groupingBy(EventPartEntity::getLinkGroupId));

        // Grupo (linkGroupId o el propio eventPartId si va sola) -> parte, para poder resolver el
        // groupKey de cualquier asignación pasada.
        Map<UUID, UUID> groupKeyByPartId = new HashMap<>();
        for (EventPartEntity p : parts) {
            groupKeyByPartId.put(p.getEventPartId(), p.getLinkGroupId() != null ? p.getLinkGroupId() : p.getEventPartId());
        }

        // Última fecha (ANTERIOR a la de hoy) en que cada persona hizo cada grupo/parte. Se usa
        // para no repetirle el mismo grupo dentro de una ventana de 7 días — así, en un evento
        // semanal, a nadie le toca el mismo grupo vinculado 2 semanas seguidas sin que pase al
        // menos una semana completa, evitando que se sature en el mismo tramo del calendario.
        Map<String, java.time.LocalDate> lastGroupDateByPerson = new HashMap<>();

        // Cuántos DÍAS DISTINTOS ya le tocaron a cada persona dentro de los 7 días previos a esta
        // fecha (no filas de asignación, por la misma razón de arriba: un día con partes
        // vinculadas no debe pesar como varios días). Se usa para no saturar a la misma persona
        // con varias partes distintas en una misma semana, aparte de la regla de arriba que solo
        // evita repetir el mismo grupo.
        Map<UUID, Set<java.time.LocalDate>> recentDaysByPerson = new HashMap<>();

        for (AssignmentEntity a : eventHistory) {
            java.time.LocalDate d = dateByOccurrenceId.get(a.getEventOccurrenceId());
            if (d == null || !d.isBefore(occurrence.getOccurrenceDate())) {
                continue; // solo se cuenta lo ya sucedido antes de esta fecha
            }
            UUID groupKey = groupKeyByPartId.get(a.getEventPartId());
            if (groupKey != null) {
                String key = a.getPersonId() + "|" + groupKey;
                java.time.LocalDate existing = lastGroupDateByPerson.get(key);
                if (existing == null || d.isAfter(existing)) {
                    lastGroupDateByPerson.put(key, d);
                }
            }
            if (java.time.temporal.ChronoUnit.DAYS.between(d, occurrence.getOccurrenceDate()) < 7) {
                recentDaysByPerson.computeIfAbsent(a.getPersonId(), k -> new HashSet<>()).add(d);
            }
        }
        Map<UUID, Long> recentAssignmentCountByPerson = new HashMap<>();
        recentDaysByPerson.forEach((personId, days) -> recentAssignmentCountByPerson.put(personId, (long) days.size()));

        // Se reinicia en vacío en cada sorteo: solo excluye a quien ya se eligió DENTRO de esta
        // misma pasada (no a quien haya quedado de un sorteo anterior, que aquí se va a reemplazar).
        Set<UUID> usedToday = new HashSet<>();

        List<AssignmentEntity> result = new ArrayList<>();
        Set<UUID> processedPartIds = new HashSet<>();

        for (EventPartEntity part : parts) {
            if (processedPartIds.contains(part.getEventPartId())) {
                continue;
            }

            List<EventPartEntity> group = part.getLinkGroupId() != null
                    ? partsByGroup.get(part.getLinkGroupId())
                    : List.of(part);
            processedPartIds.addAll(group.stream().map(EventPartEntity::getEventPartId).toList());

            UUID groupKey = part.getLinkGroupId() != null ? part.getLinkGroupId() : part.getEventPartId();

            List<PersonEntity> baseCandidates = pool.stream()
                    .filter(p -> !usedToday.contains(p.getPersonId()))
                    .filter(p -> !isUnavailable(restrictionsByPerson.get(p.getPersonId()), occurrence.getOccurrenceDate()))
                    .toList();

            // Se prioriza a quien NO hizo este grupo en los últimos 7 días. Si eso deja a todos
            // afuera (ej. muy pocas personas registradas), se relaja la restricción antes que
            // dejar la parte sin asignar.
            List<PersonEntity> candidates = baseCandidates.stream()
                    .filter(p -> {
                        java.time.LocalDate last = lastGroupDateByPerson.get(p.getPersonId() + "|" + groupKey);
                        return last == null || java.time.temporal.ChronoUnit.DAYS.between(last, occurrence.getOccurrenceDate()) >= 7;
                    })
                    .toList();
            if (candidates.isEmpty()) {
                candidates = baseCandidates;
            }

            // Además, se prioriza a quien tenga MENOS de 2 asignaciones (de cualquier parte) en
            // los últimos 7 días, para no saturar a una misma persona dentro de una semana. Si eso
            // deja a todos afuera (pool chico), se relaja igual que la regla anterior.
            List<PersonEntity> spreadCandidates = candidates.stream()
                    .filter(p -> recentAssignmentCountByPerson.getOrDefault(p.getPersonId(), 0L) < MAX_ASSIGNMENTS_PER_WEEK)
                    .toList();
            if (!spreadCandidates.isEmpty()) {
                candidates = spreadCandidates;
            }

            if (candidates.isEmpty()) {
                continue; // no candidates available for this part/group, it's left unassigned
            }

            PersonEntity chosen = pickWeighted(candidates, assignmentCountByPerson);
            assignmentCountByPerson.merge(chosen.getPersonId(), 1L, Long::sum);

            for (EventPartEntity groupPart : group) {
                AssignmentEntity entity = assignmentRepository
                        .findByEventOccurrenceIdAndEventPartId(occurrence.getEventOccurrenceId(), groupPart.getEventPartId())
                        .orElseGet(AssignmentEntity::new);

                boolean isNew = entity.getAssignmentId() == null;
                entity.setEventOccurrenceId(occurrence.getEventOccurrenceId());
                entity.setEventPartId(groupPart.getEventPartId());
                entity.setPersonId(chosen.getPersonId());
                entity.setOrigin(AssignmentOrigin.AUTOMATIC);

                if (isNew) {
                    entity.setCreatedDate(LocalDateTime.now());
                    entity.setIsActive(true);
                    entity.setCreatedBy(request.getCreatedBy());
                    assignmentRepository.persist(entity);
                } else {
                    entity.setLastModifiedDate(LocalDateTime.now());
                    entity.setLastModifiedBy(request.getCreatedBy());
                }

                result.add(entity);
            }

            usedToday.add(chosen.getPersonId());
        }

        return assignmentMapper.toResponseList(result);
    }

    // Candidatos elegibles según cómo esté configurado el evento:
    // - ALL (o sin configurar, por compatibilidad): cualquier persona de la empresa.
    // - GROUP: los miembros del grupo + invitados puntuales agregados en EventPerson.
    // - SPECIFIC: únicamente las personas listadas en EventPerson para este evento.
    private List<PersonEntity> resolvePool(EventEntity event, UUID companyId) {
        PersonSelectionMode mode = event.getPersonSelectionMode();
        if (mode == null || mode == PersonSelectionMode.ALL) {
            return personRepository.findAllByCompanyId(companyId);
        }

        if (mode == PersonSelectionMode.SPECIFIC) {
            List<UUID> personIds = eventPersonRepository.findAllByEventId(event.getEventId()).stream()
                    .map(EventPersonEntity::getPersonId)
                    .toList();
            return personRepository.findAllByIds(personIds);
        }

        // GROUP
        Map<UUID, PersonEntity> byId = new LinkedHashMap<>();
        if (event.getPersonGroupId() != null) {
            for (PersonEntity p : personRepository.findAllByCompanyIdAndGroupId(companyId, event.getPersonGroupId())) {
                byId.put(p.getPersonId(), p);
            }
        }
        List<UUID> guestIds = eventPersonRepository.findAllByEventId(event.getEventId()).stream()
                .map(EventPersonEntity::getPersonId)
                .toList();
        for (PersonEntity p : personRepository.findAllByIds(guestIds)) {
            byId.put(p.getPersonId(), p);
        }
        return new ArrayList<>(byId.values());
    }

    // Sorteo ponderado: entre más veces haya sido asignada una persona en este evento, menor su
    // peso relativo. Alguien con 0 asignaciones tiene el doble de chance que alguien con 1, el
    // triple que alguien con 2, etc. — nunca queda en cero para no excluirla por completo.
    // Primero se reduce a solo quienes tengan el MENOR número de días asignados entre los
    // candidatos disponibles (empate = igual prioridad), y el azar decide solo entre esos. Antes
    // se usaba un peso suave (1/(1+conteo)) que igual le daba chance a quien ya tenía varias
    // asignaciones, permitiendo que el azar produjera brechas grandes (ej. 6 vs 9 en el mes) —
    // así se mantiene el azar en "quién" le toca, pero la brecha máxima entre personas queda
    // mucho más ajustada.
    private PersonEntity pickWeighted(List<PersonEntity> candidates, Map<UUID, Long> assignmentCountByPerson) {
        long minCount = candidates.stream()
                .mapToLong(p -> assignmentCountByPerson.getOrDefault(p.getPersonId(), 0L))
                .min()
                .orElse(0);
        List<PersonEntity> leastLoaded = candidates.stream()
                .filter(p -> assignmentCountByPerson.getOrDefault(p.getPersonId(), 0L) == minCount)
                .toList();
        return leastLoaded.get(random.nextInt(leastLoaded.size()));
    }

    private boolean isUnavailable(List<PersonUnavailabilityEntity> restrictions, java.time.LocalDate date) {
        if (restrictions == null) return false;
        return restrictions.stream().anyMatch(r -> {
            if (r.getDayOfWeek() != null && r.getDayOfWeek().equals(date.getDayOfWeek())) {
                return true;
            }
            if (r.getUnavailableDate() != null) {
                java.time.LocalDate until = r.getUntilDate() != null ? r.getUntilDate() : r.getUnavailableDate();
                return !date.isBefore(r.getUnavailableDate()) && !date.isAfter(until);
            }
            return false;
        });
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AssignmentEntity entity = assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assignment not found: " + id));
        entity.setIsActive(false);
        assignmentRepository.persist(entity);
    }
}
