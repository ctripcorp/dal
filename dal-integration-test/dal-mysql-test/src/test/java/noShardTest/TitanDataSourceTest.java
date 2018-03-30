package noShardTest;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.datasource.titan.DataSourceConfigureHelper;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import javax.validation.constraints.AssertTrue;
import java.awt.*;

/**
 * Created by lilj on 2018/3/7.
 */
public class TitanDataSourceTest {
    //    server.properties env=fat，set serviceUrl uat
    @Test
    public void testTitanDataSourceMySql() throws Exception {
        DataSource ds = new DalDataSourceFactory().createTitanDataSource("DalService2DB_W", "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
        String connectionUrl = ds.getConnection().getMetaData().getURL();
        Assert.assertTrue(connectionUrl.contains("uat"));
        Assert.assertFalse(connectionUrl.contains("fat"));
    }

    @Test
    public void testTitanDataSourceSQLServer() throws Exception {
        DataSource ds = new DalDataSourceFactory().createTitanDataSource("DalService2DB", "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
        String connectionUrl = ds.getConnection().getMetaData().getURL();
        Assert.assertTrue(connectionUrl.contains("uat"));
        Assert.assertFalse(connectionUrl.contains("fat"));
    }

    @Test
    public void testTitanDataSourcePoolPropertiesMySQL() throws Exception {
        DataSource ds = new DalDataSourceFactory().createTitanDataSource("ActLogDB_W", "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
        Assert.assertNull(ds);
    }

    @Test
    public void testTitanDataSourcePoolPropertiesSQLServer() throws Exception {
        try {
            DataSource ds = new DalDataSourceFactory().createTitanDataSource("daltestdb_S1_R", "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
            ds.getConnection().getMetaData().getURL();
        } catch (Exception e) {
            Assert.assertEquals("Validation Query Failed, enable logValidationErrors for more details.", e.getMessage().toString().trim());
//            System.out.println("e.getMessage: " + e.getMessage());
//            e.printStackTrace();
        }
    }
}
