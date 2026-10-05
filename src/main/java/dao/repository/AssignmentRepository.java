package dao.repository;

import dao.entity.AssignmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AssignmentRepository implements PanacheRepository<AssignmentEntity> {

    public List<AssignmentEntity> findAllByEventOccurrenceId(UUID eventOccurrenceId) {
        return find("eventOccurrenceId = ?1 and isActive = true", eventOccurrenceId).list();
    }

    public List<AssignmentEntity> findAllByEventOccurrenceIds(List<UUID> eventOccurrenceIds) {
        return find("eventOccurrenceId in ?1 and isActive = true", eventOccurrenceIds).list();
    }

    public Optional<AssignmentEntity> findByEventOccurrenceIdAndEventPartId(UUID eventOccurrenceId, UUID eventPartId) {
        return find("eventOccurrenceId = ?1 and eventPartId = ?2 and isActive = true",
                eventOccurrenceId, eventPartId).firstResultOptional();
    }

    // Últimas asignaciones de una persona para una parte específica, para evitar repetirla seguido
    public List<AssignmentEntity> findRecentByEventPartId(UUID eventPartId, int limit) {
        return find("eventPartId = ?1 and isActive = true order by createdDate desc", eventPartId)
                .page(0, limit)
                .list();
    }

    public Optional<AssignmentEntity> findById(UUID id) {
        return find("assignmentId = ?1 and isActive = true", id).firstResultOptional();
    }
}
