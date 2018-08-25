package com.ctrip.framework.idgen.client;

import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import org.junit.Test;

public class IdGeneratorFactoryTest {

    public static void main(String[] args) {
        /*System.setProperty("java.awt.headless", "false");
        overrideArtemisUrl("10.2.35.218");*/
        generalTest();
    }

    @Test
    public static void generalTest() {
        String sequenceName = "testName1";
        IdGenerator generator = IdGeneratorFactory.getInstance().getOrCreateIdGenerator(sequenceName);
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

    private static void overrideArtemisUrl(String ip) {
        String url = String.format("http://%s:8080/artemis-service/", ip);
        System.setProperty("artemis.client.cdubbo.service.service.domain.url", url);
        System.setProperty("artemis.client.cdubbo.client.service.domain.url", url);
    }

}
