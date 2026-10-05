package business.impl;

import business.PersonUnavailabilityService;
import dao.entity.PersonUnavailabilityEntity;
import dao.repository.PersonUnavailabilityRepository;
import expose.dto.PersonUnavailabilityDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.PersonUnavailabilityMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class PersonUnavailabilityServiceImpl implements PersonUnavailabilityService {

    private final PersonUnavailabilityRepository repository;
    private final PersonUnavailabilityMapper mapper;

    @Override
    public List<PersonUnavailabilityDto.Response> findAllByPersonId(UUID personId) {
        return mapper.toResponseList(repository.findAllByPersonId(personId));
    }

    @Override
    @Transactional
    public PersonUnavailabilityDto.Response save(PersonUnavailabilityDto.Request request) {
        PersonUnavailabilityEntity entity = mapper.toRequest(request);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setIsActive(true);
        repository.persist(entity);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PersonUnavailabilityEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unavailability record not found: " + id));
        entity.setIsActive(false);
        repository.persist(entity);
    }
}
