package com.ctrip.platform.dal.dao.client.DalCommand.nesting.rollbackOnly;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalCommand.RollbackOnlyDalCommand;

import java.sql.SQLException;

public class OneLayerRollbackOnlyDalCommand implements DalCommand {
    public OneLayerRollbackOnlyDalCommand() throws SQLException {}

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            client.execute(new RollbackOnlyDalCommand(), new DalHints());
        } catch (Throwable e) {
            throw e;
        }
        return false;
    }

}
