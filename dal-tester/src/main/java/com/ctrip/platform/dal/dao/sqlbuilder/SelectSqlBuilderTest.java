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
	public void testMySQLWhere() throws SQLException{
		
		
		List<String> inParam = new ArrayList<String>();
		inParam.add("12");
		inParam.add("12");
		
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.MySql, false);
		
		builder.select("*","id");
		
		int sqlType = Types.INTEGER;
		Integer paramValue = 1;
		builder.between("bw", paramValue, paramValue, sqlType);
		builder.and().betweenNullable("bwNullable", paramValue, 2, sqlType);
		builder.and().equal("eq", paramValue, sqlType);
		builder.and().equalNullable("eqNullable", paramValue, sqlType);
		builder.and().greaterThan("gt", paramValue, sqlType);
		builder.and().greaterThanNullable("gtNullable", paramValue, sqlType);
		builder.and().greaterThanEquals("gteq", paramValue, sqlType);
		builder.and().greaterThanEqualsNullable("gteqNullable", paramValue, sqlType);
		builder.and().in("inparam", inParam, sqlType);
		builder.and().inNullable("inNullable", inParam, sqlType);
		builder.and().isNotNull("notnull");
		builder.and().isNull("isnull");
		builder.and().lessThan("less", paramValue, sqlType);
		builder.and().lessThanNullable("lessNullable", paramValue, sqlType);
		builder.and().lessThanEquals("lessthan", paramValue, sqlType);
		builder.and().lessThanEqualsNullable("lessthannullable", paramValue, sqlType);
		builder.and().like("lik", paramValue, sqlType);
		builder.and().likeNullable("likNullable", paramValue, sqlType);
		builder.and().notEqual("notEqual", paramValue, sqlType);
		builder.and().notEqualNullable("notEqualNullable", paramValue, sqlType);
		
		try {
			paramValue = null;
			builder.between("bw", paramValue, paramValue, sqlType);
			builder.and().betweenNullable("bwNullable", paramValue, 2, sqlType);
			builder.and().equal("eq", paramValue, sqlType);
			builder.and().equalNullable("eqNullable", paramValue, sqlType);
			builder.and().greaterThan("gt", paramValue, sqlType);
			builder.and().greaterThanNullable("gtNullable", paramValue, sqlType);
			builder.and().greaterThanEquals("gteq", paramValue, sqlType);
			builder.and().greaterThanEqualsNullable("gteqNullable", paramValue, sqlType);
			builder.and().in("inparam", inParam, sqlType);
			builder.and().inNullable("inNullable", inParam, sqlType);
			builder.and().isNotNull("notnull");
			builder.and().isNull("isnull");
			builder.and().lessThan("less", paramValue, sqlType);
			builder.and().lessThanNullable("lessNullable", paramValue, sqlType);
			builder.and().lessThanEquals("lessthan", paramValue, sqlType);
			builder.and().lessThanEqualsNullable("lessthannullable", paramValue, sqlType);
			builder.and().like("lik", paramValue, sqlType);
			builder.and().likeNullable("likNullable", paramValue, sqlType);
			builder.and().notEqual("notEqual", paramValue, sqlType);
			builder.and().notEqualNullable("notEqualNullable", paramValue, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		
		inParam = new ArrayList<String>();
		inParam.add("12");
		inParam.add(null);
		inParam.add("12");
		inParam.add(null);
		try {
			builder.and().in("inparam", inParam, sqlType);
		} catch (Exception e) {
			Assert.assertEquals("inparam is not support null value.", e.getMessage());
		}
		builder.and().inNullable("inNullable2", inParam, Types.VARCHAR);
		
		List<Integer> inParam2 = new ArrayList<Integer>();
		inParam2.add(12);
		inParam2.add(null);
		inParam2.add(12);
		inParam2.add(null);
		builder.and().inNullable("inNullable3", inParam2, Types.INTEGER);
		
		inParam2 = new ArrayList<Integer>();
		inParam2.add(null);
		inParam2.add(null);
		builder.and().inNullable("inNullable4", inParam2, Types.INTEGER);
		
		String expected = "SELECT *, `id` FROM People WHERE "
				+ "bw BETWEEN ? AND ? "
				+ "AND  bwNullable BETWEEN ? AND ? "
				+ "AND  eq = ? "
				+ "AND  eqNullable = ? "
				+ "AND  gt > ? "
				+ "AND  gtNullable > ? "
				+ "AND  gteq >= ? "
				+ "AND  gteqNullable >= ? "
				+ "AND  inparam in ( ?, ? ) "
				+ "AND  inNullable in ( ?, ? ) "
				+ "AND  notnull IS NOT NULL "
				+ "AND  isnull IS NULL "
				+ "AND  less < ? "
				+ "AND  lessNullable < ? "
				+ "AND  lessthan <= ? "
				+ "AND  lessthannullable <= ? "
				+ "AND  lik LIKE ? "
				+ "AND  likNullable LIKE ? "
				+ "AND  notEqual != ? "
				+ "AND  notEqualNullable != ? "
				+ "AND  inNullable2 in ( ?, ? ) "
				+ "AND  inNullable3 in ( ?, ? )";
		 Assert.assertEquals(expected, builder.build().trim());
		 Assert.assertEquals(26, builder.buildParameters().size());
		 Assert.assertEquals(27, builder.getStatementParameterIndex());
	}
	
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