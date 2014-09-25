package com.ctrip.platform.dal.dao.sqlbuilder;


import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.StatementParameters;

public class DeleteSqlBuilderTest {
	
	
	@Test
	public void test1() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		DeleteSqlBuilder builder = new DeleteSqlBuilder("Person");
		
		StatementParameters parameters = new StatementParameters();
		int index = 1;
		
		index = builder.addConstrant().equal("a", "paramValue", parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().in("b", in, parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().like("b", "in", parameters, index, Types.INTEGER);
		
		builder.addConstrant().and().isNotNull("c");
		
		index = builder.addConstrant().and().betweenNullable("d", null, "paramValue2",
				parameters, index, Types.INTEGER);
		
		builder.addConstrant().and().isNull("e");
		
		String build_sql = builder.buildDelectSql();
		
		String expected_sql = "DELETE FROM Person WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c IS NOT NULL AND  e IS NULL";
		Assert.assertEquals(expected_sql, build_sql);
	}
}
