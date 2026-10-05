package dao.entity;

import config.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Person", schema = "evento")
public class PersonEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PersonId")
    private UUID personId;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Email")
    private String email;

    @Column(name = "CompanyId")
    private UUID companyId;

    // Grupo/área al que pertenece esta persona (ej. "Coro", "Jóvenes"). Null = sin grupo.
    @Column(name = "PersonGroupId")
    private UUID personGroupId;
}
