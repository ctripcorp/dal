package com.ctrip.platform.dal.daogen.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.dao.DalGroupDao;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.dao.UserGroupDao;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.entity.UserGroup;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpDalGenerator;
import com.ctrip.platform.dal.daogen.generator.java.JavaDalGenerator;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

@Resource
@Singleton
@Path("project")
public class ProjectResource {

	private static Logger log = Logger.getLogger(ProjectResource.class);
	
	private static DalGroupDao group_dao = null;
	private static DaoOfProject project = null;	
	private static UserGroupDao ugDao = null;
	
	static{
		group_dao = SpringBeanGetter.getDaoOfDalGroup();
		project = SpringBeanGetter.getDaoOfProject();
		ugDao = SpringBeanGetter.getDalUserGroupDao();
	}
	
	@GET
	@Path("users")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LoginUser> getUsers() {
		return SpringBeanGetter.getDaoOfLoginUser().getAllUsers();
	}
	
	@GET
	@Path("userGroups")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DalGroup> getUserGroups(@QueryParam("root") boolean root) {
		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		LoginUser user = null;
		try {
			user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		} catch (Exception e) {
			log.warn("",e);
		}
		if(user == null) {
			user = new LoginUser();
			user.setUserNo(userNo);
			user.setUserName(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("sn").toString());
			user.setUserEmail(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("mail").toString());
			try {
				SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
				user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
			} catch (Exception e) {
				log.warn("",e);
			}
		}
		List<DalGroup> groups = new ArrayList<DalGroup>();
		List<UserGroup> urGroups = ugDao.getUserGroupByUserId(user.getId());
		if(urGroups!=null && urGroups.size()>1) {
			for(UserGroup urgroup : urGroups) {
				DalGroup dalGroup = group_dao.getDalGroupById(urgroup.getGroup_id());
				dalGroup.setText(dalGroup.getGroup_name());
				dalGroup.setIcon("fa fa-folder-o");
				dalGroup.setChildren(true);
				groups.add(dalGroup);
			}
		} else {
			DalGroup group = new DalGroup();
			group.setText("请先加入DAL Team");
			group.setIcon("fa fa-folder-o");
			group.setChildren(true);
			groups.add(group);
		}
		return groups;
	}
	
