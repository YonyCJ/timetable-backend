package mapper;

import dao.entity.AssignmentEntity;
import expose.dto.AssignmentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface AssignmentMapper {

    AssignmentDto.Response toResponse(AssignmentEntity entity);
    List<AssignmentDto.Response> toResponseList(List<AssignmentEntity> entities);
}
