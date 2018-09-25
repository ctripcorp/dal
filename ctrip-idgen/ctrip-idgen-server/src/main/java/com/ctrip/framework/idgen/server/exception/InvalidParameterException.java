package com.ctrip.framework.idgen.server.exception;

public class InvalidParameterException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidParameterException() {
        super();
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(Throwable cause) {
        super(cause);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }

}
