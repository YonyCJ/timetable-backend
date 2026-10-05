package business;

import expose.dto.EventPartDto;

import java.util.List;
import java.util.UUID;

public interface EventPartService {
    List<EventPartDto.Response> findAllByEventId(UUID eventId);
    EventPartDto.Response findById(UUID id);
    EventPartDto.Response save(EventPartDto.Request request);
    EventPartDto.Response update(UUID id, EventPartDto.Request request);
    void delete(UUID id);
}
