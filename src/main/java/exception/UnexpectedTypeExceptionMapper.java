package exception;

import jakarta.inject.Inject;
import jakarta.validation.UnexpectedTypeException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.Arrays;

@Provider
public class UnexpectedTypeExceptionMapper implements ExceptionMapper<UnexpectedTypeException> {

    @Inject
    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(UnexpectedTypeException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus("error");
        errorResponse.setMessage("Error de validación: El tipo de dato no es compatible");
        errorResponse.setDetail(exception.getMessage());
        errorResponse.setErrorCode("400");
        errorResponse.setErrorType("UnexpectedTypeException");
        errorResponse.setPath(uriInfo.getAbsolutePath().getPath());
        errorResponse.setApplicationName(applicationName);
        errorResponse.setTimestamp(LocalDateTime.now().toString());

        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
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
