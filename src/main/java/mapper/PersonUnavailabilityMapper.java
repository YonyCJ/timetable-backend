package mapper;

import dao.entity.PersonUnavailabilityEntity;
import expose.dto.PersonUnavailabilityDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PersonUnavailabilityMapper {

    PersonUnavailabilityDto.Response toResponse(PersonUnavailabilityEntity entity);
    List<PersonUnavailabilityDto.Response> toResponseList(List<PersonUnavailabilityEntity> entities);
    PersonUnavailabilityEntity toRequest(PersonUnavailabilityDto.Request request);
}
