package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.ConfigTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ConfigTemplateDao {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<ConfigTemplate> getAllConfigTemplates() {
		return this.jdbcTemplate
				.query("SELECT ID, CONFIG_TYPE, LANG_TYPE, TEMPLATE FROM CONFIG_TEMPLATE",
						new RowMapper<ConfigTemplate>() {
							public ConfigTemplate mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return ConfigTemplate.visitRow(rs);
							}
						});
	}

	public ConfigTemplate getConfigTemplateById(int templateId) {
		return this.jdbcTemplate
				.queryForObject(
						"SELECT ID, CONFIG_TYPE, LANG_TYPE, TEMPLATE FROM CONFIG_TEMPLATE WHERE ID=?",
						new Object[] { templateId },
						new RowMapper<ConfigTemplate>() {
							public ConfigTemplate mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return ConfigTemplate.visitRow(rs);
							}
						});
	}

	public ConfigTemplate getConfigTemplateByConditions(
			ConfigTemplate configTemplate) {
		if (configTemplate == null) {
			return null;
		}
		return this.jdbcTemplate
				.queryForObject(
						"SELECT ID, CONFIG_TYPE, LANG_TYPE, TEMPLATE FROM CONFIG_TEMPLATE WHERE CONFIG_TYPE=? AND LANG_TYPE=?",
						new Object[] { configTemplate.getConfig_type(),
								configTemplate.getLang_type() },
						new RowMapper<ConfigTemplate>() {
							public ConfigTemplate mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return ConfigTemplate.visitRow(rs);
							}
						});
	}

	public int insertConfigTemplate(ConfigTemplate configTemplate) {
		if (configTemplate == null) {
			return -1;
		}
		return this.jdbcTemplate
				.update("INSERT INTO CONFIG_TEMPLATE(ID, CONFIG_TYPE, LANG_TYPE, TEMPLATE) VALUES(?,?,?,?)",
						configTemplate.getId(),
						configTemplate.getConfig_type(),
						configTemplate.getLang_type(),
						configTemplate.getTemplate());
	}

	public int updateConfigTemplate(ConfigTemplate configTemplate) {
		if (configTemplate == null) {
			return -1;
		}
		try {
			return this.jdbcTemplate
					.update("UPDATE CONFIG_TEMPLATE SET CONFIG_TYPE=?, LANG_TYPE=?, TEMPLATE=? WHERE ID=?",
							configTemplate.getConfig_type(),
							configTemplate.getLang_type(),
							configTemplate.getTemplate(),
							configTemplate.getId());
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public int deleteConfigTemplate(ConfigTemplate configTemplate) {
		if (configTemplate == null) {
			return -1;
		}
		try {
			return this.jdbcTemplate.update(
					"DELETE FROM CONFIG_TEMPLATE WHERE ID=?",
					configTemplate.getId());
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
}
