package com.ctrip.platform.dal.exceptions;

/**
 * @author c7ch23en
 */
public class UnsupportedFeatureException extends DalRuntimeException {

    public UnsupportedFeatureException() {
        super();
    }

    public UnsupportedFeatureException(String message) {
        super(message);
    }

    public UnsupportedFeatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedFeatureException(Throwable cause) {
        super(cause);
    }

}
