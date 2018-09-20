package com.ctrip.framework.idgen.client.common;

import com.ctrip.framework.idgen.client.generator.DynamicIdGenerator;
import com.ctrip.framework.idgen.client.generator.MockDynamicIdGenerator;
import com.ctrip.framework.idgen.client.generator.MockStaticIdGenerator;
import com.ctrip.framework.idgen.client.generator.StaticIdGenerator;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static List<Long> mockIds(int size, long offset) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ids.add(i + offset);
        }
        return ids;
    }

    public static IdSegment mockSegment(List<Long> ids) {
        return new IdSegment(ids.get(0), ids.get(ids.size() - 1));
    }

    public static StaticIdGenerator mockStaticGenerator(String sequenceName, List<IdSegment> pool) {
        return new MockStaticIdGenerator(sequenceName, pool);
    }

    public static DynamicIdGenerator mockDynamicGenerator(String sequenceName, PrefetchStrategy strategy,
                                                          StaticIdGenerator staticGenerator) {
        return new MockDynamicIdGenerator(sequenceName, strategy, staticGenerator);
    }

}
