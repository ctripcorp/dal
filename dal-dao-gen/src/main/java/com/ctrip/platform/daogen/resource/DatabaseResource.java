package com.ctrip.platform.daogen.resource;

import java.io.IOException;
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

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.ctrip.platform.daogen.dao.MasterDAO;
import com.ctrip.platform.daogen.dao.SPDAO;
import com.ctrip.platform.daogen.dao.StringIdSet;
import com.ctrip.platform.daogen.pojo.FieldMeta;
import com.ctrip.platform.daogen.pojo.Status;

@Resource
@Singleton
@Path("db")
public class DatabaseResource {

	MasterDAO master = new MasterDAO();
	SPDAO sp = new SPDAO();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dbs")
	public String getDbNames() {
		HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet("http://localhost:8080/rest/configure/db");

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            // Body contains your json stirng
            //String responseBody;
			try {
				return httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return null;
//		Set<String> results = new HashSet<String>();
//
//		ResultSet rs = master.getAllDbNames();
//
//		try {
//			while (rs.next()) {
//				results.add(rs.getString(1));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (null != rs)
//					rs.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//
//		StringIdSet returnResults = new StringIdSet();
//		returnResults.setIds(results);
//		return returnResults;

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
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
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
	public List<FieldMeta> getFieldNames(@QueryParam("db_name") String dbName,
			@QueryParam("table_name") String tableName) {

		Set<String> indexedColumns = new HashSet<String>();
		List<FieldMeta> fields = new ArrayList<FieldMeta>();

		ResultSet rs = sp.getIndexedColumns(dbName, tableName);

		try {
			while (rs.next()) {
				for (String col : rs.getString(3).split(",")) {
					indexedColumns.add(col.trim());
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		ResultSet allColumns = master.getAllColumns(dbName, tableName);

		try {
			ResultSet primaryKeyResultSet = master.getPrimaryKey(dbName,
					tableName);
			String primaryKey = "";
			if (primaryKeyResultSet.next()) {
				primaryKey = primaryKeyResultSet.getString(1);
			}

			while (allColumns.next()) {
				String columnName = allColumns.getString(1);

				FieldMeta field = new FieldMeta();

				field.setName(columnName);
				field.setIndexed(indexedColumns.contains(columnName));
				field.setPrimary(columnName.equals(primaryKey));
				fields.add(field);
				
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				allColumns.close();
			} catch (SQLException e) {
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
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
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

		try {
			while (rs.next()) {
				sb.append(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
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
