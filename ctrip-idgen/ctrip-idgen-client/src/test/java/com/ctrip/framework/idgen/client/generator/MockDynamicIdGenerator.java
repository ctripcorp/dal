package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;

public class MockDynamicIdGenerator extends DynamicIdGenerator {

    private StaticIdGenerator mockStaticGenerator;

    public MockDynamicIdGenerator(String sequenceName, StaticIdGenerator mockStaticGenerator) {
        super(sequenceName);
        this.mockStaticGenerator = mockStaticGenerator;
    }

    public MockDynamicIdGenerator(String sequenceName, PrefetchStrategy strategy, StaticIdGenerator mockStaticGenerator) {
        super(sequenceName, strategy);
        this.mockStaticGenerator = mockStaticGenerator;
    }

    @Override
    protected StaticIdGenerator createStaticGenerator() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return mockStaticGenerator;
    }

    @Override
    protected Long fetchSingleId() {
        return -1L;
    }

}
