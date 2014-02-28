package com.ctrip.sysdev.das.tester.person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dao.DalParser;

public class DalPersonParser implements DalParser<Person> {
	public static final String DATABASE_NAME = "person";
	public static final String TABLE_NAME = "person";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_GENDER = "gender";
	private static final String[] COLUMNS = new String[]{
		COLUMN_ID,
		COLUMN_NAME,
		COLUMN_GENDER,
	};
	
	private static final String IDENTITY_COLUMN_NAME = "id";
	
	@Override
	public Person map(ResultSet rs, int rowNum) throws SQLException {
		Person person = new Person();
		// person.setId(rs.getInt());
		return person;
	}

	@Override
	public String getDatabaseName() {
		return DATABASE_NAME;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String[] getColumnNames() {
		return COLUMNS;
	}

	@Override
	public Map<String, ?> getFields(Person pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		// map.put(COLUMN_ID, pojo.getId());
		return map;
	}

	@Override
	public boolean hasIdentityColumn() {
		return false;
	}

	@Override
	public String getIdentityColumnName() {
		return IDENTITY_COLUMN_NAME;
	}

	@Override
	public Number getIdentityValue(Person pojo) {
		return 1; //pojo.getId();
	}

	@Override
	public Map<String, ?> getPk(Person pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		// map.put(COLUMN_ID, pojo.getId());
		return map;
	}
}