	@GET
	@Path("groupprojects")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Project> getGroupProjects(@QueryParam("groupId") String groupId) {

		int groupID = -1;
		try{
			groupID = Integer.parseInt(groupId);
		}catch(NumberFormatException  ex){
			log.error("Add member failed", ex);
			Status status = Status.ERROR;
			status.setInfo("Illegal group id");
			return null;
		}
		
		return project.getProjectByGroupId(groupID);

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
			@FormParam("action") String action,
			@FormParam("project_group_id") int project_group_id) {

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
				status.setInfo("Dal.config Name --> "+dalconfigname+" 已经存在，请重新命名!");
				return status;
			}
			proj.setName(name);
			proj.setNamespace(namespace);
			proj.setDal_config_name(dalconfigname);
			proj.setDal_group_id(project_group_id);
			SpringBeanGetter.getDaoOfProject().insertProject(proj);
		} else if (action.equals("update")) {
			List<Project>  pjs = SpringBeanGetter.getDaoOfProject().getProjectByConfigname(dalconfigname);
			if(null != pjs && pjs.size() > 0){
				for(Project temp:pjs){
					if(temp.getId()!=id){
						Status status = Status.ERROR;
						status.setInfo("Dal.config Name --> "+dalconfigname+" 已经存在，请重新命名!");
						return status;
					}
				}
			}
			
			proj.setId(id);
			proj.setName(name);
			proj.setNamespace(namespace);
			proj.setDal_config_name(dalconfigname);
			SpringBeanGetter.getDaoOfProject().updateProject(proj);

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

	/**
	 * 一键添加project缺失的databaseSet
	 * @param projId
	 * @return
	 */
	@POST
	@Path("addLackDbset")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addLackDbset(@FormParam("project_id") int project_id) {
		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		int groupId = user.getGroupId();
		String info = addLackDb(project_id);
		
		Set<String> notExistDbset = getLackDbset(groupId,project_id);
		for(String dbsetName:notExistDbset){
			List<DatabaseSet> dbsets = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbsetName);
			if(null != dbsets && dbsets.size() > 0){
				info += "<span style='color:red;'>databaseSet Name --> "+dbsetName+" 已经存在，请重新命名，再手动添加!"+"</span><br/>";
			}else{
				List<String> dbAllinOneNames = SpringBeanGetter.getDaoOfDalGroupDB().getAllDbAllinOneNames();
				Set<String> allInOneDbnames = new HashSet<String>(dbAllinOneNames);
				if(allInOneDbnames.contains(dbsetName)){
					info+=genDefaultDbset(groupId,dbsetName);
				}else{
					info+="<span style='color:red;'>databaseSet Name --> "+dbsetName+"在数据库中不存在，请手动添加!"+"</span><br/>";
				}
			}
		}
		if(!"".equals(info)){
			info+="点击此处添加databaseSet ： <a href='dbsetsmanage.jsp' target='_blank'>逻辑数据库管理</a><br/>";
			info+="点击此处添加组内database ： <a href='dbmanage.jsp' target='_blank'>数据库管理</a><br/>";
			Status status = Status.ERROR;
			status.setInfo(info);
			return status;
		}else{
			Status status = Status.OK;
			status.setInfo("一键补全成功！");
			return status;
		}
	}
	
	/**
	 * 生成默认的databaseSet和databaseSet Entry
	 * @param dbname
	 */
	private String genDefaultDbset(int groupId,String dbname){
		DatabaseSet dbset = new DatabaseSet();
		dbset.setName(dbname);
		dbset.setProvider("sqlProvider");
		dbset.setGroupId(groupId);
		String info="";
		int ret = SpringBeanGetter.getDaoOfDatabaseSet().insertDatabaseSet(dbset);
		if(ret>0){
			info+="databaseSet-->"+dbname+"一键补全成功!<br/>";
			dbset = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbname).get(0);
			
			DatabaseSetEntry entry = new DatabaseSetEntry();
			entry.setDatabaseSet_Id(dbset.getId());
			entry.setDatabaseType("Master");
			entry.setName(dbname);
			entry.setConnectionString(dbname);
			
			SpringBeanGetter.getDaoOfDatabaseSet().insertDatabaseSetEntry(entry);
		}
		return info;
	}
	
	private String addLackDb(int project_id){
		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		int groupId = user.getGroupId();
		Set<String> notExistDb = getLackDatabase(groupId, project_id);
		List<String> dbAllinOneNames = SpringBeanGetter.getDaoOfDalGroupDB().getAllDbAllinOneNames();
		Set<String> allInOneDbnames = new HashSet<String>(dbAllinOneNames);
		
		String info="";
		for(String dbname:notExistDb){
			DalGroupDB groupdb = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBByDbName(dbname);
			if(null != groupdb && groupdb.getDal_group_id() > 0){
				DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupdb.getDal_group_id());
				info += "<span style='color:red;'>数据库"+groupdb.getDbname()+" 已经加入 "+group.getGroup_comment()+"，一键补全失败</span><br/>";
			}else if(allInOneDbnames.contains(dbname)){
				DalGroupDB groupDb = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBByDbName(dbname);
				int ret = SpringBeanGetter.getDaoOfDalGroupDB().updateGroupDB(groupDb.getId(), groupId);
				if(ret <= 0){
					info+="<span style='color:red;'>数据库"+dbname+"一键补全失败，请手动添加。"+"</span><br/>";
				}else{
					info+="数据库"+dbname+"一键补全成功。"+"<br/>";
				}
			}else{
				info+="<span style='color:red;'>数据库"+dbname+"一键补全失败，请手动添加。"+"</span><br/>";
			}
		}
		return info;
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
		
		status = validatePermision(userNo,id);
		if(status.getCode().equals(Status.ERROR.getCode())){
			progress.setStatus(ProgressResource.FINISH);
			return status;
		}
		
		try {
			log.info(String.format("begain generate project: [id=%s; regen=%s; language=%s]",
					id, true, language));
			DalGenerator generator = null;
			CodeGenContext context = null;
			if (language.equals("java")) {
				generator = new JavaDalGenerator();
				context = generator.createContext(id, true, progress, newPojo);
			} else if (language.equals("cs")){
				generator = new CSharpDalGenerator();
				context = generator.createContext(id, true, progress, newPojo);
			}
			generator.prepareDirectory(context);
			generator.prepareData(context);
			generator.generateCode(context);
			status = Status.OK;
			log.info(String.format("generate project[%s] completed.", id));
		} catch (Exception e) {
			status = Status.ERROR;
			status.setInfo(e.getMessage());
			progress.setOtherMessage(e.getMessage());
			log.error(String.format("generate project[%s] failed.", id), e);
		} finally{
			progress.setStatus(ProgressResource.FINISH);
		}
		
		return status;
	}
	
	private Status validatePermision(String userNo,int project_id){
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
		
		String info = "";
		//验证project的task所需要的databaseSet在组内是否存在
		status = validateDbsetPermision(groupId,project_id);
		if(status.getCode().equals(Status.ERROR.getCode())){
			info = status.getInfo();
		}
		//验证project的task所需要的database在组内是否存在
		status = validateDbPermision(groupId,project_id);
		if(status.getCode().equals(Status.ERROR.getCode())){
			info += "</br>" + status.getInfo();
		}
		if(!"".equals(info)){
			status = Status.ERROR;
			status.setInfo(info);
			return status;
		}
		
		return Status.OK;
	}
	
	private Status validateDbsetPermision(int groupId,int project_id){
		Status status = Status.ERROR;
		
		Set<String> notExistDbset = getLackDbset(groupId,project_id);
		
		if(notExistDbset==null || notExistDbset.size()<=0){
			return Status.OK;
		}else{
			String info = "<ul>";
			for(String temp:notExistDbset){
				info+="<li>"+temp+"</li>";
			}
			info += "</ul>";
			DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupId);
			info = "你所在"+group.getGroup_name()+"中不存在以下逻辑数据库(databaseSet)：</br>"+ info 
					+"请先添加逻辑数据库(databaseSet)到你所在DAL Team!</br>"
					+"点击此处添加逻辑数据库(databaseSet) ： <a href='dbsetsmanage.jsp' target='_blank'>逻辑数据库管理</a>";
			status.setInfo(info);
			return status;
		}
	}
	
	private Set<String> getLackDbset(int groupId,int project_id){
		List<DatabaseSet> groupDbsets = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByGroupId(groupId);
		Set<String> group_dbset_names = new HashSet<String>();
		for(DatabaseSet dbset : groupDbsets){
			group_dbset_names.add(dbset.getName());
		}

		Set<String> notExistDbset = new HashSet<String>();
		List<GenTaskBySqlBuilder> autoTasks = SpringBeanGetter.getDaoBySqlBuilder().getTasksByProjectId(project_id);
		for(GenTaskBySqlBuilder task : autoTasks){
			String databaseSet_name = task.getDatabaseSetName();
			if(!group_dbset_names.contains(databaseSet_name)){
				notExistDbset.add(databaseSet_name);
			}
		}

		List<GenTaskByTableViewSp> tableViewSpTasks = SpringBeanGetter.getDaoByTableViewSp().getTasksByProjectId(project_id);
		for(GenTaskByTableViewSp task : tableViewSpTasks){
			String databaseSet_name = task.getDatabaseSetName();
			if(!group_dbset_names.contains(databaseSet_name)){
				notExistDbset.add(databaseSet_name);
			}
		}
		
		List<GenTaskByFreeSql> sqlTasks = SpringBeanGetter.getDaoByFreeSql().getTasksByProjectId(project_id);
		for(GenTaskByFreeSql task : sqlTasks){
			String databaseSet_name = task.getDatabaseSetName();
			if(!group_dbset_names.contains(databaseSet_name)){
				notExistDbset.add(databaseSet_name);
			}
		}
		
		return notExistDbset;
	}
	
	private Status validateDbPermision(int groupId,int project_id){
		Status status = Status.ERROR;
		
		Set<String> notExistDb = getLackDatabase(groupId, project_id);
		
		if(notExistDb==null || notExistDb.size()<=0){
			return Status.OK;
		}else{
			String info = "<ul>";
			for(String temp:notExistDb){
				info+="<li>"+temp+"</li>";
			}
			info += "</ul>";
			DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupId);
			info = "你所在"+group.getGroup_name()+"中不存在以下数据库(database)：</br>"+ info 
					+"请先添加数据库(database)到你所在DAL Team!</br>"
					+"点击此处添加组内数据库(database) ： <a href='dbmanage.jsp' target='_blank'>数据库管理</a>";
			status.setInfo(info);
			return status;
		}
		
	}
	
	private Set<String> getLackDatabase(int groupId,int project_id){
		List<DalGroupDB> groupDbs = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(groupId);
		Set<String> group_db_names = new HashSet<String>();
		for(DalGroupDB db : groupDbs){
			group_db_names.add(db.getDbname());
		}
		
		Set<String> allRequiredDb = getProjectAllRequireDbname(project_id);
		
		Set<String> notExistDb = new HashSet<String>();
		for(String db : allRequiredDb){
			if(!group_db_names.contains(db)){
				notExistDb.add(db);
			}
		}
		return notExistDb;
	}
	
	private Set<String> getProjectAllRequireDbname(int project_id){
		List<GenTaskBySqlBuilder> autoTasks = SpringBeanGetter.getDaoBySqlBuilder().getTasksByProjectId(project_id);

		List<GenTaskByTableViewSp> tableViewSpTasks = SpringBeanGetter.getDaoByTableViewSp().getTasksByProjectId(project_id);

		List<GenTaskByFreeSql> sqlTasks = SpringBeanGetter.getDaoByFreeSql().getTasksByProjectId(project_id);
		
		List<String> allRequireDbset = new ArrayList<String>();
		for(GenTaskBySqlBuilder temp:autoTasks){
			allRequireDbset.add(temp.getDatabaseSetName());
		}
		for(GenTaskByTableViewSp temp:tableViewSpTasks){
			allRequireDbset.add(temp.getDatabaseSetName());
		}
		for(GenTaskByFreeSql temp:sqlTasks){
			allRequireDbset.add(temp.getDatabaseSetName());
		}
		Set<String> result = new HashSet<String>();
		for(String dbsetName:allRequireDbset){
			List<DatabaseSet> dbset = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbsetName);
			if(dbset!=null && dbset.size()>0){
				List<DatabaseSetEntry> dbentrys = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetEntryByDbsetid(dbset.get(0).getId());
				for(DatabaseSetEntry entry : dbentrys){
					result.add(entry.getConnectionString());
				}
			}else{
				List<String> dbAllinOneNames = SpringBeanGetter.getDaoOfDalGroupDB().getAllDbAllinOneNames();
				Set<String> allInOneDbnames = new HashSet<String>(dbAllinOneNames);
				if(allInOneDbnames.contains(dbsetName)){
					result.add(dbsetName);
				}
			}
		}
		return result;
	}
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<Project> getProjects(@QueryParam("root") boolean root) {
//		
//		String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
//		
//		LoginUser user1 = null;
//		try {
//			user1 = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
//		} catch (Exception e) {
//			log.warn("",e);
//		}
//		String rootName = "组内所有项目";
//		if(user1!=null){
//			DalGroup dalGroup = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(user1.getGroupId());
//			List<UserGroup> urGroups = ugDao.getUserGroupByUserId(user1.getId());
//			if(dalGroup!=null){
//				rootName = dalGroup.getGroup_name() + rootName;
//			}
//		}else{
//			LoginUser user = new LoginUser();
//			user.setUserNo(userNo);
//			user.setUserName(AssertionHolder.getAssertion().getPrincipal()
//					.getAttributes().get("sn").toString());
//			user.setUserEmail(AssertionHolder.getAssertion().getPrincipal()
//					.getAttributes().get("mail").toString());
//			try {
//				SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
//			} catch (Exception e) {
//				log.warn("",e);
//			}
//		}
//		
//		if(root){
//			List<Project> roots = new ArrayList<Project>();
//			Project p = new Project();
//			p.setId(-1);
//			p.setName(rootName);
//			p.setText(rootName);
//			p.setNamespace("com.ctrip.platform");
//			p.setIcon("fa fa-folder-o");
//			p.setChildren(true);
//			roots.add(p);
//			return roots;
//		}
//
//		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
//		
//		if(user==null){
//			log.error("user "+userNo+ " is not exist in table of login_users.");
//		}
//		
//		return SpringBeanGetter.getDaoOfProject().getProjectByGroupId(user.getGroupId());
//
//	}
	
	
//	@POST
//	@Path("share_proj")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Status shareProject(@FormParam("id") int id,
//			@FormParam("userNo") String userNo) {
//		UserProject userProject = SpringBeanGetter.getDaoOfUserProject()
//				.getUserProject(id, userNo);
//		if (null == userProject) {
//			UserProject project = new UserProject();
//			project.setProject_id(id);
//			project.setUserNo(userNo);
//			SpringBeanGetter.getDaoOfUserProject().insertUserProject(project);
//			return Status.OK;
//		} else {
//			return Status.ERROR;
//		}
//	}
	
}















