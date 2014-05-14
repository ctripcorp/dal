
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

import com.ctrip.platform.dal.daogen.dao.DalGroupDBDao;
import com.ctrip.platform.dal.daogen.dao.DalGroupDao;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
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
	
	static{
		group_dao = SpringBeanGetter.getDaoOfDalGroup();
		user_dao = SpringBeanGetter.getDaoOfLoginUser();
		group_db_dao = SpringBeanGetter.getDaoOfDalGroupDB();
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


}
