package dao.repository;
import config.Page;
import config.PaginationConfig;
import dao.entity.HelpHomeEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

@ApplicationScoped
@RequiredArgsConstructor
public class HelpHomeRepository implements PanacheRepository<HelpHomeEntity> {

    private final PaginationConfig paginationConfig;
    private final EntityManager em;

    public Page<HelpHomeEntity> findAllByCompanyIdPage(UUID companyId, UUID employeeId, String search, Integer page, Integer size) {
        String baseQuery = "companyId = :companyId and employeeId = :employeeId and isActive = true";
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("employeeId", employeeId);

        if (search != null && !search.isBlank()) {
            baseQuery += " AND LOWER(employeeId) LIKE LOWER(CONCAT('%', :search, '%'))";
            params.put("search", search.trim());
        }

        PanacheQuery<HelpHomeEntity> query = find(baseQuery, params);
        return paginationConfig.paginate(query, page, size);
    }

    public List<HelpHomeEntity> findAllByCompanyId(UUID companyId) {
        return find("companyId = ?1 and isActive=true", companyId).list();
    }

    public Optional<HelpHomeEntity> findById(UUID id) {
        return find("helpHomeId = ?1 and isActive=true", id).firstResultOptional();
    }

    public List<HelpHomeEntity> findByEmployeeCompanyAndYear(UUID companyId, UUID employeeId, Integer year) {
        return list("companyId = ?1 and employeeId = ?2 and year = ?3 and isActive=true", companyId, employeeId, year);
    }

    public List<HelpHomeEntity> findByEmployeeCompanyYearAndMonth(UUID companyId, UUID employeeId, Integer year, Integer month) {
        return list("companyId = ?1 and employeeId = ?2 and year = ?3 and month = ?4 and isActive=true",
                companyId, employeeId, year, month);
    }

    public BigDecimal sumByEmployeeCompanyAndPeriod(UUID employeeId, UUID companyId, int year, int month) {
        List<HelpHomeEntity> rows = find("""
                        employeeId = ?1
                    and companyId = ?2
                    and year = ?3
                    and month = ?4
                    and (isActive = true or isActive is null)
                    """, employeeId, companyId, year, month)
                .list();

        if (rows == null || rows.isEmpty()) return BigDecimal.ZERO;

        BigDecimal sum = BigDecimal.ZERO;
        for (HelpHomeEntity r : rows) {
            BigDecimal amt = r.getAmount() == null ? BigDecimal.ZERO : r.getAmount();
            sum = sum.add(amt);
        }
        return sum;
    }

    public HelpHomeEntity findAllByEmployee(UUID companyId, UUID employeeId, Integer month, Integer year) {
        return find("companyId = ?1 and employeeId = ?2 and month = ?3 and year = ?4 and isActive=true",
                companyId, employeeId, month, year).firstResultOptional().orElse(null);
    }


}
