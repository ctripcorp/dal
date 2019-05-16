package com.ctrip.platform.dal.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Created by lilj on 2018/6/2.
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Application extends SpringBootServletInitializer{
    private static Logger log = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) throws Exception{
        SpringApplication.run(Application.class,args);
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(Application.class);
    }
}
