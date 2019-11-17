package com.ctrip.platform.dal.dao.helper;

public class DalRequestContext implements RequestContext {

    private String logicTableName;

    @Override
    public String getLogicTableName() {
        return logicTableName;
    }

    public DalRequestContext setLogicTableName(String logicTableName) {
        this.logicTableName = logicTableName;
        return this;
    }

}
