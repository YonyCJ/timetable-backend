package dao.repository;

import dao.entity.EventPersonEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EventPersonRepository implements PanacheRepository<EventPersonEntity> {

    public List<EventPersonEntity> findAllByEventId(UUID eventId) {
        return find("eventId = ?1 and isActive = true", eventId).list();
    }

    // Reemplaza el conjunto completo de personas específicas/invitadas de un evento.
    public void replaceForEvent(UUID eventId, List<UUID> personIds) {
        List<EventPersonEntity> current = findAllByEventId(eventId);
        for (EventPersonEntity entity : current) {
            entity.setIsActive(false);
            persist(entity);
        }
        for (UUID personId : personIds) {
            EventPersonEntity entity = new EventPersonEntity();
            entity.setEventId(eventId);
            entity.setPersonId(personId);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setIsActive(true);
            persist(entity);
        }
    }
}
