package com.ctrip.framework.dal.cluster.client.exception;

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
