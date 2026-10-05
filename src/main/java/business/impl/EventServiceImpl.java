package business.impl;

import business.EventService;
import config.Page;
import dao.entity.AssignmentEntity;
import dao.entity.EventEntity;
import dao.entity.EventOccurrenceEntity;
import dao.repository.AssignmentRepository;
import dao.repository.EventOccurrenceRepository;
import dao.repository.EventPersonRepository;
import dao.repository.EventRepository;
import expose.dto.EventDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.EventMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventOccurrenceRepository eventOccurrenceRepository;
    private final AssignmentRepository assignmentRepository;
    private final EventPersonRepository eventPersonRepository;

    @Override
    public Page<EventDto.Response> findAllPage(UUID companyId, String search, int page, int size) {
        Page<EventEntity> entity = eventRepository.findAllByCompanyIdPage(companyId, search, page, size);
        return eventMapper.toResponsePage(entity);
    }

    @Override
    public List<EventDto.Response> findAllByCompanyId(UUID companyId) {
        return eventMapper.toResponseList(eventRepository.findAllByCompanyId(companyId));
    }

    @Override
    public EventDto.Response findById(UUID id) {
        EventEntity entity = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));
        return eventMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public EventDto.Response save(EventDto.Request request) {
        EventEntity entity = eventMapper.toRequest(request);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setIsActive(true);
        eventRepository.persist(entity);
        return eventMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public EventDto.Response update(UUID id, EventDto.Request request) {
        EventEntity entity = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setRecurrenceType(request.getRecurrenceType());
        entity.setWeekDays(request.getWeekDays());
        entity.setDayOfMonth(request.getDayOfMonth());
        entity.setIntervalDays(request.getIntervalDays());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setPersonSelectionMode(request.getPersonSelectionMode());
        entity.setPersonGroupId(request.getPersonGroupId());
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setLastModifiedBy(request.getLastModifiedBy());
        eventRepository.persist(entity);
        deactivateOutOfRangeOccurrences(entity);
        return eventMapper.toResponse(entity);
    }

    // Al acortar la vigencia de un evento, las ocurrencias que quedan fuera del nuevo
    // rango (y sus asignaciones) se desactivan; no tiene sentido seguir mostrándolas.
    private void deactivateOutOfRangeOccurrences(EventEntity event) {
        List<EventOccurrenceEntity> occurrences = eventOccurrenceRepository.findAllByEventId(event.getEventId());
        for (EventOccurrenceEntity occurrence : occurrences) {
            boolean outOfRange = (event.getStartDate() != null && occurrence.getOccurrenceDate().isBefore(event.getStartDate()))
                    || (event.getEndDate() != null && occurrence.getOccurrenceDate().isAfter(event.getEndDate()));
            if (!outOfRange) continue;

            occurrence.setIsActive(false);
            eventOccurrenceRepository.persist(occurrence);

            List<AssignmentEntity> assignments = assignmentRepository.findAllByEventOccurrenceId(occurrence.getEventOccurrenceId());
            for (AssignmentEntity assignment : assignments) {
                assignment.setIsActive(false);
                assignmentRepository.persist(assignment);
            }
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        EventEntity entity = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));
        entity.setIsActive(false);
        eventRepository.persist(entity);
    }

    @Override
    public List<UUID> findEventPersonIds(UUID eventId) {
        return eventPersonRepository.findAllByEventId(eventId).stream()
                .map(dao.entity.EventPersonEntity::getPersonId)
                .toList();
    }

    @Override
    @Transactional
    public void setEventPersonIds(UUID eventId, List<UUID> personIds) {
        eventPersonRepository.replaceForEvent(eventId, personIds);
    }
}
