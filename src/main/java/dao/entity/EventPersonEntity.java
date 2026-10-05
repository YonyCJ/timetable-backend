package dao.entity;

import config.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

// Personas específicas asociadas a un evento. Su significado depende del PersonSelectionMode
// del evento: si es SPECIFIC, esta es la lista completa de candidatos; si es GROUP, son
// invitados puntuales que se suman a los miembros del grupo (ej. alguien que participa una
// sola vez sin pertenecer al grupo de siempre).
@Getter
@Setter
@Entity
@Table(name = "EventPerson", schema = "evento")
public class EventPersonEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EventPersonId")
    private UUID eventPersonId;

    @Column(name = "EventId")
    private UUID eventId;

    @Column(name = "PersonId")
    private UUID personId;
}
