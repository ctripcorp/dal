package com.ctrip.platform.dal.dao.sqlbuilder;


import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class DeleteSqlBuilderTest {
	
	
	@Test
	public void test1() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		
		DeleteSqlBuilder builder = new DeleteSqlBuilder("Person", DatabaseCategory.MySql);
		
		builder.equal("a", "paramValue", Types.INTEGER);
		builder.and().in("b", in, Types.INTEGER);
		builder.and().like("b", "in", Types.INTEGER);
		builder.and().isNotNull("c");
		builder.and().betweenNullable("d", null, "paramValue2", Types.INTEGER);
		builder.and().isNull("e");
		
		String build_sql = builder.build();
		
		String expected_sql = "DELETE FROM Person WHERE a = ? AND  b in ( ?, ? ) "
				+ "AND  b LIKE ? AND  c IS NOT NULL AND  e IS NULL";
		
		Assert.assertEquals(expected_sql, build_sql);
		
		builder.buildParameters();
		Assert.assertEquals(5, builder.getStatementParameterIndex());
		
		Assert.assertEquals(4, builder.buildParameters().size());
		Assert.assertEquals(1, builder.buildParameters().get(0).getIndex());
		Assert.assertEquals("a", builder.buildParameters().get(0).getName());
		Assert.assertEquals(Types.INTEGER, builder.buildParameters().get(0).getSqlType());
	}
}
