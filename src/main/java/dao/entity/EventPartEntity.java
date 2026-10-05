package dao.entity;

import config.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "EventPart", schema = "evento")
public class EventPartEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EventPartId")
    private UUID eventPartId;

    @Column(name = "EventId")
    private UUID eventId;

    @Column(name = "Name")
    private String name;

    @Column(name = "SortOrder")
    private Integer sortOrder;

    // Horario manual opcional de esta parte. Si ambos son null, el horario se calcula
    // automáticamente repartiendo la duración del evento entre todas sus partes.
    @Column(name = "StartTime")
    private LocalTime startTime;

    @Column(name = "EndTime")
    private LocalTime endTime;

    // Partes con el mismo LinkGroupId son sorteadas en conjunto: la persona elegida para
    // una se asigna también a las demás del grupo, en vez de excluirse entre sí. Null = sin vínculo.
    @Column(name = "LinkGroupId")
    private UUID linkGroupId;

    // Intercambio de orden por día de la semana: en los días listados en SwapWeekDays, esta parte
    // y SwapWithPartId intercambian su SortOrder efectivo (ej. "Oración Intercesora" pasa a ir
    // donde iba "Pedidos de Oración" los martes y jueves, y viceversa). Null = sin intercambio.
    @Column(name = "SwapWithPartId")
    private UUID swapWithPartId;

    @Column(name = "SwapWeekDays")
    private String swapWeekDays;
}
