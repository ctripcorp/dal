package com.ctrip.framework.idgen.client.exception;

public class ClientTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ClientTimeoutException() {
        super();
    }

    public ClientTimeoutException(String message) {
        super(message);
    }

    public ClientTimeoutException(Throwable cause) {
        super(cause);
    }

    public ClientTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
