package DRTestOnMysql;

import com.ctrip.framework.dal.datasourceswitch.IpDomainSwitch;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.framework.dal.datasourceswitch.ConnectionStringSwitch;
import com.ctrip.framework.dal.datasourceswitch.PoolPropertiesSwitch;
import com.ctrip.framework.dal.datasourceswitch.netstat.NetStat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lilj on 2018/3/4.
 */
public class IpDomainSwitchTest {
    //    PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("CorpPerformanceManagementDB_W"));
    private static ConnectionStringSwitch connectionStringSwitch = new ConnectionStringSwitch();
    private static Logger log = LoggerFactory.getLogger(IpDomainSwitchTest.class);
    private static IpDomainSwitch ipDomainSwitch = new IpDomainSwitch();

    @Test
    public void test() {
        try {
            DalClientFactory.initClientFactory();
            DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
            DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
//            SingleDataSource singleDataSource = new SingleDataSource("mysqldaltest01db_W", dataSourceConfigure);
            log.info(String.format("before switch: %s", dataSourceConfigure.getConnectionUrl()));
            connectionStringSwitch.postByMHA(true);
            log.info(String.format("10s after switch"));
            Thread.sleep(10000);
            dataSourceConfigureLocator = dataSourceConfigureLocator.getInstance();
            dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
//            singleDataSource=new SingleDataSource("mysqldaltest01db_W",dataSourceConfigure);
            log.info(String.format("after switch: %s", dataSourceConfigure.getConnectionUrl()));
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIpDomainSwitch() {
        try {
            DalClientFactory.initClientFactory();
            DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
            DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
//            SingleDataSource singleDataSource = new SingleDataSource("mysqldaltest01db_W", dataSourceConfigure);
            log.info(String.format("before switch: %s", dataSourceConfigure.getConnectionUrl()));
//            connectionStringSwitch.postByMHA(true);
            ipDomainSwitch.setFailover();
            log.info(String.format("35s after switch"));
            Thread.sleep(35000);
            dataSourceConfigureLocator = dataSourceConfigureLocator.getInstance();
            dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
//            singleDataSource=new SingleDataSource("mysqldaltest01db_W",dataSourceConfigure);
            log.info(String.format("after switch: %s", dataSourceConfigure.getConnectionUrl()));
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
