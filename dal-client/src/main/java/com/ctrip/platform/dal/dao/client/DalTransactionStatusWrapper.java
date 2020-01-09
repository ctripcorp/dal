package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.common.enums.DalTransactionStatus;

public class DalTransactionStatusWrapper {
    private DalTransactionStatus transactionStatus;
    private DalTransactionStatus actualStatus;
    private String errorMessage;

    public DalTransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(DalTransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public DalTransactionStatus getActualStatus() {
        return actualStatus;
    }

    public void setActualStatus(DalTransactionStatus actualStatus) {
        this.actualStatus = actualStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}