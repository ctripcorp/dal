package com.ctrip.platform.dal.daogen.resource;

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

import com.ctrip.platform.dal.daogen.dao.ProjectDAO;
import com.ctrip.platform.dal.daogen.gen.JavaGenerator;
import com.ctrip.platform.dal.daogen.pojo.Project;
import com.ctrip.platform.dal.daogen.pojo.Status;
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

	private static ProjectDAO projectDao;

	static {
		projectDao = SpringBeanGetter.getProjectDao();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Project> getProjects() {
		// return projectDao.getAllProjects();
		return projectDao.getProjectsByUserID(AssertionHolder.getAssertion()
				.getPrincipal().getAttributes().get("employee").toString());
	}

	@GET
	@Path("project")
	@Produces(MediaType.APPLICATION_JSON)
	public Project getProject(@QueryParam("id") String id) {

		return projectDao.getProjectByID(Integer.valueOf(id));

	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addProject(@FormParam("id") String id,
			@FormParam("name") String name,
			@FormParam("namespace") String namespace,
			@FormParam("action") String action) {

		Project proj = new Project();

		if (action.equals("insert")) {
			proj.setUser_id(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("employee").toString());
			proj.setName(name);
			proj.setNamespace(namespace);
			projectDao.insertProject(proj);
		} else if (action.equals("update")) {
			proj.setId(Integer.valueOf(id));
			proj.setUser_id(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("employee").toString());
			proj.setName(name);
			proj.setNamespace(namespace);
			projectDao.updateProject(proj);
		} else if (action.equals("delete")) {
			proj.setId(Integer.valueOf(id));
			projectDao.deleteProject(proj);
		}
		return Status.OK;

	}

	@POST
	@Path("generate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status generateProject(@FormParam("project_id") String id,
			@FormParam("language") String language) {

		if (language.equals("java"))
			JavaGenerator.getInstance().generateCode(id);
		else if (language.equals("csharp"))
			;
		else if (language.equals("python"))
			;

		return Status.OK;
	}

}
