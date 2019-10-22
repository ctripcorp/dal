package com.ctrip.platform.dal.application.service;

import com.ctrip.platform.dal.application.Application;
import com.ctrip.platform.dal.application.Config.DalApplicationConfig;
import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.ctrip.platform.dal.application.entity.DALServiceTable;
import com.ctrip.platform.dal.dao.DalHints;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DALRequestTask {
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private static Logger log = LoggerFactory.getLogger(Application.class);
    private int qps = 100;
    private int delay = 40;
    public MySQLThread mySQLThread;
    public SQLServerThread sqlServerThread;

    @Autowired
    private DalApplicationConfig dalApplicationConfig;

    @Autowired
    private DALServiceDao mySqlDao;

    @Autowired
    private DALServiceDao sqlServerDao;

    @PostConstruct
    private void init() throws Exception {
        try {
            qps = Integer.parseInt(dalApplicationConfig.getQPS());
            delay = (1 * 1000 / qps) * 4;
        } catch (Exception e) {
            Cat.logError("get qps from QConfig error", e);
        }

        try {
            mySQLThread = new MySQLThread(delay);
            sqlServerThread = new SQLServerThread(delay);
            addTask();
            Cat.logEvent("DalApplication", "ConfigChanged", Message.SUCCESS, String.format("executor start with qps %s", getQps()));
        } catch (Exception e) {
            log.error("DALRequestTask init error", e);
        }
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        executor.shutdownNow();
    }

    public void cancelThreadTask() {
        mySQLThread.exit = true;
        sqlServerThread.exit = true;
    }

    private void addTask() {
        executor.submit(mySQLThread);
        executor.submit(sqlServerThread);
    }

    public void restart() throws Exception {
        cancelThreadTask();
        init();
    }

    public int getQps() {
        return qps;
    }

    private class MySQLThread extends Thread {
        public volatile boolean exit = false;
        private int mysqlDelay;

        public MySQLThread(int delay) {
            this.mysqlDelay = delay;
        }

        @Override
        public void run() {
            while (!exit) {
                try {
                    DALServiceTable pojo = new DALServiceTable();
                    pojo.setName("mysql");
                    mySqlDao.insert(new DalHints().setIdentityBack(), pojo);
                    pojo = mySqlDao.queryByPk(pojo.getID(), null);
                    if (pojo != null) {
                        pojo.setName("update");
                        mySqlDao.update(null, pojo);
                        mySqlDao.delete(null, pojo);
                    }
                } catch (Exception e) {
                    log.error("mysql error", e);
                } finally {
                    try {
                        Thread.sleep(mysqlDelay);
                    } catch (Exception e) {

                    }
                }
            }
        }
    }

    private class SQLServerThread extends Thread {
        public volatile boolean exit = false;
        private int sqlDelay;

        public SQLServerThread(int delay) {
            this.sqlDelay = delay;
        }

        @Override
        public void run() {
            while (!exit) {
                try {
                    DALServiceTable pojo = new DALServiceTable();
                    pojo.setName("sqlServer");
                    sqlServerDao.insert(new DalHints().setIdentityBack(), pojo);
                    pojo = sqlServerDao.queryByPk(pojo.getID(), null);
                    if (pojo != null) {
                        pojo.setName("update");
                        sqlServerDao.update(null, pojo);
                        sqlServerDao.delete(null, pojo);
                    }
                } catch (Exception e) {
                    log.error("sqlserver error", e);
                } finally {
                    try {
                        Thread.sleep(sqlDelay);
                    } catch (Exception e) {

                    }
                }
            }
        }
    }
}
