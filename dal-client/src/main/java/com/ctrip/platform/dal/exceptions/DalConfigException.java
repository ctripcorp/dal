package com.ctrip.platform.dal.exceptions;

public class DalConfigException extends Exception {
    private static final long serialVersionUID = 1L;

    public DalConfigException() {
        super();
    }

    public DalConfigException(String message) {
        super(message);
    }

    public DalConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public DalConfigException(Throwable cause) {
        super(cause);
    }
}
