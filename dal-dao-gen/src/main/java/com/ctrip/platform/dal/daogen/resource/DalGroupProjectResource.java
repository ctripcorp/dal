package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * DAL Project & Code 一览
 * 
 * @author gzxia
 * 
 */
@Resource
@Singleton
@Path("projectview")
public class DalGroupProjectResource {
	private static Logger log = Logger.getLogger(DalGroupProjectResource.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DalGroup> getGroups(@QueryParam("root") boolean root) {
		List<DalGroup> groups = SpringBeanGetter.getDaoOfDalGroup().getAllGroups();
		for (DalGroup group : groups) {
			group.setText(group.getGroup_name());
			group.setIcon("glyphicon glyphicon-folder-open");
			group.setChildren(true);
		}
		return groups;
	}

	@GET
	@Path("groupprojects")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Project> getGroupProjects(@QueryParam("groupId") String groupId) {
		int groupID = -1;
		try {
			groupID = Integer.parseInt(groupId);
		} catch (NumberFormatException ex) {
			log.error("Add member failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return null;
		}

		return SpringBeanGetter.getDaoOfProject().getProjectByGroupId(groupID);
	}

}
