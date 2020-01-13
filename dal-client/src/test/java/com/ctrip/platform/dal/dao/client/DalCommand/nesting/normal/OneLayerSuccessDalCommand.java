package com.ctrip.platform.dal.dao.client.DalCommand.nesting.normal;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalCommand.SuccessDalCommand;

import java.sql.SQLException;

public class OneLayerSuccessDalCommand implements DalCommand {
    public OneLayerSuccessDalCommand() throws SQLException {}

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            client.execute(new SuccessDalCommand(), new DalHints());
        } catch (Throwable e) {
            throw e;
        }
        return false;
    }
}