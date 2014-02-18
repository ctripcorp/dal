package com.ctrip.platform.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalParser;

public class ProjectDalParser implements DalParser<Project> {
	public static final String DATABASE_NAME = "daogen";
	public static final String TABLE_NAME = "project";
	private static final String[] COLUMNS = new String[]{
	$set($sepatate = ,
)
		"id"${sepatate}		"user_id"${sepatate}		"name"${sepatate}		"namespace"${sepatate}	};
	
	@Override
	public Project map(ResultSet rs, int rowNum) throws SQLException {
		Project pojo = new Project;
		pojo.setid(rs.getInt("id"));
		pojo.setuser_id(rs.getString("user_id"));
		pojo.setname(rs.getString("name"));
		pojo.setnamespace(rs.getString("namespace"));
		return pojo;
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
	public Map<String, ?> getFields(Project pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", pojo.getid());
		map.put("user_id", pojo.getuser_id());
		map.put("name", pojo.getname());
		map.put("namespace", pojo.getnamespace());
		return map;
	}

	@Override
	public boolean hasIdentityColumn() {
		return true;
	}

	@Override
	public String getIdentityColumnName() {
		return "id";
	}

	@Override
	public Number getIdentityValue(Project pojo) {
		return pojo.getid();
	}

	@Override
	public Map<String, ?> getPk(Project pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id",pojo.getid());
		return map;
	}
}
