package com.ctrip.platform.dal.dao.datasource.log;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author c7ch23en
 */
public class KeyedDbSqlContextTest extends BaseSqlContextTest {

    private static final String DB_KEY = "test_key";

    @Test
    public void testSuccess() throws Exception {
        KeyedDbSqlContext context = new KeyedDbSqlContext(DB_KEY, CLIENT_VERSION, CLIENT_ZONE, DB_NAME);
        buildContextSuccess(context);
        assertContextSuccess(context);
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(DB_KEY, tags.get(KeyedDbSqlContext.DB_KEY));

        KeyedDbSqlContext forked = (KeyedDbSqlContext) context.fork();
        assertFork(forked);
        tags = forked.toMetricTags();
        Assert.assertEquals(DB_KEY, tags.get(KeyedDbSqlContext.DB_KEY));
    }

    @Test
    public void testFail() throws Exception {
        KeyedDbSqlContext context = new KeyedDbSqlContext(DB_KEY, CLIENT_VERSION, CLIENT_ZONE, DB_NAME);
        buildContextFail(context, new RuntimeException());
        assertContextFail(context);
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(DB_KEY, tags.get(KeyedDbSqlContext.DB_KEY));

        KeyedDbSqlContext forked = (KeyedDbSqlContext) context.fork();
        assertFork(forked);
        tags = forked.toMetricTags();
        Assert.assertEquals(DB_KEY, tags.get(KeyedDbSqlContext.DB_KEY));
    }

}
