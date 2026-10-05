package business;

import expose.dto.AssignmentDto;

import java.util.List;
import java.util.UUID;

public interface AssignmentService {
    List<AssignmentDto.Response> findAllByEventOccurrenceId(UUID eventOccurrenceId);

    // Manual assignment: creates or replaces the assignment of a part for an occurrence
    AssignmentDto.Response assignManually(AssignmentDto.Request request);

    // Automatic assignment: draws available people for all parts of the occurrence
    List<AssignmentDto.Response> draw(AssignmentDto.DrawRequest request);

    void delete(UUID id);
}
