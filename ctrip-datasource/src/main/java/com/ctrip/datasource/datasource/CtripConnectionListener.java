package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.datasource.AbstractConnectionListener;
import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.datasource.CreateConnectionCallback;
import com.ctrip.platform.dal.dao.log.Callback;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.sql.Connection;

public class CtripConnectionListener extends AbstractConnectionListener implements ConnectionListener {
    private static final String DAL = "DAL";
    private static final String CONNECTION_CREATE_CONNECTION = "Connection::createConnection";
    private static final String CONNECTION_RELEASE_CONNECTION = "Connection::releaseConnection";
    private static final String CONNECTION_ABANDON_CONNECTION = "Connection::abandonConnection";

    @Override
    public void doOnCreateConnection(String poolDesc, CreateConnectionCallback callback) {
        final String tempPoolDesc = poolDesc;
        final CreateConnectionCallback tempCallback = callback;

        logCatTransaction(CONNECTION_CREATE_CONNECTION, poolDesc, new Callback() {
            @Override
            public void execute() throws Exception {
                CtripConnectionListener.super.doOnCreateConnection(tempPoolDesc, tempCallback);
            }
        });
    }

    @Override
    public void doOnReleaseConnection(String poolDesc, Connection connection) {
        final String tempPoolDesc = poolDesc;
        final Connection tempConnection = connection;

        logCatTransaction(CONNECTION_RELEASE_CONNECTION, poolDesc, connection, new Callback() {
            @Override
            public void execute() throws Exception {
                CtripConnectionListener.super.doOnReleaseConnection(tempPoolDesc, tempConnection);
            }
        });
    }

    @Override
    protected void doOnAbandonConnection(String poolDesc, Connection connection) {
        final String tempPoolDesc = poolDesc;
        final Connection tempConnection = connection;

        logCatTransaction(CONNECTION_ABANDON_CONNECTION, poolDesc, connection, new Callback() {
            @Override
            public void execute() throws Exception {
                CtripConnectionListener.super.doOnAbandonConnection(tempPoolDesc, tempConnection);
            }
        });
    }

    private void logCatTransaction(String typeName, String poolDesc, Callback callback) {
        String transactionName = String.format("%s:%s", typeName, poolDesc);
        Transaction transaction = Cat.newTransaction(DAL, transactionName);
        try {
            transaction.addData(poolDesc);
            if (callback != null) {
                callback.execute();
            }

            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    private void logCatTransaction(String typeName, String poolDesc, Connection connection, Callback callback) {
        String connDesc = connectionDesc(connection);
        String transactionName = String.format("%s:%s", typeName, connDesc);
        Transaction transaction = Cat.newTransaction(DAL, transactionName);
        try {
            transaction.addData(String.format("%s,%s", poolDesc, connDesc));
            if (callback != null) {
                callback.execute();
            }

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
