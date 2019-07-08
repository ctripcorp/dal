package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.DynamicDS.CatSwitchDSDataProvider;
import com.ctrip.platform.dal.daogen.entity.SwitchHostIPInfo;
import com.ctrip.platform.dal.daogen.entity.TriggerMethod;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by taochen on 2019/7/4.
 */
public class DalDynamicDSTest {

    @Test
    public void fixedCheckTest() throws Exception {
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        DalDynamicDSDao dalDynamicDSDao = DalDynamicDSDao.getInstance();
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date date = sdf.parse("2019-07-08 12:23:00");
        dalDynamicDSDao.checkSwitchDataSource(env, date, TriggerMethod.MANUAL);
    }

    @Test
    public void parseCatTransactionTest() {
        CatSwitchDSDataProvider catSwitchDSDataProvider = new CatSwitchDSDataProvider();
        List<String> ips = new ArrayList<>();
        catSwitchDSDataProvider.checkAppRefreshDataSourceTransaction("dalservice2db_w","110402", "2019070319", ips, "FAT");
        System.out.println();
    }

    @Test
    public void parseCatTransactionTest1() {
        CatSwitchDSDataProvider catSwitchDSDataProvider = new CatSwitchDSDataProvider();
        SwitchHostIPInfo switchHostIPInfo = catSwitchDSDataProvider.checkIpRefreshDataSourceTransaction("dalservice2db_w","110402", "10.28.89.75", "2019070319", "FAT");
        System.out.println();
    }

    //模拟切换数据源cat打点
    @Test
    public void titanCatEvent() {
        Cat.logEvent("Titan.MHAUpdate.TitanKey", "dalservice2db_w");
    }

    @Test
    public void dalConfigCatTransaction() {
        Transaction t = Cat.newTransaction("DAL.configure", "DataSourceConfig::refreshDataSourceConfig:dalservice2db_w");
        t.setStatus(Transaction.SUCCESS);
        t.complete();
    }

    @Test
    public void dalDataSourceCatTransaction() {
        Transaction t = Cat.newTransaction("DAL.dataSource", "DataSource::createDataSource:dalservice2db_w");
        t.setStatus(Transaction.SUCCESS);
        t.complete();
    }

    @Test
    public void printAppId() {
        final String APP_PROPERTIES_CLASSPATH = "/META-INF/app.properties";
        Properties m_appProperties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        if (in == null) {
            in = DalDynamicDSTest.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        }
        try {
            m_appProperties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("App ID： " + m_appProperties.getProperty("app.id"));
    }
}
