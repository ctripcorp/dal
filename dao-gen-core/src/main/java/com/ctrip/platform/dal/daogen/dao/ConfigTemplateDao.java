package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.ConfigTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ConfigTemplateDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<ConfigTemplate> getAllConfigTemplates() {
        try {
            return jdbcTemplate.query("SELECT ID, CONFIG_TYPE, LANG_TYPE, TEMPLATE FROM config_template",
                    new RowMapper<ConfigTemplate>() {
                        public ConfigTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return ConfigTemplate.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public ConfigTemplate getConfigTemplateById(int templateId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT ID, CONFIG_TYPE, LANG_TYPE, TEMPLATE FROM config_template WHERE ID=?",
                    new Object[] {templateId}, new RowMapper<ConfigTemplate>() {
                        public ConfigTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return ConfigTemplate.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public ConfigTemplate getConfigTemplateByConditions(ConfigTemplate configTemplate) {
        if (configTemplate == null) {
            return null;
        }
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT ID, CONFIG_TYPE, LANG_TYPE, TEMPLATE FROM config_template WHERE CONFIG_TYPE=? AND LANG_TYPE=?",
                    new Object[] {configTemplate.getConfig_type(), configTemplate.getLang_type()},
                    new RowMapper<ConfigTemplate>() {
                        public ConfigTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return ConfigTemplate.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public int insertConfigTemplate(ConfigTemplate configTemplate) {
        if (configTemplate == null) {
            return -1;
        }
        try {
            return jdbcTemplate.update(
                    "INSERT INTO config_template(ID, CONFIG_TYPE, LANG_TYPE, TEMPLATE) VALUES(?,?,?,?)",
                    configTemplate.getId(), configTemplate.getConfig_type(), configTemplate.getLang_type(),
                    configTemplate.getTemplate());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int updateConfigTemplate(ConfigTemplate configTemplate) {
        if (configTemplate == null) {
            return -1;
        }
        try {
            return jdbcTemplate.update("UPDATE config_template SET CONFIG_TYPE=?, LANG_TYPE=?, TEMPLATE=? WHERE ID=?",
                    configTemplate.getConfig_type(), configTemplate.getLang_type(), configTemplate.getTemplate(),
                    configTemplate.getId());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int deleteConfigTemplate(ConfigTemplate configTemplate) {
        if (configTemplate == null) {
            return -1;
        }
        try {
            return jdbcTemplate.update("DELETE FROM config_template WHERE ID=?", configTemplate.getId());
        } catch (Throwable e) {
            throw e;
        }
    }

}
