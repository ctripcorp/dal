package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.DalEventEnum;

import java.util.Set;

public class MockLogEntry implements ILogEntry {
    private String logicDbName;
    private Set<String> tables;
    private DalEventEnum event;
    private String method;

    public MockLogEntry(String logicDbName, Set<String> tables, DalEventEnum event, String method) {
        this.logicDbName = logicDbName;
        this.tables = tables;
        this.event = event;
        this.method = method;
    }

    @Override
    public String getLogicDbName() {
        return logicDbName;
    }

    @Override
    public Set<String> getTables() {
        return tables;
    }

    @Override
    public DalEventEnum getEvent() {
        return event;
    }

    @Override
    public String getSource() {
        return method;
    }
}
