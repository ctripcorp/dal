package com.ctrip.platform.dal.dao.helpers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.helper.SQLParser;

public class SQLParserTests {

	@Test
	public void testNotInContains() throws SQLException {
		String sql = "SELECT * FROM Person WHERE ID = ? AND Age > ?";
		String new_sql = SQLParser.parse(sql);
		Assert.assertEquals(sql, new_sql);
	}
	
	@Test
	public void testContainOneIn1() throws SQLException{
		String sql = "SELECT * FROM Person WHERE ID In ? And Age BETWEEN ? AND ?";
		String expected_sql = "SELECT * FROM Person WHERE ID In (?,?) And Age BETWEEN ? AND ?";
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		String new_sql = SQLParser.parse(sql, ids);
		
		Assert.assertEquals(expected_sql, new_sql);
	}
	
	@Test
	public void testContainOneIn2() throws SQLException{
		String sql = "SELECT * FROM Person WHERE ID In (?) And Age BETWEEN ? AND ?";
		String expected_sql = "SELECT * FROM Person WHERE ID In (?,?)  And Age BETWEEN ? AND ?";
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		String new_sql = SQLParser.parse(sql, ids);
		
		Assert.assertEquals(expected_sql, new_sql);
	}
	
	@Test
	public void testContainOneIn3() throws SQLException{
		String sql = "SELECT * FROM Person WHERE ID In( ?) And Age BETWEEN ? AND ?";
		String expected_sql = "SELECT * FROM Person WHERE ID In (?,?)  And Age BETWEEN ? AND ?";
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		String new_sql = SQLParser.parse(sql, ids);
		
		Assert.assertEquals(expected_sql, new_sql);
	}
	
	@Test
	public void testContainMultipleIn() throws SQLException{
		String sql = "SELECT * FROM Person WHERE ID In (?) And Name in( ?) Age BETWEEN ? AND ?";
		String expected_sql = "SELECT * FROM Person WHERE ID In (?,?)  And Name In (?)  Age BETWEEN ? AND ?";
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		List<String> names = new ArrayList<String>();
		names.add("hi");
		String new_sql = SQLParser.parse(sql, ids, names);
		
		Assert.assertEquals(expected_sql, new_sql);
	}

}
