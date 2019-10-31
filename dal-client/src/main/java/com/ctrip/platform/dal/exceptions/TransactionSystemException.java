package com.ctrip.platform.dal.exceptions;

/**
 * Created by taochen on 2019/9/17.
 */
public class TransactionSystemException extends RuntimeException {
    public TransactionSystemException(String msg) {
        super(msg);
    }

    public TransactionSystemException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
