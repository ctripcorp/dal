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
public class ServiceInitializer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        IdFactory.getInstance().initialize();
        SpringApplication.run(ServiceInitializer.class);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServiceInitializer.class);
    }

}
