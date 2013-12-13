package com.ctrip.sysdev.das.daogen.resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.ctrip.sysdev.das.common.Status;
import com.ctrip.sysdev.das.console.domain.StringIdSet;
import com.ctrip.sysdev.das.daogen.DaoGenResources;
import com.ctrip.sysdev.das.daogen.domain.MasterDAO;
import com.ctrip.sysdev.das.daogen.domain.SPDAO;
import com.ctrip.sysdev.das.daogen.domain.TableField;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

@Resource
@Singleton
@Path("db")
public class DatabaseResource {

	MasterDAO master = new MasterDAO();
	SPDAO sp = new SPDAO();
	private DBCollection taskMetaCollection;
	private DB daoGenDB;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dbs")
	public StringIdSet getDbNames() {
		Set<String> results = new HashSet<String>();

		ResultSet rs = master.getAllDbNames();

		try {
			while (rs.next()) {
				results.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (null != rs)
					rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		StringIdSet returnResults = new StringIdSet();
		returnResults.setIds(results);
		return returnResults;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tables")
	public StringIdSet getTableNames(@QueryParam("db_name") String dbName) {
		Set<String> results = new HashSet<String>();

		String sql = String
				.format("use %s select Name from sysobjects where xtype in ('v','u') and status>=0 order by name",
						dbName);
		ResultSet rs = master.fetch(sql, null, null);

		try {
			while (rs.next()) {
				results.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		StringIdSet returnResults = new StringIdSet();
		returnResults.setIds(results);
		return returnResults;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("fields")
	public List<TableField> getFieldNames(@QueryParam("db_name") String dbName,
			@QueryParam("table_name") String tableName) {

		Set<String> indexedColumns = new HashSet<String>();
		List<TableField> fields = new ArrayList<TableField>();

		// ResultSet rs = sp.getIndexedColumns(dbName, tableName);

		// try {
		// while (rs.next()) {
		// for(String col : rs.getString(3).split(",")){
		// indexedColumns.add(col.trim());
		// }
		// }
		//
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// try {
		// rs.close();
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		ResultSet allColumns = master.getAllColumns(dbName, tableName);

		if (null == daoGenDB) {
			MongoClient client = DaoGenResources.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == taskMetaCollection) {
			taskMetaCollection = daoGenDB.getCollection("task_meta");
		}

		BasicDBObject metaQuery = new BasicDBObject()
				.append("database", dbName).append("table", tableName);

		BasicDBObject fieldTypeMap = new BasicDBObject();

		try {
			while (allColumns.next()) {
				String columnName = allColumns.getString(1);
				String columnType = allColumns.getString(2);
				TableField field = new TableField();
				field.setName(columnName);
				field.setIndexed(indexedColumns.contains(columnName));
				fields.add(field);
				fieldTypeMap.append(columnName, columnType);
			}

			// TODO: what if table schema changed?
			DBCursor cursor = taskMetaCollection.find(metaQuery);
			if (cursor == null || cursor.size() == 0) {
				ResultSet rs = master.getPrimaryKey(dbName, tableName);
				String primaryKey = "";
				if (rs.next()) {
					primaryKey = rs.getString(1);
				}
				metaQuery.append("primary_key", primaryKey);
				metaQuery.append("fields", fieldTypeMap);
				taskMetaCollection.insert(metaQuery);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				allColumns.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return fields;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("sps")
	public StringIdSet getSPNames(@QueryParam("db_name") String dbName) {
		Set<String> results = new HashSet<String>();

		String sql = String
				.format("use %s select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'",
						dbName);
		ResultSet rs = master.fetch(sql, null, null);

		try {
			while (rs.next()) {
				results.add(String.format("%s.%s", rs.getString(1),
						rs.getString(2)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		StringIdSet returnResults = new StringIdSet();
		returnResults.setIds(results);
		return returnResults;

	}

	@GET
	// @Produces(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("sp_code")
	public String getSPCode(@QueryParam("db_name") String dbName,
			@QueryParam("sp_name") String spName) {

		StringBuilder sb = new StringBuilder();

		ResultSet rs = sp.getSPCode(dbName, spName);

		if (null == daoGenDB) {
			MongoClient client = DaoGenResources.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == taskMetaCollection) {
			taskMetaCollection = daoGenDB.getCollection("task_meta");
		}

		BasicDBObject metaQuery = new BasicDBObject()
				.append("database", dbName).append("sp", spName);

		try {
			while (rs.next()) {
				sb.append(rs.getString(1));
			}

			// TODO: what if table schema changed?
			DBCursor cursor = taskMetaCollection.find(metaQuery);
			if (cursor == null || cursor.size() == 0) {
				String schema = "dbo";
				String spRealName = spName;
				if(spName.contains(".")){
					schema = spName.substring(0, spName.indexOf("."));
					spRealName = spName.substring(spName.indexOf(".")+1);
				}
				ResultSet spInfo = master.getSPParams(dbName,  schema, spRealName);
				List<BasicDBObject> list = new ArrayList<BasicDBObject>();
				while (spInfo.next()) {
					BasicDBObject obj = new BasicDBObject();
					obj.append("name", spInfo.getString(1));
					obj.append("type", spInfo.getString(2));
					obj.append("direction", spInfo.getString(3));
					obj.append("position", spInfo.getInt(4));
					list.add(obj);
				}
				metaQuery.append("params", list);

				taskMetaCollection.insert(metaQuery);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sb.toString();

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.TEXT_PLAIN)
	public Status saveSp(@FormParam("db_name") String dbName,
			@QueryParam("sp_code") String spCode) {

		sp.execute(spCode, null, null);

		return Status.OK;

	}

}
