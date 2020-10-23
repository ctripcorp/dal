package com.ctrip.platform.dal.application.service;

import com.ctrip.platform.dal.application.AppConfig;
import com.ctrip.platform.dal.application.dao.OrderConfigDao;
import com.ctrip.platform.dal.application.entity.OrderConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class OrderConfigTest {
    @Autowired
    DALService dalService;
    @Test
    public void testOrderConfig() throws Exception {
        OrderConfig orderConfig=new OrderConfig();
        orderConfig.setConfigKey("test");
        OrderConfig result=  dalService.insertMySql(orderConfig);
        Assert.assertEquals(orderConfig.getConfigKey(),result.getConfigKey());
    }
}
