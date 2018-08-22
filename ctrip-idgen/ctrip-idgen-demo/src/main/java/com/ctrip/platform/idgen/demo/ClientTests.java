package com.ctrip.platform.idgen.demo;

import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import com.ctrip.platform.idgen.client.SnowflakeIdGenerator;

public class ClientTests {

    public static void main(String[] args) {
        IdGenerator generator = new SnowflakeIdGenerator();
        Number id = generator.nextId();
        System.out.println("get id: " + id);
    }

}
