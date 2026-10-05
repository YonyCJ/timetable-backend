package dao.repository;

import dao.entity.EventOccurrenceEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class EventOccurrenceRepository implements PanacheRepository<EventOccurrenceEntity> {

    public List<EventOccurrenceEntity> findAllByEventId(UUID eventId) {
        return find("eventId = ?1 and isActive = true order by occurrenceDate asc", eventId).list();
    }

    public List<EventOccurrenceEntity> findAllByEventIdAndRange(UUID eventId, LocalDate from, LocalDate to) {
        return find("eventId = ?1 and occurrenceDate >= ?2 and occurrenceDate <= ?3 and isActive = true order by occurrenceDate asc",
                eventId, from, to).list();
    }

    public Optional<EventOccurrenceEntity> findByEventIdAndDate(UUID eventId, LocalDate date) {
        return find("eventId = ?1 and occurrenceDate = ?2 and isActive = true", eventId, date).firstResultOptional();
    }

    public Optional<EventOccurrenceEntity> findById(UUID id) {
        return find("eventOccurrenceId = ?1 and isActive = true", id).firstResultOptional();
    }
}
