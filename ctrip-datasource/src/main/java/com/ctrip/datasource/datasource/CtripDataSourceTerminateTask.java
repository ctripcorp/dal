package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.datasource.DefaultDataSourceTerminateTask;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.CatHelper;
import com.dianping.cat.message.Transaction;

public class CtripDataSourceTerminateTask extends DefaultDataSourceTerminateTask {
    private static final String DAL = "DAL";
    private static final String DATASOURCE_CLOSE_DATASOURCE = "DataSource::closeDataSource";

    public CtripDataSourceTerminateTask(SingleDataSource oldDataSource) {
        super(oldDataSource);
    }

    @Override
    public void log(String dataSourceName, boolean isForceClosing, long startTimeMilliseconds) {
        String transactionName = String.format("%s:%s", DATASOURCE_CLOSE_DATASOURCE, dataSourceName);
        Transaction transaction = Cat.newTransaction(DAL, transactionName);
        if (isForceClosing) {
            String msg = String.format("DataSource %s has been forced closed.", dataSourceName);
            transaction.addData(msg);
            transaction.setStatus(new DalException(msg));
        }

        CatHelper.completeTransaction(transaction, startTimeMilliseconds);
    }

}
