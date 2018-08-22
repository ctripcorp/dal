package com.ctrip.framework.idgen.server;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.ctrip.framework.cdubbo.spring.annotation.EnableCDubbo;
import com.ctrip.framework.idgen.server.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@DubboComponentScan
@EnableCDubbo
public class ServerInitializerTest extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

    public static void main(String[] args) {
        try {
            System.setProperty("java.awt.headless", "false");
            overrideArtemisUrl("10.2.35.218");
            ConfigManager.getInstance().initialize();
        } catch (Throwable t) {
            LOGGER.error("Server initialize failed");
            throw t;
            //System.exit(-1);
        }
        SpringApplication.run(ServiceInitializer.class);
    }

    private static void overrideArtemisUrl(String ip) {
        String url = String.format("http://%s:8080/artemis-service/", ip);
        System.setProperty("artemis.client.cdubbo.service.service.domain.url", url);
        System.setProperty("artemis.client.cdubbo.client.service.domain.url", url);
    }

}
