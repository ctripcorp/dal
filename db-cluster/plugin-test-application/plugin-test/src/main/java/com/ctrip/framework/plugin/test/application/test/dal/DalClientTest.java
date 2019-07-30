package com.ctrip.framework.plugin.test.application.test.dal;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by shenjie on 2019/7/30.
 */
@Slf4j
@Component
public class DalClientTest {

    @Autowired
    private TestDao testDao;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() throws Exception {
        try {
            addTask();
        } catch (Exception e) {
            Cat.logError("DalClientTest init failed", e);
            log.error("DalClientTest init failed", e);
            throw new Exception("DalClientTest init failed", e);
        }
    }

    private void addTask() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Transaction transaction = Cat.newTransaction("DalClientTest.QueryDatabase", "queryDatabase");
                log.info("DalClientTest query database begin...");
                try {
                    String database = testDao.queryDatabase();
                    Cat.logEvent("DalClientTest.Database", database);
                    log.info("Database is {}", database);
                    transaction.setStatus(Transaction.SUCCESS);
                } catch (Exception e) {
                    Cat.logError("Query database failed.", e);
                    log.error("Query database failed.", e);
                    transaction.setStatus(e);
                } finally {
                    transaction.complete();
                    log.info("DalClientTest query database end...");
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

}
