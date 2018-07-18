package test.com.ctrip.platform.dal.dao.client.DalCommand.nesting;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import test.com.ctrip.platform.dal.dao.client.DalCommand.SwallowExceptionDalCommand;

import java.sql.SQLException;

public class OneLayerConflictDalCommand implements DalCommand {
    public OneLayerConflictDalCommand() throws SQLException {}

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            client.execute(new SwallowExceptionDalCommand(), new DalHints());
        } catch (Throwable e) {
        }
        return false;
    }

}