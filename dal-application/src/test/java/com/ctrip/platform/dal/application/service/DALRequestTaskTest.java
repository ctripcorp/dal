package com.ctrip.platform.dal.application.service;

import com.ctrip.platform.dal.application.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class DALRequestTaskTest {
    @Autowired
    private DALRequestTask dalRequestTask;


    @Test
    public void testQPS(){
        System.out.println(dalRequestTask.getQps());
    }

}
