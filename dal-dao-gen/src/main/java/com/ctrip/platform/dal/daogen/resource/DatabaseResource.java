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

import org.springframework.jdbc.support.JdbcUtils;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.domain.ColumnMetaData;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.domain.TableSpNames;
import com.ctrip.platform.dal.daogen.entity.DbServer;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.utils.DataSourceLRUCache;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Resource
@Singleton
@Path("db")
public class DatabaseResource {

	private static ObjectMapper mapper = new ObjectMapper();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("servers")
	public List<DbServer> getDbServers() {

		List<DbServer> dbServers = SpringBeanGetter.getDaoOfDbServer()
				.getAllDbServers();

		for (DbServer dataSource : dbServers) {
			dataSource.setUser("xxx");
			dataSource.setPassword("xxx");
		}

		java.util.Collections.sort(dbServers);

		return dbServers;

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("servers")
	public Status saveDbServer(@FormParam("id") int id,
			@FormParam("server") String server, @FormParam("port") int port,
			@FormParam("domain") String domain, @FormParam("user") String user,
			@FormParam("password") String password,
			@FormParam("db_type") String db_type,
			@FormParam("action") String action) {

		DbServer dbServer = new DbServer();
		if (action.equalsIgnoreCase("delete")) {
			dbServer.setId(id);
			if (SpringBeanGetter.getDaoOfDbServer().deleteDbServer(dbServer) > 0) {
				SpringBeanGetter.getDaoByFreeSql().deleteByServerId(id);
				SpringBeanGetter.getDaoBySqlBuilder().deleteByServerId(id);
				SpringBeanGetter.getDaoByTableViewSp().deleteByServerId(id);
			}
		} else {
			// dbServer.setDriver(driver);
			if (db_type.equalsIgnoreCase("mysql")) {
				dbServer.setDriver("com.mysql.jdbc.Driver");
			} else {
				dbServer.setDriver("net.sourceforge.jtds.jdbc.Driver");
			}
			dbServer.setServer(server);
			if (port == 0) {
				if (db_type.equalsIgnoreCase("mysql")) {
					dbServer.setPort(3306);
				} else {
					dbServer.setPort(1433);
				}
			} else {
				dbServer.setPort(port);
			}
			dbServer.setDomain(domain);
			dbServer.setUser(user);
			dbServer.setPassword(password);
			dbServer.setDb_type(db_type);
			int generatedId = SpringBeanGetter.getDaoOfDbServer()
					.insertDbServer(dbServer);
			dbServer.setId(generatedId);
			if (DataSourceLRUCache.newInstance().putDataSource(dbServer) == null) {
				SpringBeanGetter.getDaoOfDbServer().deleteDbServer(generatedId);
				return Status.ERROR;
			}
		}

		return Status.OK;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dbs")
	public String getDbNames() {

		// List<String> results = new ArrayList<String>();
		//
		// DataSource ds =
		// DataSourceLRUCache.newInstance().getDataSource(server);
		// if (ds == null) {
		// DbServer dbServer =
		// SpringBeanGetter.getDaoOfDbServer().getDbServerByID(server);
		// ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		// }
		//
		// if (ds != null) {
		// ResultSet rs = null;
		// Connection connection = null;
		// try {
		// connection = ds.getConnection();
		// rs = connection.getMetaData().getCatalogs();
		// while (rs.next()) {
		// String dbName = rs.getString("TABLE_CAT");
		// if(!Consts.SystemDatabases.contains(dbName)){
		// results.add(dbName);
		// }
		// }
		// rs.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// } finally {
		// JdbcUtils.closeResultSet(rs);
		// JdbcUtils.closeConnection(connection);
		// }
		// }

		try {
			// java.util.Collections.sort(results);
			return mapper.writeValueAsString(DataSourceLocator.newInstance()
					.getDBNames());
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
		try {

			List<String> results = DbUtils.getAllTableNames(dbName);
			java.util.Collections.sort(results);
			return mapper.writeValueAsString(results);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("fields")
	public List<ColumnMetaData> getFieldNames(
			@QueryParam("db_name") String dbName,
			@QueryParam("table_name") String tableName) {

		List<ColumnMetaData> fields = new ArrayList<ColumnMetaData>();

		Connection connection = null;
		try {
			DataSource ds = DataSourceLocator.newInstance().getDataSource(
					dbName);
			connection = ds.getConnection();
			Set<String> indexedColumns = new HashSet<String>();
			Set<String> primaryKeys = new HashSet<String>();
			Set<String> allColumns = new HashSet<String>();

			// 获取所有主键
			ResultSet primaryKeyRs = null;
			try {
				primaryKeyRs = connection.getMetaData().getPrimaryKeys(null,
						null, tableName);

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
				allColumnsRs = connection.getMetaData().getColumns(null,
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
				indexColumnsRs = connection.getMetaData().getIndexInfo(null,
						null, tableName, false, false);
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
				ColumnMetaData field = new ColumnMetaData();

				field.setName(str);
				field.setIndexed(indexedColumns.contains(str));
				field.setPrimary(primaryKeys.contains(str));
				fields.add(field);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(connection);
		}

		return fields;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("table_sps")
	public TableSpNames getTableSPNames(@QueryParam("db_name") String dbName) {
		TableSpNames tableSpNames = new TableSpNames();
		List<String> views;
		List<String> tables;
		List<StoredProcedure> sps;
		try {
			views = DbUtils.getAllViewNames(dbName);
			tables = DbUtils.getAllTableNames(dbName);
			sps = DbUtils.getAllSpNames(dbName);

			tableSpNames.setSps(sps);
			tableSpNames.setViews(views);
			tableSpNames.setTables(tables);
			tableSpNames.setDbType(DbUtils.getDbType(dbName));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return tableSpNames;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("test_sql")
	public Status verifyQuery(
			@FormParam("db_name") String dbName,
			@FormParam("sql_content") String sql,
			@FormParam("params") String params) {

		return DbUtils.testAQuerySql(dbName, sql, params, CurrentLanguage.CSharp, true) == null ? Status.ERROR
				: Status.OK;

	}

}
