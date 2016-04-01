package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.DalApi;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DalApiDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<DalApi> getAllDalApi() {
        return this.jdbcTemplate.query("SELECT id, language, db_type, crud_type, method_declaration, method_description,sp_type FROM api_list",
                new RowMapper<DalApi>() {
                    public DalApi mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalApi.visitRow(rs);
                    }
                });
    }

    public DalApi getDalApiById(Integer id) {
        List<DalApi> apis = this.jdbcTemplate.query("SELECT id, language, db_type, crud_type, method_declaration, method_description,sp_type FROM api_list WHERE id = ?",
                new Object[]{id}, new RowMapper<DalApi>() {
                    public DalApi mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalApi.visitRow(rs);
                    }
                });
        return null != apis && apis.size() > 0 ? apis.get(0) : null;
    }

    public List<DalApi> getDalApiByLanguage(String language) {
        List<DalApi> apis = this.jdbcTemplate.query("SELECT id, language, db_type, crud_type, method_declaration, method_description,sp_type FROM api_list WHERE language = ?",
                new Object[]{language}, new RowMapper<DalApi>() {
                    public DalApi mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalApi.visitRow(rs);
                    }
                });
        return apis;
    }

    public List<DalApi> getDalApiByLanguageAndDbtype(String language, String db_type) {
        List<DalApi> apis = this.jdbcTemplate.query("SELECT id, language, db_type, crud_type, method_declaration, method_description,sp_type FROM api_list WHERE language = ? AND db_type=?",
                new Object[]{language, db_type}, new RowMapper<DalApi>() {
                    public DalApi mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalApi.visitRow(rs);
                    }
                });
        return apis;
    }

    public List<DalApi> getDalApiByLanguageAndDbtypeAndSptype(String language, String db_type, String sp_type) {
        List<DalApi> apis = this.jdbcTemplate.query("SELECT id, language, db_type, crud_type, method_declaration, method_description,sp_type FROM api_list WHERE language = ? AND db_type=? AND sp_type=?",
                new Object[]{language, db_type, sp_type}, new RowMapper<DalApi>() {
                    public DalApi mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalApi.visitRow(rs);
                    }
                });
        return apis;
    }

}
