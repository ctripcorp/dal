package com.ctrip.platform.dal.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class CtripTaskFactoryTest {

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        CtripTaskFactory.callSpbyName = false;
    }
    
    @Test
    public void testCreateTaskValidationFail() throws SQLException {
        // sorry for this ugly usage
        CtripTaskFactory ctf = new CtripTaskFactory();
        Map<String, String> settings = new HashMap<>();
        settings.put("callSpbyName", "false");
        ctf.initialize(settings);
        
        DalParser<PeopleOldVersion> parser = new DalDefaultJpaParser<>(PeopleOldVersion.class);
        try {
            ctf.createSingleInsertTask(parser);
            fail();
        } catch (Throwable e) {
            assertTrue(e.getMessage().startsWith("Cannot call SP by index because the primary key"));
        }
        
        try {
            ctf.createSingleDeleteTask(parser);
            fail();
        } catch (Throwable e) {
            assertTrue(e.getMessage().startsWith("Cannot call SP by index because the primary key"));
        }

        try {
            ctf.createSingleUpdateTask(parser);
            fail();
        } catch (Throwable e) {
            assertTrue(e.getMessage().startsWith("Cannot call SP by index because the primary key"));
        }

        try {
            ctf.createBatchInsertTask(parser);
            fail();
        } catch (Throwable e) {
            assertTrue(e.getMessage().startsWith("Cannot call SP by index because the primary key"));
        }
        
        try {
            ctf.createBatchDeleteTask(parser);
            fail();
        } catch (Throwable e) {
            assertTrue(e.getMessage().startsWith("Cannot call SP by index because the primary key"));
        }

        try {
            ctf.createBatchUpdateTask(parser);
            fail();
        } catch (Throwable e) {
            assertTrue(e.getMessage().startsWith("Cannot call SP by index because the primary key"));
        }
    }
}
