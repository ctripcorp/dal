package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.datasource.DefaultConnectionListener;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;

import java.sql.Connection;

public class CtripConnectionListener extends DefaultConnectionListener implements ConnectionListener {
    private static final String DAL = "DAL";
    private static final String DAL_DATASOURCE_CREATE_CONNECTION = "DataSource::createConnection";
    private static final String DAL_DATASOURCE_RELEASE_CONNECTION = "DataSource::releaseConnection";
    private static final String DAL_DATASOURCE_ABANDON_CONNECTION = "DataSource::abandonConnection";

    @Override
    public void doOnCreateConnection(String poolDesc, Connection connection) {
        super.doOnCreateConnection(poolDesc, connection);
        Cat.logEvent(DAL, DAL_DATASOURCE_CREATE_CONNECTION, Message.SUCCESS,
                String.format("[onCreateConnection]%s, %s", poolDesc, connection));
    }

    @Override
    public void doOnReleaseConnection(String poolDesc, Connection connection) {
        super.doOnReleaseConnection(poolDesc, connection);
        Cat.logEvent(DAL, DAL_DATASOURCE_RELEASE_CONNECTION, Message.SUCCESS,
                String.format("[onReleaseConnection]%s, %s", poolDesc, connection));
    }

    @Override
    protected void doOnAbandonConnection(String poolDesc, Connection connection) {
        super.doOnAbandonConnection(poolDesc, connection);
        Cat.logEvent(DAL, DAL_DATASOURCE_ABANDON_CONNECTION, Message.SUCCESS,
                String.format("[onAbandonConnection]%s, %s", poolDesc, connection));
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
