
package com.ctrip.platform.dal.daogen.resource;

import java.util.ArrayList;
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

import com.ctrip.platform.dal.daogen.dao.DalGroupDao;
import com.ctrip.platform.dal.daogen.dao.DaoOfDatabaseSet;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.dao.UserGroupDao;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.UserGroup;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

/**
 * DAL databaseSet of group manage.
 * @author gzxia
 *
 */
@Resource
@Singleton
@Path("groupdbset")
public class DalGroupDbSetResource {

	private static Logger log = Logger.getLogger(DalGroupDbSetResource.class);
	
	private static DalGroupDao group_dao = null;
	private static DaoOfLoginUser user_dao = null;
	private static DaoOfDatabaseSet dbset_dao = null;
	private static UserGroupDao ugDao = null;
	
	static{
		group_dao = SpringBeanGetter.getDaoOfDalGroup();
		user_dao = SpringBeanGetter.getDaoOfLoginUser();
		dbset_dao = SpringBeanGetter.getDaoOfDatabaseSet();
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
	@Path("getDbset")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DatabaseSet> getDatabaseSetByGroupId(@QueryParam("groupId") String id,
			@QueryParam("daoFlag") boolean daoFlag) {
		int groupId = -1;
		try{
			groupId = Integer.parseInt(id);
		}catch(NumberFormatException  ex){
			log.error("get DatabaseSet failed", ex);
			return null;
		}
		List<DatabaseSet> dbsets = dbset_dao.getAllDatabaseSetByGroupId(groupId);
		if(!daoFlag){
			return dbsets;
		}
		List<DatabaseSet> result = new ArrayList<DatabaseSet>();
		for(DatabaseSet dbset:dbsets){ //排除没有entry的dbset
			List<DatabaseSetEntry> entrys = dbset_dao.getAllDatabaseSetEntryByDbsetid(dbset.getId());
			if(entrys!=null && entrys.size()>0){
				result.add(dbset);
			}
		}
		return result;
	}
	
	@GET
	@Path("getDbsetEntry")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DatabaseSetEntry> getDatabaseSetEntryByDbsetid(@QueryParam("dbsetId") String dbsetId) {
		int databaseSet_Id = -1;
		try{
			databaseSet_Id = Integer.parseInt(dbsetId);
		}catch(NumberFormatException  ex){
			log.error("get DatabaseSetEntry failed", ex);
			return null;
		}
		List<DatabaseSetEntry> dbsetEntry = dbset_dao.getAllDatabaseSetEntryByDbsetid(databaseSet_Id);
		return dbsetEntry;
	}
	
	@POST
	@Path("addDbset")
	public Status addDbset(@FormParam("name") String name,
			@FormParam("provider") String provider,
			@FormParam("shardingStrategy") String shardingStrategy,
			@FormParam("groupId") String groupId){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId){
			log.error(String.format("Add Dbset failed, caused by illegal parameters: "
					+ "[userNo=%s, groupId=%s]",userNo, groupId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		try{
			groupID = Integer.parseInt(groupId);
		}catch(NumberFormatException  ex){
			log.error("Add Dbset failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		List<DatabaseSet> dbsets = dbset_dao.getAllDatabaseSetByName(name);
		if(null != dbsets && dbsets.size() > 0){
			Status status = Status.ERROR;
			status.setInfo("databaseSet Name --> "+name+" 已经存在，请重新命名!");
			return status;
		}
		
		int ret = -1;
		DatabaseSet dbset = new DatabaseSet();
		dbset.setName(name);
		dbset.setProvider(provider);
		dbset.setShardingStrategy(shardingStrategy);
		dbset.setGroupId(groupID);
		ret = dbset_dao.insertDatabaseSet(dbset);
		if(ret <= 0){
			log.error("Add database set failed, caused by db operation failed, pls check the log.");
			Status status = Status.ERROR;
			status.setInfo("Add operation failed.");
			return status;
		}
		
		return Status.OK;
	}
	
	@POST
	@Path("updateDbset")
	public Status updateDbset(@FormParam("id") String id,
			@FormParam("name") String name,
			@FormParam("provider") String provider,
			@FormParam("shardingStrategy") String shardingStrategy,
			@FormParam("groupId") String groupId){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId){
			log.error(String.format("Update Dbset failed, caused by illegal parameters: "
					+ "[userNo=%s, groupId=%s]",userNo, groupId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int iD = -1;
		int groupID = -1;
		try{
			iD = Integer.parseInt(id);
			groupID = Integer.parseInt(groupId);
		}catch(NumberFormatException  ex){
			log.error("Update Dbset failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		List<DatabaseSet> dbsets = dbset_dao.getAllDatabaseSetByName(name);
		if(null != dbsets && dbsets.size() > 0){
			for(DatabaseSet dbset:dbsets){
				if(dbset.getId()!=iD){
					Status status = Status.ERROR;
					status.setInfo("databaseSet Name --> "+name+"已经存在，请重新命名!");
					return status;
				}
			}
		}
		
		int ret = -1;
		DatabaseSet dbset = new DatabaseSet();
		dbset.setId(iD);
		dbset.setName(name);
		dbset.setProvider(provider);
		dbset.setShardingStrategy(shardingStrategy);
		dbset.setGroupId(groupID);
		ret = dbset_dao.updateDatabaseSet(dbset);
		if(ret <= 0){
			log.error("Update database set failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Update operation failed.");
			return status;
		}
		
		return Status.OK;
	}
	
	@POST
	@Path("deletedbset")
	public Status deleteDbset(@FormParam("groupId") String groupId,
			@FormParam("dbsetId") String dbset_Id){

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId || null == dbset_Id){
			log.error(String.format("Delete databaseSet failed, caused by illegal parameters: "
					+ "[groupId=%s, dbsetId=%s]",groupId, dbset_Id));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		int dbsetID = -1;
		try{
			groupID = Integer.parseInt(groupId);
			dbsetID =  Integer.parseInt(dbset_Id);
		}catch(NumberFormatException  ex){
			log.error("Delete databaseSet failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}

		int ret1 = dbset_dao.deleteDatabaseSetEntryByDbsetId(dbsetID);
		int ret2 = dbset_dao.deleteDatabaseSetById(dbsetID);
		if(ret1<0 || ret2<0){
			log.error("Delete databaseSet failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Delete operation failed.");
			return status;
		}
		return Status.OK;
	}
	
	@POST
	@Path("addDbsetEntry")
	public Status addDbsetEntry(@FormParam("name") String name,
			@FormParam("databaseType") String databaseType,
			@FormParam("sharding") String sharding,
			@FormParam("connectionString") String connectionString,
			@FormParam("dbsetId") String dbsetId,
			@FormParam("groupId") String groupId){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == dbsetId){
			log.error(String.format("Add Dbset Entry failed, caused by illegal parameters: "
					+ "[userNo=%s, dbsetId=%s]",userNo, dbsetId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int dbsetID = -1;
		int groupID = -1;
		try{
			dbsetID = Integer.parseInt(dbsetId);
			groupID = Integer.parseInt(groupId);
		}catch(NumberFormatException  ex){
			log.error("Add Dbset Entry failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		int ret = -1;
		DatabaseSetEntry dbsetEntry = new DatabaseSetEntry();
		dbsetEntry.setName(name);
		dbsetEntry.setDatabaseType(databaseType);
		dbsetEntry.setSharding(sharding);
		dbsetEntry.setConnectionString(connectionString);
		dbsetEntry.setDatabaseSet_Id(dbsetID);
		ret = dbset_dao.insertDatabaseSetEntry(dbsetEntry);
		if(ret <= 0){
			log.error("Add databaseSet Entry failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Add operation failed.");
			return status;
		}
		
		return Status.OK;
	}
	
	@POST
	@Path("updateDbsetEntry")
	public Status updateDbsetEntry(@FormParam("id") String id,
			@FormParam("name") String name,
			@FormParam("databaseType") String databaseType,
			@FormParam("sharding") String sharding,
			@FormParam("connectionString") String connectionString,
			@FormParam("dbsetId") String dbsetId,
			@FormParam("groupId") String groupId){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == dbsetId){
			log.error(String.format("Update Dbset Entry failed, caused by illegal parameters: "
					+ "[userNo=%s, dbsetId=%s]",userNo, dbsetId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int dbsetEntyID = -1;
		int dbsetID = -1;
		int groupID = -1;
		try{
			dbsetEntyID = Integer.parseInt(id);
			dbsetID = Integer.parseInt(dbsetId);
			groupID = Integer.parseInt(groupId);
		}catch(NumberFormatException  ex){
			log.error("Update Dbset Entry failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		int ret = -1;
		DatabaseSetEntry dbsetEntry = new DatabaseSetEntry();
		dbsetEntry.setId(dbsetEntyID);
		dbsetEntry.setName(name);
		dbsetEntry.setDatabaseType(databaseType);
		dbsetEntry.setSharding(sharding);
		dbsetEntry.setConnectionString(connectionString);
		dbsetEntry.setDatabaseSet_Id(dbsetID);
		ret = dbset_dao.updateDatabaseSetEntry(dbsetEntry);
		if(ret <= 0){
			log.error("Update databaseSet Entry failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Update operation failed.");
			return status;
		}
		
		return Status.OK;
	}
	
	@POST
	@Path("deletedbsetEntry")
	public Status deleteDbsetEntry(@FormParam("groupId") String groupId,
			@FormParam("dbsetEntryId") String dbsetEntryId){

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId || null == dbsetEntryId){
			log.error(String.format("Delete databaseSet Entry failed, caused by illegal parameters: "
					+ "[groupId=%s, dbsetId=%s]",groupId, dbsetEntryId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		int dbsetEntryID = -1;
		try{
			groupID = Integer.parseInt(groupId);
			dbsetEntryID =  Integer.parseInt(dbsetEntryId);
		}catch(NumberFormatException  ex){
			log.error("Delete databaseSet Entry failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}

		int ret = dbset_dao.deleteDatabaseSetEntryById(dbsetEntryID);
		if(ret<0){
			log.error("Delete databaseSet Entry failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Delete operation failed.");
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
	
}
