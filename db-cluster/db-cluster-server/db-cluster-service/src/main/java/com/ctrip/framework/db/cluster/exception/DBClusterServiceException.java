package com.ctrip.framework.db.cluster.exception;

/**
 * Created by shenjie on 2019/3/12.
 */
public class DBClusterServiceException extends RuntimeException {

    public DBClusterServiceException() {
    }

    public DBClusterServiceException(String message) {
        super(message);
    }

    public DBClusterServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBClusterServiceException(Throwable cause) {
        super(cause);
    }

}
