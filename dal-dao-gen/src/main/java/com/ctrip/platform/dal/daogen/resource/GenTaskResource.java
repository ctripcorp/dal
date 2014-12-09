package com.ctrip.platform.dal.daogen.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.jasig.cas.client.util.AssertionHolder;

import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.domain.FreeSqlClassPojoNames;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.domain.TaskAggeragation;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

@Resource
@Singleton
@Path("task")
public class GenTaskResource {

	private static DaoBySqlBuilder autoTask;

	private static DaoByFreeSql sqlTask;

	private static DaoByTableViewSp daoByTableViewSp;

	static {
		autoTask = SpringBeanGetter.getDaoBySqlBuilder();
		sqlTask = SpringBeanGetter.getDaoByFreeSql();
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TaskAggeragation getTasks(@QueryParam("project_id") int id) {

		List<GenTaskBySqlBuilder> autoTasks = autoTask.getTasksByProjectId(Integer.valueOf(id));

		List<GenTaskByTableViewSp> tableViewSpTasks = daoByTableViewSp.getTasksByProjectId(id);

		List<GenTaskByFreeSql> sqlTasks = sqlTask.getTasksByProjectId(Integer.valueOf(id));

		TaskAggeragation allTasks = new TaskAggeragation();
		
		java.util.Collections.sort(autoTasks);
		java.util.Collections.sort(tableViewSpTasks);
		java.util.Collections.sort(sqlTasks);

		allTasks.setAutoTasks(autoTasks);
		allTasks.setTableViewSpTasks(tableViewSpTasks);
		allTasks.setSqlTasks(sqlTasks);

		return allTasks;
	}

	@GET
	@Path("sql_class")
	@Produces(MediaType.APPLICATION_JSON)
	public FreeSqlClassPojoNames getClassPojoNames(
			@QueryParam("project_id") int id,
			@QueryParam("db_name") String db_name) {
		List<GenTaskByFreeSql> sqlTasks = sqlTask.getTasksByProjectId(Integer.valueOf(id));

		FreeSqlClassPojoNames result = new FreeSqlClassPojoNames();

		Set<String> clazz = new HashSet<String>();
		Set<String> pojos = new HashSet<String>();

		for (GenTaskByFreeSql freesql : sqlTasks) {
			if (freesql.getDatabaseSetName().trim().equals(db_name.trim())) {
				clazz.add(freesql.getClass_name());
				pojos.add(freesql.getPojo_name());
			}
		}

		result.setClasses(clazz);
		result.setPojos(pojos);
		return result;
	}
	
	@POST
	@Path("checkDaoNameConflict")
	@Produces(MediaType.APPLICATION_JSON)
	public Status checkDaoNameConflict(@FormParam("project_id") int project_id,
			@FormParam("db_set_name") String db_set_name,
			@FormParam("daoName") String daoName,
			@FormParam("is_update") String is_update,
			@FormParam("dao_id") int dao_id,
			@FormParam("prefix") String prefix,
			@FormParam("suffix") String suffix){
		
		Status status = Status.ERROR;
		
		daoName = daoName.replaceAll("_", "");
		
		List<GenTaskByTableViewSp> tableViewSpTasks = daoByTableViewSp.getTasksByProjectId(project_id);
		
		List<GenTaskBySqlBuilder> autoTasks = autoTask.getTasksByProjectId(Integer.valueOf(project_id));

		List<GenTaskByFreeSql> sqlTasks = sqlTask.getTasksByProjectId(Integer.valueOf(project_id));
		
		//在同一个project中，不同数据库下面不能存在相同的表名或者Dao类名
		if(tableViewSpTasks!=null && tableViewSpTasks.size()>0){
			for(GenTaskByTableViewSp task:tableViewSpTasks){
				if("1".equalsIgnoreCase(is_update) && task.getId() == dao_id)//修改操作，过滤掉修改的当前记录
					continue;
				String []daoClassName = daoName.split(",");
				for(String name : daoClassName){
					if(name.indexOf(prefix) == 0)
						name = name.replaceFirst(prefix, "");
					name = name + suffix;
					String []existTableName = task.getTable_names().replaceAll("_", "").split(",");
					for(String tableName : existTableName) {
						if(tableName.indexOf(task.getPrefix()) == 0)
							tableName = tableName.replaceFirst(task.getPrefix(), "");
						String existDaoName = tableName + task.getSuffix();
						if (existDaoName.equalsIgnoreCase(name) && 
								!task.getDatabaseSetName().equalsIgnoreCase(db_set_name)) {
							status.setInfo("在同一个project中，不同数据库下面不能定义相同的表名或者DAO类名.<br/>"
									+"逻辑数据库"+task.getDatabaseSetName()+"下已经存在名为"+name+"的DAO.");
								return status;
						}
					}
				}
			}
		}
		
		if(autoTasks!=null && autoTasks.size()>0){
			for(GenTaskBySqlBuilder task:autoTasks){
				if("1".equalsIgnoreCase(is_update) && task.getId() == dao_id)//修改操作，过滤掉修改的当前记录
					continue;
				String existBuildSqlTableName = task.getTable_name().replaceAll("_", "");
				String existBuildSqlDaoName = existBuildSqlTableName;
				
				if(tableViewSpTasks!=null && tableViewSpTasks.size()>0) {
					for(GenTaskByTableViewSp tableTask:tableViewSpTasks) {
						String []tableNames = tableTask.getTable_names().replaceAll("_", "").split(",");
						for(String tableName : tableNames) {
							if(tableName.equalsIgnoreCase(existBuildSqlTableName)) {
								if(tableName.indexOf(tableTask.getPrefix()) == 0)
									tableName = tableName.replaceFirst(tableTask.getPrefix(), "");
								tableName = tableName + tableTask.getSuffix();
								existBuildSqlDaoName = tableName;
								break;
							}
						}
					}
				}
				
				if(existBuildSqlDaoName.equalsIgnoreCase(daoName) && 
						!task.getDatabaseSetName().equalsIgnoreCase(db_set_name)){
					status.setInfo("在同一个project中，不同数据库下面不能定义相同的表名.<br/>"
							+"逻辑数据库"+task.getDatabaseSetName()+"下已经存在名为"+daoName+"的DAO.");
					return status;
				}
			}
		}
		
		if(sqlTasks!=null && sqlTasks.size()>0){
			for(GenTaskByFreeSql task:sqlTasks){
				if("1".equalsIgnoreCase(is_update) && task.getId() == dao_id)//修改操作，过滤掉修改的当前记录
					continue;
				if(task.getClass_name().equalsIgnoreCase(daoName) && 
						!task.getDatabaseSetName().equalsIgnoreCase(db_set_name)){
					status.setInfo("在同一个project中，不同数据库下面不能定义相同的DAO类名.<br/>"
							+"逻辑数据库"+task.getDatabaseSetName()+"下已经存在名为"+daoName+"的DAO.");
					return status;
				}
			}
		}
		
		return Status.OK;
	}
	
	@POST
	@Path("approveTask")
	@Produces(MediaType.APPLICATION_JSON)
	public Status approveTask(@FormParam("taskId") String taskId,
			@FormParam("taskType") String taskType,
			@FormParam("userId") int userId) {
		
		Status status = Status.ERROR;
		
		LoginUser approver = SpringBeanGetter.getDaoOfLoginUser().getUserById(userId);
		if (approver == null) {
			return status;
		}
		
		String []taskIds = taskId.split(",");
		String []taskTypes = taskType.split(",");
		
		List<GenTaskByTableViewSp> tableViewSpTasks = new ArrayList<GenTaskByTableViewSp>();
		List<GenTaskBySqlBuilder> autoTasks = new ArrayList<GenTaskBySqlBuilder>();
		List<GenTaskByFreeSql> sqlTasks = new ArrayList<GenTaskByFreeSql>();
		
		for (int i=0; i<taskIds.length; i++) {
			int id = Integer.parseInt(taskIds[i]);
			String type = taskTypes[i].trim();
			if ("table_view_sp".equalsIgnoreCase(type)) {
				GenTaskByTableViewSp task = SpringBeanGetter.getDaoByTableViewSp().getTasksByTaskId(id);
				tableViewSpTasks.add(task);
			} else if ("auto".equalsIgnoreCase(type)) {
				GenTaskBySqlBuilder task = SpringBeanGetter.getDaoBySqlBuilder().getTasksByTaskId(id);
				autoTasks.add(task);
			} else if ("sql".equalsIgnoreCase(type)) {
				GenTaskByFreeSql task = SpringBeanGetter.getDaoByFreeSql().getTasksByTaskId(id);
				sqlTasks.add(task);
			}
		}
		
		java.util.Collections.sort(tableViewSpTasks);
		java.util.Collections.sort(autoTasks);
		java.util.Collections.sort(sqlTasks);

		VelocityContext context = GenUtils.buildDefaultVelocityContext();
		context.put("standardDao", tableViewSpTasks);
		context.put("autoDao", autoTasks);
		context.put("sqlDao", sqlTasks);
		String msg = GenUtils.mergeVelocityContext(context, "templates/approval/approveDao.tpl");
		
		HtmlEmail email = new HtmlEmail();
		email.setHostName("appmail.sh.ctriptravel.com");
		email.setAuthentication("dalcodegen", "password1!");
		
		try {
			email.addTo(approver.getUserEmail());
			String userNo = AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
			LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
			email.addBcc(user.getUserEmail());
			email.setFrom(user.getUserEmail());
			email.setSubject("Codegen DAO 审批");
			email.setHtmlMsg(msg);
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
		}
		
		return Status.OK;
	}

}


