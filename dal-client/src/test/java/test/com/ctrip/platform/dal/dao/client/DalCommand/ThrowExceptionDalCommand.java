package test.com.ctrip.platform.dal.dao.client.DalCommand;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThrowExceptionDalCommand implements DalCommand {
    private TestTableDao dao;

    public ThrowExceptionDalCommand() throws SQLException {
        dao = new TestTableDao();
    }

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            List<TestTable> list = new ArrayList<>();
            TestTable a = new TestTable();
            a.setID(3);
            a.setName("3333333333"); // over length to make a exception
            list.add(a);
            TestTable b = new TestTable();
            b.setID(4);
            b.setName("4444444444"); // over length to make a exception
            list.add(b);

            dao.batchInsert(list);
        } catch (Throwable e) {
            throw e;
        }
        return false;
    }
}