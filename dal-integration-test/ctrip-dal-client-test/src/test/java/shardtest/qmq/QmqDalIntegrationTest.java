package shardtest.qmq;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class QmqDalIntegrationTest {
    private static final String keyname = "SimpleShardByDBTableOnMysql";

    @Before
    public void setUp() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testQmqDalIntegerationInShard() throws Exception {
        try {
            DalClient client = DalClientFactory.getClient(keyname);
            DalHints hints = new DalHints();
            hints.inShard("1");
            client.execute(new QmqDalCommand(), hints);
            Assert.assertTrue(true);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

}
