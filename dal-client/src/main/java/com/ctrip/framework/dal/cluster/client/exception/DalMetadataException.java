package com.ctrip.framework.dal.cluster.client.exception;

public class DalMetadataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DalMetadataException() {
        super();
    }

    public DalMetadataException(String message) {
        super(message);
    }

    public DalMetadataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DalMetadataException(Throwable cause) {
        super(cause);
    }
}
