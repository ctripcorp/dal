package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.DynamicDS.CatSwitchDSDataProvider;
import com.ctrip.platform.dal.daogen.entity.SwitchHostIPInfo;
import com.ctrip.platform.dal.daogen.entity.TriggerMethod;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by taochen on 2019/7/4.
 */
public class DalDynamicDSTest {

    @Test
    public void fixedCheckTest() {
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        DalDynamicDSDao dalDynamicDSDao = DalDynamicDSDao.getInstance();
        dalDynamicDSDao.checkSwitchDataSource(env, new Date(), TriggerMethod.MANUAL);
    }

    @Test
    public void parseCatTransactionTest() {
        CatSwitchDSDataProvider catSwitchDSDataProvider = new CatSwitchDSDataProvider();
        List<String> ips = new ArrayList<>();
        catSwitchDSDataProvider.checkAppRefreshDataSourceTransaction("110402", "2019070319", ips);
        System.out.println();
    }

    @Test
    public void parseCatTransactionTest1() {
        CatSwitchDSDataProvider catSwitchDSDataProvider = new CatSwitchDSDataProvider();
        SwitchHostIPInfo switchHostIPInfo = catSwitchDSDataProvider.checkIpRefreshDataSourceTransaction("110402", "10.28.89.75", "2019070319");
        System.out.println();
    }
}
