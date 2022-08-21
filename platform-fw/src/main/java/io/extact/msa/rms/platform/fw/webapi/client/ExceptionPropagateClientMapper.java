package io.extact.msa.rms.platform.fw.webapi.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Priority;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.fw.exception.RmsSystemException;
import io.extact.msa.rms.platform.fw.exception.RmsValidationException;
import io.extact.msa.rms.platform.fw.webapi.GenericErrorInfo;
import io.extact.msa.rms.platform.fw.webapi.SecurityConstraintException;
import io.extact.msa.rms.platform.fw.webapi.ValidationErrorInfoImpl;

@Priority(Priorities.USER)
@ConstrainedTo(RuntimeType.CLIENT)
public class ExceptionPropagateClientMapper implements ResponseExceptionMapper<RuntimeException> {

    private static final String RMS_EXCEPTION_HEADER = "Rms-Exception";
    private static final List<Integer> SECURITY_ERROR_STATUS =
            List.of(
                Status.UNAUTHORIZED.getStatusCode(),
                Status.FORBIDDEN.getStatusCode()
            );

    private Map<String, Function<Response, RuntimeException>> rmsExceptionMappers = new HashMap<>();
    private Function<Response, RuntimeException> otherExceptionMapper;


    // ------------------------------------------------------------------ constructor methods.

    public ExceptionPropagateClientMapper() {
        rmsExceptionMappers.put(BusinessFlowException.class.getSimpleName(), this::toBusinessFlowException);
        rmsExceptionMappers.put(RmsValidationException.class.getSimpleName(), this::toRmsValidationException);
        rmsExceptionMappers.put(ConstraintViolationException.class.getSimpleName(), this::toRmsValidationException);
        rmsExceptionMappers.put(RmsSystemException.class.getSimpleName(), this::toRmsSystemException);
        otherExceptionMapper = this::toOtherExceptionMapping;
    }

    // ------------------------------------------------------------------ handle methods.

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return headers.containsKey(RMS_EXCEPTION_HEADER)
                || SECURITY_ERROR_STATUS.contains(status);
    }

    @Override
    public RuntimeException toThrowable(Response response) {
        var className = response.getHeaderString(RMS_EXCEPTION_HEADER);
        var exceptionMapper = rmsExceptionMappers.getOrDefault(className, otherExceptionMapper);
        return exceptionMapper.apply(response);
    }


    // ------------------------------------------------------------------ mapping methods.

    private BusinessFlowException toBusinessFlowException(Response response) {
        var errorInfo = response.readEntity(GenericErrorInfo.class);
        return new BusinessFlowException(errorInfo.getErrorMessage(),
                CauseType.valueOf(errorInfo.getErrorReason()));
    }

    private RmsValidationException toRmsValidationException(Response response) {
        var errorInfo = response.readEntity(ValidationErrorInfoImpl.class);
        return new RmsValidationException(errorInfo.getErrorMessage(), errorInfo);
    }

    private RmsSystemException toRmsSystemException(Response response) {
        var errorInfo = response.readEntity(GenericErrorInfo.class);
        return new RmsSystemException(errorInfo.getErrorMessage());
    }

    private RuntimeException toOtherExceptionMapping(Response response) {
        if (SECURITY_ERROR_STATUS.contains(response.getStatus())) {
            return new SecurityConstraintException(response);
        }
        return new RmsSystemException("unknown server error.");
    }
}
