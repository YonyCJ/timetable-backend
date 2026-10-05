package mapper;

import dao.entity.PersonGroupEntity;
import expose.dto.PersonGroupDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PersonGroupMapper {

    PersonGroupDto.Response toResponse(PersonGroupEntity entity);
    List<PersonGroupDto.Response> toResponseList(List<PersonGroupEntity> entities);
    PersonGroupEntity toRequest(PersonGroupDto.Request request);
}
