package com.ctrip.framework.dal.cluster.client.exception;

/**
 * @author c7ch23en
 */
public class ClusterConfigException extends RuntimeException {

    public ClusterConfigException() {
        super();
    }

    public ClusterConfigException(String message) {
        super(message);
    }

    public ClusterConfigException(Throwable cause) {
        super(cause);
    }

    public ClusterConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
