package com.ctrip.platform.dal.exceptions;

public class HostNotExpectedException extends Exception {

    public HostNotExpectedException() {
    }

    public HostNotExpectedException(String message) {
        super(message);
    }

    public HostNotExpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public HostNotExpectedException(Throwable cause) {
        super(cause);
    }

    public HostNotExpectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
