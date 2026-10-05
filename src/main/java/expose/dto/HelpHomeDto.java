package expose.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


public class HelpHomeDto {

    @Getter
    @Setter
    @RegisterForReflection
    public static class Response{
        private UUID helpHomeId;
        private UUID employeeId;
        private BigDecimal exchangeRate;
        private BigDecimal amount;
        private BigDecimal amountDollar;
        private Integer month;
        private Integer year;
        private UUID companyId;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class Request {
        private UUID helpHomeId;
        private UUID employeeId;
        private BigDecimal exchangeRate;
        private BigDecimal amount;
        private BigDecimal amountDollar;
        private Integer month;
        private Integer year;
        private UUID companyId;
        private String createdBy;
        private String lastModifiedBy;
    }


}
