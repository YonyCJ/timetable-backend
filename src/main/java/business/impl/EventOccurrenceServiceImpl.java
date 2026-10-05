package business.impl;

import business.EventOccurrenceService;
import dao.entity.EventEntity;
import dao.entity.EventOccurrenceEntity;
import dao.repository.EventOccurrenceRepository;
import dao.repository.EventRepository;
import expose.dto.EventOccurrenceDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.EventOccurrenceMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class EventOccurrenceServiceImpl implements EventOccurrenceService {

    private final EventOccurrenceRepository eventOccurrenceRepository;
    private final EventRepository eventRepository;
    private final EventOccurrenceMapper eventOccurrenceMapper;

    @Override
    public List<EventOccurrenceDto.Response> findAllByEventId(UUID eventId) {
        return eventOccurrenceMapper.toResponseList(eventOccurrenceRepository.findAllByEventId(eventId));
    }

    @Override
    public List<EventOccurrenceDto.Response> findAllByEventIdAndRange(UUID eventId, LocalDate from, LocalDate to) {
        return eventOccurrenceMapper.toResponseList(eventOccurrenceRepository.findAllByEventIdAndRange(eventId, from, to));
    }

    @Override
    @Transactional
    public List<EventOccurrenceDto.Response> generate(UUID eventId, LocalDate from, LocalDate to) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date cannot be after 'to' date");
        }

        LocalDate rangeStart = event.getStartDate() != null && event.getStartDate().isAfter(from)
                ? event.getStartDate() : from;
        LocalDate rangeEnd = event.getEndDate() != null && event.getEndDate().isBefore(to)
                ? event.getEndDate() : to;

        List<LocalDate> dates = computeDates(event, rangeStart, rangeEnd);

        for (LocalDate date : dates) {
            boolean exists = eventOccurrenceRepository.findByEventIdAndDate(eventId, date).isPresent();
            if (exists) continue;

            EventOccurrenceEntity entity = new EventOccurrenceEntity();
            entity.setEventId(eventId);
            entity.setOccurrenceDate(date);
            entity.setCancelled(false);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setIsActive(true);
            eventOccurrenceRepository.persist(entity);
        }

        return eventOccurrenceMapper.toResponseList(eventOccurrenceRepository.findAllByEventIdAndRange(eventId, rangeStart, rangeEnd));
    }

    private List<LocalDate> computeDates(EventEntity event, LocalDate from, LocalDate to) {
        List<LocalDate> dates = new ArrayList<>();

        switch (event.getRecurrenceType()) {
            case ONCE -> {
                LocalDate date = event.getStartDate();
                if (date != null && !date.isBefore(from) && !date.isAfter(to)) {
                    dates.add(date);
                }
            }
            case DAILY -> {
                for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
                    dates.add(d);
                }
            }
            case WEEKLY -> {
                Set<DayOfWeek> weekDays = parseWeekDays(event.getWeekDays());
                for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
                    if (weekDays.contains(d.getDayOfWeek())) {
                        dates.add(d);
                    }
                }
            }
            case MONTHLY -> {
                if (event.getDayOfMonth() == null) {
                    throw new BadRequestException("Monthly events require 'dayOfMonth'");
                }
                for (LocalDate d = from.withDayOfMonth(1); !d.isAfter(to); d = d.plusMonths(1)) {
                    int day = Math.min(event.getDayOfMonth(), d.lengthOfMonth());
                    LocalDate date = d.withDayOfMonth(day);
                    if (!date.isBefore(from) && !date.isAfter(to)) {
                        dates.add(date);
                    }
                }
            }
            case EVERY_N_DAYS -> {
                if (event.getIntervalDays() == null || event.getIntervalDays() < 1) {
                    throw new BadRequestException("Every-N-days events require 'intervalDays' >= 1");
                }
                LocalDate base = event.getStartDate() != null ? event.getStartDate() : from;
                LocalDate d = base;
                while (d.isBefore(from)) {
                    d = d.plusDays(event.getIntervalDays());
                }
                for (; !d.isAfter(to); d = d.plusDays(event.getIntervalDays())) {
                    if (!d.isBefore(from)) {
                        dates.add(d);
                    }
                }
            }
        }
        return dates;
    }

    private Set<DayOfWeek> parseWeekDays(String weekDays) {
        if (weekDays == null || weekDays.isBlank()) {
            throw new BadRequestException("Weekly events require 'weekDays'");
        }
        return Arrays.stream(weekDays.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public EventOccurrenceDto.Response cancel(UUID eventOccurrenceId, EventOccurrenceDto.CancelRequest request) {
        EventOccurrenceEntity entity = eventOccurrenceRepository.findById(eventOccurrenceId)
                .orElseThrow(() -> new NotFoundException("Event occurrence not found: " + eventOccurrenceId));
        entity.setCancelled(true);
        entity.setCancellationReason(request.getCancellationReason());
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setLastModifiedBy(request.getLastModifiedBy());
        eventOccurrenceRepository.persist(entity);
        return eventOccurrenceMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public EventOccurrenceDto.Response reactivate(UUID eventOccurrenceId, String lastModifiedBy) {
        EventOccurrenceEntity entity = eventOccurrenceRepository.findById(eventOccurrenceId)
                .orElseThrow(() -> new NotFoundException("Event occurrence not found: " + eventOccurrenceId));
        entity.setCancelled(false);
        entity.setCancellationReason(null);
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setLastModifiedBy(lastModifiedBy);
        eventOccurrenceRepository.persist(entity);
        return eventOccurrenceMapper.toResponse(entity);
    }
}
