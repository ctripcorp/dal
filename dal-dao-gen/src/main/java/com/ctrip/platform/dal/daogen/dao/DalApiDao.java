package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.entity.DalApi;

public class DalApiDao{
	
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<DalApi> getAllDalApi(){
		return this.jdbcTemplate
				.query("select id, language, db_type, crud_type, method_declaration"
						+ ", method_description,sp_type from api_list",
				new RowMapper<DalApi>() {
					public DalApi mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return DalApi.visitRow(rs);
					}
				});
	}
	
	public DalApi getDalApiById(Integer id){
		List<DalApi> apis= this.jdbcTemplate
				.query("select id, language, db_type, crud_type, method_declaration"
						+ ", method_description,sp_type from api_list where id = " + id,
						new RowMapper<DalApi>() {
							public DalApi mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DalApi.visitRow(rs);
							}
						});
		return null != apis && apis.size() > 0 ? apis.get(0) : null;
	}
	
	public List<DalApi> getDalApiByLanguage(String language){
		List<DalApi> apis= this.jdbcTemplate
				.query("select id, language, db_type, crud_type, method_declaration"
						+ ", method_description,sp_type from api_list where language = ?" ,
						new Object[] { language },
						new RowMapper<DalApi>() {
							public DalApi mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DalApi.visitRow(rs);
							}
						});
		return apis;
	}
	
	public List<DalApi> getDalApiByLanguageAndDbtype(String language,String db_type){
		List<DalApi> apis= this.jdbcTemplate
				.query("select id, language, db_type, crud_type, method_declaration"
						+ ", method_description,sp_type from api_list where language = ?"
						+ " and db_type=?" ,
						new Object[] { language, db_type },
						new RowMapper<DalApi>() {
							public DalApi mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DalApi.visitRow(rs);
							}
						});
		return apis;
	}
	
	public List<DalApi> getDalApiByLanguageAndDbtypeAndSptype(String language,String db_type,
			String sp_type){
		List<DalApi> apis= this.jdbcTemplate
				.query("select id, language, db_type, crud_type, method_declaration"
						+ ", method_description,sp_type from api_list where language = ?"
						+ " and db_type=? and sp_type=?" ,
						new Object[] { language, db_type, sp_type },
						new RowMapper<DalApi>() {
							public DalApi mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DalApi.visitRow(rs);
							}
						});
		return apis;
	}
	
}
