package com.ctrip.framework.db.cluster;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;

// 默认只会Component Scan com.ctrip.framework.db.cluster以及其子package，如果需要Scan更多的package可以使用@SpringBootApplication(scanBasePackages = {"com.ctrip.framework.db.cluster", "other package"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ServletComponentScan
public class ServiceInitializer extends SpringBootServletInitializer {

    /**
     * Configure your application when it’s launched by the servlet container
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServiceInitializer.class);
    }
}
