package com.ctrip.platform.dal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.helper.AbstractDalParser;

public class PeopleParser extends AbstractDalParser<People> {
	public static final String DATABASE_NAME = "SimpleShard";
	public static final String TABLE_NAME = "People";
	private static final String[] COLUMNS = new String[]{
		"PeopleID",
		"Name",
		"CityID",
		"ProvinceID",
		"CountryID",
	};
	
	private static final String[] PRIMARY_KEYS = new String[]{
		"PeopleID",
	};
	
	private static final int[] COLUMN_TYPES = new int[]{
		Types.BIGINT,
		Types.VARCHAR,
		Types.INTEGER,
		Types.INTEGER,
		Types.INTEGER,
	};
	
	public PeopleParser() {
		super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
	}
	
	public PeopleParser(String logicDbName) {
		super(logicDbName, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
	}
	
	@Override
	public People map(ResultSet rs, int rowNum) throws SQLException {
		People pojo = new People();
		
		pojo.setPeopleID((Long)rs.getObject("PeopleID"));
		pojo.setName((String)rs.getObject("Name"));
		pojo.setCityID((Integer)rs.getObject("CityID"));
		pojo.setProvinceID((Integer)rs.getObject("ProvinceID"));
		pojo.setCountryID((Integer)rs.getObject("CountryID"));

		return pojo;
	}

	@Override
	public boolean isAutoIncrement() {
		return true;
	}

	@Override
	public Number getIdentityValue(People pojo) {
		return pojo.getPeopleID();
	}

	@Override
	public Map<String, ?> getPrimaryKeys(People pojo) {
		Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
		
		primaryKeys.put("PeopleID", pojo.getPeopleID());

		return primaryKeys;
	}

	@Override
	public Map<String, ?> getFields(People pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		map.put("PeopleID", pojo.getPeopleID());
		map.put("Name", pojo.getName());
		map.put("CityID", pojo.getCityID());
		map.put("ProvinceID", pojo.getProvinceID());
		map.put("CountryID", pojo.getCountryID());

		return map;
	}
}