package config;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseRepository<Entity> implements PanacheRepositoryBase<Entity, UUID> {

    public Optional<Entity> getById(UUID id) {
        return find("id", id).firstResultOptional();
    }

    public List<Entity> getAllById(List<UUID> ids) {
        return find("id in ?1", ids).list();
    }
}
