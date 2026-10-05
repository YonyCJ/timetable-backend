package config;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PaginationConfig {

    public <T> Page<T> paginate(PanacheQuery<T> query, int page, int size) {
        int adjustedPage = Math.max(page - 1, 0);
        List<T> items = query.page(adjustedPage, size).list();
        long totalItems = query.count();
        // Ajuste del tamaño de página si la página actual contiene menos elementos
        if (items.size() < size && adjustedPage > 0) {
            size = items.size(); // Establecer el tamaño de página al número real de elementos en la página
        }
        return new Page<>(items, totalItems, adjustedPage + 1, size);
    }
}