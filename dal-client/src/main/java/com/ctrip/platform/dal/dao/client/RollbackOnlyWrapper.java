package com.ctrip.platform.dal.dao.client;

public class RollbackOnlyWrapper {
    private boolean rollbackOnly;

    private Throwable error;

    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

}
