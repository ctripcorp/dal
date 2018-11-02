package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TVPTest {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
        ((CtripTaskFactory) DalClientFactory.getTaskFactory()).setCallSpt(true);
    }

    @Test
    public void testTVP() throws Exception {
        // column names without underline
        testTVPBatchInsert();
        testTVPBatchUpdate();
        testTVPBatchDelete();

        // column names with underline
        testTVPColumnsWithUnderlineBatchInsert();
        testTVPColumnsWithUnderlineBatchUpdate();
        testTVPColumnsWithUnderlineBatchDelete();
        // Thread.sleep(60 * 1000);
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

    private void testTVPColumnsWithUnderlineBatchInsert() throws SQLException {
        DalTableDao<PTicketNo> client = new DalTableDao<>(new DalDefaultJpaParser<>(PTicketNo.class));
        DalHints hints = DalHints.createIfAbsent(null);
        List<PTicketNo> daoPojos = new ArrayList<>();
        PTicketNo p1 = new PTicketNo();
        p1.setTicketNo("1");
        p1.setTicketType("1");
        p1.setFlightIntl("1");
        p1.setFlightAgency(1);
        p1.setSendSite(1);
        p1.setStatus("1");
        Short s1 = 1;
        p1.setPaidStatus(s1);
        p1.setBSPChecked("1");
        p1.setAirLineChecked("1");
        p1.setAirLine("1");

        daoPojos.add(p1);

        PTicketNo p2 = new PTicketNo();
        p2.setTicketNo("2");
        p2.setTicketType("2");
        p2.setFlightIntl("2");
        p2.setFlightAgency(2);
        p2.setSendSite(2);
        p2.setStatus("2");
        Short s2 = 2;
        p2.setPaidStatus(s2);
        p2.setBSPChecked("2");
        p2.setAirLineChecked("2");
        p2.setAirLine("2");
        daoPojos.add(p2);

        int[] result = client.batchInsert(hints, daoPojos);
        int length = 2;
        int[] expected = new int[length];
        for (int i = 0; i < length; i++) {
            expected[i] = 0;
        }
        Assert.assertArrayEquals(expected, result);
    }

    private void testTVPColumnsWithUnderlineBatchUpdate() throws SQLException {
        DalTableDao<PTicketNo> client = new DalTableDao<>(new DalDefaultJpaParser<>(PTicketNo.class));
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll();
        List<PTicketNo> list = client.query(builder, hints);
        if (list == null || list.size() == 0)
            Assert.assertTrue(false);

        for (PTicketNo p : list) {
            p.setPassenger("testUpdate");
        }

        int[] result = client.batchUpdate(hints, list);
        int length = list.size();
        int[] expected = new int[length];
        for (int i = 0; i < length; i++) {
            expected[i] = 0;
        }
        Assert.assertArrayEquals(expected, result);
    }

    private void testTVPColumnsWithUnderlineBatchDelete() throws SQLException {
        DalTableDao<PTicketNo> client = new DalTableDao<>(new DalDefaultJpaParser<>(PTicketNo.class));
        DalHints hints = DalHints.createIfAbsent(null);
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll();
        List<PTicketNo> list = client.query(builder, hints);
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
