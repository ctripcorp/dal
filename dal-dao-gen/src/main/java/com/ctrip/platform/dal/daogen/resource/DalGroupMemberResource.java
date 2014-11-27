
package com.ctrip.platform.dal.daogen.resource;

import java.util.Iterator;
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
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.dao.UserGroupDao;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.UserGroup;
import com.ctrip.platform.dal.daogen.entity.UserProject;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

/**
 * DAL Member Manage.
 * @author gzxia
 *
 */
@Resource
@Singleton
@Path("member")
public class DalGroupMemberResource {

	private static Logger log = Logger.getLogger(DalGroupMemberResource.class);
	
	private static DalGroupDao group_dao = null;
	private static DaoOfLoginUser user_dao = null;
	private static UserGroupDao ugDao = null;
	
	static{
		group_dao = SpringBeanGetter.getDaoOfDalGroup();
		user_dao = SpringBeanGetter.getDaoOfLoginUser();
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
	@Path("groupuser")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LoginUser> getGroupUsers(@QueryParam("groupId") String id) {
		int groupId = -1;
		try{
			groupId = Integer.parseInt(id);
		}catch(NumberFormatException  ex){
			log.error("get Group Users failed", ex);
			return null;
		}
		List<LoginUser> users = user_dao.getUserByGroupId(groupId);
		Iterator<LoginUser> ite = users.iterator();
		while(ite.hasNext()) {
			LoginUser user = ite.next();
			if ("1".equalsIgnoreCase(user.getRole())) {
				user.setRole("Admin");
			} else if ("2".equalsIgnoreCase(user.getRole())) {
				user.setRole("Limited");
			} else {
				user.setRole("Unkown");
			}
			if ("1".equalsIgnoreCase(user.getAdduser())) {
				user.setAdduser("允许");
			} else {
				user.setAdduser("禁止");
			}
		}
		return users;
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LoginUser> getAllUsers() {
		List<LoginUser> users = user_dao.getAllUsers();
		return users;
	}
	
	@POST
	@Path("add")
	public Status add(@FormParam("groupId") String groupId,
			@FormParam("userId") String userId,
			@FormParam("user_role") int user_role,
			@FormParam("allowAddUser") boolean allowAddUser){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId || null == userId){
			log.error(String.format("Add member failed, caused by illegal parameters: "
					+ "[groupId=%s, userId=%s]",groupId, userId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		int userID = -1;
		try{
			groupID = Integer.parseInt(groupId);
			userID =  Integer.parseInt(userId);
		}catch(NumberFormatException  ex){
			log.error("Add member failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		
		LoginUser user = user_dao.getUserById(userID);
		
		List<UserGroup> ugGroups = ugDao.getUserGroupByUserId(user.getId());
		Iterator<UserGroup> ite = ugGroups.iterator();
		while(ite.hasNext()) {
			if(ite.next().getGroup_id() == groupID) {
				Status status = Status.ERROR;
				status.setInfo("用户["+user.getUserName()+"]已经加入当前DAL Team.");
				return status;
			}
		}
		int adduser = allowAddUser==true? 1:2;
		int ret = ugDao.insertUserGroup(userID, groupID, user_role, adduser);
		if(ret <= 0){
			log.error("Add dal group member failed, caused by db operation failed, pls check the log.");
			Status status = Status.ERROR;
			status.setInfo("Add operation failed.");
			return status;
		}else{
			transferProjectToGroup(user.getUserNo(), groupID);
		}
		return Status.OK;
	}
	
	@POST
	@Path("update")
	public Status update(@FormParam("groupId") String groupId,
			@FormParam("userId") String userId,
			@FormParam("user_role") int user_role,
			@FormParam("allowAddUser") boolean allowAddUser){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId || null == userId){
			log.error(String.format("Add member failed, caused by illegal parameters: "
					+ "[groupId=%s, userId=%s]",groupId, userId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		int userID = -1;
		try{
			groupID = Integer.parseInt(groupId);
			userID =  Integer.parseInt(userId);
		}catch(NumberFormatException  ex){
			log.error("Add member failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if (!this.validatePermision(userNo, groupID)) {
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}
		int adduser = allowAddUser==true? 1:2;
		int ret = ugDao.updateUserPersimion(userID, groupID, user_role, adduser);
		if(ret <= 0){
			log.error("Update dal group user failed, caused by db operation failed, pls check the log.");
			Status status = Status.ERROR;
			status.setInfo("Update operation failed.");
			return status;
		}
		return Status.OK;
	}
	
	@POST
	@Path("delete")
	public Status delete(@FormParam("groupId") String groupId,
			@FormParam("userId") String userId){

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupId || null == userId){
			log.error(String.format("Add member failed, caused by illegal parameters: "
					+ "[groupId=%s, userId=%s]",groupId, userId));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		int groupID = -1;
		int userID = -1;
		try{
			groupID = Integer.parseInt(groupId);
			userID =  Integer.parseInt(userId);
		}catch(NumberFormatException  ex){
			log.error("Add member failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		if(!this.validatePermision(userNo,groupID)){
			Status status = Status.ERROR;
			status.setInfo("你没有当前DAL Team的操作权限.");
			return status;
		}

		int ret = ugDao.deleteUserFromGroup(userID, groupID);
		if(ret <= 0){
			log.error("Delete memeber failed, caused by db operation failed, pls check the log.");
			Status status = Status.ERROR;
			status.setInfo("Delete operation failed.");
			return status;
		}
		return Status.OK;
	}

	private boolean validatePermision(String userNo,int groupId){
		LoginUser user = user_dao.getUserByNo(userNo);
		//用户加入的所有组
		List<UserGroup> urgroups = ugDao.getUserGroupByUserId(user.getId());
		if(urgroups==null){
			return false;
		}
		for(UserGroup ug : urgroups){
			if(ug.getGroup_id() == DalGroupResource.SUPER_GROUP_ID){
				return true;
			}
			if(ug.getGroup_id() == groupId && ug.getAdduser()==1){
				return true;
			}
		}
		return false;
	}
	
	
	private void transferProjectToGroup(String userNo, int groupId){
		//当前用户的所有Project
		List<UserProject> userProjects = SpringBeanGetter.getDaoOfUserProject()
				.getUserProjectsByUser(userNo);
		for(UserProject proj : userProjects){
			int project_id = proj.getProject_id();
			//project_id符合当前迭代的Project，且在user_project中id最小
			UserProject project = SpringBeanGetter.getDaoOfUserProject().getMinUserProjectByProjectId(project_id);
			//验证当前project是否是由当前user创建
			if(proj.getId() == project.getId()){
				//更新Project表的groupId为当前用户的gourpId
				SpringBeanGetter.getDaoOfProject().updateProjectGroupById(groupId, project_id);
				//删除user_project表中所有project_id符合当前迭代的Project
				SpringBeanGetter.getDaoOfUserProject().deleteUserProject(project_id);
			}
			
		}
	}

}










