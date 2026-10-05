package expose.dto;

import dao.entity.AssignmentOrigin;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class AssignmentDto {

    @Getter
    @Setter
    @RegisterForReflection
    public static class Response {
        private UUID assignmentId;
        private UUID eventOccurrenceId;
        private UUID eventPartId;
        private UUID personId;
        private AssignmentOrigin origin;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class Request {
        private UUID eventOccurrenceId;
        private UUID eventPartId;
        private UUID personId;
        private String createdBy;
        private String lastModifiedBy;
    }

    // Randomly draws people from "companyId" to cover all parts of the occurrence
    @Getter
    @Setter
    @RegisterForReflection
    public static class DrawRequest {
        private UUID eventOccurrenceId;
        private UUID companyId;
        private String createdBy;
    }
}
