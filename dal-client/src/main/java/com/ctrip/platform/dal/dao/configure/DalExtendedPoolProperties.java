package com.ctrip.platform.dal.dao.configure;

import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * @author c7ch23en
 */
public class DalExtendedPoolProperties extends PoolProperties implements DalExtendedPoolConfiguration {

    private static final int DEFAULT_SERVER_WAIT_TIMEOUT = 120;

    // seconds
    private volatile int serverWaitTimeout = DEFAULT_SERVER_WAIT_TIMEOUT;

    @Override
    public int getServerWaitTimeout() {
        return serverWaitTimeout >= 0 ? serverWaitTimeout : DEFAULT_SERVER_WAIT_TIMEOUT;
    }

    public void setServerWaitTimeout(int serverWaitTimeout) {
        this.serverWaitTimeout = serverWaitTimeout;
    }

}
