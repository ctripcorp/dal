package com.ctrip.platform.dal.daogen.sql.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SQLBuilderTests {

	@Test
	public void testNet2Java() {
		String sql = "SELECT * FROM Person Where id=@id and name like @name and id in(@id)";
		String sql2 = SQLBuilder.net2Java(sql);
		assertEquals("SELECT * FROM Person Where id=? and name like ? and id in(?)", sql2);
	}
	
	@Test
	public void testJava2Java(){
		String sql = "SELECT * FROM Person Where id=? and name like ? and id in(?)";
		String sql2 = SQLBuilder.net2Java(sql);
		assertEquals(sql, sql2);
	}
}
