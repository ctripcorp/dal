package com.ctrip.framework.idgen.demo;

import com.ctrip.framework.idgen.client.IdGeneratorFactory;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;

public class IdGenDemo {

    public static void main(String[] args) {
        String sequenceName = "testName1";
        IdGenerator generator = IdGeneratorFactory.getInstance().getOrCreateLongIdGenerator(sequenceName);
        Number id = null;
        for (int i = 0; i < 1200; i++) {
            if (i >= 998 && i <= 1002) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            id = generator.nextId();
            if ((i >= 0 && i <= 2) || (i >= 598 && i <= 602) || (i >= 998 && i <= 1002) || (i >= 1190 && i <= 1200)) {
                System.out.println("Id [" + i + "]: " + id);
            }
        }
    }

}
