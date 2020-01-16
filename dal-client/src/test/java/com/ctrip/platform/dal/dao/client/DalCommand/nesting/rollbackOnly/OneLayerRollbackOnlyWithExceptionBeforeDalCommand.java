package com.ctrip.platform.dal.dao.client.DalCommand.nesting.rollbackOnly;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.exceptions.DalException;

import java.sql.SQLException;

public class OneLayerRollbackOnlyWithExceptionBeforeDalCommand implements DalCommand {
    public OneLayerRollbackOnlyWithExceptionBeforeDalCommand() throws SQLException {}

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            throw new DalException("Manual throw exception.");
            // unreachable
            // client.execute(new RollbackOnlyDalCommand(), new DalHints());
        } catch (Throwable e) {
            throw e;
        }
    }
}
