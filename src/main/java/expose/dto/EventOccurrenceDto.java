package expose.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

public class EventOccurrenceDto {

    @Getter
    @Setter
    @RegisterForReflection
    public static class Response {
        private UUID eventOccurrenceId;
        private UUID eventId;
        private LocalDate occurrenceDate;
        private Boolean cancelled;
        private String cancellationReason;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class GenerateRequest {
        private UUID eventId;
        private LocalDate from;
        private LocalDate to;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class CancelRequest {
        private String cancellationReason;
        private String lastModifiedBy;
    }
}
