package exception;

import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Arrays;

@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

    @Inject
    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @Context
    UriInfo uriInfo;
    @Override
    public Response toResponse(EntityNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus("error");
        errorResponse.setMessage("Registro no econtrado");
        errorResponse.setDetail(exception.getMessage());
        errorResponse.setErrorCode("404");
        errorResponse.setErrorType("NotFoundException");
        errorResponse.setPath(uriInfo.getAbsolutePath().getPath());
        errorResponse.setApplicationName(applicationName);

        return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
    }

    private StackTraceElement findStackTraceElement(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .filter(stackTraceElement -> stackTraceElement.getClassName().contains("ServiceImpl"))
                .findFirst()
                .orElse(null);
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}
