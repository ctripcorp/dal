package com.ctrip.platform.dal.dao.client.DalCommand.nesting.rollbackOnly;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;

import java.sql.SQLException;

public class TwoLayerRollbackOnlyDalCommandWithRollbackOnlyAndException implements DalCommand {
    public TwoLayerRollbackOnlyDalCommandWithRollbackOnlyAndException() throws SQLException {}

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            DalTransactionManager.setRollbackOnly();
            client.execute(new OneLayerRollbackOnlyWithExceptionBeforeDalCommand(), new DalHints());
        } catch (Throwable e) {

        }
        return true;
    }
}
