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
	
	public List<String> getAllDbAllinOneNames() {
		return this.jdbcTemplate.query(
				"select dbname from alldbs ",
				new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString("dbname");
					}
				});
	}
	
	public List<DalGroupDB> getAllGroupDbs() {
		return this.jdbcTemplate.query(
				"select t1.id as id, t1.dbname as dbname,t2.group_name as comment,t1.dal_group_id as dal_group_id,"
				+ "t1.db_address as db_address,"
				+ "t1.db_port as db_port,"
				+ "t1.db_user as db_user,"
				+ "t1.db_password as db_password,"
				+ "t1.db_catalog as db_catalog,"
				+ "t1.db_providerName as db_providerName from alldbs t1 left join dal_group t2 on t1.dal_group_id=t2.id ",
				new RowMapper<DalGroupDB>() {
					public DalGroupDB mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return DalGroupDB.visitRow(rs);
					}
				});
	}

	public List<DalGroupDB> getGroupDBsByGroup(int groupId) {
		return this.jdbcTemplate.query(
				"select id, dbname, comment,dal_group_id,"
				+ "db_address, db_port, db_user, db_password, db_catalog, db_providerName from alldbs"
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
				"select id, dbname, comment,dal_group_id,"
				+ "db_address, db_port, db_user, db_password, db_catalog, db_providerName from alldbs"
						+ " where dbname=?", new Object[] { dbname },
				new RowMapper<DalGroupDB>() {
					public DalGroupDB mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return DalGroupDB.visitRow(rs);
					}
				});
		return dbs!=null && dbs.size()>0? dbs.get(0):null;
	}
	
	public DalGroupDB getGroupDBByDbId(int id) {
		List<DalGroupDB> dbs = this.jdbcTemplate.query(
				"select id, dbname, comment,dal_group_id,"
				+ "db_address, db_port, db_user, db_password, db_catalog, db_providerName from alldbs"
						+ " where id=?", new Object[] { id },
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
				.update("insert into alldbs(dbname, comment, dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_providerName)"
						+ "value(?,?,?,?,?,?,?,?,?)",
						groupDb.getDbname(),
						groupDb.getComment(),
						groupDb.getDal_group_id(),
						groupDb.getDb_address(),
						groupDb.getDb_port(),
						groupDb.getDb_user(),
						groupDb.getDb_password(),
						groupDb.getDb_catalog(),
						groupDb.getDb_providerName());
	}
	
	public int updateGroupDB(int id,String dbname,String db_address,String db_port,String db_user,String db_password,String db_catalog,String db_providerName){
		return this.jdbcTemplate
				.update("update alldbs set dbname=?, db_address=?, db_port=?, db_user=?, db_password=?, db_catalog=?, db_providerName=?"
						+ " where id=?",
						dbname, db_address, db_port, db_user, db_password, db_catalog, db_providerName,
						id);
	}
	
	public int updateGroupDB(int id,String comment){
		return this.jdbcTemplate
				.update("update alldbs set comment=?"
						+ " where id=?",
						comment,
						id);
	}
	
	public int updateGroupDB(int id,Integer groupId){
		return this.jdbcTemplate
				.update("update alldbs set dal_group_id=?"
						+ " where id=?",
						groupId,
						id);
	}
	
	public int deleteDalGroupDB(int dbId){
		return this.jdbcTemplate
				.update("delete from alldbs where id=?",
						dbId);
	}

}
