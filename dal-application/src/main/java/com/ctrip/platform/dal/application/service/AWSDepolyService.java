package com.ctrip.platform.dal.application.service;

import com.ctrip.platform.dal.application.Application;
import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.ctrip.platform.dal.dao.DalHints;
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
public class AWSDepolyService {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static Logger log = LoggerFactory.getLogger(Application.class);

//    @Autowired
    private DALServiceDao dao = null;

    @PostConstruct
    private void init() throws Exception {
        try {
//            addTask();
        } catch (Exception e) {
            log.error("AwsDeployService init error", e);
        }
    }

    @PreDestroy
    public void cleanUp() throws Exception{
        executor.shutdownNow();
    }

    private void addTask() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        log.info(String.format("current hostname: %s", dao.selectHostname(new DalHints())));
                    }catch (Exception e){
                        log.error("select hostname error",e);
                    }finally {
                        try {
                            Thread.sleep(3000);
                        }catch (Exception e){

                        }
                    }
                }
            }
        }, 0, 3, TimeUnit.SECONDS);
    }
}
