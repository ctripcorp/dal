package com.ctrip.platform.dal.application.service;

import com.ctrip.platform.dal.application.AppConfig;
import com.ctrip.platform.dal.application.entity.DALServiceTable;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Test;

/**
 * Created by lilj on 2018/5/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class AppTest {
    @Autowired
    DALService dalService;

    @Test
    public void testQuery() throws Exception {
        DALServiceTable testPojo=new DALServiceTable();
        testPojo.setID(1);
        DALServiceTable mysqlPojo=dalService.queryMySql(testPojo);
        Assert.assertEquals(1,mysqlPojo.getID().intValue());

        DALServiceTable sqlServerPojo=dalService.querySqlServer(testPojo);
        Assert.assertEquals(1,sqlServerPojo.getID().intValue());
    }

    @Test
    public void testInsert() throws Exception {
        DALServiceTable testPojo=new DALServiceTable();
        testPojo.setName("testInsertMysql");
        DALServiceTable mySqlPojo=dalService.insertMySql(testPojo);
        Assert.assertEquals("testInsertMysql", mySqlPojo.getName());

        DALServiceTable testSqlServerPojo=new DALServiceTable();
        testSqlServerPojo.setName("testInsertSqlServer");
        DALServiceTable sqlServerPojo=dalService.insertSqlServer(testPojo);
        Assert.assertEquals("testInsertSqlServer", sqlServerPojo.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        DALServiceTable testPojo=new DALServiceTable();
        testPojo.setID(1);
        testPojo.setName("testUpdate");
        DALServiceTable retPojo=dalService.updateMySql(testPojo);
        Assert.assertEquals("testUpdate", retPojo.getName());
    }
}
