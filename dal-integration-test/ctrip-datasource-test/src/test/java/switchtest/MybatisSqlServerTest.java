package switchtest;


import mybatis.sqlserver.DRTestSQLServerMapperDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by lilj on 2018/3/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/application-context-SqlServer.xml")
public class MybatisSqlServerTest {
    @Autowired
    DRTestSQLServerMapperDao drTestSQLServerMapperDao;

    @Test
    public void test() throws Exception{
        drTestSQLServerMapperDao.truncateTableSQLServer();
        drTestSQLServerMapperDao.addDRTestMybatisSQLServerPojo();
        String name=drTestSQLServerMapperDao.getDRTestMybatisSQLServerPojo().getName();
        System.out.println(name);
    }
}
