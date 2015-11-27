package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class AbstractBuilderTest {

	@Test
	public void testNullValue() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.MySql, false);
		
		builder.select("PeopleID","Name","CityID");
		
		builder.equal("a", "paramValue", Types.INTEGER);
		builder.and().in("b", in, Types.INTEGER);
		builder.and().likeNullable("b", null, Types.INTEGER);
		builder.and().between("c", "paramValue1", "paramValue2", Types.INTEGER);
		builder.and().betweenNullable("d", null, "paramValue2", Types.INTEGER);
		builder.and().isNull("sss");
		builder.orderBy("PeopleID", false);
		
		String sql = builder.buildFirst();
		
		String expect_sql = "SELECT `PeopleID`, `Name`, `CityID` FROM People "
				+ "WHERE a = ? AND b in ( ?, ? ) AND c BETWEEN ? AND ? "
				+ "AND sss IS NULL ORDER BY `PeopleID` DESC limit 0,1";
		
		Assert.assertEquals(expect_sql, sql);
	}
	
	@Test
	public void testEqual() throws SQLException {
		validate("equal", "WHERE a = ?");
		validate("equalNull", "");
		validate("equal AND equal", "WHERE a = ? AND a = ?");
		validate("equal AND equalNull", "WHERE a = ?");
		validate("equalNull AND equal", "WHERE a = ?");
		validate("equalNull AND equalNull", "");
		
		validate("( equal )", "WHERE ( a = ? )");
		validate("( equalNull )", "");
		validate("( equal AND equal )", "WHERE ( a = ? AND a = ? )");
		validate("( equal AND equalNull )", "WHERE ( a = ? )");
		validate("( equalNull AND equal )", "WHERE ( a = ? )");
		validate("( equalNull AND equalNull )", "");
	}
	
	@Test
	public void testLike() throws SQLException {
		validate("like", "WHERE a LIKE ?");
		validate("likeNull", "");
		validate("like AND like", "WHERE a LIKE ? AND a LIKE ?");
		validate("like AND likeNull", "WHERE a LIKE ?");
		validate("likeNull AND like", "WHERE a LIKE ?");
		validate("likeNull AND likeNull", "");
		
		validate("( like )", "WHERE ( a LIKE ? )");
		validate("( likeNull )", "");
		validate("( like AND like )", "WHERE ( a LIKE ? AND a LIKE ? )");
		validate("( like AND likeNull )", "WHERE ( a LIKE ? )");
		validate("( likeNull AND like )", "WHERE ( a LIKE ? )");
		validate("( likeNull AND likeNull )", "");
	}
	
	@Test
	public void testBetween() throws SQLException {
		validate("between", "WHERE a BETWEEN ? AND ?");
		validate("betweenNull", "");
		validate("between AND between", "WHERE a BETWEEN ? AND ? AND a BETWEEN ? AND ?");
		validate("between AND betweenNull", "WHERE a BETWEEN ? AND ?");
		validate("betweenNull AND between", "WHERE a BETWEEN ? AND ?");
		validate("betweenNull AND betweenNull", "");
		
		validate("( between )", "WHERE ( a BETWEEN ? AND ? )");
		validate("( betweenNull )", "");
		validate("( between AND between )", "WHERE ( a BETWEEN ? AND ? AND a BETWEEN ? AND ? )");
		validate("( between AND betweenNull )", "WHERE ( a BETWEEN ? AND ? )");
		validate("( betweenNull AND between )", "WHERE ( a BETWEEN ? AND ? )");
		validate("( betweenNull AND betweenNull )", "");
	}
	
	@Test
	public void testIsNull() throws SQLException {
		validate("isNull", "WHERE a IS NULL");
		validate("isNull AND isNull", "WHERE a IS NULL AND a IS NULL");
		
		validate("( isNull )", "WHERE ( a IS NULL )");
		validate("( isNull AND isNull )", "WHERE ( a IS NULL AND a IS NULL )");
	}
	
	@Test
	public void testIsNotNull() throws SQLException {
		validate("isNotNull", "WHERE a IS NOT NULL");
		validate("isNotNull AND isNotNull", "WHERE a IS NOT NULL AND a IS NOT NULL");
		
		validate("( isNotNull )", "WHERE ( a IS NOT NULL )");
		validate("( isNotNull AND isNotNull )", "WHERE ( a IS NOT NULL AND a IS NOT NULL )");
	}
	
	@Test
	public void testNot() throws SQLException {
		validate("NOT equal", "WHERE NOT a = ?");
		validate("NOT equalNull", "");
		validate("NOT NOT NOT equal", "WHERE NOT NOT NOT a = ?");
		validate("NOT NOT NOT equalNull", "");
		validate("NOT equal AND NOT equal", "WHERE NOT a = ? AND NOT a = ?");
		validate("NOT equal AND NOT equalNull", "WHERE NOT a = ?");
		validate("NOT equalNull AND NOT equal", "WHERE NOT a = ?");
		validate("NOT equalNull AND NOT equalNull", "");
		
		validate("( NOT equal )", "WHERE ( NOT a = ? )");
		validate("( NOT NOT NOT equal )", "WHERE ( NOT NOT NOT a = ? )");
		validate("( NOT equalNull )", "");
		validate("( NOT NOT NOT equalNull )", "");
		validate("( NOT equal AND NOT equal )", "WHERE ( NOT a = ? AND NOT a = ? )");
		validate("( NOT equal AND NOT equalNull )", "WHERE ( NOT a = ? )");
		validate("( NOT equalNull AND NOT equal )", "WHERE ( NOT a = ? )");
		validate("( NOT equalNull AND NOT equalNull )", "");
	}
	
	@Test
	public void testBracket() throws SQLException {
		validate("( ( equalNull ) )", "");
		validate("( ( ( equalNull ) ) )", "");
		validate("( ( ( equalNull ) ) ) AND ( ( ( equalNull ) ) )", "");
		validate("( ( ( equalNull ) ) ) OR ( ( ( equalNull ) ) )", "");
		validate("NOT ( NOT ( NOT ( NOT equalNull ) ) ) OR ( ( ( equalNull ) ) )", "");
	}
	
	@Test
	public void testOr() throws SQLException {
		validate("equal OR equal", "WHERE a = ? OR a = ?");
		validate("equal AND ( equal OR equal )", "WHERE a = ? AND ( a = ? OR a = ? )");
	}

	public void validate(String exp, String expected) throws SQLException {
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.SqlServer, false);
		// equal equalNull between betweenNull in inNull like likeNull isNull isNotNull AND OR NOT ( )
		String[] tokens = exp.split(" "); 
		for(String token: tokens) {
			switch (token) {
				case "equal":
					builder.equal("a", "", Types.INTEGER);
					break;
				case "equalNull":
					builder.equalNullable("a", null, Types.INTEGER);
					break;
				case "like":
					builder.like("a", "", Types.INTEGER);
					break;
				case "likeNull":
					builder.likeNullable("a", null, Types.INTEGER);
					break;
				case "isNull":
					builder.isNull("a");
					break;
				case "isNotNull":
					builder.isNotNull("a");
					break;
				case "in":
					List<?> l = new ArrayList<>();
					builder.in("a", l, Types.INTEGER);
					break;
				case "between":
					builder.between("a", "", "", Types.INTEGER);
					break;
				case "inNull":
					builder.inNullable("a", null, Types.INTEGER);
					break;
				case "betweenNull":
					builder.betweenNullable("a", null, null, Types.INTEGER);
					break;
				case "AND":
					builder.and();
					break;
				case "OR":
					builder.or();
					break;
				case "NOT":
					builder.not();
					break;
				case "(":
					builder.leftBracket();
					break;
				case ")":
					builder.rightBracket();
					break;
				default:
					Assert.fail("Unknown token: " + token);
			}
		}
		
		Assert.assertEquals(expected, builder.getWhereExp());
	}
}
