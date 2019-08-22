package com.ctrip.datasource.spring.config;

import com.ctrip.datasource.mybatis.DalMybatisConfig;
import com.ctrip.datasource.mybatis.interceptor.DalMybatisInterceptorFactory;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This registrar only works when annotation config is activated, i.e. Autowired should be supported.
 * For old Spring projects which use XML Config, you need to have either
 * context:annotation-config or context:component-scan configured.
 * For newer Spring projects which use Java Config, it should work by nature.
 */
public class DalMybatisInterceptorRegistrar implements InitializingBean {

    private boolean tracing;
    private boolean encryptParameters;
    private String encryptionKey;
    private static AtomicBoolean INTERCEPTOR_ADDED = new AtomicBoolean(false);

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @Override
    public void afterPropertiesSet() throws Exception {
        initialize();
    }

    private void initialize() throws Exception {
        if (!INTERCEPTOR_ADDED.compareAndSet(false, true)) {
            return;
        }
        if (!tracing || sqlSessionFactoryList == null || sqlSessionFactoryList.isEmpty()) {
            return;
        }

        DalMybatisConfig mybatisConfig = new DalMybatisConfig();
        mybatisConfig.setNeedEncryptParam(encryptParameters);
        mybatisConfig.setEncryptKey(encryptionKey);

        Set<Interceptor> interceptors = DalMybatisInterceptorFactory.dalMybatisInterceptors(mybatisConfig);

        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            for (Interceptor interceptor : interceptors) {
                configuration.addInterceptor(interceptor);
            }
        }
    }

    public void setEncryptParameters(boolean encryptParameters) {
        this.encryptParameters = encryptParameters;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public void setTracing(boolean tracing) {
        this.tracing = tracing;
    }

}
