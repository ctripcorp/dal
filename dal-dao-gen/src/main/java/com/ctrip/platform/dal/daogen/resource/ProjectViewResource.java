
package com.ctrip.platform.dal.daogen.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.jasig.cas.client.util.AssertionHolder;

import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.entity.UserProject;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

/**
 * DAL Project & Code 一览
 * @author gzxia
 *
 */
@Resource
@Singleton
@Path("projectview")
public class ProjectViewResource {

	private static Logger log = Logger.getLogger(ProjectViewResource.class);
	
	@GET
	@Path("users")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LoginUser> getUsers() {
		return SpringBeanGetter.getDaoOfLoginUser().getAllUsers();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Project> getProjects(@QueryParam("root") boolean root) {
		// return projectDao.getAllProjects();
		
		if(root){
			List<Project> roots = new ArrayList<Project>();
			Project p = new Project();
			p.setId(-1);
			p.setName("所有项目");
			p.setText("所有项目");
			p.setNamespace("com.ctrip.platform");
			p.setIcon("fa fa-folder-o");
			p.setChildren(true);
			roots.add(p);
			return roots;
		}

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();

		if (SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo) == null) {
			LoginUser user = new LoginUser();
			user.setUserNo(userNo);
			user.setUserName(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("sn").toString());
			user.setUserEmail(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("mail").toString());
			SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
		}

		List<UserProject> projects = SpringBeanGetter.getDaoOfUserProject()
				.getUserProjectsByUser(userNo);

		List<Integer> ids = new ArrayList<Integer>();

		for (UserProject u : projects) {
			ids.add(u.getProject_id());
		}

		if (ids.size() > 0)
			return SpringBeanGetter.getDaoOfProject().getProjectByIDS(
					ids.toArray());
		else
			return new ArrayList<Project>();

	}

	@GET
	@Path("project")
	@Produces(MediaType.APPLICATION_JSON)
	public Project getProject(@QueryParam("id") String id) {
		return SpringBeanGetter.getDaoOfProject().getProjectByID(
				Integer.valueOf(id));
	}


}
