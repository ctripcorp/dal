
package com.ctrip.platform.dal.daogen.resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import org.jasig.cas.client.util.AssertionHolder;
import org.springframework.jdbc.support.JdbcUtils;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.dao.DalGroupDBDao;
import com.ctrip.platform.dal.daogen.dao.UserGroupDao;
import com.ctrip.platform.dal.daogen.domain.ColumnMetaData;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.domain.TableSpNames;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.UserGroup;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.utils.AllInOneConfigParser;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Resource
@Singleton
@Path("db")
public class DatabaseResource {

	private static ClassLoader classLoader;
	private static ObjectMapper mapper = new ObjectMapper();
	
	private static UserGroupDao ugDao = null;
	
	static {
		classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = Configuration.class.getClassLoader();
		}
		ugDao = SpringBeanGetter.getDalUserGroupDao();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("merge")
	public Status mergeDB() {
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();

		Status status = Status.OK;
		
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		
		if (user.getGroupId() != DalGroupResource.SUPER_GROUP_ID){
			status = Status.ERROR;
			status.setInfo("You have no permision, only DAL Admin Team can do this.");
			return status;
		}
		
		DalGroupDBDao allDbDao = SpringBeanGetter.getDaoOfDalGroupDB();
		
		Map<String, DalGroupDB> allDbs = new AllInOneConfigParser(Configuration.get("all_in_one")).getDBAllInOneConfig();
		Set<String> keys = allDbs.keySet();
		for(String key:keys){
			DalGroupDB db = allDbDao.getGroupDBByDbName(key);
			if(db==null){
				allDbDao.insertDalGroupDB(allDbs.get(key));
			}else{
				DalGroupDB fileDB = allDbs.get(key);
				allDbDao.updateGroupDB(db.getId(), key, fileDB.getDb_address(), 
						fileDB.getDb_port(),fileDB.getDb_user(), fileDB.getDb_password(),
						fileDB.getDb_catalog(), fileDB.getDb_providerName());
			}
		}

		return Status.OK;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("connectionTest")
	public Status connectionTest(@FormParam("dbtype") String dbtype,
			@FormParam("dbaddress") String dbaddress,
			@FormParam("dbport") String dbport,
			@FormParam("dbuser") String dbuser,
			@FormParam("dbpassword") String dbpassword) {
		Status status = Status.OK;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtil.getConnection(dbaddress, dbport, dbuser, dbpassword,
							DatabaseType.valueOf(dbtype).getValue());
			rs = conn.getMetaData().getCatalogs();
			Set<String> allCatalog = new HashSet<String>();
			while(rs.next()){
				allCatalog.add(rs.getString("TABLE_CAT"));
			}
			status.setInfo(mapper.writeValueAsString(allCatalog));
		} catch (SQLException e) {
			status = Status.ERROR;
			status.setInfo(e.getMessage());
			return status;
		} catch (JsonProcessingException e) {
			status = Status.ERROR;
			status.setInfo(e.getMessage());
			return status;
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeConnection(conn);
		}

		return status;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("addNewAllInOneDB")
	public Status addNewAllInOneDB(@FormParam("dbtype") String dbtype,
			@FormParam("allinonename") String allinonename,@FormParam("dbaddress") String dbaddress,
			@FormParam("dbport") String dbport,@FormParam("dbuser") String dbuser,
			@FormParam("dbpassword") String dbpassword,@FormParam("dbcatalog") String dbcatalog) {

		Status status = Status.OK;
		
		DalGroupDBDao allDbDao = SpringBeanGetter.getDaoOfDalGroupDB();
		
		if (allDbDao.getGroupDBByDbName(allinonename)!=null) {
			status = Status.ERROR;
			status.setInfo(allinonename+"已经存在!");
			return status;
		} else {
			DalGroupDB groupDb = new DalGroupDB();
			groupDb.setDbname(allinonename);
			groupDb.setDb_address(dbaddress);
			groupDb.setDb_port(dbport);
			groupDb.setDb_user(dbuser);
			groupDb.setDb_password(dbpassword);
			groupDb.setDb_catalog(dbcatalog);
			groupDb.setDb_providerName(DatabaseType.valueOf(dbtype).getValue());
			groupDb.setDal_group_id(-1);
			allDbDao.insertDalGroupDB(groupDb);
		}
		
		return Status.OK;
	}
	
	private boolean validatePermision(int userId, int db_group_id) {
		boolean havaPermision = false;
		List<UserGroup> urGroups = ugDao.getUserGroupByUserId(userId);
		if (urGroups!=null && urGroups.size()>0) {
			for (UserGroup urGroup : urGroups) {
				if (urGroup.getGroup_id() == DalGroupResource.SUPER_GROUP_ID){
					havaPermision = true;
				}
				if (urGroup.getGroup_id() == db_group_id) {
					havaPermision = true;
				}
			}
		}
		return havaPermision;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("deleteAllInOneDB")
	public Status deleteAllInOneDB(@FormParam("allinonename") String allinonename) {
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();

		Status status = Status.OK;
		
		DalGroupDBDao allDbDao = SpringBeanGetter.getDaoOfDalGroupDB();
		DalGroupDB groupDb = allDbDao.getGroupDBByDbName(allinonename);
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		
		if (!validatePermision(user.getId(), groupDb.getDal_group_id())) {
			status = Status.ERROR;
			status.setInfo("你没有当前DataBase的操作权限.");
		} else {
			allDbDao.deleteDalGroupDB(groupDb.getId());		
		}
		return status;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getOneDB")
	public Status getOneDB(@FormParam("allinonename") String allinonename) {
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();

		Status status = Status.OK;
		
		DalGroupDBDao allDbDao = SpringBeanGetter.getDaoOfDalGroupDB();
		DalGroupDB groupDb = allDbDao.getGroupDBByDbName(allinonename);
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		
		if (!validatePermision(user.getId(), groupDb.getDal_group_id())) {
			status = Status.ERROR;
			status.setInfo("你没有当前DataBase的操作权限.");
			return status;
		}
		
		try {
			if(DatabaseType.MySQL.getValue().equals(groupDb.getDb_providerName())){
				groupDb.setDb_providerName(DatabaseType.MySQL.toString());
			}else if(DatabaseType.SQLServer.getValue().equals(groupDb.getDb_providerName())){
				groupDb.setDb_providerName(DatabaseType.SQLServer.toString());
			}else{
				groupDb.setDb_providerName("no");
			}
			status.setInfo(mapper.writeValueAsString(groupDb));
		} catch (JsonProcessingException e) {
			status = Status.ERROR;
			status.setInfo(e.getMessage());
			return status;
		}		
		
		return Status.OK;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("updateDB")
	public Status updateDB(@FormParam("id") int id,@FormParam("dbtype") String dbtype,
			@FormParam("allinonename") String allinonename,@FormParam("dbaddress") String dbaddress,
			@FormParam("dbport") String dbport,@FormParam("dbuser") String dbuser,
			@FormParam("dbpassword") String dbpassword,@FormParam("dbcatalog") String dbcatalog) {

		Status status = Status.OK;
		
		DalGroupDBDao allDbDao = SpringBeanGetter.getDaoOfDalGroupDB();
		DalGroupDB db = allDbDao.getGroupDBByDbName(allinonename);
		
		if(db!=null && db.getId()!=id){
			status = Status.ERROR;
			status.setInfo(allinonename+"已经存在!");
			return status;
		}
		
		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		DalGroupDB groupDb = allDbDao.getGroupDBByDbId(id);
		
		if (!validatePermision(user.getId(), groupDb.getDal_group_id())) {
			status = Status.ERROR;
			status.setInfo("你没有当前DataBase的操作权限.");
			return status;
		}
		
		allDbDao.updateGroupDB(id, allinonename, dbaddress, dbport, dbuser, dbpassword, dbcatalog, DatabaseType.valueOf(dbtype).getValue());
		
		return Status.OK;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dbs")
	public String getDbNames(@QueryParam("groupDBs") boolean groupDBs,
			@QueryParam("groupId") int groupId) {
		if(groupDBs) {
			if(-1 != groupId && groupId>0) {
				Set<String> sets = new HashSet<String>();
				List<DalGroupDB> dbs = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(groupId);
				for(DalGroupDB db:dbs){
					sets.add(db.getDbname());
				}
				try {
					return mapper.writeValueAsString(sets);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				List<String> dbAllinOneNames = SpringBeanGetter.getDaoOfDalGroupDB().getAllDbAllinOneNames();
				return mapper.writeValueAsString(dbAllinOneNames);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tables")
	public String getTableNames(@QueryParam("db_name") String db_set) throws Exception {
		try {
			DatabaseSetEntry databaseSetEntry = SpringBeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(db_set);
			String dbName = databaseSetEntry.getConnectionString();
			List<String> results = DbUtils.getAllTableNames(dbName);
			java.util.Collections.sort(results);
			return mapper.writeValueAsString(results);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("fields")
	public List<ColumnMetaData> getFieldNames(
			@QueryParam("db_name") String dbName,
			@QueryParam("table_name") String tableName) throws Exception {

		List<ColumnMetaData> fields = new ArrayList<ColumnMetaData>();

		Connection connection = null;
		try {
			DatabaseSetEntry databaseSetEntry = SpringBeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(dbName);
			String db_Name = databaseSetEntry.getConnectionString();
			
			connection = DataSourceUtil.getConnection(db_Name);
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
			throw e1;
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		} finally {
			JdbcUtils.closeConnection(connection);
		}

		return fields;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("table_sps")
	public Status getTableSPNames(@QueryParam("db_name") String setName) {
		Status status = Status.OK;
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
			sps = filterSP(tables, sps);
			
			java.util.Collections.sort(views, new Comparator<String>(){
				@Override
				public int compare(String o1, String o2) {
					return o1.toLowerCase().compareTo(o2.toLowerCase());
				}
			});
			java.util.Collections.sort(tables,  new Comparator<String>(){
				@Override
				public int compare(String o1, String o2) {
					return o1.toLowerCase().compareTo(o2.toLowerCase());
				}
			});
			java.util.Collections.sort(sps);
			
			tableSpNames.setSps(sps);
			tableSpNames.setViews(views);
			tableSpNames.setTables(tables);
			tableSpNames.setDbType(DbUtils.getDbType(dbName));
			
			status.setInfo(mapper.writeValueAsString(tableSpNames));
		} catch (Exception e1) {
			status = Status.ERROR;
			status.setInfo(e1.getMessage());
			return status;
		}
		return status;
	}
	
	private List<StoredProcedure> filterSP(List<String> tables, List<StoredProcedure> sps){
		List<StoredProcedure> result = new ArrayList<StoredProcedure>();
		if(tables!=null && sps!=null && tables.size()>0 && sps.size()>0){
			for(StoredProcedure sp:sps){
				String spName = sp.getName()!=null?sp.getName().toLowerCase():null;
				boolean isSpAOrSp3orSpT = false;
				for(String tableName:tables){
					tableName = tableName.toLowerCase();
					if(spName==null || "".equals(spName)){
						isSpAOrSp3orSpT = true;
						break;
					}
					if(spName.indexOf(String.format("spa_%s", tableName))>-1
							|| spName.indexOf(String.format("sp3_%s", tableName))>-1
							|| spName.indexOf(String.format("spt_%s", tableName))>-1){
						isSpAOrSp3orSpT = true;
						break;
					}
				}
				if(!isSpAOrSp3orSpT){
					result.add(sp);
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println("123".indexOf("12"));
	}
}












