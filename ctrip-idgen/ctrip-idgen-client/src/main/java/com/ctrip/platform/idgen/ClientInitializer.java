package com.ctrip.platform.idgen;

import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import com.ctrip.platform.idgen.client.SnowflakeIdGenerator;

public class ClientInitializer {

    public static void main(String[] args) {
        IdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator();
        SnowflakeIdGenerator snowflakeIdGenerator2 = new SnowflakeIdGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator.nextId());
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator.nextId());
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator.nextId());
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test2"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test2"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test2"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test1"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test2"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test2"));
            System.out.printf("round " + i + " - get: %s%n", snowflakeIdGenerator2.nextId("test2"));
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
