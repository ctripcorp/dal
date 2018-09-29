package com.ctrip.framework.idgen.test.exception;

public class DuplicatedIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicatedIdException() {
        super();
    }

    public DuplicatedIdException(String message) {
        super(message);
    }

    public DuplicatedIdException(Throwable cause) {
        super(cause);
    }

    public DuplicatedIdException(String message, Throwable cause) {
        super(message, cause);
    }

}
