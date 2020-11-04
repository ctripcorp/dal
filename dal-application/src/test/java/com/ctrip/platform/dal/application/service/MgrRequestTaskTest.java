package com.ctrip.platform.dal.application.service;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.application.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class MgrRequestTaskTest {
    private String clusterName = "mytest_dalcluster";

//    @Autowired
//    private MgrRequestTask task;
//
//    @Test
//    public void cancelTasks() {
//        task.cancelTasks();
//    }

    @Test
    public void test() throws Exception {
        DataSource dataSource = new DalDataSourceFactory().getOrCreateDataSource(clusterName);

    }

    private volatile ScheduledExecutorService fixed1sValidateService = Executors.newSingleThreadScheduledExecutor();

    @Test
    public void test1() {
        fixed1sValidateService.scheduleAtFixedRate(() -> System.out.println(new Date()), 1000, 1000, TimeUnit.MILLISECONDS);
    }
}