package com.ctrip.platform.dal.tester.baseDao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;

public class MiscTest {
	
	private static void testMySql(String dbName) throws SQLException {
		DalQueryDao dao = new DalQueryDao(dbName);
		
		String findAllTables = "SELECT TABLE_NAME,TABLE_ROWS FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + dbName + "'";
		List<String> l = dao.query(findAllTables, new StatementParameters(), new DalHints(), new DalObjectRowMapper<String>());
		Map<String, String> dataMap = new HashMap<String, String>();
		
		String findAllColumns = "SELECT * from ";
		String limit = " limit 1";
		DalClient client = DalClientFactory.getClient(dbName);
		
		for(String table: l) {
			System.out.println(table);
			Map<String, String> result = client.query(findAllColumns + table + limit, new StatementParameters(), new DalHints(), new ColumnTypeExtractor());
			for(String key: result.keySet()) {
				if(dataMap.containsKey(key))
					continue;
				dataMap.put(key, result.get(key));
			}
		}
		
		for(String key: dataMap.keySet()) {
			System.out.println(key);
			System.out.println(dataMap.get(key));
			System.out.println();
		}
	}
	
	private static void testSqlServer(String dbName) throws SQLException {
		DalQueryDao dao = new DalQueryDao(dbName);
		
		String findAllTables = "select name from sysobjects where type='U'";//
		List<String> l = dao.query(findAllTables, new StatementParameters(), new DalHints(), new DalObjectRowMapper<String>());
		Map<String, String> dataMap = new HashMap<String, String>();
		
		String findAllColumns = "SELECT TOP 1 * from ";
		DalClient client = DalClientFactory.getClient(dbName);
		
		for(String table: l) {
			System.out.println(table);
			Map<String, String> result;
			try {
				result = client.query(findAllColumns + table, new StatementParameters(), new DalHints(), new ColumnTypeExtractor());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			for(String key: result.keySet()) {
				if(dataMap.containsKey(key))
					continue;
				dataMap.put(key, result.get(key));
			}
		}
		
		for(String key: dataMap.keySet()) {
			System.out.println(key);
			System.out.println(dataMap.get(key));
			System.out.println();
		}
	}
	
	
	public static void main(String[] args) {
		try {
			DalClientFactory.initClientFactory();
			testSqlServer("AccDB_INSERT_1");
			
//			DalClientFactory.initPrivateFactory();
//			test("centralloggingdb");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	static class ColumnTypeExtractor implements DalResultSetExtractor<Map<String, String>> {
		@Override
		public Map<String, String> extract(ResultSet rs) throws SQLException {
			ResultSetMetaData rsmd = rs.getMetaData();
			Map<String, String> dataMap = new HashMap<String, String>();
			
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				String type = rsmd.getColumnTypeName(i);
				if(dataMap.containsKey(type))
					continue;

				String columnName = rsmd.getTableName(1) + ": " + rsmd.getColumnName(i);
				String value = String.format("Sql Type: %d; Location: %s; Java Type: %s", rsmd.getColumnType(i), columnName, rsmd.getColumnClassName(i));
				dataMap.put(type, value);
			}
			return dataMap;
		}
	}
}
