package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class RWClusterQueryDaoTest {

    private static final String CLUSTER_NAME = "RWDemoCluster";
    private static final String MASTER_DB_NAME = "cluster_rw_master";
    private static final String SLAVE1_DB_NAME = "cluster_rw_slave1";
    private static final String SLAVE2_DB_NAME = "cluster_rw_slave2";
    private static final String SQL_SELECT_DATABASE = "select database() as db_name";

    private DemoTableDao tableDao;
    private DalQueryDao queryDao;

    public RWClusterQueryDaoTest() throws Exception {
        tableDao = new DemoTableDao();
        queryDao = new DalQueryDao(CLUSTER_NAME);
    }

    @Before
    public void before() {}

    @After
    public void after() {}

    @Test
    public void testTableDao() throws SQLException {
        DemoTable pojo = buildPojo("insertName", null);
        tableDao.insert(new DalHints().setIdentityBack(), pojo);
        Assert.assertNotNull(pojo.getId());

        pojo.setName("updateName");
        int count = tableDao.update(new DalHints(), pojo);
        Assert.assertEquals(1, count);

        DemoTable result = tableDao.queryByPk(pojo, new DalHints());
        Assert.assertNull(result);

        result = tableDao.queryByPk(pojo, new DalHints().masterOnly());
        Assert.assertEquals("updateName", result.getName());
    }

    @Test
    public void testQueryDao() throws SQLException {
        String dbName = queryDao.queryForObject(SQL_SELECT_DATABASE, new StatementParameters(), new DalHints(), String.class);
        Assert.assertTrue(SLAVE1_DB_NAME.equalsIgnoreCase(dbName) || SLAVE2_DB_NAME.equalsIgnoreCase(dbName));

//        dbName = queryDao.queryForObject(SQL_SELECT_DATABASE, new StatementParameters(), new DalHints().inDatabase("slave"), String.class);
//        Assert.assertTrue(SLAVE1_DB_NAME.equalsIgnoreCase(dbName) || SLAVE2_DB_NAME.equalsIgnoreCase(dbName));
//
//        dbName = queryDao.queryForObject(SQL_SELECT_DATABASE, new StatementParameters(), new DalHints().inDatabase("slave1"), String.class);
//        Assert.assertTrue(SLAVE1_DB_NAME.equalsIgnoreCase(dbName));
//
//        dbName = queryDao.queryForObject(SQL_SELECT_DATABASE, new StatementParameters(), new DalHints().inDatabase("slave2"), String.class);
//        Assert.assertTrue(SLAVE2_DB_NAME.equalsIgnoreCase(dbName));

        dbName = queryDao.queryForObject(SQL_SELECT_DATABASE, new StatementParameters(), new DalHints().masterOnly(), String.class);
        Assert.assertTrue(MASTER_DB_NAME.equalsIgnoreCase(dbName));

        try {
            queryDao.queryForObject(SQL_SELECT_DATABASE, new StatementParameters(), new DalHints().inDatabase("none"), String.class);
            Assert.fail("route fail expected");
        } catch (Exception e) {
            // ignore
        }
    }

    private DemoTable buildPojo(String name, Integer age) {
        return buildPojo(null, name, age);
    }

    private DemoTable buildPojo(Long id, String name, Integer age) {
        DemoTable pojo = new DemoTable();
        pojo.setId(id);
        pojo.setName(name);
        pojo.setAge(age);
        return pojo;
    }

}
