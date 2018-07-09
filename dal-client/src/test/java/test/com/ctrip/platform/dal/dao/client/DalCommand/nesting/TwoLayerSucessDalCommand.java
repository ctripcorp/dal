package test.com.ctrip.platform.dal.dao.client.DalCommand.nesting;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;

import java.sql.SQLException;

public class TwoLayerSucessDalCommand implements DalCommand {
    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            client.execute(new OneLayerSuccessDalCommand(), new DalHints());
        } catch (Throwable e) {
            throw e;
        }
        return false;
    }
}