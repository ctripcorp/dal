package test.com.ctrip.platform.dal.dao.sharding.idgen;

import com.ctrip.platform.dal.sharding.idgen.IdGeneratorFactoryManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class IdGeneratorFactoryManagerTest {

    @Test
    public void testGetFactoryClassNames() throws Exception {
        IdGeneratorFactoryManager manager = new IdGeneratorFactoryManager();
        List<String> names = manager.getFactoryClassNames();
        Assert.assertNotNull(manager.getOrCreateDefaultFactory());
    }

}
