package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class SelectSqlBuilderTest {
	
	@Test
	public void testMySQL() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.MySql, false);
		
		builder.select("PeopleID","Name","CityID");
		
		builder.equal("a", "paramValue", Types.INTEGER);
		builder.and().in("b", in, Types.INTEGER);
		builder.and().like("b", "in", Types.INTEGER);
		builder.and().betweenNullable("c", "paramValue1", "paramValue2", Types.INTEGER);
		builder.and().betweenNullable("d", null, "paramValue2", Types.INTEGER);
		builder.and().isNull("sss");
		builder.orderBy("PeopleID", false);
		
		String sql = builder.build();
		
		String expect_sql = "SELECT `PeopleID`, `Name`, `CityID` FROM People "
				+ "WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c BETWEEN ? AND ? "
				+ "AND  sss IS NULL ORDER BY `PeopleID` DESC";
		
		Assert.assertEquals(expect_sql, sql);
		
	}
	
	@Test
	public void testMySQLPagination() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.MySql, true);
		
		builder.select("PeopleID","Name","CityID");
		
		builder.equal("a", "paramValue", Types.INTEGER);
		builder.and().in("b", in, Types.INTEGER);
		builder.and().like("b", "in", Types.INTEGER);
		builder.and().betweenNullable("c", "paramValue1", "paramValue2", Types.INTEGER);
		builder.and().betweenNullable("d", null, "paramValue2", Types.INTEGER);
		builder.and().isNull("sss");
		
		builder.orderBy("PeopleID", false);
		
		String sql = builder.build();
		
		String expect_sql = "SELECT `PeopleID`, `Name`, `CityID` FROM People "
				+ "WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c BETWEEN ? AND ? "
				+ "AND  sss IS NULL ORDER BY `PeopleID` DESC limit %s, %s";
		
		Assert.assertEquals(expect_sql, sql);
		
	}
	
	@Test
	public void testSQLServer() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.SqlServer, false);
		
		builder.select("PeopleID","Name","CityID");
		
		builder.equal("a", "paramValue", Types.INTEGER);
		builder.and().in("b", in, Types.INTEGER);
		builder.and().like("b", "in", Types.INTEGER);
		builder.and().betweenNullable("c", "paramValue1", "paramValue2", Types.INTEGER);
		builder.and().betweenNullable("d", null, "paramValue2", Types.INTEGER);
		builder.and().isNull("sss");
		
		builder.orderBy("PeopleID", true);
		
		String sql = builder.build();
		
		String expect_sql = "SELECT [PeopleID], [Name], [CityID] FROM People WITH (NOLOCK) "
				+ "WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c BETWEEN ? AND ? "
				+ "AND  sss IS NULL ORDER BY [PeopleID] ASC";
		
		Assert.assertEquals(expect_sql, sql);
		
	}
	
	@Test
	public void testSQLServerPagination() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.SqlServer, true);
		
		builder.select("PeopleID","Name","CityID");
		
		builder.equal("a", "paramValue", Types.INTEGER);
		builder.and().in("b", in, Types.INTEGER);
		builder.and().like("b", "in", Types.INTEGER);
		builder.and().betweenNullable("c", "paramValue1", "paramValue2", Types.INTEGER);
		builder.and().betweenNullable("d", null, "paramValue2", Types.INTEGER);
		builder.and().isNull("sss");
		
		builder.orderBy("PeopleID", true);
		
		String sql = builder.build();
		
		String expect_sql = "WITH CET AS (SELECT [PeopleID], [Name], [CityID], "
				+ "ROW_NUMBER() OVER ( ORDER BY [PeopleID] ASC) AS rownum "
				+ "FROM People WITH (NOLOCK) "
				+ "WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c BETWEEN ? AND ? "
				+ "AND  sss IS NULL) "
				+ "SELECT [PeopleID], [Name], [CityID] FROM CET WHERE rownum BETWEEN %s AND %s";
		
		Assert.assertEquals(expect_sql, sql);
		
	}
	
	@Test
	public void testMySQLFirst() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.MySql, false);
		
		builder.select("PeopleID","Name","CityID");
		
		builder.equal("a", "paramValue", Types.INTEGER);
		builder.and().in("b", in, Types.INTEGER);
		builder.and().like("b", "in", Types.INTEGER);
		builder.and().betweenNullable("c", "paramValue1", "paramValue2", Types.INTEGER);
		builder.and().betweenNullable("d", null, "paramValue2", Types.INTEGER);
		builder.and().isNull("sss");
		builder.orderBy("PeopleID", false);
		
		String sql = builder.buildFirst();
		
		String expect_sql = "SELECT `PeopleID`, `Name`, `CityID` FROM People "
				+ "WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c BETWEEN ? AND ? "
				+ "AND  sss IS NULL ORDER BY `PeopleID` DESC limit 0,1";
		
		Assert.assertEquals(expect_sql, sql);
		
	}
	
	@Test
	public void testSQLServerFirst() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.SqlServer, false);
		
		builder.select("PeopleID","Name","CityID");
		
		builder.equal("a", "paramValue", Types.INTEGER);
		builder.and().in("b", in, Types.INTEGER);
		builder.and().like("b", "in", Types.INTEGER);
		builder.and().betweenNullable("c", "paramValue1", "paramValue2", Types.INTEGER);
		builder.and().betweenNullable("d", null, "paramValue2", Types.INTEGER);
		builder.and().isNull("sss");
		
		builder.orderBy("PeopleID", true);
		
		String sql = builder.buildFirst();
		
		String expect_sql = "SELECT TOP 1 [PeopleID], [Name], [CityID] FROM People WITH (NOLOCK) "
				+ "WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c BETWEEN ? AND ? "
				+ "AND  sss IS NULL ORDER BY [PeopleID] ASC";
		
		Assert.assertEquals(expect_sql, sql);
		
	}

}