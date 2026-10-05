package dao.entity;
import config.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "HelpHome")
public class HelpHomeEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "HelpHomeId")
    private UUID helpHomeId;
    @Column(name = "EmployeeId")
    private UUID employeeId;
    @Column(name = "ExchangeRate")
    private BigDecimal exchangeRate;
    @Column(name = "Amount")
    private BigDecimal amount;
    @Column(name = "AmountDollar")
    private BigDecimal amountDollar;
    @Column(name = "Month")
    private Integer month;
    @Column(name = "Year")
    private Integer year;

    @Column(name = "CompanyId")
    private UUID companyId;


}
