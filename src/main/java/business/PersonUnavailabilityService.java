package business;

import expose.dto.PersonUnavailabilityDto;

import java.util.List;
import java.util.UUID;

public interface PersonUnavailabilityService {
    List<PersonUnavailabilityDto.Response> findAllByPersonId(UUID personId);
    PersonUnavailabilityDto.Response save(PersonUnavailabilityDto.Request request);
    void delete(UUID id);
}
