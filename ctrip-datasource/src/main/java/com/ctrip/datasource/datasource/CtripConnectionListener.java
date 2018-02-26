package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.datasource.DefaultConnectionListener;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.sql.Connection;

public class CtripConnectionListener extends DefaultConnectionListener implements ConnectionListener {
    private static final String DAL = "DAL";
    private static final String DAL_DATASOURCE_CREATE_CONNECTION = "DataSource::createConnection";
    private static final String DAL_DATASOURCE_RELEASE_CONNECTION = "DataSource::releaseConnection";
    private static final String DAL_DATASOURCE_ABANDON_CONNECTION = "DataSource::abandonConnection";

    @Override
    public void doOnCreateConnection(String poolDesc, Connection connection) {
        super.doOnCreateConnection(poolDesc, connection);
        String transactionName = String.format("%s:%s", DAL_DATASOURCE_CREATE_CONNECTION, connection);
        logCatTransaction(transactionName);
    }

    @Override
    public void doOnReleaseConnection(String poolDesc, Connection connection) {
        super.doOnReleaseConnection(poolDesc, connection);
        String transactionName = String.format("%s:%s", DAL_DATASOURCE_RELEASE_CONNECTION, connection);
        logCatTransaction(transactionName);
    }

    @Override
    protected void doOnAbandonConnection(String poolDesc, Connection connection) {
        super.doOnAbandonConnection(poolDesc, connection);
        String transactionName = String.format("%s:%s", DAL_DATASOURCE_ABANDON_CONNECTION, connection);
        logCatTransaction(transactionName);
    }

    private void logCatTransaction(String transactionName) {
        Transaction transaction = Cat.newTransaction(DAL, transactionName);
        try {
            transaction.addData(transactionName);
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
