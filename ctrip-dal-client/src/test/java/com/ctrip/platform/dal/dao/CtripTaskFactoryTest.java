package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.BulkTask;
import com.ctrip.platform.dal.dao.task.SingleTask;
import org.junit.Test;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

import static org.junit.Assert.*;

public class CtripTaskFactoryTest {
    protected static final String CALL_SP_BY_NAME = "callSpbyName";
    protected static final String CALL_SP_BY_SQLSEVER = "callSpbySqlServerSyntax";
    protected static final String CALL_SPT = "callSpt";

    @Test
    public void testCreateTaskValidationFail() throws SQLException {
        CtripTaskFactory ctf = new CtripTaskFactory();

        Map<String, String> settings = new HashMap<>();
        settings.put(CALL_SP_BY_NAME, "false");
        settings.put(CALL_SP_BY_SQLSEVER, "false");
        settings.put(CALL_SPT, "false");

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

    @Test
    public void testCtripTaskSettings() throws SQLException {
//        settings exist
        CtripTaskFactory ctf = new CtripTaskFactory();
        ctf.setCallSpt(true);
        ctf.setCallSpByName(true);
        ctf.setCallSpbySqlServerSyntax(true);

        Map<String, String> settings = new HashMap<>();
        settings.put(CALL_SP_BY_NAME, "false");
        settings.put(CALL_SP_BY_SQLSEVER, "false");
        settings.put(CALL_SPT, "false");

        ctf.initialize(settings);

        assertFalse(Boolean.parseBoolean(ctf.getProperty("callSpbyName")));
        assertFalse(Boolean.parseBoolean(ctf.getProperty("callSpbySqlServerSyntax")));
        assertFalse(Boolean.parseBoolean(ctf.getProperty("callSpt")));

//       no settings
        CtripTaskFactory ctf2 = new CtripTaskFactory();
        ctf2.setCallSpt(true);
        ctf2.setCallSpByName(true);
        ctf2.setCallSpbySqlServerSyntax(true);

        Map<String, String> settings2 = new HashMap<>();

        ctf2.initialize(settings2);

        assertTrue(Boolean.parseBoolean(ctf2.getProperty(CALL_SP_BY_NAME)));
        assertTrue(Boolean.parseBoolean(ctf2.getProperty(CALL_SP_BY_SQLSEVER)));
        assertTrue(Boolean.parseBoolean(ctf2.getProperty(CALL_SPT)));
    }

    @Test
    public void testCreateSingleDeleteTask() {
        CtripTaskFactory ctf = new CtripTaskFactory();
        ctf.setCallSpt(true);
        ctf.setCallSpByName(true);
        ctf.setCallSpbySqlServerSyntax(true);

        Map<String, String> settings = new HashMap<>();
        settings.put(CALL_SP_BY_NAME, "false");
        settings.put(CALL_SP_BY_SQLSEVER, "true");
        settings.put(CALL_SPT, "false");

        ctf.initialize(settings);

        PeopleParser parser = new PeopleParser();
        SingleTask task = ctf.createSingleDeleteTask(parser);
        assertFalse(Boolean.parseBoolean(((SingleDeleteSpaTask) task).getTaskSetting("callSpbyName")));
        assertTrue(Boolean.parseBoolean(((SingleDeleteSpaTask) task).getTaskSetting("callSpbySqlServerSyntax")));
        assertFalse(Boolean.parseBoolean(((SingleDeleteSpaTask) task).getTaskSetting("callSpt")));
    }

    @Test
    public void testCreateSingleInsertTask() {
        CtripTaskFactory ctf = new CtripTaskFactory();
        ctf.setCallSpt(true);
        ctf.setCallSpByName(true);
        ctf.setCallSpbySqlServerSyntax(true);

        Map<String, String> settings = new HashMap<>();
        settings.put(CALL_SP_BY_NAME, "false");
        settings.put(CALL_SP_BY_SQLSEVER, "true");
        settings.put(CALL_SPT, "false");

        ctf.initialize(settings);

        PeopleParser parser = new PeopleParser();
        SingleTask task = ctf.createSingleInsertTask(parser);
        assertFalse(Boolean.parseBoolean(((SingleInsertSpaTask) task).getTaskSetting("callSpbyName")));
        assertTrue(Boolean.parseBoolean(((SingleInsertSpaTask) task).getTaskSetting("callSpbySqlServerSyntax")));
        assertFalse(Boolean.parseBoolean(((SingleInsertSpaTask) task).getTaskSetting("callSpt")));
    }

    @Test
    public void testCreateSingleUpdateTask() {
        CtripTaskFactory ctf = new CtripTaskFactory();
        ctf.setCallSpt(true);
        ctf.setCallSpByName(true);
        ctf.setCallSpbySqlServerSyntax(true);

        Map<String, String> settings = new HashMap<>();
        settings.put(CALL_SP_BY_NAME, "false");
        settings.put(CALL_SP_BY_SQLSEVER, "true");
        settings.put(CALL_SPT, "false");

        ctf.initialize(settings);

        PeopleParser parser = new PeopleParser();
        SingleTask task = ctf.createSingleUpdateTask(parser);
        assertFalse(Boolean.parseBoolean(((SingleUpdateSpaTask) task).getTaskSetting("callSpbyName")));
        assertTrue(Boolean.parseBoolean(((SingleUpdateSpaTask) task).getTaskSetting("callSpbySqlServerSyntax")));
        assertFalse(Boolean.parseBoolean(((SingleUpdateSpaTask) task).getTaskSetting("callSpt")));
    }

    @Test
    public void testCreateBatchInsertTask() {
        CtripTaskFactory ctf = new CtripTaskFactory();
        ctf.setCallSpt(true);
        ctf.setCallSpByName(true);
        ctf.setCallSpbySqlServerSyntax(true);

        Map<String, String> settings = new HashMap<>();
        settings.put(CALL_SP_BY_NAME, "false");
        settings.put(CALL_SP_BY_SQLSEVER, "true");
        settings.put(CALL_SPT, "false");

        ctf.initialize(settings);

        PeopleParser parser = new PeopleParser();
        BulkTask task = ctf.createBatchInsertTask(parser);
        assertFalse(Boolean.parseBoolean(((BatchSp3Task) task).getTaskSetting("callSpbyName")));
        assertTrue(Boolean.parseBoolean(((BatchSp3Task) task).getTaskSetting("callSpbySqlServerSyntax")));
        assertFalse(Boolean.parseBoolean(((BatchSp3Task) task).getTaskSetting("callSpt")));
    }

    @Test
    public void testCreateBatchDeleteTask() {
        CtripTaskFactory ctf = new CtripTaskFactory();
        ctf.setCallSpt(true);
        ctf.setCallSpByName(true);
        ctf.setCallSpbySqlServerSyntax(true);

        Map<String, String> settings = new HashMap<>();
        settings.put(CALL_SP_BY_NAME, "false");
        settings.put(CALL_SP_BY_SQLSEVER, "true");
        settings.put(CALL_SPT, "false");

        ctf.initialize(settings);

        PeopleParser parser = new PeopleParser();
        BulkTask task = ctf.createBatchDeleteTask(parser);
        assertFalse(Boolean.parseBoolean(((BatchSp3Task) task).getTaskSetting("callSpbyName")));
        assertTrue(Boolean.parseBoolean(((BatchSp3Task) task).getTaskSetting("callSpbySqlServerSyntax")));
        assertFalse(Boolean.parseBoolean(((BatchSp3Task) task).getTaskSetting("callSpt")));
    }

    @Test
    public void testCreateBatchUpdateTask() {
        CtripTaskFactory ctf = new CtripTaskFactory();
        ctf.setCallSpt(true);
        ctf.setCallSpByName(true);
        ctf.setCallSpbySqlServerSyntax(true);

        Map<String, String> settings = new HashMap<>();
        settings.put(CALL_SP_BY_NAME, "false");
        settings.put(CALL_SP_BY_SQLSEVER, "true");
        settings.put(CALL_SPT, "false");

        ctf.initialize(settings);

        PeopleParser parser = new PeopleParser();
        BulkTask task = ctf.createBatchUpdateTask(parser);
        assertFalse(Boolean.parseBoolean(((BatchSp3Task) task).getTaskSetting("callSpbyName")));
        assertTrue(Boolean.parseBoolean(((BatchSp3Task) task).getTaskSetting("callSpbySqlServerSyntax")));
        assertFalse(Boolean.parseBoolean(((BatchSp3Task) task).getTaskSetting("callSpt")));
    }

}
