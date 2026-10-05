package mapper;
import config.Page;
import dao.entity.HelpHomeEntity;
import expose.dto.HelpHomeDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface HelpHomeMapper {

    Page<HelpHomeDto.Response> toResponsePage(Page<HelpHomeEntity> entity);
    HelpHomeDto.Response toResponse(HelpHomeEntity entity);
    List<HelpHomeDto.Response> toResponseList(List<HelpHomeEntity> entities);
    HelpHomeEntity toRequest(HelpHomeDto.Request request);
    List<HelpHomeEntity> toRequestList(List<HelpHomeDto.Request> request);

}
