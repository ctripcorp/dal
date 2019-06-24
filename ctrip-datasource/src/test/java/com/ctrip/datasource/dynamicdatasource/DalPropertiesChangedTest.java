package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.platform.dal.common.enums.ImplicitAllShardsSwitch;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.datasource.dynamicdatasource.provider.LocalDalPropertiesProvider;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Created by lilj on 2018/7/24.
 */
public class DalPropertiesChangedTest {
    private static LocalDalPropertiesProvider localDalPropertiesProvider = new LocalDalPropertiesProvider();
    private static DalPropertiesManager dalPropertiesManager = DalPropertiesManager.getInstance();
    private static DalPropertiesLocator locator = DalPropertiesManager.getInstance().getDalPropertiesLocator();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        dalPropertiesManager.tearDown();
        dalPropertiesManager.setDalPropertiesProvider(localDalPropertiesProvider);
        dalPropertiesManager.setup();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        dalPropertiesManager.tearDown();
        dalPropertiesManager.setDalPropertiesProvider(DalElementFactory.DEFAULT.getDalPropertiesProvider());
        dalPropertiesManager.setup();
    }

    @Test
    public void testSetUpWhenTableParseSwitchOn() throws Exception {
        localDalPropertiesProvider.setOn();
        dalPropertiesManager.setup();
        Assert.assertEquals(TableParseSwitch.ON, locator.getTableParseSwitch());
    }

    @Test
    public void testSetUpWhenTableParseSwitchOff() throws Exception {
        localDalPropertiesProvider.setOff();
        dalPropertiesManager.setup();
        Assert.assertEquals(TableParseSwitch.OFF, locator.getTableParseSwitch());
    }

    @Test
    public void testTableParseSwitchChange() throws Exception {
        localDalPropertiesProvider.initStatus();
        dalPropertiesManager.setup();
        for (int i = 0; i < 10; i++) {
            TableParseSwitch initStatus = locator.getTableParseSwitch();
            localDalPropertiesProvider.triggerTableParseSwitchChanged();
            Thread.sleep(1 * 1000);
            TableParseSwitch switchStatus = locator.getTableParseSwitch();
            Assert.assertNotEquals(initStatus, switchStatus);
        }
    }

    @Test
    public void testImplicitAllShardsSwitchChange() throws Exception {
        localDalPropertiesProvider.initStatus();
        dalPropertiesManager.setup();
        for (int i = 0; i < 10; i++) {
            ImplicitAllShardsSwitch initStatus = locator.getImplicitAllShardsSwitch();
            localDalPropertiesProvider.triggerImplicitAllShardsSwitchChanged();
            Thread.sleep(1 * 1000);
            ImplicitAllShardsSwitch switchStatus = locator.getImplicitAllShardsSwitch();
            Assert.assertNotEquals(initStatus, switchStatus);
        }
    }

}
