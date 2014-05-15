package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.entity.DalGroupDB;

public class DalGroupDBDao {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<DalGroupDB> getGroupDBsByGroup(int groupId) {
		return this.jdbcTemplate.query(
				"select id, dbname, comment,dal_group_id from alldbs"
						+ " where dal_group_id=?", new Object[] { groupId },
				new RowMapper<DalGroupDB>() {
					public DalGroupDB mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return DalGroupDB.visitRow(rs);
					}
				});
	}
	
	public DalGroupDB getGroupDBByDbName(String dbname) {
		List<DalGroupDB> dbs = this.jdbcTemplate.query(
				"select id, dbname, comment,dal_group_id from alldbs"
						+ " where dbname=?", new Object[] { dbname },
				new RowMapper<DalGroupDB>() {
					public DalGroupDB mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return DalGroupDB.visitRow(rs);
					}
				});
		return dbs!=null && dbs.size()>0? dbs.get(0):null;
	}
	
	public int insertDalGroupDB(DalGroupDB groupDb){
		return this.jdbcTemplate
				.update("insert into alldbs(dbname, comment, dal_group_id)"
						+ "value(?,?,?)",
						groupDb.getDbname(),
						groupDb.getComment(),
						groupDb.getDal_group_id());
	}
	
	public int updateGroupDB(int id,Integer groupId){
		return this.jdbcTemplate
				.update("update alldbs set dal_group_id=?"
						+ " where id=?",
						groupId,
						id);
	}
	
	public int updateGroupDB(int id,String comment){
		return this.jdbcTemplate
				.update("update alldbs set comment=?"
						+ " where id=?",
						comment,
						id);
	}
	
	public int deleteDalGroupDB(int dbId){
		return this.jdbcTemplate
				.update("delete from alldbs where id=?",
						dbId);
	}

}
