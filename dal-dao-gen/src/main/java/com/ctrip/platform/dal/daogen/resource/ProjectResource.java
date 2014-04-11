
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
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.entity.UserProject;
import com.ctrip.platform.dal.daogen.java.JavaGenerator;
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

	@GET
	@Path("users")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LoginUser> getUsers() {
		return SpringBeanGetter.getDaoOfLoginUser().getAllUsers();
	}

	@POST
	@Path("share_proj")
	@Produces(MediaType.APPLICATION_JSON)
	public Status shareProject(@FormParam("id") int id,
			@FormParam("userNo") String userNo) {
		UserProject userProject = SpringBeanGetter.getDaoOfUserProject()
				.getUserProject(id, userNo);
		if (null == userProject) {
			UserProject project = new UserProject();
			project.setProject_id(id);
			project.setUserNo(userNo);
			SpringBeanGetter.getDaoOfUserProject().insertUserProject(project);
			return Status.OK;
		} else {
			return Status.ERROR;
		}
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
			int pk = SpringBeanGetter.getDaoOfProject().insertProject(proj);

			shareProject(pk, userNo);
		} else if (action.equals("update")) {
			proj.setId(id);
			proj.setName(name);
			proj.setNamespace(namespace);
			SpringBeanGetter.getDaoOfProject().updateProject(proj);

			shareProject(id, userNo);
		} else if (action.equals("delete")) {
			proj.setId(Integer.valueOf(id));
			if (SpringBeanGetter.getDaoOfProject().deleteProject(proj) > 0) {
				SpringBeanGetter.getDaoOfUserProject().deleteUserProject(id);
				SpringBeanGetter.getDaoByFreeSql().deleteByProjectId(id);
				SpringBeanGetter.getDaoBySqlBuilder().deleteByProjectId(id);
				SpringBeanGetter.getDaoByTableViewSp().deleteByProjectId(id);
			}
		}
		return Status.OK;

	}

	@POST
	@Path("generate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status generateProject(@FormParam("project_id") int id,
			@FormParam("regenerate") boolean regen,
			@FormParam("language") String language,
			@FormParam("random") String random) {
		Status status = null;
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		Progress progress = ProgressResource.getProgress(userNo, id,random);
		try {
			if (language.equals("java"))
			{
				JavaGenerator.getInstance().generateCode(id, regen, progress);
			}
			else if (language.equals("cs")){
				CSharpGenerator.getInstance().generateCode(id, regen, progress);
			}
			status = Status.OK;
		} catch (Exception e) {
			status = Status.ERROR;
			status.setInfo(e.getMessage());
			e.printStackTrace();
		} finally{
			progress.setStatus(ProgressResource.FINISH);
		}
		
		return status;
	}

}
