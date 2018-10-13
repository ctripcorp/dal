package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.SnowflakeConfig;
import com.ctrip.framework.idgen.server.config.TestConfigFactory;
import com.ctrip.framework.idgen.server.exception.TimeRunOutException;
import com.ctrip.framework.idgen.service.api.IdSegment;
import org.junit.Assert;
import org.junit.Test;

public class CASSnowflakeWorkerTest {

    private TestConfigFactory configFactory = new TestConfigFactory();
    private String sequenceName1 = "test1";

    @Test
    public void getSegmentTest() {
        int requestSize = 2000;
        CASSnowflakeWorker worker = new CASSnowflakeWorker(sequenceName1, configFactory.mockSnowflakeConfig());
        IdSegment segment = worker.getSegment(requestSize);
        long actualSize = segment.getEnd().longValue() - segment.getStart().longValue() + 1;
        Assert.assertEquals(requestSize, actualSize);
    }

    @Test
    public void timeRunOutTest() {
        try {
            CASSnowflakeWorkerMod1 worker = new CASSnowflakeWorkerMod1(sequenceName1, configFactory.mockSnowflakeConfig());
            worker.getSegment(1);
            Assert.fail();
        } catch (TimeRunOutException e) {
        }
    }

    @Test
    public void timeBackwardsTest() {
        CASSnowflakeWorker worker = new CASSnowflakeWorker(sequenceName1, configFactory.mockSnowflakeConfig());
        long lastId = worker.constructId(worker.getTimestamp() + 10,
                worker.config.getSequenceMask());
        worker.atomLastId.set(lastId);
        IdSegment segment = worker.getSegment(10);
        Assert.assertNull(segment);
        lastId = worker.constructId(worker.getTimestamp() + 10,
                worker.config.getSequenceMask() - 1);
        worker.atomLastId.set(lastId);
        segment = worker.getSegment(10);
        long actualSize = segment.getEnd().longValue() - segment.getStart().longValue() + 1;
        Assert.assertEquals(1, actualSize);
    }

    class CASSnowflakeWorkerMod1 extends CASSnowflakeWorker {
        public CASSnowflakeWorkerMod1(String sequenceName, SnowflakeConfig config) {
            super(sequenceName, config);
        }
        @Override
        protected long getTimestamp() {
            return config.getMaxTimestamp() + 1;
        }
    }

}
