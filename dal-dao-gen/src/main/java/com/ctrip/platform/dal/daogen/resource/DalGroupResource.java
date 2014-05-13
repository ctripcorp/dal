package com.ctrip.platform.dal.daogen.resource;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.jasig.cas.client.util.AssertionHolder;

import com.ctrip.platform.dal.daogen.dao.DalGroupDao;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

@Resource
@Singleton
@Path("group")
public class DalGroupResource {
	private static Logger log = Logger.getLogger(DalGroupResource.class);
	private static final int SUPER_GROUP_ID = 1; //The default supper user group
	private static DalGroupDao dal_dao = null;
	private static DaoOfLoginUser user_dao = null;
	
	static{
		dal_dao = SpringBeanGetter.getDaoOfDalGroup();
		user_dao = SpringBeanGetter.getDaoOfLoginUser();
	}
	
	@GET
	@Path("get")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DalGroup> getAllGroup(){
		List<DalGroup> groups =  dal_dao.getAllGroups();
		return groups;
	}
	
	@POST
	@Path("add")
	public Status add(@FormParam("groupName") String groupName,
			@FormParam("groupComment") String groupComment){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupName || groupName.isEmpty()){
			log.error(String.format("Add dal group failed, caused by illegal parameters: "
					+ "[groupName=%s, groupComment=%s]",groupName, groupComment));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		//TODO: How to validate the userNo has the permission or not to operate the dal_group table
		if(!this.validate(userNo)){
			Status status = Status.ERROR;
			status.setInfo("Permission deny.");
			return status;
		}
		
		DalGroup group = new DalGroup();
		group.setGroup_name(groupName);
		group.setGroup_comment(groupComment);
		group.setCreate_user_no(userNo);
		group.setCreate_time(new Timestamp(System.currentTimeMillis()));
		
		int ret = dal_dao.insertDalGroup(group);
		if(ret <= 0){
			log.error("Add dal group failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Add operation failed.");
			return status;
		}
		return Status.OK;
	}
	
	@POST
	@Path("delete")
	public Status delete(@FormParam("id") String id){

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == id || id.isEmpty()){
			log.error(String.format("Delete dal group failed, caused by illegal parameters "
					+ "[ids=%s]", id));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		if(!this.validate(userNo)){
			Status status = Status.ERROR;
			status.setInfo("Permission deny.");
			return status;
		}
		int groupId = -1;
		try{
			groupId = Integer.parseInt(id);
		}catch(NumberFormatException  ex){
			log.error("Delete dal group failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}

		int ret = dal_dao.deleteDalGroup(groupId);	
		if(ret <= 0){
			log.error("Delete dal group failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("Delete operation failed.");
			return status;
		}
		return Status.OK;
	}
	
	@POST
	@Path("update")
	public Status update(@FormParam("groupId") String id,
			@FormParam("groupName") String groupName,
			@FormParam("groupComment") String groupComment){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == id || id.isEmpty()){
			log.error(String.format("Update dal group failed, caused by illegal parameters, "
					+ "[id=%s, groupName=%s, groupComment=%s]", id, groupName, groupComment));
			Status status = Status.ERROR;
			status.setInfo("Illegal parameters.");
			return status;
		}
		
		if(!this.validate(userNo)){
			Status status = Status.ERROR;
			status.setInfo("Permission deny.");
			return status;
		}
		
		int groupId = -1;
		try{
			groupId = Integer.parseInt(id);
		}catch(NumberFormatException  ex){
			log.error("Update dal group failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return status;
		}
		
		DalGroup group = dal_dao.getDalGroupById(groupId);
		if(null == group) {
			log.error("Update dal group failed, caused by group_id specifed not existed.");
			Status status = Status.ERROR;
			status.setInfo("Group id not existed");
			return status;
		}
		if(null != groupName && !groupName.trim().isEmpty()){
			group.setGroup_name(groupName);
		}
		if(null != groupComment && !groupComment.trim().isEmpty()){
			group.setGroup_comment(groupComment);
		}
			
		group.setCreate_time(new Timestamp(System.currentTimeMillis()));
		
		int ret = dal_dao.updateDalGroup(group);

		if(ret <= 0){
			log.error("Delete dal group failed, caused by db operation failed, pls check the spring log");
			Status status = Status.ERROR;
			status.setInfo("update operation failed.");
			return status;
		}
		return Status.OK;
	}
	
	private boolean validate(String userNo){
		LoginUser user = user_dao.getUserByNo(userNo);
		return null != user && user.getGroupId() == SUPER_GROUP_ID;
	}
}
