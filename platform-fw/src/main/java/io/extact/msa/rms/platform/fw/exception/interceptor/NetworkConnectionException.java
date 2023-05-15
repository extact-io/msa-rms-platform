package io.extact.msa.rms.platform.fw.exception.interceptor;

import io.extact.msa.rms.platform.fw.exception.RmsServiceUnavailableException;

public class NetworkConnectionException extends RmsServiceUnavailableException {

    public NetworkConnectionException(Throwable e) {
        super(e);
    }

    public NetworkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
