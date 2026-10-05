package dao.repository;

import dao.entity.PersonGroupEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PersonGroupRepository implements PanacheRepository<PersonGroupEntity> {

    public List<PersonGroupEntity> findAllByCompanyId(UUID companyId) {
        return find("companyId = ?1 and isActive = true order by name asc", companyId).list();
    }

    public Optional<PersonGroupEntity> findById(UUID id) {
        return find("personGroupId = ?1 and isActive = true", id).firstResultOptional();
    }
}
