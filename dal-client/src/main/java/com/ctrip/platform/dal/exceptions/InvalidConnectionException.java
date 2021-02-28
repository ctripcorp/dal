package com.ctrip.platform.dal.exceptions;

/**
 * @author c7ch23en
 */
public class InvalidConnectionException extends DalRuntimeException {

    public InvalidConnectionException() {
        super();
    }

    public InvalidConnectionException(String message) {
        super(message);
    }

    public InvalidConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConnectionException(Throwable cause) {
        super(cause);
    }

}
