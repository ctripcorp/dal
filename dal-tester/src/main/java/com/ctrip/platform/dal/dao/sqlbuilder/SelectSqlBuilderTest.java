package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.StatementParameters;

public class SelectSqlBuilderTest {
	
	@Test
	public void test1() throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		SelectSqlBuilder builder = new SelectSqlBuilder("[HotelPubDB].[dbo].[People] with (nolock)");
		builder.addSelectField("[PeopleID]","[Name]","[CityID]");
		StatementParameters parameters = new StatementParameters();
		int index = 1;
		index = builder.addConstrant().equal("a", "paramValue", parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().in("b", in, parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().like("b", "in", parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().betweenNullable("c", "paramValue1", "paramValue2", 
				parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().betweenNullable("d", null, "paramValue2", 
				parameters, index, Types.INTEGER);
		
		builder.addConstrant().and().isNull("sss");
		
		builder.addOrderByExp("ORDER BY [PeopleID] DESC");
		
		String expected_sql = "SELECT [PeopleID], [Name], [CityID] FROM [HotelPubDB].[dbo].[People] with (nolock) WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c BETWEEN ? AND ? AND  sss IS NULL ORDER BY [PeopleID] DESC";
		String expected_PaginationSql4MySQL = "SELECT [PeopleID], [Name], [CityID] FROM [HotelPubDB].[dbo].[People] with (nolock) WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c BETWEEN ? AND ? AND  sss IS NULL ORDER BY [PeopleID] DESC limit %s, %s";;
		String expected_PaginationSql4SqlServer = "WITH CET AS (SELECT [PeopleID], [Name], [CityID], ROW_NUMBER() OVER ( ORDER BY [PeopleID] DESC) AS rownum FROM [HotelPubDB].[dbo].[People] with (nolock) WHERE a = ? AND  b in ( ?, ? ) AND  b LIKE ? AND  c BETWEEN ? AND ? AND  sss IS NULL) SELECT [PeopleID], [Name], [CityID] FROM CET WHERE rownum BETWEEN %s AND %s";
		
		Assert.assertEquals(expected_sql, builder.buildSelectSql());
		Assert.assertEquals(expected_PaginationSql4MySQL, builder.buildPaginationSql4MySQL());
		Assert.assertEquals(expected_PaginationSql4SqlServer, builder.buildPaginationSql4SqlServer());
		
	}

}