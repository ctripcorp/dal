package com.ctrip.framework.plugin.test.application.test.dal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by shenjie on 2019/7/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestDaoTest {

    @Autowired
    private TestDao testDao;

    @Test
    public void queryDatabase() throws Exception {
        String database = testDao.queryDatabase();
        System.out.println(database);
    }

}