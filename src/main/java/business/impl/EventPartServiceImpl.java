package business.impl;

import business.EventPartService;
import dao.entity.EventPartEntity;
import dao.repository.EventPartRepository;
import expose.dto.EventPartDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.EventPartMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class EventPartServiceImpl implements EventPartService {

    private final EventPartRepository eventPartRepository;
    private final EventPartMapper eventPartMapper;

    @Override
    public List<EventPartDto.Response> findAllByEventId(UUID eventId) {
        return eventPartMapper.toResponseList(eventPartRepository.findAllByEventId(eventId));
    }

    @Override
    public EventPartDto.Response findById(UUID id) {
        EventPartEntity entity = eventPartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event part not found: " + id));
        return eventPartMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public EventPartDto.Response save(EventPartDto.Request request) {
        EventPartEntity entity = eventPartMapper.toRequest(request);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setIsActive(true);
        eventPartRepository.persist(entity);
        return eventPartMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public EventPartDto.Response update(UUID id, EventPartDto.Request request) {
        EventPartEntity entity = eventPartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event part not found: " + id));
        entity.setName(request.getName());
        entity.setSortOrder(request.getSortOrder());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        entity.setLinkGroupId(request.getLinkGroupId());
        entity.setSwapWithPartId(request.getSwapWithPartId());
        entity.setSwapWeekDays(request.getSwapWeekDays());
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setLastModifiedBy(request.getLastModifiedBy());
        eventPartRepository.persist(entity);
        return eventPartMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        EventPartEntity entity = eventPartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event part not found: " + id));
        entity.setIsActive(false);
        eventPartRepository.persist(entity);
    }
}
