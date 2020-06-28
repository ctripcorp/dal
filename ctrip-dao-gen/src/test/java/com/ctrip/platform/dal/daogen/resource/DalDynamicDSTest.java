package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.DynamicDS.CatSwitchDSDataProvider;
import com.ctrip.platform.dal.daogen.config.MonitorConfigManager;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.util.DateUtils;
import com.ctrip.platform.dal.daogen.util.EmailUtils;
import com.ctrip.platform.dal.daogen.util.IPUtils;
import com.ctrip.platform.dal.daogen.utils.JsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
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
        DalDynamicDSDao dalDynamicDSDao = DalDynamicDSDao.getInstance();
        dalDynamicDSDao.checkSwitchDataSource("2019072518", null, null, TriggerMethod.AUTO);

    }


    @Test
    public void parseCatTransactionTest() {
        CatSwitchDSDataProvider catSwitchDSDataProvider = new CatSwitchDSDataProvider();
        List<String> ips = new ArrayList<>();
        //catSwitchDSDataProvider.checkAppRefreshDataSourceTransaction("dalservice2db_w","110402", "2019070319", ips, "FAT");
        System.out.println();
    }

//    @Test
    public void parseCatTransactionTest1() {
        CatSwitchDSDataProvider catSwitchDSDataProvider = new CatSwitchDSDataProvider();
        SwitchHostIPInfo switchHostIPInfo = catSwitchDSDataProvider.checkIpRefreshDataSourceTransaction("dalservice2db_w","110402", "10.28.89.75", "2019070319", "FAT");
        System.out.println();
    }

    //模拟切换数据源cat打点
    @Test
    public void titanCatEvent() {
        Cat.logEvent("Titan.MHAUpdate.TitanKey:fat", "dalservice2db_w");
    }

    @Test
    public void dalConfigCatTransaction() {
        Transaction t = Cat.newTransaction("DAL.configure", "DataSourceConfig::refreshDataSourceConfig:dalservice2db_w");
        t.setStatus(Transaction.SUCCESS);
        t.complete();
    }

    @Test
    public void dalDataSourceCatTransaction() throws Exception {
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

    @Test
    public void testSendEmail() {
        DalDynamicDSDao dalDynamicDSDao = DalDynamicDSDao.getInstance();
//        TitanKeySwitchInfoDB titanKeySwitchInfoDB1 = new TitanKeySwitchInfoDB();
//        titanKeySwitchInfoDB1.setTitanKey("abcd");
//        titanKeySwitchInfoDB1.setSwitchCount(10);
//        titanKeySwitchInfoDB1.setAppIDCount(50);
//        titanKeySwitchInfoDB1.setIpCount(100);
//        TitanKeySwitchInfoDB titanKeySwitchInfoDB2 = new TitanKeySwitchInfoDB();
//        titanKeySwitchInfoDB2.setTitanKey("efgh");
//        titanKeySwitchInfoDB2.setSwitchCount(8);
//        titanKeySwitchInfoDB2.setAppIDCount(30);
//        titanKeySwitchInfoDB2.setIpCount(80);
//        List<TitanKeySwitchInfoDB> titanKeySwitchInfoDBList = new ArrayList<>();
//        titanKeySwitchInfoDBList.add(titanKeySwitchInfoDB1);
//        titanKeySwitchInfoDBList.add(titanKeySwitchInfoDB2);
        Date checkDate = new Date();
        String startCheckTime = DateUtils.getStartOneWeek(checkDate);
        String endCheckTime = DateUtils.getEndOneWeek(checkDate);
        String content = dalDynamicDSDao.generateBodyContent(dalDynamicDSDao.getSwitchDataInRange(startCheckTime, endCheckTime));
        String subject = String.format("动态数据源切换统计(%s-%s)",startCheckTime.substring(0,8), endCheckTime.substring(0,8));
        EmailUtils.sendEmail(content, subject, MonitorConfigManager.getMonitorConfig().getSwitchEmailRecipient(),
                MonitorConfigManager.getMonitorConfig().getSwitchEmailCc(), null);
    }

    @Test
    public void testGetCMSIP() {
        DalDynamicDSDao dalDynamicDSDao = DalDynamicDSDao.getInstance();
        List<String> appIds = new ArrayList<>();
        appIds.add("930201");
        appIds.add("100005701");
        List<AppIDInfo> appIDInfos = dalDynamicDSDao.getBatchAppIdIp(appIds, "pro");
        System.out.println(appIDInfos.size());
    }

    @Test
    public void testAll() {
        DalDynamicDSDao dalDynamicDSDao = DalDynamicDSDao.getInstance();
        Date checkDate = new Date();
        String checkTime = dalDynamicDSDao.getNowDateString(checkDate);
        dalDynamicDSDao.checkSwitchDataSource(checkTime, null, null, TriggerMethod.AUTO);
        dalDynamicDSDao.notifyByEmail(checkDate);
    }

    @Test
    public void getExecutorIP() {
        System.out.println(IPUtils.getExecuteIPFromQConfig());
    }


}
