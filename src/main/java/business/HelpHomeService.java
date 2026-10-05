package business;
import config.Page;
import expose.dto.HelpHomeDto;
import java.util.UUID;


public interface HelpHomeService {
    Page<HelpHomeDto.Response> findAllPage(UUID companyId, UUID employeeId, String search, int page, int size);
    HelpHomeDto.Response findAllByEmployee(UUID companyId, UUID employeeId, Integer month, Integer year);
    HelpHomeDto.Response findById(UUID id);
    HelpHomeDto.Response save(HelpHomeDto.Request request);
    HelpHomeDto.Response update(UUID id, HelpHomeDto.Request request);
    void delete(UUID id);
}
