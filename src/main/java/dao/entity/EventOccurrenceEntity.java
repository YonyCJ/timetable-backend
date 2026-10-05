package dao.entity;

import config.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "EventOccurrence", schema = "evento")
public class EventOccurrenceEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EventOccurrenceId")
    private UUID eventOccurrenceId;

    @Column(name = "EventId")
    private UUID eventId;

    @Column(name = "OccurrenceDate")
    private LocalDate occurrenceDate;

    // true si el usuario canceló esta fecha puntual (ej. "Actividad social")
    @Column(name = "Cancelled")
    private Boolean cancelled;

    @Column(name = "CancellationReason")
    private String cancellationReason;
}
