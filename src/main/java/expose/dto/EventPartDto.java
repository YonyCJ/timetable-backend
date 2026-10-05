package expose.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

public class EventPartDto {

    @Getter
    @Setter
    @RegisterForReflection
    public static class Response {
        private UUID eventPartId;
        private UUID eventId;
        private String name;
        private Integer sortOrder;
        private LocalTime startTime;
        private LocalTime endTime;
        private UUID linkGroupId;
        private UUID swapWithPartId;
        private String swapWeekDays;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class Request {
        private UUID eventPartId;
        private UUID eventId;
        private String name;
        private Integer sortOrder;
        private LocalTime startTime;
        private LocalTime endTime;
        private UUID linkGroupId;
        private UUID swapWithPartId;
        private String swapWeekDays;
        private String createdBy;
        private String lastModifiedBy;
    }
}
