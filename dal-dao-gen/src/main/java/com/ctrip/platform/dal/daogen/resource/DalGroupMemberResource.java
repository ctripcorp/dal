
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

import com.ctrip.platform.dal.daogen.dao.DalGroupDao;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
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
	
	static{
		group_dao = SpringBeanGetter.getDaoOfDalGroup();
		user_dao = SpringBeanGetter.getDaoOfLoginUser();
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
			status.setInfo("Permission deny.");
			return status;
		}
		
		LoginUser user = user_dao.getUserById(userID);
		if(null != user && user.getGroupId() > 0){
			DalGroup group = group_dao.getDalGroupById(user.getGroupId());
			Status status = Status.ERROR;
			status.setInfo(user.getUserName()+" is already added in "+group.getGroup_comment());
			return status;
		}
		
		int ret = user_dao.updateUserGroup(userID, groupID);
		if(ret <= 0){
			log.error("Add dal group member failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Add operation failed.");
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
			status.setInfo("Permission deny.");
			return status;
		}

		int ret = user_dao.updateUserGroup(userID, null);
		if(ret <= 0){
			log.error("Delete memeber failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Delete operation failed.");
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

}
