package business;

import expose.dto.EventOccurrenceDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventOccurrenceService {
    List<EventOccurrenceDto.Response> findAllByEventId(UUID eventId);
    List<EventOccurrenceDto.Response> findAllByEventIdAndRange(UUID eventId, LocalDate from, LocalDate to);

    // Generates (or completes) an event's occurrences between "from" and "to" according to its recurrence type
    List<EventOccurrenceDto.Response> generate(UUID eventId, LocalDate from, LocalDate to);

    EventOccurrenceDto.Response cancel(UUID eventOccurrenceId, EventOccurrenceDto.CancelRequest request);
    EventOccurrenceDto.Response reactivate(UUID eventOccurrenceId, String lastModifiedBy);
}
