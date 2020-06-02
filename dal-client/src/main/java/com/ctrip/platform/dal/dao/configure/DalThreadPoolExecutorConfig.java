package com.ctrip.platform.dal.dao.configure;

/**
 * @author c7ch23en
 */
public interface DalThreadPoolExecutorConfig {

    int getCorePoolSize();

    int getMaxPoolSize();

    long getKeepAliveSeconds();

    int getMaxThreadsPerShard(String logicDbName);

}
