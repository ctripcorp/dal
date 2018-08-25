package test.com.ctrip.platform.dal.dao.client.DalCommand;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SwallowExceptionDalCommand implements DalCommand {
    private TestTableDao dao;

    public SwallowExceptionDalCommand() throws SQLException {
        dao = new TestTableDao();
    }

    @Override
    public boolean execute(DalClient client) throws SQLException {
        try {
            Random r = new Random();
            List<TestTable> list = new ArrayList<>();
            TestTable a = new TestTable();
            a.setID(r.nextInt());
            a.setName("1111111111"); // over length to make a exception
            list.add(a);
            TestTable b = new TestTable();
            b.setID(r.nextInt());
            b.setName("2222222222"); // over length to make a exception
            list.add(b);

            dao.batchInsert(list);
        } catch (Throwable e) {
            // swallow exception in DalCommand to reproduce case.
        }
        return false;
    }

}
