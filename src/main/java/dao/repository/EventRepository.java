package dao.repository;

import config.Page;
import config.PaginationConfig;
import dao.entity.EventEntity;
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
public class EventRepository implements PanacheRepository<EventEntity> {

    private final PaginationConfig paginationConfig;

    public Page<EventEntity> findAllByCompanyIdPage(UUID companyId, String search, Integer page, Integer size) {
        String baseQuery = "companyId = :companyId and isActive = true";
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);

        if (search != null && !search.isBlank()) {
            baseQuery += " AND LOWER(name) LIKE LOWER(CONCAT('%', :search, '%'))";
            params.put("search", search.trim());
        }

        PanacheQuery<EventEntity> query = find(baseQuery, params);
        return paginationConfig.paginate(query, page, size);
    }

    public List<EventEntity> findAllByCompanyId(UUID companyId) {
        return find("companyId = ?1 and isActive = true", companyId).list();
    }

    public Optional<EventEntity> findById(UUID id) {
        return find("eventId = ?1 and isActive = true", id).firstResultOptional();
    }
}
