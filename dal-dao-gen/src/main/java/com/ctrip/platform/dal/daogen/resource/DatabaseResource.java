package com.ctrip.platform.dal.daogen.resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.sql.DataSource;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.ctrip.platform.dal.daogen.dao.DbServerDAO;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.FieldMeta;
import com.ctrip.platform.dal.daogen.pojo.Status;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.daogen.utils.DataSourceLRUCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Resource
@Singleton
@Path("db")
public class DatabaseResource {

	
	private static ObjectMapper mapper = new ObjectMapper();
	private static DbServerDAO dbServerDao;

	static {
		dbServerDao = SpringBeanGetter.getDBServerDao();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("servers")
	public List<DbServer> getDbServers() {

		List<DbServer> dbServers = dbServerDao.getAllDbServers();

		for (DbServer dataSource : dbServers) {
			dataSource.setUser("不告诉你");
			dataSource.setPassword("不告诉你");
		}

		return dbServers;

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("servers")
	public Status saveDbServer(@FormParam("id") int id,
			@FormParam("driver") String driver, @FormParam("url") String url,
			@FormParam("user") String user,
			@FormParam("password") String password,
			@FormParam("db_type") String db_type,
			@FormParam("action") String action) {

		DbServer dbServer = new DbServer();
		if (action.equalsIgnoreCase("delete")) {
			dbServer.setId(id);
			dbServerDao.deleteDbServer(dbServer);
		} else {
			dbServer.setDriver(driver);
			dbServer.setUrl(url);
			dbServer.setUser(user);
			dbServer.setPassword(password);
			dbServer.setDb_type(db_type);
			int generatedId = dbServerDao.insertDbServer(dbServer);
			dbServer.setId(generatedId);
			if (DataSourceLRUCache.newInstance().putDataSource(dbServer) == null) {
				dbServerDao.deleteDbServer(generatedId);
				return Status.ERROR;
			}
		}

		return Status.OK;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dbs")
	public String getDbNames(@QueryParam("server") int server) {

		List<String> results = new ArrayList<String>();

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);
		if(ds == null){
			DbServer dbServer = dbServerDao.getDbServerByID(server);
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		if (ds != null) {
			ResultSet rs = null;
			Connection connection = null;
			try {
				connection = ds.getConnection();
				rs = connection.getMetaData().getCatalogs();
				while (rs.next()) {
					results.add(rs.getString("TABLE_CAT"));
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.closeResultSet(rs);
				JdbcUtils.closeConnection(connection);
			}
		}

		try {
			return mapper.writeValueAsString(results);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tables")
	public String getTableNames(@QueryParam("server") int server,
			@QueryParam("db_name") String dbName) {

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);

		DbServer dbServer = dbServerDao.getDbServerByID(server);
		
		if(ds == null){
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		List<String> results = new ArrayList<String>();

		// 如果是Sql Server，通过Sql语句获取所有表和视图的名称
		if (dbServer != null
				&& dbServer.getDb_type().equalsIgnoreCase("sqlserver")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

			String sql = String
					.format("use %s select Name from sysobjects where xtype in ('v','u') and status>=0 order by name",
							dbName);
			results = jdbcTemplate.query(sql, new RowMapper<String>() {
				public String mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					return rs.getString(1);
				}
			});
		}
		// 如果是MySql，通过JDBC标准方式获取所有表和视图的名称
		else if (dbServer != null
				&& dbServer.getDb_type().equalsIgnoreCase("mysql")) {
			String[] types = { "TABLE", "VIEW" };
			ResultSet rs = null;
			Connection connection = null;
			try {
				connection = ds.getConnection();
				rs = connection.getMetaData().getTables(dbName, null, "%",
						types);
				while (rs.next()) {
					results.add(rs.getString("TABLE_NAME"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.closeResultSet(rs);
				JdbcUtils.closeConnection(connection);
			}
		}

		try {
			return mapper.writeValueAsString(results);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("fields")
	public List<FieldMeta> getFieldNames(@QueryParam("server") int server,
			@QueryParam("db_name") String dbName,
			@QueryParam("table_name") String tableName) {

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);
		if(ds == null){
			DbServer dbServer = dbServerDao.getDbServerByID(server);
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		List<FieldMeta> fields = new ArrayList<FieldMeta>();

		if (ds != null) {

			Connection connection = null;
			try {
				connection = ds.getConnection();
				Set<String> indexedColumns = new HashSet<String>();
				Set<String> primaryKeys = new HashSet<String>();
				Set<String> allColumns = new HashSet<String>();

				// 获取所有主键
				ResultSet primaryKeyRs = null;
				try {
					primaryKeyRs = connection.getMetaData().getPrimaryKeys(
							dbName, null, tableName);

					while (primaryKeyRs.next()) {
						primaryKeys.add(primaryKeyRs.getString("COLUMN_NAME"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JdbcUtils.closeResultSet(primaryKeyRs);
				}

				// 获取所有列
				ResultSet allColumnsRs = null;
				try {
					allColumnsRs = connection.getMetaData().getColumns(dbName,
							null, tableName, null);
					while (allColumnsRs.next()) {
						allColumns.add(allColumnsRs.getString("COLUMN_NAME"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JdbcUtils.closeResultSet(allColumnsRs);
				}

				// 获取所有索引信息
				ResultSet indexColumnsRs = null;

				try {
					indexColumnsRs = connection.getMetaData().getIndexInfo(
							dbName, null, tableName, false, false);
					while (indexColumnsRs.next()) {
						String column = indexColumnsRs.getString("COLUMN_NAME");
						if (column != null) {
							indexedColumns.add(column);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JdbcUtils.closeResultSet(indexColumnsRs);
				}

				for (String str : allColumns) {
					FieldMeta field = new FieldMeta();

					field.setName(str);
					field.setIndexed(indexedColumns.contains(str));
					field.setPrimary(primaryKeys.contains(str));
					fields.add(field);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				JdbcUtils.closeConnection(connection);
			}

		}

		return fields;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("sps")
	public String getSPNames(@QueryParam("server") int server,
			@QueryParam("db_name") String dbName) {

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);
		
		DbServer dbServer = dbServerDao.getDbServerByID(server);
		
		if(ds == null){
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		List<String> results = new ArrayList<String>();

		// 如果是Sql Server，通过Sql语句获取所有表和视图的名称
		if (dbServer != null
				&& dbServer.getDb_type().equalsIgnoreCase("sqlserver")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

			String sql = String
					.format("use %s select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'",
							dbName);
			results = jdbcTemplate.query(sql, new RowMapper<String>() {
				public String mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					return String.format("%s.%s", rs.getString(1),
							rs.getString(2));
				}
			});
		}

		try {
			return mapper.writeValueAsString(results);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;

	}

	@GET
	// @Produces(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("sp_code")
	public String getSPCode(@QueryParam("server") int server,
			@QueryParam("db_name") String dbName,
			@QueryParam("sp_name") String spName) {

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);
		
		if(ds == null){
			DbServer dbServer = dbServerDao.getDbServerByID(server);
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		JdbcTemplate template = new JdbcTemplate(ds);

		final StringBuilder spCode = new StringBuilder();

		template.query(String.format("use %s exec sp_helptext ?", dbName),
				new Object[] { spName }, new RowMapper<Integer>() {
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						spCode.append(rs.getString(1));
						return spCode.length();
					}
				});

		return spCode.toString();

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.TEXT_PLAIN)
	public Status saveSp(@QueryParam("server") int server,
			@FormParam("db_name") String dbName,
			@QueryParam("sp_code") String spCode) {

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);
		
		if(ds == null){
			DbServer dbServer = dbServerDao.getDbServerByID(server);
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		JdbcTemplate template = new JdbcTemplate(ds);

		template.update(spCode);

		return Status.OK;

	}

}
