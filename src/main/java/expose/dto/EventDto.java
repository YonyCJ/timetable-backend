package expose.dto;

import dao.entity.PersonSelectionMode;
import dao.entity.RecurrenceType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class EventDto {

    @Getter
    @Setter
    @RegisterForReflection
    public static class Response {
        private UUID eventId;
        private String name;
        private String description;
        private RecurrenceType recurrenceType;
        private String weekDays;
        private Integer dayOfMonth;
        private Integer intervalDays;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate endDate;
        private UUID companyId;
        private PersonSelectionMode personSelectionMode;
        private UUID personGroupId;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class Request {
        private UUID eventId;
        private String name;
        private String description;
        private RecurrenceType recurrenceType;
        private String weekDays;
        private Integer dayOfMonth;
        private Integer intervalDays;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate endDate;
        private UUID companyId;
        private PersonSelectionMode personSelectionMode;
        private UUID personGroupId;
        private String createdBy;
        private String lastModifiedBy;
    }
}
