package com.ctrip.platform.dal.dao.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractConnectionListener implements ConnectionListener {
    protected String connectionDesc(Connection connection) {
        if (connection == null) {
            return "null";
        }

        String url = "";
        try {
            url = connection.getMetaData().getURL();
            url = simpleUrl(url);
        } catch (SQLException e) {
            // ignore
        }
        return url;
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

    @Override
    public void onCreateConnection(String poolDesc, Connection connection) {
        if (connection != null) {
            doOnCreateConnection(poolDesc, connection);
        }
    }

    protected abstract void doOnCreateConnection(String poolDesc, Connection connection);


    @Override
    public void onReleaseConnection(String poolDesc, Connection connection) {
        if (connection != null) {
            doOnReleaseConnection(poolDesc, connection);
        }
    }

    protected abstract void doOnReleaseConnection(String poolDesc, Connection connection);


    @Override
    public void onAbandonConnection(String poolDesc, Connection connection) {
        if (connection != null)
            doOnAbandonConnection(poolDesc, connection);
    }

    protected abstract void doOnAbandonConnection(String poolDesc, Connection connection);

}
