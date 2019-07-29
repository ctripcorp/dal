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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DALRequestTask {
    private ScheduledExecutorService executor;
    private static Logger log = LoggerFactory.getLogger(Application.class);
    private int qps = 100;
    private int delay = 40;

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
            executor = Executors.newScheduledThreadPool(2);
            addTask();
            Cat.logEvent("DalApplication", "ConfigChanged", Message.SUCCESS,String.format("executor start with qps %s",getQps()));
        } catch (Exception e) {
            log.error("DALRequestTask init error", e);
        }
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        executor.shutdownNow();
        Cat.logEvent("DalApplication", "ConfigChanged", Message.SUCCESS,"executor clean up");
    }

    private void addTask() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        DALServiceTable pojo = new DALServiceTable();
                        pojo.setName("mysql");
                        mySqlDao.insert(new DalHints().setIdentityBack(), pojo);
                        pojo = mySqlDao.queryByPk(pojo.getID(), null);
                        pojo.setName("update");
                        mySqlDao.update(null, pojo);
                        mySqlDao.delete(null, pojo);
                    } catch (Exception e) {
                        log.error("mysql error", e);
                    }
                }
            }
        }, 0, delay, TimeUnit.MILLISECONDS);

        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        DALServiceTable pojo = new DALServiceTable();
                        pojo.setName("sqlServer");
                        sqlServerDao.insert(new DalHints().setIdentityBack(), pojo);
                        pojo = sqlServerDao.queryByPk(pojo.getID(), null);
                        pojo.setName("update");
                        sqlServerDao.update(null, pojo);
                        sqlServerDao.delete(null, pojo);
                    } catch (Exception e) {
                        log.error("sqlserver error", e);
                    }
                }
            }
        }, 0, delay, TimeUnit.MILLISECONDS);
    }

    public void restart() throws Exception{
        cleanUp();
        init();
    }

    public int getQps(){
        return qps;
    }
}
