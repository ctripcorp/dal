package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.pojo.ServerDbMap;

public class ServerDbMapDAO {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<ServerDbMap> getServerDbs() {

		return this.jdbcTemplate.query(
				"select id, server_id, db_name from server_db_map",
				new RowMapper<ServerDbMap>() {
					public ServerDbMap mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return ServerDbMap.visitRow(rs);
					}
				});

	}

	public ServerDbMap getServerByDbname(String dbName) {

		return this.jdbcTemplate
				.queryForObject(
						"select id, server_id, db_name from server_db_map where db_name=?",
						new Object[] { dbName }, new RowMapper<ServerDbMap>() {
							public ServerDbMap mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return ServerDbMap.visitRow(rs);
							}
						});

	}
	
	public int insertServerDbMap(ServerDbMap map){
		return this.jdbcTemplate
				.update("insert into server_db_map ( server_id, db_name) values (?,?)",
						map.getServer_id(), map.getDb_name());
	}
	
	public int updateServerDbmap(ServerDbMap map){
		return this.jdbcTemplate
				.update("update server_db_map set server_id=?, db_name=? where id=?",
						map.getServer_id(), map.getDb_name(), map.getId());
	}
	
}
