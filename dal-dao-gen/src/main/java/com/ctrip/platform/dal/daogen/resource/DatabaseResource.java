
package com.ctrip.platform.dal.daogen.resource;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jasig.cas.client.util.AssertionHolder;
import org.springframework.jdbc.support.JdbcUtils;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.domain.ColumnMetaData;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.domain.TableSpNames;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.IgnoreCaseCampare;
import com.ctrip.platform.dal.daogen.utils.JavaIOUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.datasource.LocalDataSourceLocator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Resource
@Singleton
@Path("db")
public class DatabaseResource {

	private static ClassLoader classLoader;
	private static ObjectMapper mapper = new ObjectMapper();
	
	static {
		classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = Configuration.class.getClassLoader();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("all_in_one")
	public Status saveAllInOne(@FormParam("data") String data) {
		SAXReader saxReader = new SAXReader();
		InputStream inStream= null;
		FileWriter writer = null;
		Document document;
		try {
			
			if(!LocalDataSourceLocator.newInstance().refresh(data)){
				Status status = Status.ERROR;
				status.setInfo("此数据库已存在，或者连接字符串非法，请修改后再保存");
				return status;
			}
			
			//inStream = classLoader.getResourceAsStream("Database.config");
//			URL url = classLoader.getResource("Database.config");
//			if(url == null){
//				return Status.ERROR;
//			}
			//inStream = url.openStream();
			inStream = new FileInputStream(Configuration.get("all_in_one"));
			
			document = saxReader.read(inStream);

			Element root = document.getRootElement();

			Document temp = DocumentHelper.parseText(data);
			root.add(temp.getRootElement());
			writer = new FileWriter(Configuration.get("all_in_one"));
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter output = new XMLWriter(writer, format);
			output.write(document);
			//output.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			JavaIOUtils.closeInputStream(inStream);
			JavaIOUtils.closeWriter(writer);
		}

		return Status.OK;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dbs")
	public String getDbNames(@QueryParam("groupDBs") boolean groupDBs) {
		if(groupDBs){
			String userNo = AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("employee").toString();
			LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
			if(user!=null){
				List<DalGroupDB> dbs = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(user.getGroupId());
				Set<String> sets = new HashSet<String>();
				for(DalGroupDB db:dbs){
					sets.add(db.getDbname());
				}
				try {
					return mapper.writeValueAsString(sets);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}else{
			try {
				return mapper.writeValueAsString(LocalDataSourceLocator.newInstance()
						.getDBNames());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tables")
	public String getTableNames(@QueryParam("db_name") String db_set) {
		try {
			DatabaseSetEntry databaseSetEntry = SpringBeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(db_set);
			String dbName = databaseSetEntry.getConnectionString();
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
			DatabaseSetEntry databaseSetEntry = SpringBeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(dbName);
			String db_Name = databaseSetEntry.getConnectionString();
			
			DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(
					db_Name);
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
				allColumnsRs = connection.getMetaData().getColumns(null, null,
						tableName, null);
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
	public TableSpNames getTableSPNames(@QueryParam("db_name") String setName) {
		TableSpNames tableSpNames = new TableSpNames();
		List<String> views;
		List<String> tables;
		List<StoredProcedure> sps;
		try {
			
			DatabaseSetEntry databaseSetEntry = SpringBeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(setName);
			String dbName = databaseSetEntry.getConnectionString();
			views = DbUtils.getAllViewNames(dbName);
			tables = DbUtils.getAllTableNames(dbName);
			sps = DbUtils.getAllSpNames(dbName);
			
			java.util.Collections.sort(views, new IgnoreCaseCampare());
			java.util.Collections.sort(tables, new IgnoreCaseCampare());
			java.util.Collections.sort(sps);

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
	public Status verifyQuery(@FormParam("db_name") String set_name,
			@FormParam("sql_content") String sql,
			@FormParam("params") String params) {

		DatabaseSetEntry databaseSetEntry = SpringBeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(set_name);
		String dbName = databaseSetEntry.getConnectionString();
		
		return DbUtils.testAQuerySql(dbName, sql, params,
				CurrentLanguage.CSharp, true) == null ? Status.ERROR
				: Status.OK;

	}

}
