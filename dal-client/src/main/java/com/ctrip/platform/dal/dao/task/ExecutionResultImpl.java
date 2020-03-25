package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.ExecutionResult;

/**
 * @author c7ch23en
 */
public class ExecutionResultImpl<V> implements ExecutionResult<V> {

    private V result;
    private Throwable errorCause;

    public ExecutionResultImpl(V result) {
        this.result = result;
    }

    public ExecutionResultImpl(Throwable errorCause) {
        this.errorCause = errorCause;
    }

    @Override
    public boolean isSuccess() {
        return errorCause == null;
    }

    @Override
    public V getResult() {
        return result;
    }

    @Override
    public Throwable getErrorCause() {
        return errorCause;
    }

}
