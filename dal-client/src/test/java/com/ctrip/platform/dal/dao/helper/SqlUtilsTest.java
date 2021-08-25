package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.common.enums.SqlType;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class SqlUtilsTest {

    private static final String select_sql = "select * from table1 where id = 1";
    private static final String insert_sql = "insert into table1 values (1, name, 10)";
    private static final String update_sql = "update table1 set name = name, age = 10 where id = 1";
    private static final String delete_sql = "delete from table1 where id = 1";
    private static final String replace_sql = "replace into table1 values (1, name, 11)";
    private static final String truncate_sql = "truncate TABLE dbname.ZONESERVICE";
    private static final String create_sql = "create table table2 ( id varchar not null, name varchar not null)";
    private static final String drop_sql = "drop table table2";
    private static final String load_sql = "load data infile '/workspace'";
    private static final String show_sql = "show databases;";
    private static final String explain_sql = "explain select surname,first_name form a,b where a.id=b.id";
    private static final String alter_sql = "alter table employee add constraint";
    private static final String unknown_sql = "this is not a sql";
    private static final String empty_sql = "this is not a sql";
    private static final String execute_sql = "{call sp_proc}";
    private static final String select_for_update_sql = "select * from xx for update";
    private static final String select_for_identity = "select @@identity";

    @Test
    public void getSqlType() {
        Assert.assertEquals(SqlType.SELECT, SqlUtils.getSqlType(select_sql));
        Assert.assertEquals(SqlType.UNKNOWN_SQL_TYPE, SqlUtils.getSqlType(empty_sql));
        Assert.assertEquals(SqlType.INSERT, SqlUtils.getSqlType(insert_sql));
        Assert.assertEquals(SqlType.UPDATE, SqlUtils.getSqlType(update_sql));
        Assert.assertEquals(SqlType.DELETE, SqlUtils.getSqlType(delete_sql));
        Assert.assertEquals(SqlType.REPLACE, SqlUtils.getSqlType(replace_sql));
        Assert.assertEquals(SqlType.TRUNCATE, SqlUtils.getSqlType(truncate_sql));
        Assert.assertEquals(SqlType.CREATE, SqlUtils.getSqlType(create_sql));
        Assert.assertEquals(SqlType.DROP, SqlUtils.getSqlType(drop_sql));
        Assert.assertEquals(SqlType.LOAD, SqlUtils.getSqlType(load_sql));
        Assert.assertEquals(SqlType.SHOW, SqlUtils.getSqlType(show_sql));
        Assert.assertEquals(SqlType.EXPLAIN, SqlUtils.getSqlType(explain_sql));
        Assert.assertEquals(SqlType.ALTER, SqlUtils.getSqlType(alter_sql));
        Assert.assertEquals(SqlType.UNKNOWN_SQL_TYPE, SqlUtils.getSqlType(unknown_sql));
        Assert.assertEquals(SqlType.EXECUTE, SqlUtils.getSqlType(execute_sql));
        Assert.assertEquals(SqlType.SELECT_FOR_UPDATE, SqlUtils.getSqlType(select_for_update_sql));
        Assert.assertEquals(SqlType.SELECT_FOR_IDENTITY, SqlUtils.getSqlType(select_for_identity));
    }

    @Test
    public void testComment() throws SQLException {
        String sql = "/*sdasdf*/select * from xx";
        SqlType sqlType = SqlUtils.getSqlType(sql);
        Assert.assertEquals(SqlType.SELECT, sqlType);
        Assert.assertEquals(true, sqlType.isRead());
    }

    @Test
    public void testComment2() throws SQLException {
        String sql = "/*sdasdf*/select * from /*123123*/ xx";
        SqlType sqlType = SqlUtils.getSqlType(sql);
        Assert.assertEquals(SqlType.SELECT, sqlType);
        Assert.assertEquals(true, sqlType.isRead());
    }

    @Test
    public void keyWordTest() {
        Assert.assertEquals(SqlUtils.isStartWithKeyWord("set session group_concat_max_len=1024000",0, "select"), false);
        Assert.assertEquals(SqlUtils.isStartWithKeyWord("select * from test",0, "select"), true);
        Assert.assertEquals(SqlUtils.isStartWithKeyWord("select" ,0, "select"), true);
        Assert.assertEquals(SqlUtils.isStartWithKeyWord("set" ,0, "select"), false);
        Assert.assertEquals(SqlUtils.isStartWithKeyWord(" set" ,0, "select"), false);
        Assert.assertEquals(SqlUtils.isStartWithKeyWord(" select" ,0, "select"), false);
        Assert.assertEquals(SqlUtils.isStartWithKeyWord("select " ,0, "select"), true);
        Assert.assertEquals(SqlUtils.isStartWithKeyWord("select1" ,0, "select"), false);
        Assert.assertEquals(SqlUtils.isStartWithKeyWord("select1 " ,0, "select"), false);
    }
}