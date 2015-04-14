
package com.ctrip.platform.dal.daogen.resource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.ctrip.platform.dal.daogen.entity.GroupRelation;
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
		return sortGroups(groups);
	}
	
	private List<DalGroup> sortGroups(List<DalGroup> groups) {
		List<DalGroup> result = new ArrayList<DalGroup>(groups.size());
		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		LoginUser user = user_dao.getUserByNo(userNo);
		List<UserGroup> joinedGroups = ugDao.getUserGroupByUserId(user.getId());
		if (joinedGroups != null && joinedGroups.size()>0) {
			for (UserGroup joinedGroup : joinedGroups) {
				Iterator<DalGroup> ite = groups.iterator();
				while(ite.hasNext()) {
					DalGroup group = ite.next();
					if (group.getId() == joinedGroup.getGroup_id()) {
						result.add(group);
						ite.remove();
					}
				}
			}
			result.addAll(groups);
		}
		return result;
	}
	
	@GET
	@Path("getDbset")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DatabaseSet> getDatabaseSetByGroupId(@QueryParam("groupId") int groupId,
			@QueryParam("daoFlag") boolean daoFlag) {
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
			@FormParam("groupId") int groupID){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		
		if(null == userNo){
			log.error(String.format("Add Dbset failed, caused by illegal parameters:[userNo=%s]", userNo));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
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
		dbset.setUpdate_time(new Timestamp(System.currentTimeMillis()));
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		String upNo = user.getUserName()+"("+userNo+")";
		dbset.setUpdate_user_no(upNo);
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
	public Status updateDbset(@FormParam("id") int iD,
			@FormParam("name") String name,
			@FormParam("provider") String provider,
			@FormParam("shardingStrategy") String shardingStrategy,
			@FormParam("groupId") int groupID){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		
		if(null == userNo){
			log.error(String.format("Update Dbset failed, caused by illegal parameters:[userNo=%s]", userNo));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		if (!validatePermision(userNo, groupID, iD)) {
			Status status = Status.ERROR;
			status.setInfo("你只能操作你们组创建的逻辑数据库.");
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
		dbset.setUpdate_time(new Timestamp(System.currentTimeMillis()));
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		String upNo = user.getUserName()+"("+userNo+")";
		dbset.setUpdate_user_no(upNo);
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
	public Status deleteDbset(@FormParam("groupId") int groupID,
			@FormParam("dbsetId") int dbsetID){

		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		
		if (null == userNo) {
			log.error(String.format("Delete databaseSet failed, caused by illegal parameters:[userNo=%s]", userNo));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		if (!validatePermision(userNo, groupID, dbsetID)) {
			Status status = Status.ERROR;
			status.setInfo("你只能操作你们组创建的逻辑数据库.");
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
			@FormParam("dbsetId") int dbsetID,
			@FormParam("groupId") int groupID){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		
		if(null == userNo){
			log.error(String.format("Add Dbset Entry failed, caused by illegal parameters:[userNo=%s]", userNo));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		if(!this.validatePermision(userNo, groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		if (!validatePermision(userNo, groupID, dbsetID)) {
			Status status = Status.ERROR;
			status.setInfo("你只能操作你们组创建的逻辑数据库.");
			return status;
		}
		
		int ret = -1;
		DatabaseSetEntry dbsetEntry = new DatabaseSetEntry();
		dbsetEntry.setName(name);
		dbsetEntry.setDatabaseType(databaseType);
		dbsetEntry.setSharding(sharding);
		dbsetEntry.setConnectionString(connectionString);
		dbsetEntry.setDatabaseSet_Id(dbsetID);
		dbsetEntry.setUpdate_time(new Timestamp(System.currentTimeMillis()));
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		String upNo = user.getUserName()+"("+userNo+")";
		dbsetEntry.setUpdate_user_no(upNo);
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
	public Status updateDbsetEntry(@FormParam("id") int dbsetEntyID,
			@FormParam("name") String name,
			@FormParam("databaseType") String databaseType,
			@FormParam("sharding") String sharding,
			@FormParam("connectionString") String connectionString,
			@FormParam("dbsetId") int dbsetID,
			@FormParam("groupId") int groupID){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		
		if(null == userNo){
			log.error(String.format("Update Dbset Entry failed, caused by illegal parameters:[userNo=%s]", userNo));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		if (!validatePermision(userNo, groupID, dbsetID)) {
			Status status = Status.ERROR;
			status.setInfo("你只能操作你们组创建的逻辑数据库.");
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
		dbsetEntry.setUpdate_time(new Timestamp(System.currentTimeMillis()));
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		String upNo = user.getUserName()+"("+userNo+")";
		dbsetEntry.setUpdate_user_no(upNo);
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
	public Status deleteDbsetEntry(@FormParam("groupId") int groupID,
			@FormParam("dbsetEntryId") int dbsetEntryID,
			@FormParam("dbsetId") int dbsetID){

		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		
		if(null == userNo){
			log.error(String.format("Delete databaseSet Entry failed, caused by illegal parameters:[userNo=%s]", userNo));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		if (!validatePermision(userNo, groupID, dbsetID)) {
			Status status = Status.ERROR;
			status.setInfo("你只能操作你们组创建的逻辑数据库.");
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
	
	private boolean validatePermision(String userNo, int currentGroupId) {
		boolean havePermision = false;
		havePermision = validateUserPermisionInCurrentGroup(userNo, currentGroupId);
		if (havePermision) {
			return havePermision;
		}
		havePermision = validateUserPermisionInChildGroup(userNo, currentGroupId);
		return havePermision;
	}
	
	private boolean validateUserPermisionInCurrentGroup(String userNo, int currentGroupId) {
		LoginUser user = user_dao.getUserByNo(userNo);
		//用户加入的所有组
		List<UserGroup> urgroups = ugDao.getUserGroupByUserId(user.getId());
		if (urgroups==null) {
			return false;
		}
		for (UserGroup ug : urgroups) {
			if (ug.getGroup_id() == currentGroupId) {
				return true;
			}
		}
		return false;
	}
	
	private boolean validateUserPermisionInChildGroup(String userNo, int currentGroupId) {
		boolean havePermison = false;
		int userId = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
		List<GroupRelation> relations = SpringBeanGetter.getGroupRelationDao().getAllGroupRelationByCurrentGroupId(currentGroupId);
		Iterator<GroupRelation> ite = relations.iterator();
		while (ite.hasNext()) {
			GroupRelation relation = ite.next();
			//then check the user whether or not exist in this child group
			List<UserGroup> ugs = SpringBeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(relation.getChild_group_id(), userId);
			if (ugs!=null && ugs.size()>0) {
				havePermison = true;
			}
		}
		return havePermison;
	}
	
	private boolean validatePermision(String userNo, int currentGroupId, int pk_DbSetId) {
		boolean havePermision = false;
		havePermision = validateUserPermisionInCurrentGroup(userNo, currentGroupId);
		if (havePermision) {
			return havePermision;
		}
		havePermision = validateUserPermisionInChildGroup(userNo, currentGroupId, pk_DbSetId);
		return havePermision;
	}
	
	private boolean validateUserPermisionInChildGroup(String userNo, int currentGroupId, int pk_DbSetId) {
		DatabaseSet dbset = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetById(pk_DbSetId);
		if (dbset==null) {
			return false;
		}
		String updateUN = dbset.getUpdate_user_no();
		if (updateUN==null || updateUN.isEmpty()) { //the dbset have no update user info
			// check the user is or not in the current group
			int userId = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
			List<UserGroup> check = SpringBeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(currentGroupId, userId);
			if (check!=null && check.size()>0) {
				return true;
			}
			return false;
		}
		Pattern pattern = Pattern.compile(".+\\((\\w+)\\).*");
		Matcher m = pattern.matcher(updateUN);
		String upNo = "";
		if (m.find()) {
			upNo = m.group(1);
		}
		if (upNo.equalsIgnoreCase(userNo)) {
			return true;
		}
		// the owner of the current database set
		LoginUser currentDbSetUser = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(upNo);
		if (currentDbSetUser == null) {
			return false;
		}
		// the group that the owner of the current database set have been joined in
		List<UserGroup> currentDbSetUserGroup = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(currentDbSetUser.getId());
		if (currentDbSetUserGroup==null || currentDbSetUserGroup.size()<1) {
			return false;
		}
		
		//now check, the user who want to modify the dbset is or not in the same group compare with the current dbset owner
		int userId = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
		Set<Integer> childGroupIds = getChildGroupId(currentGroupId);
		Iterator<UserGroup> ite = currentDbSetUserGroup.iterator();
		while (ite.hasNext()) {
			UserGroup ug = ite.next();
			if (!childGroupIds.contains(ug.getGroup_id())) {
				continue;
			}
			List<UserGroup> exists = SpringBeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(ug.getGroup_id(), userId);
			if (exists!=null && exists.size()>0) {
				return true;
			}
		}
		
		return false;
	}
	
	private Set<Integer> getChildGroupId(int currentGroupId) {
		Set<Integer> sets = new HashSet<Integer>();
		List<GroupRelation> relations = SpringBeanGetter.getGroupRelationDao().getAllGroupRelationByCurrentGroupId(currentGroupId);
		if (relations == null) {
			return sets;
		}
		
		for (GroupRelation relation : relations) {
			sets.add(relation.getChild_group_id());
		}
		return sets;
	}
	
}
