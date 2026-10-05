package dao.entity;

import config.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "PersonGroup", schema = "evento")
public class PersonGroupEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PersonGroupId")
    private UUID personGroupId;

    @Column(name = "Name")
    private String name;

    @Column(name = "CompanyId")
    private UUID companyId;
}
