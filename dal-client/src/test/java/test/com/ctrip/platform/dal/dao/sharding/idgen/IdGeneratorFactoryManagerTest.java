package test.com.ctrip.platform.dal.dao.sharding.idgen;

import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorFactory;
import com.ctrip.platform.dal.sharding.idgen.IdGeneratorFactoryManager;
import org.junit.Assert;
import org.junit.Test;

public class IdGeneratorFactoryManagerTest {

    @Test
    public void testGetNullFactory() {
        IdGeneratorFactoryManager manager = new IdGeneratorFactoryManager();
        IIdGeneratorFactory factory1 = manager.getOrCreateNullFactory();
        IIdGeneratorFactory factory2 = manager.getOrCreateNullFactory();
        Assert.assertTrue(factory1.equals(factory2));
    }

    @Test
    public void testGetDefaultFactory() {
        IdGeneratorFactoryManager manager = new IdGeneratorFactoryManager();
        IIdGeneratorFactory factory1 = manager.getOrCreateDefaultFactory();
        IIdGeneratorFactory factory2 = manager.getOrCreateDefaultFactory();
        IIdGeneratorFactory factory3 = manager.getOrCreateFactory(TestIdGeneratorFactory2.class.getName());
        Assert.assertTrue(factory1.equals(factory2));
        Assert.assertTrue(factory1.equals(factory3));
        Assert.assertTrue(factory1 instanceof TestIdGeneratorFactory2);

        manager = new IdGeneratorFactoryManager();
        IIdGeneratorFactory factory4 = manager.getOrCreateFactory(TestIdGeneratorFactory2.class.getName());
        IIdGeneratorFactory factory5 = manager.getOrCreateFactory(TestIdGeneratorFactory2.class.getName());
        IIdGeneratorFactory factory6 = manager.getOrCreateDefaultFactory();
        Assert.assertTrue(factory4.equals(factory5));
        Assert.assertTrue(factory4.equals(factory6));
        Assert.assertTrue(factory4 instanceof TestIdGeneratorFactory2);
    }

}
