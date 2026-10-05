package business;

import config.Page;
import expose.dto.PersonDto;

import java.util.List;
import java.util.UUID;

public interface PersonService {
    Page<PersonDto.Response> findAllPage(UUID companyId, String search, int page, int size);
    List<PersonDto.Response> findAllByCompanyId(UUID companyId);
    PersonDto.Response findById(UUID id);
    PersonDto.Response save(PersonDto.Request request);
    PersonDto.Response update(UUID id, PersonDto.Request request);
    void delete(UUID id);
}
