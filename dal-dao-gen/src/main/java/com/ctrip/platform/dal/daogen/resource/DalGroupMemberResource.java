
package com.ctrip.platform.dal.daogen.resource;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.dao.DalGroupDao;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
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
	
	private static DalGroupDao dal_dao = null;
	private static DaoOfLoginUser user_dao = null;
	
	static{
		dal_dao = SpringBeanGetter.getDaoOfDalGroup();
		user_dao = SpringBeanGetter.getDaoOfLoginUser();
	}
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DalGroup> getProjects(@QueryParam("root") boolean root) {

		List<DalGroup> groups =  dal_dao.getAllGroups();
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



}
