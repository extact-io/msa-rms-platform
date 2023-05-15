package io.extact.msa.rms.platform.fw.exception;

public class RmsServiceUnavailableException extends RentalReservationServiceException {

    public RmsServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public RmsServiceUnavailableException(String message) {
        super(message);
    }

    public RmsServiceUnavailableException(Throwable cause) {
        super(cause);
    }
}
