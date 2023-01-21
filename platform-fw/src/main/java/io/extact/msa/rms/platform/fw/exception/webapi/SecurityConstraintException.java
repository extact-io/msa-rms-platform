package io.extact.msa.rms.platform.fw.exception.webapi;

import jakarta.ws.rs.core.Response;

import io.extact.msa.rms.platform.fw.exception.RentalReservationServiceException;

public class SecurityConstraintException extends RentalReservationServiceException {

    private final transient Response response;

    public SecurityConstraintException(Response response) {
        super(getMessage(response));
        this.response = response;
    }

    public int getErrorStatus() {
        return response.getStatus();
    }

    private static String getMessage(Response response) {
        return switch (response.getStatus()) {
            case 401 -> "認証エラー";
            case 403 -> "認可エラー";
            default -> "不明のエラー";
        };
    }
}
