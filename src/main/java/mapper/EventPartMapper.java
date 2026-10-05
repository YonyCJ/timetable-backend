package mapper;

import dao.entity.EventPartEntity;
import expose.dto.EventPartDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface EventPartMapper {

    EventPartDto.Response toResponse(EventPartEntity entity);
    List<EventPartDto.Response> toResponseList(List<EventPartEntity> entities);
    EventPartEntity toRequest(EventPartDto.Request request);
    List<EventPartEntity> toRequestList(List<EventPartDto.Request> request);
}
