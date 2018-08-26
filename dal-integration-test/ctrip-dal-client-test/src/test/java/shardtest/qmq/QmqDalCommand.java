package shardtest.qmq;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QmqDalCommand implements DalCommand {

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            // dal insert
            String sql = "insert into dalservicetable (name,age) values('qmqTest',null)";
            StatementParameters parameters = new StatementParameters();
            DalHints hints = new DalHints();
            hints.inShard("1");
            client.update(sql, parameters, hints);

            // qmq send message
            Producer producer = new Producer();
            producer.senMessage();

            // delete
            String sql1 = "delete from dalservicetable where name = 'qmqTest'";
            StatementParameters parameters1 = new StatementParameters();
            DalHints hints1 = new DalHints();
            hints1.inShard("1");
            client.update(sql1, parameters1, hints1);

            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private class IntegerRowMapper implements DalRowMapper<Integer> {
        @Override
        public Integer map(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        }
    }

}
