package business.impl;

import business.PersonGroupService;
import dao.entity.PersonGroupEntity;
import dao.repository.PersonGroupRepository;
import expose.dto.PersonGroupDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.PersonGroupMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class PersonGroupServiceImpl implements PersonGroupService {

    private final PersonGroupRepository personGroupRepository;
    private final PersonGroupMapper personGroupMapper;

    @Override
    public List<PersonGroupDto.Response> findAllByCompanyId(UUID companyId) {
        return personGroupMapper.toResponseList(personGroupRepository.findAllByCompanyId(companyId));
    }

    @Override
    public PersonGroupDto.Response findById(UUID id) {
        PersonGroupEntity entity = personGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person group not found: " + id));
        return personGroupMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonGroupDto.Response save(PersonGroupDto.Request request) {
        PersonGroupEntity entity = personGroupMapper.toRequest(request);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setIsActive(true);
        personGroupRepository.persist(entity);
        return personGroupMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonGroupDto.Response update(UUID id, PersonGroupDto.Request request) {
        PersonGroupEntity entity = personGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person group not found: " + id));
        entity.setName(request.getName());
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setLastModifiedBy(request.getLastModifiedBy());
        personGroupRepository.persist(entity);
        return personGroupMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PersonGroupEntity entity = personGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person group not found: " + id));
        entity.setIsActive(false);
        personGroupRepository.persist(entity);
    }
}
