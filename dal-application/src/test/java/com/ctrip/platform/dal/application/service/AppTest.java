package com.ctrip.platform.dal.application.service;

import com.ctrip.platform.dal.application.AppConfig;
import com.ctrip.platform.dal.application.Config.DalApplicationConfig;
import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.ctrip.platform.dal.application.entity.DALServiceTable;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilj on 2018/5/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class AppTest {
    @Autowired
    DALService dalService;

    @Autowired
    private DALServiceDao mySqlDao;

    @Autowired
    private DALServiceDao sqlServerDao;

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
        DALServiceTable sqlServerPojo=dalService.insertSqlServer(testSqlServerPojo);
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

    @Test
    public void testDeleteMySql() throws Exception{
        DALServiceTable testPojo=new DALServiceTable();
        testPojo.setName("testInsert");
        dalService.insertMySql(testPojo);
        Assert.assertNotNull(testPojo);
        testPojo.setName("testUpdate");
        dalService.updateMySql(testPojo);
        Assert.assertEquals("testUpdate",dalService.queryMySql(testPojo).getName());
        dalService.deleteMySql(testPojo);
        Assert.assertNull(dalService.queryMySql(testPojo));
    }


    @Test
    public void testDeleteSqlServer() throws Exception{
        DALServiceTable testPojo=new DALServiceTable();
        testPojo.setName("testInsert");
        dalService.insertSqlServer(testPojo);
        Assert.assertNotNull(testPojo);
        testPojo.setName("testUpdate");
        dalService.updateSqlServer(testPojo);
        Assert.assertEquals("testUpdate",dalService.querySqlServer(testPojo).getName());
        dalService.deleteSqlServer(testPojo);
        Assert.assertNull(dalService.querySqlServer(testPojo));
    }

    @Test
    public void queryAtPageWithoutOrderBy() throws Exception{
        dalService.queryAtPageWithoutOrderBy();
    }

    @Test
    public void queryAtPageWithOrderBy() throws Exception{
        dalService.queryAtPageWithOrderBy();
    }

    @Test
    public void queryTopWithOrderby() throws Exception{
        dalService.queryTopWithOrderby();
    }

    @Test
    public void queryTopWithNoOrderby() throws Exception{
        dalService.queryTopWithNoOrderby();
    }

    @Test
    public void testMySql() throws Exception{
        DALServiceTable pojo = new DALServiceTable();
        pojo.setName("mysql");
        mySqlDao.insert(new DalHints().setIdentityBack(), pojo);
        pojo = mySqlDao.queryByPk(pojo.getID(), null);
        Assert.assertEquals("mysql",pojo.getName());
        pojo.setName("update");
        mySqlDao.update(null,pojo);
        pojo = mySqlDao.queryByPk(pojo.getID(), null);
        Assert.assertEquals("update",pojo.getName());
        mySqlDao.delete(null, pojo);
        pojo = mySqlDao.queryByPk(pojo.getID(), null);
        Assert.assertNull(pojo);
    }

    @Test
    public void testSqlServer() throws Exception{
        DALServiceTable pojo = new DALServiceTable();
        pojo.setName("sqlServer");
        sqlServerDao.insert(new DalHints().setIdentityBack(), pojo);
        pojo = sqlServerDao.queryByPk(pojo.getID(), null);
        Assert.assertEquals("sqlServer",pojo.getName());
        pojo.setName("update");
        sqlServerDao.update(null,pojo);
        pojo = sqlServerDao.queryByPk(pojo.getID(), null);
        Assert.assertEquals("update",pojo.getName());
        sqlServerDao.delete(null, pojo);
        pojo = sqlServerDao.queryByPk(pojo.getID(), null);
        Assert.assertNull(pojo);
    }

//    @Test
//    public void testQConfig() throws Exception{
//        DalApplicationConfig config=new DalApplicationConfig();
//        config.initMap();
//        System.out.println(config.getQPS());
//    }
}
