package com.ctrip.framework.idgen.client;

import com.ctrip.platform.dal.sharding.idgen.IdGenerator;

public class IdGeneratorFactoryTest {

    public static void main(String[] args) {
        IdGenerator idGenerator = IdGeneratorFactory.getInstance().getOrCreateIdGenerator("testName1");
        for (int i = 0; i < 100; i++) {
            System.out.println("fetched id: " + idGenerator.nextId());
        }
    }

}
