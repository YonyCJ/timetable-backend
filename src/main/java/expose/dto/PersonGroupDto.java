package expose.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class PersonGroupDto {

    @Getter
    @Setter
    @RegisterForReflection
    public static class Response {
        private UUID personGroupId;
        private String name;
        private UUID companyId;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class Request {
        private UUID personGroupId;
        private String name;
        private UUID companyId;
        private String createdBy;
        private String lastModifiedBy;
    }
}
