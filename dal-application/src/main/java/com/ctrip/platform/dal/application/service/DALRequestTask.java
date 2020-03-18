package com.ctrip.platform.dal.application.service;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.application.Application;
import com.ctrip.platform.dal.application.Config.DalApplicationConfig;
import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.ctrip.platform.dal.application.entity.DALServiceTable;
import com.ctrip.platform.dal.dao.DalHints;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
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

    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private static Logger log = LoggerFactory.getLogger(Application.class);
    private int qps = 100;
    private int delay = 40;
    private SQLThread mySQLThread;
    private SQLThread clusterThread;
    private SQLThread sqlServerThread;

    @Autowired
    private DalApplicationConfig dalApplicationConfig;
    @Autowired
    private DALServiceDao mySqlDao;
    @Autowired
    private DALServiceDao clusterDao;
    @Autowired
    private DALServiceDao sqlServerDao;

    @PostConstruct
    private void init() throws Exception {
        try {
            String qpsCfg = dalApplicationConfig.getQPS();
            if (qpsCfg != null)
                qps = Integer.parseInt(qpsCfg);
            delay = (1000 / qps) * 4;
        } catch (Exception e) {
            Cat.logError("get qps from QConfig error", e);
        }

        try {
            mySQLThread = new SQLThread(mySqlDao, delay);
            clusterThread = new SQLThread(clusterDao, delay);
            sqlServerThread = new SQLThread(sqlServerDao, delay);
            startTasks();
            Cat.logEvent("DalApplication", "ConfigChanged", Message.SUCCESS, String.format("executor start with qps %s", getQps()));
        } catch (Exception e) {
            log.error("DALRequestTask init error", e);
        }
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        executor.shutdownNow();
    }

    public void cancelTasks() {
        mySQLThread.exit = true;
        clusterThread.exit = true;
        sqlServerThread.exit = true;
    }

    private void startTasks() {
        executor.submit(mySQLThread);
        executor.submit(clusterThread);
        executor.submit(sqlServerThread);
    }

    public void restart() throws Exception {
        cancelTasks();
        init();
    }

    public int getQps() {
        return qps;
    }

    private static class SQLThread extends Thread {
        public volatile boolean exit = false;
        private final DALServiceDao dao;
        private final long delay;

        public SQLThread(DALServiceDao dao, long delay) {
            this.dao = dao;
            this.delay = delay;
        }

        @Override
        public void run() {
            while (!exit) {
                Transaction t = Cat.newTransaction("DAL.App.Task", dao.getDatabaseName());
                try {
                    DALServiceTable pojo = new DALServiceTable();
                    pojo.setName("insertName");
//                    dao.insert(new DalHints().setIdentityBack(), pojo);
//                    dao.insert(new DalHints(), pojo);
                    pojo.setID(1);
                    pojo = dao.queryByPk(pojo.getID(), null);
                    if (pojo != null) {
                        pojo.setName("updateName");
                        dao.update(null, pojo);
                        dao.delete(null, pojo);
                    }
                    t.setStatus(Transaction.SUCCESS);
                } catch (Exception e) {
                    log.error(dao.getDatabaseName() + " error", e);
                    t.setStatus(e);
                } finally {
                    t.complete();
                    try {
                        Thread.sleep(delay);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

}
