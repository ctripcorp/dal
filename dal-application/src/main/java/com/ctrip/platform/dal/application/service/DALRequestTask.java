package com.ctrip.platform.dal.application.service;

import com.ctrip.platform.dal.application.Application;
import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.ctrip.platform.dal.application.entity.DALServiceTable;
import com.ctrip.platform.dal.dao.DalHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DALRequestTask {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private static Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private DALServiceDao mySqlDao;

    @Autowired
    private DALServiceDao sqlServerDao;

    @PostConstruct
    private void init() throws Exception {
        try {
            addTask();
        } catch (Exception e) {
            log.error("AwsDeployService init error", e);
        }
    }

    private void addTask() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        DALServiceTable pojo = new DALServiceTable();
                        pojo.setName("mysql");
                        mySqlDao.insert(new DalHints().setIdentityBack(), pojo);
                        pojo = mySqlDao.queryByPk(pojo.getID(), null);
                        pojo.setName("update");
                        mySqlDao.update(null,pojo);
                        mySqlDao.delete(null, pojo);
                    }catch (Exception e){
                        log.error("mysql error",e);
                    }
                }
            }
        }, 0, 1000, TimeUnit.MICROSECONDS);

        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        DALServiceTable pojo = new DALServiceTable();
                        pojo.setName("sqlServer");
                        sqlServerDao.insert(new DalHints().setIdentityBack(), pojo);
                        pojo = sqlServerDao.queryByPk(pojo.getID(), null);
                        pojo.setName("update");
                        sqlServerDao.update(null,pojo);
                        sqlServerDao.delete(null, pojo);
                    }catch (Exception e){
                        log.error("sqlserver error",e);
                    }
                }
            }
        }, 0, 1000, TimeUnit.MICROSECONDS);
    }
}
