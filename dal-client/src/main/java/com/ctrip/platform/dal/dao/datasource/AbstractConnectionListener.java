package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractConnectionListener implements ConnectionListener {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractConnectionListener.class);
    private String ON_CREATE_CONNECTION_FORMAT = "[onCreateConnection]{}, {}";
    private String ON_RELEASE_CONNECTION_FORMAT = "[onReleaseConnection]{}, {}";
    private String ON_ABANDON_CONNECTION_FORMAT = "[onAbandonConnection]{}, {}";
    private Map<String, String> connectionUrlCache = new ConcurrentHashMap<>();

    @Override
    public void onCreateConnection(String poolDesc, Connection connection) {
        if (connection != null) {
            putConnectionUrlToCache(connection);
            doOnCreateConnection(poolDesc, connection);
        }
    }

    protected void doOnCreateConnection(String poolDesc, Connection connection) {
        logInfo(ON_CREATE_CONNECTION_FORMAT, poolDesc, connection);
    }

    @Override
    public void onReleaseConnection(String poolDesc, Connection connection) {
        if (connection != null) {
            doOnReleaseConnection(poolDesc, connection);
            removeConnectionUrlFromCache(connection);
        }
    }

    protected void doOnReleaseConnection(String poolDesc, Connection connection) {
        logInfo(ON_RELEASE_CONNECTION_FORMAT, poolDesc, connection);
    }

    @Override
    public void onAbandonConnection(String poolDesc, Connection connection) {
        if (connection != null)
            doOnAbandonConnection(poolDesc, connection);
    }

    protected void doOnAbandonConnection(String poolDesc, Connection connection) {
        logInfo(ON_ABANDON_CONNECTION_FORMAT, poolDesc, connection);
    }

    private void logInfo(String format, String poolDesc, Connection connection) {
        String connDesc = connectionDesc(connection);
        LOGGER.info(format, poolDesc, connDesc);
    }

    protected String connectionDesc(Connection connection) {
        if (connection == null) {
            return "null";
        }

        return getConnectionUrlFromCache(connection);
        /*
         * try { url = connection.getMetaData().getURL(); url = simpleUrl(url); } catch (SQLException e) { url =
         * getConnectionUrlFromCache(connection); if (url == null) return "null"; }
         */
    }

    private String simpleUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        int index = url.indexOf("?");
        if (index > -1) {
            return url.substring(0, index);
        }

        return url;
    }

    private void putConnectionUrlToCache(Connection connection) {
        if (connection == null)
            return;

        try {
            String connectionId = connection.toString();
            String url = LoggerHelper.getSimplifiedDBUrl(connection.getMetaData().getURL());
            if (url == null || url.isEmpty())
                return;

            connectionUrlCache.put(connectionId, url);
            LOGGER.info(String.format("%s put to url cache.", connectionId));
        } catch (Throwable e) {
        }
    }

    private void removeConnectionUrlFromCache(Connection connection) {
        if (connection == null)
            return;

        try {
            String connectionId = connection.toString();
            connectionUrlCache.remove(connectionId);
            LOGGER.info(String.format("%s removed from url cache.", connectionId));
        } catch (Throwable e) {
        }
    }

    private String getConnectionUrlFromCache(Connection connection) {
        if (connection == null)
            return "null";

        String connectionId = connection.toString();
        return connectionUrlCache.get(connectionId);
    }

}