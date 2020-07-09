package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * @author c7ch23en
 */
public class DalExtendedPoolProperties extends PoolProperties implements DalExtendedPoolConfiguration {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private static final int DEFAULT_SESSION_WAIT_TIMEOUT = DataSourceConfigureConstants.DEFAULT_SESSION_WAIT_TIMEOUT;

    // seconds
    private volatile int sessionWaitTimeout = DEFAULT_SESSION_WAIT_TIMEOUT;

    private DataSourceIdentity dataSourceId;

    @Override
    public int getSessionWaitTimeout() {
        return getAppliedSessionWaitTimeout();
    }

    public void setSessionWaitTimeout(int sessionWaitTimeout) {
        this.sessionWaitTimeout = sessionWaitTimeout;
    }

    private int getAppliedSessionWaitTimeout() {
        LOGGER.logEvent(DalLogTypes.DAL_VALIDATION,
                "SessionWaitTimeout=" + sessionWaitTimeout, "");
        if (sessionWaitTimeout == 0) {
            LOGGER.info("sessionWaitTimeout = 0");
            return 0;
        }
        int appliedSessionWaitTimeout = sessionWaitTimeout;
        if (sessionWaitTimeout < 0) {
            LOGGER.info(String.format("sessionWaitTimeout < 0, set to %d by default", DEFAULT_SESSION_WAIT_TIMEOUT));
            appliedSessionWaitTimeout = DEFAULT_SESSION_WAIT_TIMEOUT;
        }
        if (!isTestOnBorrow()) {
            LOGGER.info("testOnBorrow=false");
            if (getMinIdle() > 0) {
                LOGGER.info(String.format("minIdle=%d, sessionWaitTimeout set to 0", getMinIdle()));
                return 0;
            }
        }
        LOGGER.logEvent(DalLogTypes.DAL_VALIDATION,
                "AppliedSessionWaitTimeout=" + appliedSessionWaitTimeout, "");
        return appliedSessionWaitTimeout;
    }

    @Override
    public DataSourceIdentity getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(DataSourceIdentity dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

}
