package com.ctrip.platform.dal.dao.datasource.log;

/**
 * @author c7ch23en
 */
public enum OperationType {

    QUERY(false),
    UPDATE(true),
    DELETE(true),
    INSERT(true);

    private boolean isUpdateOperation;

    OperationType(boolean isUpdateOperation) {
        this.isUpdateOperation = isUpdateOperation;
    }

    public boolean isUpdateOperation() {
        return isUpdateOperation;
    }


}
