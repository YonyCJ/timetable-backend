package dao.repository;

import dao.entity.EventPartEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class EventPartRepository implements PanacheRepository<EventPartEntity> {

    public List<EventPartEntity> findAllByEventId(UUID eventId) {
        return find("eventId = ?1 and isActive = true order by sortOrder asc", eventId).list();
    }

    public Optional<EventPartEntity> findById(UUID id) {
        return find("eventPartId = ?1 and isActive = true", id).firstResultOptional();
    }
}
