package test.com.ctrip.platform.dal.dao.sharding.idgen;

import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorConfig;
import com.ctrip.platform.dal.sharding.idgen.IdGeneratorConfig;
import com.ctrip.platform.dal.sharding.idgen.NullIdGeneratorFactory;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class IdGeneratorConfigTest {

    @Test
    public void testNonEntityDB() {
        IIdGeneratorConfig config = new IdGeneratorConfig("sdb", "edb", "pack", new NullIdGeneratorFactory(), null);
        config.warmUp();
/*        try {
            TimeUnit.SECONDS.sleep(120);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

}
