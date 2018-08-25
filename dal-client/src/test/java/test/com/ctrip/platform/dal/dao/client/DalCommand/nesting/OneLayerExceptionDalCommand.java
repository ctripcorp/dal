package test.com.ctrip.platform.dal.dao.client.DalCommand.nesting;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import test.com.ctrip.platform.dal.dao.client.DalCommand.ThrowExceptionDalCommand;

import java.sql.SQLException;

public class OneLayerExceptionDalCommand implements DalCommand {
    public OneLayerExceptionDalCommand() throws SQLException {}

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            client.execute(new ThrowExceptionDalCommand(), new DalHints());
        } catch (Throwable e) {
            throw e;
        }
        return false;
    }

}