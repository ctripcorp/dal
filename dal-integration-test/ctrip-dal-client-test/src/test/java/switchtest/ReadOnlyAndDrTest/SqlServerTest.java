package switchtest.ReadOnlyAndDrTest;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import dao.noshard.NoShardOnMysqlDao;
import dao.noshard.NoShardOnSqlServerDao;
import entity.SqlServerPeopleTable;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;

public class SqlServerTest {
    private static Logger log = LoggerFactory.getLogger(SqlServerTest.class);
    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "DalConfigForDEV/Dal.config");
    }

    @Test
    public void testNormalQuery() throws Exception {
        NoShardOnSqlServerDao dao = new NoShardOnSqlServerDao();
        while (true) {
            try {
                log.info("current server name: " + dao.select_servername_timeout(0, null));
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                Thread.sleep(1000);
            }
        }
    }

    @Test
    public void testLongQuery() throws Exception {
        NoShardOnSqlServerDao dao = new NoShardOnSqlServerDao();
        while (true)
            try {
                log.info("current server name: " + dao.select_servername_timeout(10, null));
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                Thread.sleep(1000);
            }
    }

    @Test
    public void testUpdate() throws Exception {
        NoShardOnSqlServerDao dao = new NoShardOnSqlServerDao();
        SqlServerPeopleTable pojo=new SqlServerPeopleTable();
        pojo.setPeopleID(2l);
        pojo.setName("test");

        while (true)
            try {
                log.info("update: " + dao.update(null,pojo));
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                Thread.sleep(1000);
            }
    }

    @Test
    public void testLeakConnection() throws Exception {
       Connection connection;
        while (true)
            try {
                connection= new DalDataSourceFactory().createDataSource("DalServiceDB").getConnection();
//                connection.createStatement().execute("select @@servername");
                connection.createStatement().execute("update people set name='test' where peopleid=1");
                log.info("select done.");
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                Thread.sleep(1000);
            }
    }

    @Test
    public void testSetTimeoutMarkdown() throws Exception{
        DalStatusManager.getTimeoutMarkdown().setTimeoutThreshold(10);
       try {
           NoShardOnMysqlDao dao = new NoShardOnMysqlDao();
           dao.test_timeout(15, new DalHints());
       }catch (Exception e){
           e.printStackTrace();
       }
        try {
            NoShardOnSqlServerDao dao = new NoShardOnSqlServerDao();
            dao.test_timeout(15, new DalHints());
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
