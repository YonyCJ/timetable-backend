package business.impl;
import business.HelpHomeService;
import config.Page;
import dao.entity.HelpHomeEntity;
import dao.repository.HelpHomeRepository;
import expose.dto.HelpHomeDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.HelpHomeMapper;
import java.time.LocalDateTime;
import java.util.UUID;


@ApplicationScoped
@RequiredArgsConstructor
public class HelpHomeServiceImpl implements HelpHomeService {

    private final HelpHomeRepository helpHomeRepository;
    private final HelpHomeMapper helpHomeMapper;

    @Override
    public Page<HelpHomeDto.Response> findAllPage(UUID companyId, UUID employeeId, String search, int page, int size) {
        Page<HelpHomeEntity> entity = helpHomeRepository.findAllByCompanyIdPage(companyId, employeeId, search, page, size);
        return helpHomeMapper.toResponsePage(entity);
    }

    @Override
    public HelpHomeDto.Response findAllByEmployee(UUID companyId, UUID employeeId, Integer month, Integer year) {
        HelpHomeEntity entity = helpHomeRepository.findAllByEmployee(companyId, employeeId, month, year);
        return helpHomeMapper.toResponse(entity);
    }

    @Override
    public HelpHomeDto.Response findById(UUID id) {
        HelpHomeEntity entity = helpHomeRepository.findById(id).
                orElseThrow(() -> new NotFoundException("No se encontró el ID: " + id));
        return helpHomeMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public HelpHomeDto.Response save(HelpHomeDto.Request request) {
        HelpHomeEntity entity = helpHomeMapper.toRequest(request);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setIsActive(true);
        helpHomeRepository.persist(entity);
        return helpHomeMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public HelpHomeDto.Response update(UUID id, HelpHomeDto.Request request) {
        HelpHomeEntity entity = helpHomeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No se encontró el ID: " + id));
        entity.setEmployeeId(request.getEmployeeId());
        entity.setAmount(request.getAmount());
        entity.setExchangeRate(request.getExchangeRate());
        entity.setAmountDollar(request.getAmountDollar());
        entity.setYear(request.getYear());
        entity.setMonth(request.getMonth());
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setLastModifiedBy(request.getLastModifiedBy());
        helpHomeRepository.persist(entity);
        return helpHomeMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        HelpHomeEntity entity = helpHomeRepository.findById(id).
                orElseThrow(() -> new NotFoundException("No se encontró el ID: " + id));
        entity.setIsActive(false);
        helpHomeRepository.persist(entity);
    }

}
