package com.ctrip.platform.dal.exceptions;

/**
 * Created by lilj on 2018/4/27.
 */
public class DalRuntimeException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public DalRuntimeException() {
        super();
    }

    public DalRuntimeException(String message) {
        super(message);
    }

    public DalRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DalRuntimeException(Throwable cause) {
        super(cause);
    }
}
