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
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jasig.cas.client.util.AssertionHolder;

import ch.qos.logback.core.status.Status;

import com.ctrip.platform.dal.daogen.dao.DalGroupDao;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
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
	public Response getAllGroup(){
		List<DalGroup> groups =  dal_dao.getAllGroups();
		return Response.ok(groups, MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(@FormParam("groupName") String groupName,
			@FormParam("groupComment") String groupComment){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == groupName || groupName.isEmpty()){
			log.error(String.format("Add dal group failed, caused by illegal parameters: "
					+ "[groupName=%s, groupComment=%s]",groupName, groupComment));
			return Response.status(Status.ERROR)
					.entity("Illegal parameters.").build();
		}
		
		//TODO: How to validate the userNo has the permission or not to operate the dal_group table
		if(!this.validate(userNo))
			return Response.status(Status.ERROR)
					.entity("Permission deny.").build();
		
		DalGroup group = new DalGroup();
		group.setGroupName(groupName);
		group.setGroupComment(groupComment);
		group.setCreateUserNo(userNo);
		group.setCteateTime(new Timestamp(System.currentTimeMillis()));
		
		int ret = dal_dao.insertDalGroup(group);
		if(ret > 0){
			log.error("Add dal group failed, caused by db operation failed, pls check the spring log");
			return Response.status(Status.ERROR)
					.entity("Add operation failed.").build();
		}
		return Response.ok("Success").build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("delete")
	public Response delete(@FormParam("id") String id){

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == id || id.isEmpty()){
			log.error(String.format("Delete dal group failed, caused by illegal parameters "
					+ "[ids=%s]", id));
			return Response.status(Status.ERROR)
					.entity("Illegal parameters.").build();
		}
		
		if(!this.validate(userNo))
			return Response.status(Status.ERROR)
					.entity("Permission deny.").build();
		int groupId = -1;
		try{
			groupId = Integer.parseInt(id);
		}catch(NumberFormatException  ex){
			log.error("Delete dal group failed", ex);
			return Response.status(Status.ERROR)
					.entity("Illegal group id").build();
		}

		int ret = dal_dao.deleteDalGroup(groupId);	
		if(ret > 0){
			log.error("Delete dal group failed, caused by db operation failed, pls check the spring log");
			return Response.status(Status.ERROR)
					.entity("Add operation failed.").build();
		}
		return Response.ok("Success").build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("update")
	public Response update(@FormParam("groupId") String id,
			@FormParam("groupName") String groupName,
			@FormParam("groupComment") String groupComment){
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if(null == userNo || null == id || id.isEmpty()){
			log.error(String.format("Update dal group failed, caused by illegal parameters, "
					+ "[id=%s, groupName=%s, groupComment=%s]", id, groupName, groupComment));
			return Response.status(Status.ERROR)
					.entity("Illegal parameters.").build();
		}
		
		if(!this.validate(userNo))
			return Response.status(Status.ERROR)
					.entity("Permission deny.").build();
		
		int groupId = -1;
		try{
			groupId = Integer.parseInt(id);
		}catch(NumberFormatException  ex){
			log.error("Update dal group failed", ex);
			return Response.status(Status.ERROR)
					.entity("Illegal group id").build();
		}
		
		DalGroup group = dal_dao.getDalGroupById(groupId);
		if(null == group) {
			log.error("Update dal group failed, caused by group_id specifed not existed.");
			return Response.status(Status.ERROR)
					.entity("Group id not existed").build();
		}
		if(null != groupName && !groupName.trim().isEmpty())
			group.setGroupName(groupName);
		if(null != groupComment && !groupComment.trim().isEmpty())
			group.setGroupComment(groupComment);
		group.setCteateTime(new Timestamp(System.currentTimeMillis()));
		
		int ret = dal_dao.updateDalGroup(group);

		if(ret > 0){
			log.error("Delete dal group failed, caused by db operation failed, pls check the spring log");
			return Response.status(Status.ERROR)
					.entity("Delete operation failed.").build();
		}
		return Response.ok("Success").build();
	}
	
	private boolean validate(String userNo){
		LoginUser user = user_dao.getUserByNo(userNo);
		return null != user && user.getGroupId() == SUPER_GROUP_ID;
	}
}
