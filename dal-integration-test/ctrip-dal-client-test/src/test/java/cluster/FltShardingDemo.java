package cluster;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import dao.CommonDao;
import entity.MysqlPersonTable;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FltShardingDemo {

    private CommonDao<MysqlPersonTable> dao;
    private DalQueryDao queryDao;

    public FltShardingDemo() {
        try {
            dao = new CommonDao<>(MysqlPersonTable.class, "dal_sharding_cluster");
            queryDao = new DalQueryDao("dal_sharding_cluster");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Before
    public void before() {
        System.setProperty("qserver.http.urls", "localhost:8080");
        System.setProperty("qserver.https.urls", "localhost:8443");
    }

    @Test
    public void testSingleInsert() throws SQLException {
        dao.insert(new DalHints(), createPojo(83));
    }

    @Test
    public void testBatchInsert() throws SQLException {
        dao.batchInsert(new DalHints(), createPojos(1,2,3,4,41,42,43,44,81,82,83,84));
    }

    @Test
    public void testFreeSql() throws SQLException {
        String db = queryDao.queryForObject("select database()", new StatementParameters(), new DalHints().inShard(2), String.class);
        System.out.println("database: " + db);
    }

    private MysqlPersonTable createPojo(int age, String name) {
        MysqlPersonTable pojo = new MysqlPersonTable();
        pojo.setAge(age);
        pojo.setName(name);
        return pojo;
    }

    private MysqlPersonTable createPojo(int age) {
        return createPojo(age, "name-" + age);
    }

    private List<MysqlPersonTable> createPojos(int... ages) {
        List<MysqlPersonTable> pojos = new ArrayList<>();
        for (int age :ages)
            pojos.add(createPojo(age));
        return pojos;
    }

}
