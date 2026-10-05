package dao.repository;

import config.Page;
import config.PaginationConfig;
import dao.entity.PersonEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class PersonRepository implements PanacheRepository<PersonEntity> {

    private final PaginationConfig paginationConfig;

    public Page<PersonEntity> findAllByCompanyIdPage(UUID companyId, String search, Integer page, Integer size) {
        String baseQuery = "companyId = :companyId and isActive = true";
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);

        if (search != null && !search.isBlank()) {
            baseQuery += " AND (LOWER(firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(lastName) LIKE LOWER(CONCAT('%', :search, '%')))";
            params.put("search", search.trim());
        }

        PanacheQuery<PersonEntity> query = find(baseQuery, params);
        return paginationConfig.paginate(query, page, size);
    }

    public List<PersonEntity> findAllByCompanyId(UUID companyId) {
        return find("companyId = ?1 and isActive = true", companyId).list();
    }

    public List<PersonEntity> findAllByIds(List<UUID> ids) {
        return find("personId in ?1 and isActive = true", ids).list();
    }

    public List<PersonEntity> findAllByCompanyIdAndGroupId(UUID companyId, UUID personGroupId) {
        return find("companyId = ?1 and personGroupId = ?2 and isActive = true", companyId, personGroupId).list();
    }

    public Optional<PersonEntity> findById(UUID id) {
        return find("personId = ?1 and isActive = true", id).firstResultOptional();
    }
}
