package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfigTemplate {
	private int id;
	private int config_type;
	private int lang_type;
	private String template;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getConfig_type() {
		return config_type;
	}

	public void setConfig_type(int config_type) {
		this.config_type = config_type;
	}

	public int getLang_type() {
		return lang_type;
	}

	public void setLang_type(int lang_type) {
		this.lang_type = lang_type;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public static ConfigTemplate visitRow(ResultSet rs) throws SQLException {
		ConfigTemplate template = new ConfigTemplate();
		template.setId(rs.getInt("id"));
		template.setConfig_type(rs.getInt("config_type"));
		template.setLang_type(rs.getInt("lang_type"));
		template.setTemplate(rs.getString("template"));
		return template;
	}
}
