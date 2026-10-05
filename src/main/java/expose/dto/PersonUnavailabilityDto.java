package expose.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

public class PersonUnavailabilityDto {

    @Getter
    @Setter
    @RegisterForReflection
    public static class Response {
        private UUID personUnavailabilityId;
        private UUID personId;
        private DayOfWeek dayOfWeek;
        private LocalDate unavailableDate;
        private LocalDate untilDate;
        private String reason;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class Request {
        private UUID personUnavailabilityId;
        private UUID personId;
        private DayOfWeek dayOfWeek;
        private LocalDate unavailableDate;
        private LocalDate untilDate;
        private String reason;
        private String createdBy;
        private String lastModifiedBy;
    }
}
