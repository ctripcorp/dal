package test.com.ctrip.platform.dal.dao.client.DalCommand;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SuccessDalCommand implements DalCommand {
    private TestTableDao dao;

    public SuccessDalCommand() throws SQLException {
        dao = new TestTableDao();
    }

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            List<TestTable> list = new ArrayList<>();
            TestTable a = new TestTable();
            a.setID(5);
            a.setName("5");
            list.add(a);
            TestTable b = new TestTable();
            b.setID(6);
            b.setName("6");
            list.add(b);

            dao.batchInsert(list);
        } catch (Throwable e) {
            throw e;
        }
        return false;
    }
}