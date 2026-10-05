package exception;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.Arrays;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Inject
    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Throwable rootCause = getRootCause(exception);
        ErrorResponse errorResponse = new ErrorResponse();
        if (rootCause != null) {
            StackTraceElement targetStackTraceElement = findStackTraceElement(rootCause);
            if (targetStackTraceElement != null) {
                errorResponse.setClassName(targetStackTraceElement.getClassName());
                errorResponse.setMethodName(targetStackTraceElement.getMethodName());
                errorResponse.setErrorLine(targetStackTraceElement.getLineNumber());
            } else {
                StackTraceElement lastStackTraceElement = rootCause.getStackTrace()[0];
                errorResponse.setClassName(lastStackTraceElement.getClassName());
                errorResponse.setMethodName(lastStackTraceElement.getMethodName());
                errorResponse.setErrorLine(lastStackTraceElement.getLineNumber());
            }
        }
        errorResponse.setStatus("error");
        errorResponse.setMessage("No se puede realizar la operación debido a que se viola una restricción de integridad en la base de datos");
        errorResponse.setDetail(exception.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setErrorCode("500");
        errorResponse.setErrorType("ConstraintViolationException");
        errorResponse.setPath(uriInfo.getAbsolutePath().getPath());
        errorResponse.setApplicationName(applicationName);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
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
