package com.ctrip.platform.dal.cluster.exception;

/**
 * @author c7ch23en
 */
public class ClusterRuntimeException extends RuntimeException {

    public ClusterRuntimeException() {
        super();
    }

    public ClusterRuntimeException(String message) {
        super(message);
    }

    public ClusterRuntimeException(Throwable cause) {
        super(cause);
    }

    public ClusterRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
