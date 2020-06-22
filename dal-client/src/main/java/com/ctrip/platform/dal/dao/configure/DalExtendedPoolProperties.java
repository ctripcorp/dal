package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * @author c7ch23en
 */
public class DalExtendedPoolProperties extends PoolProperties implements DalExtendedPoolConfiguration {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private static final int DEFAULT_SERVER_WAIT_TIMEOUT = DataSourceConfigureConstants.DEFAULT_SERVER_WAIT_TIMEOUT;

    // seconds
    private volatile int serverWaitTimeout = DEFAULT_SERVER_WAIT_TIMEOUT;

    @Override
    public int getServerWaitTimeout() {
        return getAdjustedServerWaitTimeout();
    }

    public void setServerWaitTimeout(int serverWaitTimeout) {
        this.serverWaitTimeout = serverWaitTimeout;
    }

    private int getAdjustedServerWaitTimeout() {
        if (serverWaitTimeout == 0) {
            LOGGER.info("serverWaitTimeout = 0");
            return 0;
        }
        int adjustedServerWaitTimeout = serverWaitTimeout;
        if (serverWaitTimeout < 0) {
            LOGGER.info(String.format("serverWaitTimeout < 0, set to %d by default", DEFAULT_SERVER_WAIT_TIMEOUT));
            adjustedServerWaitTimeout = DEFAULT_SERVER_WAIT_TIMEOUT;
        }
        if (!isTestOnBorrow()) {
            LOGGER.info("testOnBorrow=false");
            if (getMinIdle() > 0) {
                LOGGER.info(String.format("minIdle=%d, serverWaitTimeout set to 0", getMinIdle()));
                return 0;
            }
            /*if (getTimeBetweenEvictionRunsMillis() <= 0 || getMinEvictableIdleTimeMillis() <= 0) {
                LOGGER.info("idle cleaner disabled, serverWaitTimeout set to 0");
                return 0;
            }
            int maxIdleSeconds = ceil(getTimeBetweenEvictionRunsMillis() +
                    getMinEvictableIdleTimeMillis(), 1000);
            if (maxIdleSeconds > adjustedServerWaitTimeout) {
                LOGGER.info(String.format("serverWaitTimeout set to possible maxIdleSeconds: %d", maxIdleSeconds));
                adjustedServerWaitTimeout = maxIdleSeconds;
            }*/
        }
        return adjustedServerWaitTimeout;
    }

    private int ceil(int dividend, int divisor) {
        return (dividend / divisor) + (dividend % divisor > 0 ? 1 : 0);
    }

}
