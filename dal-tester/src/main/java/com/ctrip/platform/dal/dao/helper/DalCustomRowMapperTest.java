package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.unitbase.MySqlUnitBase;

public class DalCustomRowMapperTest extends MySqlUnitBase{
	@Test
	public void testArray() throws SQLException {
		DalCustomRowMapper mapper = new DalCustomRowMapper("id", "quantity", "type");
		String sql = "select id, quantity, type from " + TABLE_NAME;
		
		DalRowMapperExtractor<Map<String, Object>> rse =
				new DalRowMapperExtractor<Map<String, Object>>(mapper);
		List<Map<String, Object>> rest = client.query(sql, new StatementParameters(), new DalHints(), rse);
		Assert.assertEquals(3, rest.size());
		Assert.assertEquals("1", rest.get(0).get("id").toString());
	}
	
	@Test
	public void testList() throws SQLException {
		List<String> columns = new ArrayList<String>();
		columns.add("id");
		DalCustomRowMapper mapper = new DalCustomRowMapper(columns);
		String sql = "select id from " + TABLE_NAME;
		
		DalRowMapperExtractor<Map<String, Object>> rse =
				new DalRowMapperExtractor<Map<String, Object>>(mapper);
		List<Map<String, Object>> rest = client.query(sql, new StatementParameters(), new DalHints(), rse);
		Assert.assertEquals(3, rest.size());
		Assert.assertEquals("1", rest.get(0).get("id").toString());
	}
}
