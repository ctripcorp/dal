package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TVPTest {

    @Test
    public void testTVP() throws Exception {
        testTVPBatchInsert();
        testTVPBatchUpdate();
        testTVPBatchDelete();
    }

    private void testTVPBatchInsert() throws SQLException {
        DalTableDao<Person> client = new DalTableDao<>(new DalDefaultJpaParser<>(Person.class));
        DalHints hints = DalHints.createIfAbsent(null);
        List<Person> daoPojos = new ArrayList<>();
        Person p1 = new Person();
        p1.setID(1);
        p1.setName("Insert1");
        p1.setAge(10);
        p1.setBirth(Timestamp.valueOf("2017-07-12 00:00:00"));
        p1.setTest("Test Insert1");
        daoPojos.add(p1);

        Person p2 = new Person();
        p2.setID(2);
        p2.setName("Insert2");
        p2.setAge(20);
        p2.setBirth(Timestamp.valueOf("2017-07-12 00:00:00"));
        p2.setTest("Test Insert2");
        daoPojos.add(p2);

        int[] result = client.batchInsert(hints, daoPojos);
        int length = 2;
        int[] expected = new int[length];
        for (int i = 0; i < length; i++) {
            expected[i] = 0;
        }
        Assert.assertArrayEquals(expected, result);
    }

    private void testTVPBatchUpdate() throws SQLException {
        DalTableDao<Person> client = new DalTableDao<>(new DalDefaultJpaParser<>(Person.class));
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll();
        List<Person> list = client.query(builder, hints);
        if (list == null || list.size() == 0)
            Assert.assertTrue(false);

        for (Person p : list) {
            p.setName("Update");
            p.setTest("Test Update");
        }

        int[] result = client.batchUpdate(hints, list);
        int length = list.size();
        int[] expected = new int[length];
        for (int i = 0; i < length; i++) {
            expected[i] = 0;
        }
        Assert.assertArrayEquals(expected, result);
    }

    private void testTVPBatchDelete() throws SQLException {
        DalTableDao<Person> client = new DalTableDao<>(new DalDefaultJpaParser<>(Person.class));
        DalHints hints = DalHints.createIfAbsent(null);
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll();
        List<Person> list = client.query(builder, hints);
        if (list == null || list.size() == 0)
            Assert.assertTrue(false);

        int[] result = client.batchDelete(hints, list);
        int length = list.size();
        int[] expected = new int[length];
        for (int i = 0; i < length; i++) {
            expected[i] = 0;
        }
        Assert.assertArrayEquals(expected, result);
    }

}
