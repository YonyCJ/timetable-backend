package mapper;

import config.Page;
import dao.entity.EventEntity;
import expose.dto.EventDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface EventMapper {

    Page<EventDto.Response> toResponsePage(Page<EventEntity> entity);
    EventDto.Response toResponse(EventEntity entity);
    List<EventDto.Response> toResponseList(List<EventEntity> entities);
    EventEntity toRequest(EventDto.Request request);
    List<EventEntity> toRequestList(List<EventDto.Request> request);
}
