package com.ctrip.platform.dal.daogen.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Project {
	
	private int id;
	
	private String name;
	
	private String namespace;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public static Project visitRow(ResultSet rs) throws SQLException {
		Project project = new Project();
        project.setId(rs.getInt(1));
        project.setName(rs.getString(2));
        project.setNamespace(rs.getString(3));
         return project;
	}

}
