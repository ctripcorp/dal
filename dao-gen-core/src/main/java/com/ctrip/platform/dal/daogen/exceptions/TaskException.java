package com.ctrip.platform.dal.daogen.exceptions;

public class TaskException extends Exception {
    private static final long serialVersionUID = 1L;

    public TaskException() {
        super();
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskException(Throwable cause) {
        super(cause);
    }

}
