package com.ctrip.platform.dal.daogen.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jasig.cas.client.util.AssertionHolder;

import com.ctrip.platform.dal.daogen.cs.CSharpGenerator;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.dao.DaoOfUserProject;
import com.ctrip.platform.dal.daogen.java.JavaGenerator;
import com.ctrip.platform.dal.daogen.pojo.LoginUser;
import com.ctrip.platform.dal.daogen.pojo.Project;
import com.ctrip.platform.dal.daogen.pojo.Status;
import com.ctrip.platform.dal.daogen.pojo.UserProject;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

/**
 * The schema of {daogen.project} { "name": "InternationalFightEntine",
 * "namespace": "com.ctrip.flight.intl.engine" }
 * 
 * @author gawu
 * 
 */
@Resource
@Singleton
@Path("project")
public class ProjectResource {

	private static DaoOfProject projectDao;

	private static DaoOfLoginUser daoOfLoginUser;

	private static DaoOfUserProject daoOfUserProject;

	static {
		projectDao = SpringBeanGetter.getDaoOfProject();
		daoOfLoginUser = SpringBeanGetter.getDaoOfLoginUser();
		daoOfUserProject = SpringBeanGetter.getDaoOfUserProject();
	}

	@GET
	@Path("users")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LoginUser> getUsers() {
		return daoOfLoginUser.getAllUsers();
	}

	@POST
	@Path("share_proj")
	@Produces(MediaType.APPLICATION_JSON)
	public Status shareProject(@FormParam("id") int id,
			@FormParam("userNo") String userNo) {
		UserProject userProject = daoOfUserProject.getUserProject(id, userNo);
		if (null == userProject) {
			UserProject project = new UserProject();
			project.setProject_id(id);
			project.setUserNo(userNo);
			daoOfUserProject.insertUserProject(project);
			return Status.OK;
		} else {
			return Status.ERROR;
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Project> getProjects() {
		// return projectDao.getAllProjects();

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		if (daoOfLoginUser.getUserByNo(userNo) == null) {
			LoginUser user = new LoginUser();
			user.setUserNo(userNo);
			user.setUserName(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("sn").toString());
			user.setUserEmail(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("mail").toString());
			daoOfLoginUser.insertUser(user);
		}
		
		List<UserProject> projects = daoOfUserProject
				.getUserProjectsByUser(userNo);

		List<Integer> ids = new ArrayList<Integer>();

		for (UserProject u : projects) {
			ids.add(u.getProject_id());
		}

		if (ids.size() > 0)
			return projectDao.getProjectByIDS(ids.toArray());
		else
			return new ArrayList<Project>();

	}

	@GET
	@Path("project")
	@Produces(MediaType.APPLICATION_JSON)
	public Project getProject(@QueryParam("id") String id) {

		return projectDao.getProjectByID(Integer.valueOf(id));

	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addProject(@FormParam("id") int id,
			@FormParam("name") String name,
			@FormParam("namespace") String namespace,
			@FormParam("action") String action) {

		Project proj = new Project();

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();

		

		if (action.equals("insert")) {
			proj.setName(name);
			proj.setNamespace(namespace);
			int pk = projectDao.insertProject(proj);

			shareProject(pk, userNo);

		} else if (action.equals("update")) {
			proj.setId(id);
			proj.setName(name);
			proj.setNamespace(namespace);
			projectDao.updateProject(proj);

			shareProject(id, userNo);
		} else if (action.equals("delete")) {
			proj.setId(Integer.valueOf(id));
			if (projectDao.deleteProject(proj) > 0) {
				daoOfUserProject.deleteUserProject(id, userNo);
			}
		}
		return Status.OK;

	}

	@POST
	@Path("generate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status generateProject(@FormParam("project_id") String id,
			@FormParam("language") String language) {

		if (language.equals("java"))
			JavaGenerator.getInstance().generateCode(Integer.valueOf(id));
		else if (language.equals("csharp"))
			CSharpGenerator.getInstance().generateCode(Integer.valueOf(id));
		else if (language.equals("python"))
			;

		return Status.OK;
	}

}
