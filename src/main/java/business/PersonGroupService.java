package business;

import expose.dto.PersonGroupDto;

import java.util.List;
import java.util.UUID;

public interface PersonGroupService {
    List<PersonGroupDto.Response> findAllByCompanyId(UUID companyId);
    PersonGroupDto.Response findById(UUID id);
    PersonGroupDto.Response save(PersonGroupDto.Request request);
    PersonGroupDto.Response update(UUID id, PersonGroupDto.Request request);
    void delete(UUID id);
}
