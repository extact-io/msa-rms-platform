package io.extact.msa.rms.platform.fw.exception;

import java.util.List;

public class RmsValidationException extends RentalReservationServiceException {
    private ValidationErrorInfo errorInfo;

    public RmsValidationException(String message, ValidationErrorInfo errorInfo) {
        super(message);
        this.errorInfo = errorInfo;
    }
    public ValidationErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public interface ValidationErrorInfo {
        String getErrorReason();
        String getErrorMessage();
        List<ValidationErrorItem> getErrorItems();
    }
    public interface ValidationErrorItem {
        String getFieldName();
        String getMessage();
    }
}
