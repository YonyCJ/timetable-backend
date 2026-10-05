package expose.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class PersonDto {

    @Getter
    @Setter
    @RegisterForReflection
    public static class Response {
        private UUID personId;
        private String firstName;
        private String lastName;
        private String phone;
        private String email;
        private UUID companyId;
        private UUID personGroupId;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class Request {
        private UUID personId;
        private String firstName;
        private String lastName;
        private String phone;
        private String email;
        private UUID companyId;
        private UUID personGroupId;
        private String createdBy;
        private String lastModifiedBy;
    }
}
