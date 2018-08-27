package com.ctrip.framework.idgen.client.generator;

public class StaticIdGeneratorTest {

    public void testNextIdConcurrent() {
        String sequenceName = "testName1";
        StaticIdGenerator generator = new StaticIdGenerator(sequenceName);

    }

}
