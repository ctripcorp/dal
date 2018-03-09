package com.ctrip.platform.dal.daogen.sql.builder;

import com.ctrip.platform.dal.daogen.utils.SqlBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SQLBuilderTests {

    @Test
    public void testNet2Java() {
        String sql = "SELECT * FROM Person Where id=@id and name like @name and id in(@id)";
        String sql2 = SqlBuilder.net2Java(sql);
        assertEquals("SELECT * FROM Person Where id=? and name like ? and id in(?)", sql2);
    }

    @Test
    public void testJava2Java() {
        String sql = "SELECT * FROM Person Where id=? and name like ? and id in(?)";
        String sql2 = SqlBuilder.net2Java(sql);
        assertEquals(sql, sql2);
    }
}
