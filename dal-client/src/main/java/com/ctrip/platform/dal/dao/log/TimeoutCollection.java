package com.ctrip.platform.dal.dao.log;

import com.ctrip.platform.dal.dao.status.DalStatusManager;

import java.util.HashMap;
import java.util.Map;

import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT;
import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.SERVER_WAIT_TIMEOUT;

public class TimeoutCollection {

    private static final long DEFAULT_MAX_WAIT_VALUE = 4000;
    private static final long DEFAULT_QUERY_TIMEOUT = 60000;

    // dal timeout param : ms
    private Long connectTimeout = null;
    private Long connectionPoolBorrowTimeout = DEFAULT_MAX_WAIT_VALUE;
    private Long requestTimeout = DEFAULT_QUERY_TIMEOUT;
    private Long socketTimeout = null;
    private Map<String, Long> customTimeouts;

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public TimeoutCollection setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public long getConnectionPoolBorrowTimeout() {
        return connectionPoolBorrowTimeout;

    }

    public TimeoutCollection setConnectionPoolBorrowTimeout(long connectionPoolBorrowTimeout) {
        this.connectionPoolBorrowTimeout = connectionPoolBorrowTimeout;
        return this;
    }

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public TimeoutCollection setRequestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public long getSocketTimeout() {
        return socketTimeout;
    }

    public TimeoutCollection setSocketTimeout(long socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public Map<String, Long> getCustomTimeouts() {
        return customTimeouts;
    }

    public TimeoutCollection setCustomTimeouts(Map<String, Long> customTimeouts) {
        this.customTimeouts = customTimeouts;
        return this;
    }

    public TimeoutCollection overrideBy(TimeoutCollection collection) {
        if (this.connectTimeout == null)
            this.connectTimeout = collection.getConnectTimeout();

        if (this.socketTimeout == null)
            this.socketTimeout = collection.getSocketTimeout();



        if (customTimeouts != null && !customTimeouts.isEmpty()){
            if (!customTimeouts.containsKey(REMOVEABANDONEDTIMEOUT))
                customTimeouts.put(REMOVEABANDONEDTIMEOUT, collection.getCustomTimeouts().get(REMOVEABANDONEDTIMEOUT));

            if (!customTimeouts.containsKey(SERVER_WAIT_TIMEOUT))
                customTimeouts.put(SERVER_WAIT_TIMEOUT, collection.getCustomTimeouts().get(SERVER_WAIT_TIMEOUT));
        } else
            this.customTimeouts = collection.getCustomTimeouts();

        return this;
    }

    public TimeoutCollection clone() {

        return new TimeoutCollection().setConnectionPoolBorrowTimeout(this.getConnectionPoolBorrowTimeout())
                .setConnectTimeout(this.getConnectTimeout())
                .setCustomTimeouts(new HashMap<>(this.getCustomTimeouts()))
                .setRequestTimeout(this.getRequestTimeout())
                .setSocketTimeout(this.getSocketTimeout());
    }
}
