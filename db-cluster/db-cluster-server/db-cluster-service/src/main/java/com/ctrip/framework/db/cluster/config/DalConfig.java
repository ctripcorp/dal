package com.ctrip.framework.db.cluster.config;

import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.annotation.EnableDalTransaction;
import com.ctrip.platform.dal.dao.helper.DalClientFactoryListener;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDalTransaction
public class DalConfig {

    @Bean
    public DalClient dalClient() {
        return DalClientFactory.getClient(Constants.DATABASE_SET_NAME);
    }

    @Bean
    public ServletListenerRegistrationBean dalListener() {
        return new ServletListenerRegistrationBean<>(new DalClientFactoryListener());
    }
}
