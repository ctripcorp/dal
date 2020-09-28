package com.ctrip.platform.dal.dao.datasource.log;

import com.ctrip.platform.dal.dao.datasource.ValidationResult;
import org.junit.Assert;
import pkg.caller.test.TestCaller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author c7ch23en
 */
public class BaseSqlContextTest {

    protected static final String CLIENT_VERSION = "test_version";
    protected static final String CLIENT_ZONE = "c_zone";
    protected static final String DB_NAME = "test_db";
    protected static final String DB_ZONE = "s_zone";
    protected static final String CALLER_CLASS = "dao";
    protected static final String CALLER_METHOD = "method";
    protected static final OperationType OP_TYPE = OperationType.UPDATE;
    protected static final String TABLES = "tbl1,tlb2";
    protected static final String UCS_VALIDATION = "ucs_result";
    protected static final String DAL_VALIDATION = "dal_result";
    protected static final long INTERNAL_DELAY = 5;

    protected void buildContextSuccess(BaseSqlContext context) throws Exception {
        buildContext(context);
        context.endExecution(null);
    }

    protected void buildContextFail(BaseSqlContext context, Throwable t) throws Exception {
        buildContext(context);
        context.endExecution(t);
    }

    protected void buildContext(BaseSqlContext context) throws Exception {
        context.populateDbZone(DB_ZONE);
        context.populateCaller(CALLER_CLASS, CALLER_METHOD);
        new TestCaller().call(context::populateCaller);
        context.populateOperationType(OP_TYPE);
        context.populateTables(new HashSet<>(Arrays.asList(TABLES.split(","))));
        context.startExecution();
        TimeUnit.MILLISECONDS.sleep(INTERNAL_DELAY);
        context.populateValidationResult(new ValidationResult(true, UCS_VALIDATION, DAL_VALIDATION));
    }

    protected void assertContextSuccess(BaseSqlContext context) {
        assertContext(context);
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(BaseSqlContext.STATUS_SUCCESS, tags.get(BaseSqlContext.STATUS));
    }

    protected void assertContextFail(BaseSqlContext context) {
        assertContext(context);
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(BaseSqlContext.STATUS_FAIL, tags.get(BaseSqlContext.STATUS));
    }

    protected void assertContext(BaseSqlContext context) {
        Assert.assertTrue(context.getExecutionTime() >= INTERNAL_DELAY);
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(CLIENT_VERSION, tags.get(BaseSqlContext.CLIENT));
        Assert.assertEquals(CLIENT_ZONE, tags.get(BaseSqlContext.CLIENT_ZONE));
        Assert.assertEquals(DB_NAME, tags.get(BaseSqlContext.DB_NAME));
        Assert.assertEquals(DB_ZONE, tags.get(BaseSqlContext.DB_ZONE));
        Assert.assertEquals("pkg.caller.test.TestCaller", tags.get(BaseSqlContext.DAO));
        Assert.assertEquals("call", tags.get(BaseSqlContext.METHOD));
        Assert.assertEquals(OP_TYPE.name(), tags.get(BaseSqlContext.OP_TYPE));
        Assert.assertEquals(UCS_VALIDATION, tags.get(BaseSqlContext.UCS_VALIDATION));
        Assert.assertEquals(DAL_VALIDATION, tags.get(BaseSqlContext.DAL_VALIDATION));
    }

    protected void assertFork(BaseSqlContext context) {
        Assert.assertEquals(0, context.getExecutionTime());
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(CLIENT_VERSION, tags.get(BaseSqlContext.CLIENT));
        Assert.assertEquals(CLIENT_ZONE, tags.get(BaseSqlContext.CLIENT_ZONE));
        Assert.assertEquals(DB_NAME, tags.get(BaseSqlContext.DB_NAME));
        Assert.assertEquals(DB_ZONE, tags.get(BaseSqlContext.DB_ZONE));
        Assert.assertEquals(BaseSqlContext.UNDEFINED, tags.get(BaseSqlContext.DAO));
        Assert.assertEquals(BaseSqlContext.UNDEFINED, tags.get(BaseSqlContext.METHOD));
        Assert.assertEquals(BaseSqlContext.UNDEFINED, tags.get(BaseSqlContext.OP_TYPE));
        Assert.assertEquals(BaseSqlContext.UNDEFINED, tags.get(BaseSqlContext.UCS_VALIDATION));
        Assert.assertEquals(BaseSqlContext.UNDEFINED, tags.get(BaseSqlContext.DAL_VALIDATION));
    }

}
