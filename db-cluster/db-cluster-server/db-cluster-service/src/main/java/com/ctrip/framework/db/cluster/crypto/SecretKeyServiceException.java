package com.ctrip.framework.db.cluster.crypto;

public class SecretKeyServiceException extends Exception {
    public SecretKeyServiceException(String message) {
        super(message);
    }

    public SecretKeyServiceException(Exception exception) {
        super(exception);
    }
}
