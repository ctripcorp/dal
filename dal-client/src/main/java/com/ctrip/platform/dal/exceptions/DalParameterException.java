package com.ctrip.platform.dal.exceptions;

import com.ctrip.platform.dal.dao.StatementParameter;

import java.sql.SQLException;

public class DalParameterException extends SQLException {
    private static final long serialVersionUID = 1L;

    public DalParameterException(Throwable cause, StatementParameter parameter) {
        super(String.format("%s. Parameter[Index:%d, Name:%s, java.sql.Types:%d, java.sql.Types name:%s, Value:%s]",
                cause.getMessage(), parameter.getIndex(), parameter.getName() == null ? "" : parameter.getName(),
                parameter.getSqlType(), parameter.getSqlTypeName(),
                parameter.getValue() == null ? "" : parameter.getValue()), cause);
    }
}