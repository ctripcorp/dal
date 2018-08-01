package com.ctrip.platform.dal.dao.tableDao.SingleInsertSpaTask;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Random;

public class InsertWithSetIdentityBack {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testInsertWithSetIdentityBackWhenNonAutoIncrement() {
        try {
            Random r = new Random();
            OFlightExt ext = new OFlightExt();
            ext.setOrderID(r.nextLong());
            Short sequence = 1;
            ext.setSequence(sequence);
            ext.setSubPrice("Test");
            ext.setRealSubclass("Test");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ext.setDatachangeLasttime(timestamp);

            DalHints hints = new DalHints();
            hints.setIdentityBack();

            OFlightExtDao dao = new OFlightExtDao();
            dao.insert(hints, ext);
            Assert.assertTrue(true);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }

    }
}
