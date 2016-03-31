package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DaoOfDatabaseSet {

	private JdbcTemplate jdbcTemplate;
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public DatabaseSet getAllDatabaseSetById(Integer id){
		List<DatabaseSet> dbset= this.jdbcTemplate
				.query("select id, name, provider, shardingStrategy, groupId, update_user_no, update_time "
						+ "from databaseSet where id = ?",
						new Object[] { id },
						new RowMapper<DatabaseSet>() {
							public DatabaseSet mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DatabaseSet.visitRow(rs);
							}
						});
		return dbset!=null && dbset.size()>0 ? dbset.get(0) : null;
	}
	
	public List<DatabaseSet> getAllDatabaseSetByName(String name){
		List<DatabaseSet> dbset= this.jdbcTemplate
				.query("select id, name, provider, shardingStrategy, groupId, update_user_no, update_time "
						+ "from databaseSet where name = ?",
						new Object[] { name },
						new RowMapper<DatabaseSet>() {
							public DatabaseSet mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DatabaseSet.visitRow(rs);
							}
						});
		return dbset;
	}
	
	public List<DatabaseSet> getAllDatabaseSetByGroupId(Integer groupId){
		List<DatabaseSet> dbset= this.jdbcTemplate
				.query("select id, name, provider, shardingStrategy, groupId, update_user_no, update_time "
						+ "from databaseSet where groupId = " + groupId,
						new RowMapper<DatabaseSet>() {
							public DatabaseSet mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DatabaseSet.visitRow(rs);
							}
						});
		return dbset;
	}
	
	public List<DatabaseSetEntry> getAllDatabaseSetEntryByDbsetid(Integer databaseSet_Id){
		List<DatabaseSetEntry> dbset= this.jdbcTemplate
				.query("select id, name, databaseType, sharding, connectionString, databaseSet_Id, update_user_no, update_time "
						+ "from databaseSetEntry where databaseSet_Id = " + databaseSet_Id,
						new RowMapper<DatabaseSetEntry>() {
							public DatabaseSetEntry mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return DatabaseSetEntry.visitRow(rs);
							}
						});
		return dbset;
	}
	
	public DatabaseSetEntry getMasterDatabaseSetEntryByDatabaseSetName(String dbName){
		List<DatabaseSetEntry> list = this.jdbcTemplate
				.query("select en.id, en.name, en.databaseType, en.sharding, en.connectionString, en.databaseSet_Id, en.update_user_no, en.update_time "
						+ "from databaseSetEntry as en "
						+ "join databaseSet as se on en.databaseSet_Id = se.id "
						+ "where se.name = '" + dbName + "' and en.databaseType = 'Master' limit 1;", new RowMapper<DatabaseSetEntry>(){
					@Override
					public DatabaseSetEntry mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return DatabaseSetEntry.visitRow(rs);
					}
					
				});
		return null != list && list.size() > 0 ? list.get(0) : null;
	}
	
	public int insertDatabaseSet(DatabaseSet dbset){
		return this.jdbcTemplate
				.update("insert into databaseSet(name, provider, shardingStrategy, groupId, update_user_no, update_time)"
						+ "value(?,?,?,?,?,?)",
						dbset.getName(),
						dbset.getProvider(),
						dbset.getShardingStrategy(),
						dbset.getGroupId(),
						dbset.getUpdate_user_no(),
						dbset.getUpdate_time());
	}
	
	public int insertDatabaseSetEntry(DatabaseSetEntry dbsetEntry){
		return this.jdbcTemplate
				.update("insert into databaseSetEntry(name, databaseType, sharding, connectionString, databaseSet_Id, update_user_no, update_time)"
						+ "value(?,?,?,?,?,?,?)",
						dbsetEntry.getName(),
						dbsetEntry.getDatabaseType(),
						dbsetEntry.getSharding(),
						dbsetEntry.getConnectionString(),
						dbsetEntry.getDatabaseSet_Id(),
						dbsetEntry.getUpdate_user_no(),
						dbsetEntry.getUpdate_time());
	}
	
	public int updateDatabaseSet(DatabaseSet dbset){
		return this.jdbcTemplate
				.update("update databaseSet set name=?, provider=?, shardingStrategy=?, groupId=?, update_user_no=?, update_time=? "
						+ " where id=?",
						dbset.getName(),
						dbset.getProvider(),
						dbset.getShardingStrategy(),
						dbset.getGroupId(),
						dbset.getUpdate_user_no(),
						dbset.getUpdate_time(),
						dbset.getId());
	}
	
	public int updateDatabaseSetEntry(DatabaseSetEntry dbsetEntry){
		return this.jdbcTemplate
				.update("update databaseSetEntry set name=?, databaseType=?, sharding=?, connectionString=?, databaseSet_Id=?, update_user_no=?, update_time=? "
						+ " where id=?",
						dbsetEntry.getName(),
						dbsetEntry.getDatabaseType(),
						dbsetEntry.getSharding(),
						dbsetEntry.getConnectionString(),
						dbsetEntry.getDatabaseSet_Id(),
						dbsetEntry.getUpdate_user_no(),
						dbsetEntry.getUpdate_time(),
						dbsetEntry.getId());
	}
	
	/**
	 * 依据外键databaseSet_Id删除entry
	 * @param dbsetId
	 * @return
	 */
	public int deleteDatabaseSetEntryByDbsetId(Integer dbsetId){
		return this.jdbcTemplate.update("delete from databaseSetEntry where databaseSet_Id=?", dbsetId);
	}
	
	/**
	 * 根据主键id删除entry
	 * @param id
	 * @return
	 */
	public int deleteDatabaseSetEntryById(Integer id){
		return this.jdbcTemplate.update("delete from databaseSetEntry where id=?", id);
	}
	
	public int deleteDatabaseSetById(Integer dbsetId){
		return this.jdbcTemplate.update("delete from databaseSet where id=?", dbsetId);
	}
	
}













