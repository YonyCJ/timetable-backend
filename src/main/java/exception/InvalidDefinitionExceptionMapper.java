package exception;


import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.Arrays;

@Provider
public class InvalidDefinitionExceptionMapper implements ExceptionMapper<InvalidDefinitionException> {

    @Inject
    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(InvalidDefinitionException exception) {
        String errorMessage = "No se pudo serializar el tipo";
        Throwable rootCause = getRootCause(exception);

        ErrorResponse errorResponse = new ErrorResponse();
        if (rootCause != null) {
            errorMessage += " Tipo de excepción: " + rootCause.getClass().getName() + ", Mensaje: " + rootCause.getMessage();
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
        errorResponse.setMessage(errorMessage);
        errorResponse.setDetail(exception.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setErrorCode("500");
        errorResponse.setErrorType("SerializationException");
        errorResponse.setPath(uriInfo.getAbsolutePath().getPath());
        errorResponse.setApplicationName(applicationName);

        // Devolver una respuesta con el objeto de ErrorResponse y el código de estado HTTP 500
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
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
