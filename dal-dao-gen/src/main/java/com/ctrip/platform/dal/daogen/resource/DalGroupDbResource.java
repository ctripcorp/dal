
package com.ctrip.platform.dal.daogen.resource;

import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.jasig.cas.client.util.AssertionHolder;

import com.ctrip.platform.dal.daogen.dao.DalGroupDBDao;
import com.ctrip.platform.dal.daogen.dao.DalGroupDao;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.dao.UserGroupDao;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.UserGroup;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

/**
 * DAL database of group manage.
 * @author gzxia
 *
 */
@Resource
@Singleton
@Path("groupdb")
public class DalGroupDbResource {

	private static Logger log = Logger.getLogger(DalGroupDbResource.class);
	
	private static DalGroupDao group_dao = null;
	private static DaoOfLoginUser user_dao = null;
	private static DalGroupDBDao group_db_dao = null;
	private static UserGroupDao ugDao = null;
	
	static{
		group_dao = SpringBeanGetter.getDaoOfDalGroup();
		user_dao = SpringBeanGetter.getDaoOfLoginUser();
		group_db_dao = SpringBeanGetter.getDaoOfDalGroupDB();
		ugDao = SpringBeanGetter.getDalUserGroupDao();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DalGroup> getGroups(@QueryParam("root") boolean root) {

		List<DalGroup> groups =  group_dao.getAllGroups();
		for(DalGroup group:groups){
			group.setText(group.getGroup_name());
			group.setIcon("fa fa-folder-o");
			group.setChildren(false);
		}
		return groups;

	}
	
	@GET
	@Path("groupdb")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DalGroupDB> getGroupUsers(@QueryParam("groupId") String id) {
		int groupId = -1;
		try{
			groupId = Integer.parseInt(id);
		}catch(NumberFormatException  ex){
			log.error("get Group Users failed", ex);
			return null;
		}
		List<DalGroupDB> dbs = group_db_dao.getGroupDBsByGroup(groupId);
		return dbs;
	}
	
	@GET
	@Path("allgroupdbs")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DalGroupDB> getAllGroupDbs() {
		
		List<DalGroupDB> dbs = group_db_dao.getAllGroupDbs();
		for(DalGroupDB db : dbs){
			db.setDb_user("******");
			db.setDb_password("******");
			
			if(DatabaseType.SQLServer.getValue().equals(db.getDb_providerName())){
				db.setDb_providerName("SQLServer");
			}else if(DatabaseType.MySQL.getValue().equals(db.getDb_providerName())){
				db.setDb_providerName("MySQL");
			}else{
				db.setDb_providerName("unknown");
			}
		}
		Collections.sort(dbs, new Comparator<DalGroupDB>() {
			@Override
			public int compare(DalGroupDB o1, DalGroupDB o2) {
				return o1.getDbname().compareTo(o2.getDbname());
			}
		});
		return dbs;
	}
	
	@POST
	@Path("add")
	public Status add(@FormParam("groupId") String groupId,
			@FormParam("dbname") String dbname,
			@FormParam("comment") String comment,
			@FormParam("gen_default_dbset") boolean gen_default_dbset){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId || null == dbname){
			log.error(String.format("Add member failed, caused by illegal parameters: "
					+ "[groupId=%s, dbname=%s]",groupId, dbname));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		try{
			groupID = Integer.parseInt(groupId);
		}catch(NumberFormatException  ex){
			log.error("Add dal team database failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		DalGroupDB groupdb = group_db_dao.getGroupDBByDbName(dbname);
		if(null != groupdb && groupdb.getDal_group_id() > 0){
			DalGroup group = group_dao.getDalGroupById(groupdb.getDal_group_id());
			Status status = Status.ERROR;
			status.setInfo(groupdb.getDbname()+" is already added in "+group.getGroup_name());
			return status;
		}
		
		int ret = -1;
		if(null != groupdb){
			ret = group_db_dao.updateGroupDB(groupdb.getId(), groupID);
			group_db_dao.updateGroupDB(groupdb.getId(), comment);
		}else{
			Status status = Status.ERROR;
			status.setInfo(dbname + " 不存在，请先到数据库一览界面添加DB.");
			return status;
		}
		if(ret <= 0){
			log.error("Add dal group db failed, caused by db operation failed, pls check the log.");
			Status status = Status.ERROR;
			status.setInfo("Add operation failed.");
			return status;
		}

		if(gen_default_dbset){
			genDefaultDbset(groupID,dbname);
		}
		
		return Status.OK;
	}
	
	@POST
	@Path("update")
	public Status update(@FormParam("groupId") String groupId,
			@FormParam("dbId") String dbId,
			@FormParam("comment") String comment){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId || null == dbId){
			log.error(String.format("Add member failed, caused by illegal parameters: "
					+ "[groupId=%s, dbId=%s]",groupId, dbId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		int dbID = -1;
		try{
			dbID = Integer.parseInt(dbId); 
			groupID = Integer.parseInt(groupId);
		}catch(NumberFormatException  ex){
			log.error("Update failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		int ret = group_db_dao.updateGroupDB(dbID, comment);
		if(ret <= 0){
			log.error("Update dal group db failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Update operation failed.");
			return status;
		}
		
		return Status.OK;
	}
	
	@POST
	@Path("delete")
	public Status delete(@FormParam("groupId") String groupId,
			@FormParam("dbId") String dbId){

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId || null == dbId){
			log.error(String.format("Delete db failed, caused by illegal parameters: "
					+ "[groupId=%s, dbId=%s]",groupId, dbId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		int dbID = -1;
		try{
			groupID = Integer.parseInt(groupId);
			dbID =  Integer.parseInt(dbId);
		}catch(NumberFormatException  ex){
			log.error("Delete db failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}

		int ret = group_db_dao.updateGroupDB(dbID, -1);
		if(ret <= 0){
			log.error("Delete db failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Delete operation failed.");
			return status;
		}
		return Status.OK;
	}
	
	@POST
	@Path("transferdb")
	public Status transferdb(@FormParam("groupId") String groupId,
			@FormParam("dbId") String dbId){

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId || null == dbId){
			log.error(String.format("transfer db failed, caused by illegal parameters: "
					+ "[groupId=%s, dbId=%s]",groupId, dbId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		int dbID = -1;
		try{
			groupID = Integer.parseInt(groupId);
			dbID =  Integer.parseInt(dbId);
		}catch(NumberFormatException  ex){
			log.error("transfer db failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id or db id");
			return status;
		}
		
		if(!this.validateTransferPermision(userNo,dbID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DataBase的操作权限.");
			return status;
		}

		int ret = group_db_dao.updateGroupDB(dbID, groupID);
		if(ret <= 0){
			log.error("transfer db failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("transfer operation failed.");
			return status;
		}
		return Status.OK;
	}
	
	private boolean validatePermision(String userNo,int groupId){
		boolean havaPermision = false;
		LoginUser user = user_dao.getUserByNo(userNo);
		List<UserGroup> urGroups = ugDao.getUserGroupByUserId(user.getId());
		if (urGroups!=null && urGroups.size()>0) {
			for (UserGroup urGroup : urGroups) {
				if (urGroup.getGroup_id() == groupId) {
					havaPermision = true;
				}
			}
		}
		return havaPermision;
	}
	
	private boolean validateTransferPermision(String userNo,int dbId){
		LoginUser user = user_dao.getUserByNo(userNo);
		if(user==null){
			return false;
		}
		List<UserGroup> urGroups = ugDao.getUserGroupByUserId(user.getId());
		if(urGroups!=null && urGroups.size()>0){
			for(UserGroup urGroup : urGroups){
				List<DalGroupDB> groupDbs = group_db_dao.getGroupDBsByGroup(urGroup.getGroup_id());
				if(groupDbs!=null && groupDbs.size()>0){
					for(DalGroupDB db:groupDbs){
						if(db.getId()==dbId){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 生成默认的databaseSet和databaseSet Entry
	 * @param dbname
	 */
	private void genDefaultDbset(int groupId,String dbname){
		List<DatabaseSet> exist = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbname);
		if(exist!=null && exist.size()>0){
			return;
		}
		DatabaseSet dbset = new DatabaseSet();
		dbset.setName(dbname);
		dbset.setProvider("sqlProvider");
		try {
			Connection connection = DataSourceUtil.getConnection(dbname);
			String dbType = connection.getMetaData().getDatabaseProductName();
			if (dbType !=null && (!dbType.equals("Microsoft SQL Server"))){
				dbset.setProvider("mySqlProvider");
			}
		} catch (Exception e) {
			log.warn("", e);
		}
		
		dbset.setGroupId(groupId);
		int ret = SpringBeanGetter.getDaoOfDatabaseSet().insertDatabaseSet(dbset);
		if(ret>0){
			dbset = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbname).get(0);
			
			DatabaseSetEntry entry = new DatabaseSetEntry();
			entry.setDatabaseSet_Id(dbset.getId());
			entry.setDatabaseType("Master");
			entry.setName(dbname);
			entry.setConnectionString(dbname);
			
			SpringBeanGetter.getDaoOfDatabaseSet().insertDatabaseSetEntry(entry);
		}
	}

}

