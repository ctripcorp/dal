package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

public class DbMetaTest {
    private static final String name = "mysqldaltest01db_W";
    private static DalQueryDao queryDao = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
        queryDao = new DalQueryDao(name);
    }

    @Test
    public void testDbMeta() throws Exception {
        DalHints hints = new DalHints();
        StatementParameters parameters = new StatementParameters();

        for (int i = 0; i < 10000; i++) {
            try {
                test();
                Thread.sleep(2 * 1000);
            } catch (Throwable e) {
                System.out.println(e);
            }
        }

    }

    private void test() throws SQLException {
        DalHints hints = new DalHints();

        FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(DatabaseCategory.MySql);
        builder.setTemplate("select @@hostname");
        StatementParameters parameters = new StatementParameters();
        builder.simpleType().requireSingle().nullable();

        Object o = queryDao.query(builder, parameters, hints);
        System.out.println(o);
    }

}
