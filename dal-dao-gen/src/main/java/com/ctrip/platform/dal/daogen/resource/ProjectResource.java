
package com.ctrip.platform.dal.daogen.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.apache.log4j.Logger;
import org.jasig.cas.client.util.AssertionHolder;

import com.ctrip.platform.dal.daogen.cs.CSharpGenerator;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
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

	private static Logger log = Logger.getLogger(ProjectResource.class);
	
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
		
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		LoginUser user1 = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		String rootName = "组内所有项目";
		if(user1!=null){
			DalGroup dalGroup = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(user1.getGroupId());
			if(dalGroup!=null){
				rootName = dalGroup.getGroup_name() + rootName;
			}
		}
		
		if(root){
			List<Project> roots = new ArrayList<Project>();
			Project p = new Project();
			p.setId(-1);
			p.setName(rootName);
			p.setText(rootName);
			p.setNamespace("com.ctrip.platform");
			p.setIcon("fa fa-folder-o");
			p.setChildren(true);
			roots.add(p);
			return roots;
		}

		if (SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo) == null) {
			LoginUser user = new LoginUser();
			user.setUserNo(userNo);
			user.setUserName(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("sn").toString());
			user.setUserEmail(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("mail").toString());
			SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
		}
		
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		
		if(user==null){
			log.error("user "+userNo+ " is not exist in table of login_users.");
		}
		
		return SpringBeanGetter.getDaoOfProject().getProjectByGroupId(user.getGroupId());

		/*List<UserProject> projects = SpringBeanGetter.getDaoOfUserProject()
				.getUserProjectsByUser(userNo);

		List<Integer> ids = new ArrayList<Integer>();

		for (UserProject u : projects) {
			ids.add(u.getProject_id());
		}

		if (ids.size() > 0)
			return SpringBeanGetter.getDaoOfProject().getProjectByIDS(
					ids.toArray());
		else
			return new ArrayList<Project>();*/

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
			@FormParam("dalconfigname") String dalconfigname,
			@FormParam("action") String action) {

		Project proj = new Project();

		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		
		if(user==null){
			Status status = Status.ERROR;
			status.setInfo("You have not login.");
			return status;
		}
		
		if(user.getGroupId()<=0){
			Status status = Status.ERROR;
			status.setInfo("请先加入某个DAL Team.");
			return status;
		}

		if (action.equals("insert")) {
			List<Project>  pjs = SpringBeanGetter.getDaoOfProject().getProjectByConfigname(dalconfigname);
			if(null != pjs && pjs.size() > 0){
				Status status = Status.ERROR;
				status.setInfo("Dal.config Name --> "+name+" 已经存在，请重新命名!");
				return status;
			}
			proj.setName(name);
			proj.setNamespace(namespace);
			proj.setDal_config_name(dalconfigname);
			proj.setDal_group_id(user.getGroupId());
			SpringBeanGetter.getDaoOfProject().insertProject(proj);
//			int pk = SpringBeanGetter.getDaoOfProject().insertProject(proj);
//			shareProject(pk, userNo);
		} else if (action.equals("update")) {
			List<Project>  pjs = SpringBeanGetter.getDaoOfProject().getProjectByConfigname(dalconfigname);
			if(null != pjs && pjs.size() > 0){
				for(Project temp:pjs){
					if(temp.getId()!=id){
						Status status = Status.ERROR;
						status.setInfo("Dal.config Name --> "+name+" 已经存在，请重新命名!");
						return status;
					}
				}
			}
			
			proj.setId(id);
			proj.setName(name);
			proj.setNamespace(namespace);
			proj.setDal_config_name(dalconfigname);
			SpringBeanGetter.getDaoOfProject().updateProject(proj);

//			shareProject(id, userNo);
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
			@FormParam("newPojo") boolean newPojo,
			@FormParam("random") String random) {
		Status status = null;
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		Progress progress = ProgressResource.getProgress(userNo, id,random);
		status = validateDbsetPermision(userNo,id);
		if(status.getCode().equals(Status.ERROR.getCode())){
			progress.setStatus(ProgressResource.FINISH);
			return status;
		}
		
		try {
			log.info(String.format("begain generate project: [id=%s; regen=%s; language=%s]",
					id, regen, language));
			if (language.equals("java"))
			{
				new JavaGenerator().generate(id, regen, progress, null);
			}
			else if (language.equals("cs")){
				Map<String, Boolean> hints = new HashMap<String, Boolean>();
				hints.put("newPojo", newPojo);
				new CSharpGenerator().generate(id, regen, progress, hints);
			}
			status = Status.OK;
			log.info(String.format("generate project[%s] completed.", id));
		} catch (Exception e) {
			status = Status.ERROR;
			status.setInfo(e.getMessage());
			log.error(String.format("generate project[%s] failed.", id), e);
		} finally{
			progress.setStatus(ProgressResource.FINISH);
		}
		
		return status;
	}
	
	private Status validateDbsetPermision(String userNo,int project_id){
		Status status = Status.ERROR;
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		int groupId = -1;
		if(user!=null){
			groupId = user.getGroupId();
			if(groupId <=0){
				status.setInfo("你没有权限生成代码.请先加入一个DAL Team.");
				return status;
			}
		}else{
			status.setInfo(userNo + "is not exist in system.");
			return status;
		}
		
		List<DatabaseSet> groupDbsets = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByGroupId(groupId);
		Set<String> group_dbset_names = new HashSet<String>();
		for(DatabaseSet dbset : groupDbsets){
			group_dbset_names.add(dbset.getName());
		}

		boolean flag = true;
		status.setInfo("");
		Set<String> notExistDbset = new HashSet<String>();
		List<GenTaskBySqlBuilder> autoTasks = SpringBeanGetter.getDaoBySqlBuilder().getTasksByProjectId(project_id);
		for(GenTaskBySqlBuilder task : autoTasks){
			String databaseSet_name = task.getDb_name();
			if(!group_dbset_names.contains(databaseSet_name)){
				notExistDbset.add(databaseSet_name);
				flag = false;
			}
		}

		List<GenTaskByTableViewSp> tableViewSpTasks = SpringBeanGetter.getDaoByTableViewSp().getTasksByProjectId(project_id);
		for(GenTaskByTableViewSp task : tableViewSpTasks){
			String databaseSet_name = task.getDb_name();
			if(!group_dbset_names.contains(databaseSet_name)){
				notExistDbset.add(databaseSet_name);
				flag = false;
			}
		}
		
		List<GenTaskByFreeSql> sqlTasks = SpringBeanGetter.getDaoByFreeSql().getTasksByProjectId(project_id);
		for(GenTaskByFreeSql task : sqlTasks){
			String databaseSet_name = task.getDb_name();
			if(!group_dbset_names.contains(databaseSet_name)){
				notExistDbset.add(databaseSet_name);
				flag = false;
			}
		}
		
		if(flag){
			return Status.OK;
		}else{
			String info = "<ul>";
			for(String temp:notExistDbset){
				info+="<li>"+temp+"</li>";
			}
			info += "</ul>";
			DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupId);
			info = "你所在DAL Team-->"+group.getGroup_name()+"中不存在以下databaseSet：</br>"+ info 
					+"请先添加databaseSet到所在Group!</br>"
					+"点击此处添加databaseSet ： <a href='dbsetsmanage.jsp'>组内databaseSet管理</a>";
			status.setInfo(info);
			return status;
		}
	}
	
	/*private Status validateDBPermision(String userNo,int project_id){
		Status status = Status.ERROR;
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		int groupId = -1;
		if(user!=null){
			groupId = user.getGroupId();
			if(groupId <=0){
				status.setInfo("你没有权限生成代码.请先加入一个DAL Team.");
				return status;
			}
		}else{
			status.setInfo(userNo + "is not exist in system.");
			return status;
		}
		
		List<DalGroupDB> groupDBs = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(groupId);
		Set<String> group_db_names = new HashSet<String>();
		for(DalGroupDB groupDB : groupDBs){
			group_db_names.add(groupDB.getDbname());
		}

		boolean flag = true;
		status.setInfo("");
		Set<String> notExistDB = new HashSet<String>();
		List<GenTaskBySqlBuilder> autoTasks = SpringBeanGetter.getDaoBySqlBuilder().getTasksByProjectId(project_id);
		for(GenTaskBySqlBuilder task : autoTasks){
			String db_name = task.getDb_name();
			if(!group_db_names.contains(db_name)){
				notExistDB.add(db_name);
				flag = false;
			}
		}

		List<GenTaskByTableViewSp> tableViewSpTasks = SpringBeanGetter.getDaoByTableViewSp().getTasksByProjectId(project_id);
		for(GenTaskByTableViewSp task : tableViewSpTasks){
			String db_name = task.getDb_name();
			if(!group_db_names.contains(db_name)){
				notExistDB.add(db_name);
				flag = false;
			}
		}
		
		List<GenTaskByFreeSql> sqlTasks = SpringBeanGetter.getDaoByFreeSql().getTasksByProjectId(project_id);
		for(GenTaskByFreeSql task : sqlTasks){
			String db_name = task.getDb_name();
			if(!group_db_names.contains(db_name)){
				notExistDB.add(db_name);
				flag = false;
			}
		}
		
		if(flag){
			return Status.OK;
		}else{
			String info = "<ul>";
			for(String temp:notExistDB){
				info+="<li>"+temp+"</li>";
			}
			info += "</ul>";
			DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupId);
			info = "在DAL Team Group："+group.getGroup_name()+"中不存在以下数据库：</br>"+ info +"<br/>请先添加DB到所在Group!</br>"
					+"点击此处添加组内数据库 ： <a href='dbmanage.jsp'>组内All In One数据库管理</a>";
			status.setInfo(info);
			return status;
		}
	}*/

}















