package dao.entity;

import config.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Event", schema = "evento")
public class EventEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EventId")
    private UUID eventId;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "RecurrenceType")
    private RecurrenceType recurrenceType;

    // Para WEEKLY: días separados por coma, ej "MONDAY,TUESDAY,WEDNESDAY"
    @Column(name = "WeekDays")
    private String weekDays;

    // Para MONTHLY: día del mes en que ocurre (1-31)
    @Column(name = "DayOfMonth")
    private Integer dayOfMonth;

    // Para EVERY_N_DAYS: cada cuántos días se repite
    @Column(name = "IntervalDays")
    private Integer intervalDays;

    @Column(name = "StartTime")
    private LocalTime startTime;

    @Column(name = "EndTime")
    private LocalTime endTime;

    @Column(name = "StartDate")
    private LocalDate startDate;

    // Fin de la vigencia de la recurrencia (null = indefinido)
    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "CompanyId")
    private UUID companyId;

    // Quiénes son candidatos al sorteo de este evento: ALL = cualquier persona registrada,
    // GROUP = solo el grupo indicado en personGroupId (+ invitados puntuales en EventPerson),
    // SPECIFIC = únicamente las personas listadas en EventPerson.
    @Enumerated(EnumType.STRING)
    @Column(name = "PersonSelectionMode")
    private PersonSelectionMode personSelectionMode;

    @Column(name = "PersonGroupId")
    private UUID personGroupId;
}
