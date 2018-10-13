package com.ctrip.framework.idgen.test.exception;

public class NullIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NullIdException() {
        super();
    }

    public NullIdException(String message) {
        super(message);
    }

    public NullIdException(Throwable cause) {
        super(cause);
    }

    public NullIdException(String message, Throwable cause) {
        super(message, cause);
    }

}
