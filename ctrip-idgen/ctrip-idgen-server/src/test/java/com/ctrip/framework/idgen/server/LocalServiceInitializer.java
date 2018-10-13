package com.ctrip.framework.idgen.server;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.ctrip.framework.cdubbo.spring.annotation.EnableCDubbo;
import com.ctrip.framework.idgen.server.service.IdFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@DubboComponentScan
@EnableCDubbo
public class LocalServiceInitializer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        setEnvironment();
        IdFactory.getInstance().initialize();
        SpringApplication.run(LocalServiceInitializer.class);
    }

    private static void setEnvironment() {
        System.setProperty("java.awt.headless", "false");
        overrideArtemisUrl("10.2.35.218");
    }

    private static void overrideArtemisUrl(String ip) {
        String url = String.format("http://%s:8080/artemis-service/", ip);
        System.setProperty("artemis.client.cdubbo.service.service.domain.url", url);
        System.setProperty("artemis.client.cdubbo.client.service.domain.url", url);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LocalServiceInitializer.class);
    }

}
