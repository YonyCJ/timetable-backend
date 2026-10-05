package mapper;

import dao.entity.EventOccurrenceEntity;
import expose.dto.EventOccurrenceDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface EventOccurrenceMapper {

    EventOccurrenceDto.Response toResponse(EventOccurrenceEntity entity);
    List<EventOccurrenceDto.Response> toResponseList(List<EventOccurrenceEntity> entities);
}
