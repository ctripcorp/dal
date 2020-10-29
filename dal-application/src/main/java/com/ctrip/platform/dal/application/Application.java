package com.ctrip.platform.dal.application;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Created by lilj on 2018/6/2.
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ServletComponentScan("com.ctrip.framework.fireman.web")
public class Application extends SpringBootServletInitializer{
    public static void main(String[] args) throws Exception{
        try {

            SpringApplication.run(Application.class,args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(Application.class);
    }
}
