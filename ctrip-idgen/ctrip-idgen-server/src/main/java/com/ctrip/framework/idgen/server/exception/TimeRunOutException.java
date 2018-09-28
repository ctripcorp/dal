package com.ctrip.framework.idgen.server.exception;

public class TimeRunOutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TimeRunOutException() {
        super();
    }

    public TimeRunOutException(String message) {
        super(message);
    }

    public TimeRunOutException(Throwable cause) {
        super(cause);
    }

    public TimeRunOutException(String message, Throwable cause) {
        super(message, cause);
    }

}
