
package com.ctrip.platform.dal.daogen.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ctrip.platform.dal.daogen.entity.DbServer;
import com.ctrip.platform.dal.daogen.utils.TimeoutCache;

public class DaoOfDbServer {
	
	private static TimeoutCache<Integer, DbServer> _cache = new TimeoutCache<Integer, DbServer>();

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<DbServer> getAllDbServers() {

		return this.jdbcTemplate
				.query("select id, driver, server,port,domain, user,password, db_type from data_source",
						new RowMapper<DbServer>() {
							public DbServer mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DbServer.visitRow(rs);
							}
						});
	}

	public DbServer getDbServerByID(int iD) {
		
		if(_cache.containsKey(iD)){
			return _cache.get(iD);
		}

		DbServer db =  this.jdbcTemplate
				.queryForObject(
						"select id, driver,  server,port,domain, user,password, db_type from data_source where id=?",
						new Object[] { iD }, new RowMapper<DbServer>() {
							public DbServer mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DbServer.visitRow(rs);
							}
						});
		_cache.put(iD, db);
		return db;
	}

	public int insertDbServer(final DbServer data) {

		KeyHolder holder = new GeneratedKeyHolder();

		this.jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection
						.prepareStatement(
								"insert into data_source ( driver,  server,port,domain, user,password, db_type) values (?,?,?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, data.getDriver());
				ps.setString(2, data.getServer());
				ps.setInt(3, data.getPort());
				ps.setString(4, data.getDomain());
				ps.setString(5, data.getUser());
				ps.setString(6, data.getPassword());
				ps.setString(7, data.getDb_type());
				return ps;
			}
		}, holder);

		return holder.getKey().intValue();

	}

	public int DataSource(DbServer data) {

		return this.jdbcTemplate
				.update("update data_source set driver=?,  server=?,port=?,domain=?, user=?,password=?,db_type=? where id=?",
						data.getDriver(), data.getServer(),data.getPort(),data.getDomain(), data.getUser(),
						data.getPassword(), data.getDb_type(), data.getId());

	}

	public int deleteDbServer(DbServer data) {

		return this.jdbcTemplate.update("delete from data_source where id=?",
				data.getId());
	}
	
	public int deleteDbServer(int id) {

		return this.jdbcTemplate.update("delete from data_source where id=?",id);
	}

}
