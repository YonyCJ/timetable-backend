package dao.entity;

import config.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "PersonUnavailability", schema = "evento")
public class PersonUnavailabilityEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PersonUnavailabilityId")
    private UUID personUnavailabilityId;

    @Column(name = "PersonId")
    private UUID personId;

    // Día de la semana en el que la persona NUNCA está disponible (recurrente). Nullable.
    @Enumerated(EnumType.STRING)
    @Column(name = "DayOfWeek")
    private DayOfWeek dayOfWeek;

    // Fecha puntual, o inicio de un rango, en la que la persona no está disponible. Nullable.
    @Column(name = "UnavailableDate")
    private LocalDate unavailableDate;

    // Fin del rango de no disponibilidad. Si es null, "unavailableDate" es una fecha puntual.
    @Column(name = "UntilDate")
    private LocalDate untilDate;

    @Column(name = "Reason")
    private String reason;
}
