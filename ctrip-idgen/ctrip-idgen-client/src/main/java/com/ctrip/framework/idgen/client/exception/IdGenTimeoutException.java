package com.ctrip.framework.idgen.client.exception;

public class IdGenTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IdGenTimeoutException() {
        super();
    }

    public IdGenTimeoutException(String message) {
        super(message);
    }

    public IdGenTimeoutException(Throwable cause) {
        super(cause);
    }

    public IdGenTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
