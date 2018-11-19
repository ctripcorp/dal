package com.ctrip.platform.dal.exceptions;

public class DataSourceValidatorException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataSourceValidatorException() {
        super();
    }

    public DataSourceValidatorException(String message) {
        super(message);
    }

    public DataSourceValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceValidatorException(Throwable cause) {
        super(cause);
    }

}
