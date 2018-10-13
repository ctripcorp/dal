package com.ctrip.framework.idgen.server.exception;

public class ServiceTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceTimeoutException() {
        super();
    }

    public ServiceTimeoutException(String message) {
        super(message);
    }

    public ServiceTimeoutException(Throwable cause) {
        super(cause);
    }

    public ServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
