package test.com.ctrip.platform.dal.dao.client.DalCommand;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ThrowExceptionDalCommand implements DalCommand {
    private TestTableDao dao;

    public ThrowExceptionDalCommand() throws SQLException {
        dao = new TestTableDao();
    }

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            Random r = new Random();
            List<TestTable> list = new ArrayList<>();
            TestTable a = new TestTable();
            a.setID(r.nextInt());
            a.setName("3333333333"); // over length to make a exception
            list.add(a);
            TestTable b = new TestTable();
            b.setID(r.nextInt());
            b.setName("4444444444"); // over length to make a exception
            list.add(b);

            dao.batchInsert(list);
        } catch (Throwable e) {
            throw e;
        }
        return false;
    }
}
