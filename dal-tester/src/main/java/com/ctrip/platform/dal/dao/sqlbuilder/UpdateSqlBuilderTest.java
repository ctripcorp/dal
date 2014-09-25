package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ctrip.platform.dal.dao.StatementParameters;

public class UpdateSqlBuilderTest {
	
	@Test
	public void test1() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		
		UpdateSqlBuilder builder = new UpdateSqlBuilder("Person");
		builder.addUpdateField("`a`","[b]","c");
		
		StatementParameters parameters = new StatementParameters();
		int index = 1;
		
		index = builder.addConstrant().equal("a", "paramValue", parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().in("b", in, parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().like("b", "in", parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().betweenNullable("c", "paramValue1", "paramValue2", 
				parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().betweenNullable("d", null, "paramValue2", 
				parameters, index, Types.INTEGER);
		
		builder.addConstrant().and().isNull("e");
		String sql = builder.buildUpdateSql();
		System.out.println(sql);
	}

}
