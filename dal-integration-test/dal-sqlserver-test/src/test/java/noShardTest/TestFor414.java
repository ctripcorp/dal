package noShardTest;

import com.ctrip.platform.dal.dao.DalClientFactory;
import org.junit.Test;

/**
 * Created by lilj on 2017/11/28.
 */
public class TestFor414 {
    @Test
    public void testBigReuqest() throws Exception {
        DalClientFactory.initClientFactory("target\\test-classes\\DalConfig\\Dal.config");
    }
}
