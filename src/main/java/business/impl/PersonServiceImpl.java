package business.impl;

import business.PersonService;
import config.Page;
import dao.entity.PersonEntity;
import dao.repository.PersonRepository;
import expose.dto.PersonDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.PersonMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Override
    public Page<PersonDto.Response> findAllPage(UUID companyId, String search, int page, int size) {
        Page<PersonEntity> entity = personRepository.findAllByCompanyIdPage(companyId, search, page, size);
        return personMapper.toResponsePage(entity);
    }

    @Override
    public List<PersonDto.Response> findAllByCompanyId(UUID companyId) {
        return personMapper.toResponseList(personRepository.findAllByCompanyId(companyId));
    }

    @Override
    public PersonDto.Response findById(UUID id) {
        PersonEntity entity = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person not found: " + id));
        return personMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonDto.Response save(PersonDto.Request request) {
        PersonEntity entity = personMapper.toRequest(request);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setIsActive(true);
        personRepository.persist(entity);
        return personMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonDto.Response update(UUID id, PersonDto.Request request) {
        PersonEntity entity = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person not found: " + id));
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setPersonGroupId(request.getPersonGroupId());
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setLastModifiedBy(request.getLastModifiedBy());
        personRepository.persist(entity);
        return personMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PersonEntity entity = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person not found: " + id));
        entity.setIsActive(false);
        personRepository.persist(entity);
    }
}
