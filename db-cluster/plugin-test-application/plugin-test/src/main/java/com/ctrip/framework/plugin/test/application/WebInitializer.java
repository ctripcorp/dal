package com.ctrip.framework.plugin.test.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;

// 默认只会Component Scan com.ctrip.framework.plugin.test.application以及其子package，如果需要Scan更多的package可以使用@SpringBootApplication(scanBasePackages = {"com.ctrip.framework.plugin.test.application", "other package"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ServletComponentScan
public class WebInitializer extends SpringBootServletInitializer {

    /**
     * Configure your application when it’s launched by the servlet container
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebInitializer.class);
    }
}
