package com.ctrip.framework.dal.cluster.client.exception;

public class HostNotExpectedException extends ClusterRuntimeException {

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

}
