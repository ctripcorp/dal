package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

import java.sql.SQLException;

/**
 * Created by taochen on 2019/10/23.
 */
public class CustomDalConnectionManager extends DalConnectionManager {
    public CustomDalConnectionManager(String logicDbName, DalConfigure config) {
        super(logicDbName, config);
    }

    @Override
    public DalConnection getNewConnection(DalHints hints, boolean useMaster, ConnectionAction action)
            throws SQLException {
        return new DalConnection(new CustomConnection(), true, null, super.getNewConnection(hints, useMaster, action).getMeta());
    }
}
