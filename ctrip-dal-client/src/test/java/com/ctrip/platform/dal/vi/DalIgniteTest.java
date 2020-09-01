package com.ctrip.platform.dal.vi;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DaoInitializationTest;
import com.ctrip.platform.dal.sql.logging.TestDalLogger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class DalIgniteTest {

    @Test
    public void testWarmUp() throws Exception {
        DalClientFactory.shutdownFactory();
        String path = DaoInitializationTest.class.getClassLoader().getResource("Dal.config.warmuptest").getPath();
        DalClientFactory.initClientFactory(path);
        DalClientFactory.warmUpConnections();
        TestDalLogger logger = (TestDalLogger) DalClientFactory.getDalLogger();
        Assert.assertEquals(0, logger.getErrorCount());
        DalClientFactory.shutdownFactory();
    }

}
