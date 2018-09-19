package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.IdGeneratorFactory;
import com.ctrip.framework.idgen.client.strategy.DefaultStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class DynamicIdGeneratorTest {

    @Test
    public void testActiveFetchId() {
        String sequenceName = "testName1";
//        DynamicIdGenerator generator = new DynamicIdGenerator(sequenceName, new NoPrefetchStrategy());
        DynamicIdGenerator generator = (DynamicIdGenerator) IdGeneratorFactory.getInstance().getOrCreateLongIdGenerator(sequenceName);
        generator.initialize();
        for (int i = 1; i <= 10006; i++) {
            Long id = generator.nextId();
            Assert.assertNotNull(id);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            if ((i >= 1996 && i <= 2006) || (i >= 3996 && i <= 4006)) System.out.println("i = " + i + ",id = " + id);
        }
        keepRunning();
    }

    private void keepRunning() {
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    class NoPrefetchStrategy extends DefaultStrategy {
        private boolean flag = false;
        @Override
        public boolean checkIfNeedPrefetch() {
            if (!flag) {
                flag = true;
                return true;
            }
            return false;
        }
    }

}
