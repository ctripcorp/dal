package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.datasource.DefaultDataSourceTerminateTask;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CtripDataSourceTerminateTask extends DefaultDataSourceTerminateTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CtripDataSourceTerminateTask.class);
    private static final String DAL = "DAL";
    private static final String DATASOURCE_CLOSE_DATASOURCE = "DataSource::closeDataSource";

    @Override
    public void run() {
        String transactionName = String.format("%s:%s", DATASOURCE_CLOSE_DATASOURCE, name);
        Transaction transaction = Cat.newTransaction(DAL, transactionName);
        try {
            transaction.addData(DATASOURCE_CLOSE_DATASOURCE);

            StopWatch watch = new StopWatch();
            LOGGER.info(String.format("**********Start closing datasource %s.**********", name));
            watch.start();
            boolean success = false;
            while (!success) {
                try {
                    success = closeDataSource(dataSource);
                    if (success)
                        break;

                    Thread.sleep(FIXED_DELAY);
                } catch (Throwable e) {
                }
            }

            watch.stop();
            LOGGER.info(
                    String.format("**********End closing datasource %s,cost:%s ms.**********", name, watch.getTime()));
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }
}
