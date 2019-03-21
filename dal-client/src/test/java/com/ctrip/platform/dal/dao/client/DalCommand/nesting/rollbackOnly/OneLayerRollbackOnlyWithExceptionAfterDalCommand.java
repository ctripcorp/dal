package com.ctrip.platform.dal.dao.client.DalCommand.nesting.rollbackOnly;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalCommand.RollbackOnlyDalCommand;
import com.ctrip.platform.dal.exceptions.DalException;

import java.sql.SQLException;

public class OneLayerRollbackOnlyWithExceptionAfterDalCommand implements DalCommand {
    public OneLayerRollbackOnlyWithExceptionAfterDalCommand() throws SQLException {}

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            client.execute(new RollbackOnlyDalCommand(), new DalHints());
            throw new DalException("Manual throw exception.");
        } catch (Throwable e) {
            throw e;
        }
    }
}
