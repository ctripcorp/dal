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
		builder.between("bw1", paramValue, paramValue, sqlType);
		builder.and().betweenNullable("bwNullable1", paramValue, 2, sqlType);
		builder.and().equal("eq1", paramValue, sqlType);
		builder.and().equalNullable("eqNullable1", paramValue, sqlType);
		builder.and().greaterThan("gt1", paramValue, sqlType);
		builder.and().greaterThanNullable("gtNullable1", paramValue, sqlType);
		builder.and().greaterThanEquals("gteq1", paramValue, sqlType);
		builder.and().greaterThanEqualsNullable("gteqNullable1", paramValue, sqlType);
		builder.and().in("inparam1", inParam, sqlType);
		builder.and().inNullable("inNullable1", inParam, sqlType);
		builder.and().isNotNull("notnull1");
		builder.and().isNull("isnull1");
		builder.and().lessThan("less1", paramValue, sqlType);
		builder.and().lessThanNullable("lessNullable1", paramValue, sqlType);
		builder.and().lessThanEquals("lessThan1", paramValue, sqlType);
		builder.and().lessThanEqualsNullable("lessThanNullable1", paramValue, sqlType);
		builder.and().like("like1", paramValue, sqlType);
		builder.and().likeNullable("likeNullable1", paramValue, sqlType);
		builder.and().notEqual("notEqual1", paramValue, sqlType);
		builder.and().notEqualNullable("notEqualNullable1", paramValue, sqlType);
		
		paramValue = null;
		try {
			builder.between("bw2", paramValue, paramValue, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		builder.and().betweenNullable("bwNullable2", paramValue, 2, sqlType);
		try {
			builder.and().equal("eq2", paramValue, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		builder.and().equalNullable("eqNullable2", paramValue, sqlType);
		try {
			builder.and().greaterThan("gt2", paramValue, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		builder.and().greaterThanNullable("gtNullable2", paramValue, sqlType);
		try {
			builder.and().greaterThanEquals("gteq2", paramValue, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		builder.and().greaterThanEqualsNullable("gteqNullable2", paramValue, sqlType);
		List<String> inParam2 = new ArrayList<String>();
		inParam2.add("12");
		inParam2.add(null);
		try {
			builder.and().in("inparam2", inParam2, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		builder.and().inNullable("inNullable2", inParam2, sqlType);
		try {
			builder.and().lessThan("less2", paramValue, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		builder.and().lessThanNullable("lessNullable2", paramValue, sqlType);
		try {
			builder.and().lessThanEquals("lessthan2", paramValue, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		builder.and().lessThanEqualsNullable("lessThanNullable2", paramValue, sqlType);
		try {
			builder.and().like("like2", paramValue, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		builder.and().likeNullable("likeNullable2", paramValue, sqlType);
		try {
			builder.and().notEqual("notEqual2", paramValue, sqlType);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		builder.and().notEqualNullable("notEqualNullable2", paramValue, sqlType);
		
		List<String> inParam3 = new ArrayList<String>();
		inParam3.add("12");
		inParam3.add(null);
		inParam3.add("12");
		inParam3.add(null);
		try {
			builder.and().in("inparam3", inParam3, sqlType);
		} catch (Exception e) {
			Assert.assertEquals("inparam3 is not support null value.", e.getMessage());
		}
		builder.and().inNullable("inNullable3", inParam3, Types.VARCHAR);
		
		List<Integer> inParam4 = new ArrayList<Integer>();
		inParam4.add(12);
		inParam4.add(null);
		inParam4.add(12);
		inParam4.add(null);
		builder.and().inNullable("inNullable4", inParam4, Types.INTEGER);
		
		List<Integer> inParam5 = new ArrayList<Integer>();
		inParam5.add(null);
		inParam5.add(null);
		builder.and().inNullable("inNullable5", inParam5, Types.INTEGER);
		String expected = "SELECT *, `id` FROM People WHERE "
				+ "bw1 BETWEEN ? AND ? "
				+ "AND  bwNullable1 BETWEEN ? AND ? "
				+ "AND  eq1 = ? "
				+ "AND  eqNullable1 = ? "
				+ "AND  gt1 > ? "
				+ "AND  gtNullable1 > ? "
				+ "AND  gteq1 >= ? "
				+ "AND  gteqNullable1 >= ? "
				+ "AND  inparam1 in ( ?, ? ) "
				+ "AND  inNullable1 in ( ?, ? ) "
				+ "AND  notnull1 IS NOT NULL "
				+ "AND  isnull1 IS NULL "
				+ "AND  less1 < ? "
				+ "AND  lessNullable1 < ? "
				+ "AND  lessThan1 <= ? "
				+ "AND  lessThanNullable1 <= ? "
				+ "AND  like1 LIKE ? "
				+ "AND  likeNullable1 LIKE ? "
				+ "AND  notEqual1 != ? "
				+ "AND  notEqualNullable1 != ? "
				+ "AND  inNullable2 in ( ? ) "
				+ "AND  inNullable3 in ( ?, ? ) "
				+ "AND  inNullable4 in ( ?, ? )";
		Assert.assertEquals(expected, builder.build().trim());
		Assert.assertEquals(27, builder.buildParameters().size());
		Assert.assertEquals(28, builder.getStatementParameterIndex());
		
		Assert.assertEquals(27, builder.buildParameters().get(26).getIndex());
		Assert.assertEquals("inNullable4", builder.buildParameters().get(26).getName());
		Assert.assertEquals(Types.INTEGER, builder.buildParameters().get(26).getSqlType());
		 
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