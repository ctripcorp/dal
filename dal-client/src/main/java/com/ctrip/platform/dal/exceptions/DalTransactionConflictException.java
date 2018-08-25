package com.ctrip.platform.dal.exceptions;

import java.sql.SQLException;

public class DalTransactionConflictException extends SQLException {
    private static final long serialVersionUID = 1L;

    public DalTransactionConflictException(ErrorCode errorCode, String message) {
        super(String.format("%s %s", errorCode == null ? "" : errorCode.getMessage(), message));
    }
}