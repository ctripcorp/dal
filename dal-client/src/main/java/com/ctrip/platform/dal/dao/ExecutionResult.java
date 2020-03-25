package com.ctrip.platform.dal.dao;

/**
 * @author c7ch23en
 */
public interface ExecutionResult<V> extends CallbackContext {

    boolean isSuccess();

    V getResult();

    Throwable getErrorCause();

}
