package dao.repository;

import dao.entity.PersonUnavailabilityEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PersonUnavailabilityRepository implements PanacheRepository<PersonUnavailabilityEntity> {

    public List<PersonUnavailabilityEntity> findAllByPersonId(UUID personId) {
        return find("personId = ?1 and isActive = true", personId).list();
    }

    public List<PersonUnavailabilityEntity> findAllByPersonIds(List<UUID> personIds) {
        return find("personId in ?1 and isActive = true", personIds).list();
    }

    public Optional<PersonUnavailabilityEntity> findById(UUID id) {
        return find("personUnavailabilityId = ?1 and isActive = true", id).firstResultOptional();
    }
}
