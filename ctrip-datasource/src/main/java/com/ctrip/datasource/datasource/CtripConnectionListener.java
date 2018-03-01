package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.datasource.AbstractConnectionListener;
import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.sql.Connection;

public class CtripConnectionListener extends AbstractConnectionListener implements ConnectionListener {
    private static final String DAL = "DAL";
    private static final String DAL_DATASOURCE_CREATE_CONNECTION = "DataSource::createConnection";
    private static final String DAL_DATASOURCE_RELEASE_CONNECTION = "DataSource::releaseConnection";
    private static final String DAL_DATASOURCE_ABANDON_CONNECTION = "DataSource::abandonConnection";

    @Override
    public void doOnCreateConnection(String poolDesc, Connection connection) {
        super.doOnCreateConnection(poolDesc, connection);
        logCatTransaction(DAL_DATASOURCE_CREATE_CONNECTION, poolDesc, connection);
    }

    @Override
    public void doOnReleaseConnection(String poolDesc, Connection connection) {
        super.doOnReleaseConnection(poolDesc, connection);
        logCatTransaction(DAL_DATASOURCE_RELEASE_CONNECTION, poolDesc, connection);
    }

    @Override
    protected void doOnAbandonConnection(String poolDesc, Connection connection) {
        super.doOnAbandonConnection(poolDesc, connection);
        logCatTransaction(DAL_DATASOURCE_ABANDON_CONNECTION, poolDesc, connection);
    }

    private void logCatTransaction(String typeName, String poolDesc, Connection connection) {
        String connDesc = connectionDesc(connection);
        String transactionName = String.format("%s:%s", typeName, connDesc);
        Transaction transaction = Cat.newTransaction(DAL, transactionName);
        try {
            transaction.addData(String.format("%s,%s", poolDesc, connDesc));
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
