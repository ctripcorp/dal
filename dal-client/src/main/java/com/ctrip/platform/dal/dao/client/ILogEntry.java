package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.DalEventEnum;

import java.util.Set;

public interface ILogEntry {
    String getLogicDbName();
    Set<String> getTables();
    DalEventEnum getEvent();
    String getSource();
}
