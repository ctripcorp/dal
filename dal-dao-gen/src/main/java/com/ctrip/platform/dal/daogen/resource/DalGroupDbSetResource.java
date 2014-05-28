
package com.ctrip.platform.dal.daogen.resource;

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
import com.ctrip.platform.dal.daogen.dao.DaoOfDatabaseSet;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
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
	private static DalGroupDBDao group_db_dao = null;
	private static DaoOfDatabaseSet dbset_dao = null;
	
	static{
		group_dao = SpringBeanGetter.getDaoOfDalGroup();
		user_dao = SpringBeanGetter.getDaoOfLoginUser();
		group_db_dao = SpringBeanGetter.getDaoOfDalGroupDB();
		dbset_dao = SpringBeanGetter.getDaoOfDatabaseSet();
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
	public List<DatabaseSet> getDatabaseSetByGroupId(@QueryParam("groupId") String id) {
		int groupId = -1;
		try{
			groupId = Integer.parseInt(id);
		}catch(NumberFormatException  ex){
			log.error("get DatabaseSet failed", ex);
			return null;
		}
		List<DatabaseSet> dbset = dbset_dao.getAllDatabaseSetByGroupId(groupId);
		return dbset;
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
			status.setInfo("name:"+name+"已经存在，请重新命名!");
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
			log.error("Add database set failed, caused by db operation failed, pls check the spring log");
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
					status.setInfo("name:"+name+"已经存在，请重新命名!");
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

		int ret1 = dbset_dao.deleteDatabaseSetEntry(dbsetID);
		int ret2 = dbset_dao.deleteDatabaseSet(dbsetID);
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

		int ret = group_db_dao.deleteDalGroupDB(dbID);
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
		LoginUser user = user_dao.getUserByNo(userNo);
		if(null != user && user.getGroupId() == DalGroupResource.SUPER_GROUP_ID){
			return true;
		}
		if(null != user && user.getGroupId() == groupId){
			return true;
		}
		return false;
	}
	
	private boolean validateTransferPermision(String userNo,int dbId){
		LoginUser user = user_dao.getUserByNo(userNo);
		if(user!=null && user.getGroupId()==DalGroupResource.SUPER_GROUP_ID){
			return true;
		}
		List<DalGroupDB> groupDbs = group_db_dao.getGroupDBsByGroup(user.getGroupId());
		if(groupDbs!=null && groupDbs.size()>0){
			for(DalGroupDB db:groupDbs){
				if(db.getId()==dbId){
					return true;
				}
			}
		}
		return false;
	}


}
