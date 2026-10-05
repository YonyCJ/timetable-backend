package mapper;

import config.Page;
import dao.entity.PersonEntity;
import expose.dto.PersonDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PersonMapper {

    Page<PersonDto.Response> toResponsePage(Page<PersonEntity> entity);
    PersonDto.Response toResponse(PersonEntity entity);
    List<PersonDto.Response> toResponseList(List<PersonEntity> entities);
    PersonEntity toRequest(PersonDto.Request request);
    List<PersonEntity> toRequestList(List<PersonDto.Request> request);
}
