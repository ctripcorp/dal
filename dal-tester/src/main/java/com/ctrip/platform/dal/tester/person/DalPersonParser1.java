package com.ctrip.platform.dal.tester.person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalParser;

public class DalPersonParser1 implements DalParser<Person> {
	public static final String DATABASE_NAME = "dao_test";
	public static final String TABLE_NAME = "Person";
	private static final String[] COLUMNS = new String[] { 
		"ID", "Address",
			"Telephone", "Name", "Age", "Gender", "Birth" };

	@Override
	public Person map(ResultSet rs, int rowNum) throws SQLException {
		Person vo = new Person();
		vo.setID(rs.getInt("ID"));
		vo.setAddress(rs.getString("Address"));
		vo.setTelephone(rs.getString("Telephone"));
		vo.setName(rs.getString("Name"));
		vo.setAge(rs.getInt("Age"));
		vo.setGender(rs.getInt("Gender"));
		vo.setBirth(rs.getTimestamp("Birth"));
		return vo;
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
		map.put("ID", pojo.getID());
		map.put("Address", pojo.getAddress());
		map.put("Telephone", pojo.getTelephone());
		map.put("Name", pojo.getName());
		map.put("Age", pojo.getAge());
		map.put("Gender", pojo.getGender());
		map.put("Birth", pojo.getBirth());
		return map;
	}

	@Override
	public boolean isAutoIncrement() {
		return true;
	}

	@Override
	public Number getIdentityValue(Person pojo) {
		return pojo.getID();
	}

	@Override
	public Map<String, ?> getPrimaryKeys(Person pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ID", pojo.getID());
		return map;
	}

	@Override
	public String[] getPrimaryKeyNames() {
		// TODO Auto-generated method stub
		return new String[] {"ID"};
	}
	
	@Override
	public int[] getColumnTypes() {
		// TODO Auto-generated method stub
		return null;
	}
}