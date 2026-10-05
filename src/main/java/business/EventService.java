package business;

import config.Page;
import expose.dto.EventDto;

import java.util.List;
import java.util.UUID;

public interface EventService {
    Page<EventDto.Response> findAllPage(UUID companyId, String search, int page, int size);
    List<EventDto.Response> findAllByCompanyId(UUID companyId);
    EventDto.Response findById(UUID id);
    EventDto.Response save(EventDto.Request request);
    EventDto.Response update(UUID id, EventDto.Request request);
    void delete(UUID id);

    // Personas específicas o invitadas de un evento. Su interpretación depende del
    // personSelectionMode del evento (ver EventEntity).
    List<UUID> findEventPersonIds(UUID eventId);
    void setEventPersonIds(UUID eventId, List<UUID> personIds);
}
