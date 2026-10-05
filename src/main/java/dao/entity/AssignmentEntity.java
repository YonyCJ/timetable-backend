package dao.entity;

import config.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Assignment", schema = "evento")
public class AssignmentEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "AssignmentId")
    private UUID assignmentId;

    @Column(name = "EventOccurrenceId")
    private UUID eventOccurrenceId;

    @Column(name = "EventPartId")
    private UUID eventPartId;

    @Column(name = "PersonId")
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Origin")
    private AssignmentOrigin origin;
}
