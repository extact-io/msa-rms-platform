package io.extact.msa.rms.platform.fw.webapi;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;

import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.RmsSystemException;
import io.extact.msa.rms.platform.fw.exception.RmsValidationException;
import io.extact.msa.rms.platform.fw.exception.webapi.GenericErrorInfo;
import io.extact.msa.rms.platform.fw.exception.webapi.SecurityConstraintException;
import io.extact.msa.rms.platform.fw.exception.webapi.ValidationErrorInfoImpl;
import io.extact.msa.rms.platform.fw.exception.webapi.ValidationErrorInfoImpl.ValidationErrorItemImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConstrainedTo(RuntimeType.SERVER)
public class ServerExceptionMappers {

    private static final String RMS_EXCEPTION_HEAD = "Rms-Exception";

    // ----------------------------------------------------- ExceptionMapper classes

    // Provider Class
    @Produces(MediaType.APPLICATION_JSON)
    public static class BusinessFlowExceptionMapper implements ExceptionMapper<BusinessFlowException> {

        @Override
        public Response toResponse(BusinessFlowException exception) {
            log.warn("exception occured. message={}", exception.getMessage());

            var errorInfo = new GenericErrorInfo(exception.getCauseType().name(), exception.getMessage());

            Status status = switch (exception.getCauseType()) {
                case NOT_FOUND          -> Status.NOT_FOUND;
                case DUPRICATE, REFERED -> Status.CONFLICT;
                case FORBIDDEN          -> Status.FORBIDDEN;
            };

            return Response
                        .status(status)
                        .header(RMS_EXCEPTION_HEAD, BusinessFlowException.class.getSimpleName())
                        .entity(errorInfo)
                        .build();
        }
    }

    // Provider Class
    @Produces(MediaType.APPLICATION_JSON)
    public static class RmsValidationExceptionMapper implements ExceptionMapper<RmsValidationException> {

        @Override
        public Response toResponse(RmsValidationException exception) {
            log.warn("exception occured. message={}", exception.getMessage());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .header(RMS_EXCEPTION_HEAD, exception.getClass().getSimpleName())
                    .entity(exception.getErrorInfo())
                    .build();
        }
    }

    // Provider Class
    @Produces(MediaType.APPLICATION_JSON)
    public static class RmsSystemExceptionMapper implements ExceptionMapper<RmsSystemException> {

        @Override
        public Response toResponse(RmsSystemException exception) {
            log.warn("exception occured. message={}", exception.getMessage());
            var errorInfo = new GenericErrorInfo(exception.getClass().getSimpleName(), exception.getMessage());
            return Response
                        .status(Status.INTERNAL_SERVER_ERROR)
                        .header(RMS_EXCEPTION_HEAD, RmsSystemException.class.getSimpleName())
                        .entity(errorInfo)
                        .build();
        }
    }

    // Provider Class
    @Produces(MediaType.APPLICATION_JSON)
    public static class ConstraintExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Response toResponse(ConstraintViolationException exception) {
            log.warn("exception occured. message={}", exception.getMessage());

            Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
            List errorItems = constraintViolations.stream()
                    .map(v -> new ValidationErrorItemImpl(v.getPropertyPath().toString(), v.getMessage()))
                    .toList();

            var validationErrorInfo = new ValidationErrorInfoImpl(
                    exception.getClass().getSimpleName(),
                    "validation error occurred.",
                    errorItems);

            return Response
                        .status(Response.Status.BAD_REQUEST)
                        .header(RMS_EXCEPTION_HEAD, exception.getClass().getSimpleName())
                        .entity(validationErrorInfo)
                        .build();
        }
    }

    // Provider Class
    @Produces(MediaType.APPLICATION_JSON)
    public static class SecurityConstraintExceptionMapper implements ExceptionMapper<SecurityConstraintException> {

        @Override
        public Response toResponse(SecurityConstraintException exception) {
            log.warn("exception occured. message={}", exception.getMessage());
            return Response
                        .status(exception.getErrorStatus())
                        .build();
        }
    }
}
