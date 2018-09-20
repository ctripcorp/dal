package com.ctrip.framework.idgen.client.generator;

import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.List;

public class MockStaticIdGenerator extends StaticIdGenerator {

    private List<IdSegment> mockPool;

    public MockStaticIdGenerator(String sequenceName, List<IdSegment> mockPool) {
        super(sequenceName);
        this.mockPool = mockPool;
    }

    @Override
    protected List<IdSegment> fetchPool() {
        return mockPool;
    }

}
