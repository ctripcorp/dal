package com.ctrip.platform.dal.daogen.resource;

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

import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.domain.FreeSqlClassPojoNames;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.domain.TaskAggeragation;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

/**
 * The schema of {daogen.task} { "project_id": , "task_type": , "database" : ,
 * "table": , "dao_name": , "func_name": , "sql_spname": , "fields": ,
 * "condition": , "crud": } The schema of {daogen.task_meta} { "database" : ,
 * "table": , "primary_key": , "fields": }
 * 
 * @author gawu
 * 
 */
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
		List<GenTaskByFreeSql> sqlTasks = sqlTask.getTasksByProjectId(Integer
				.valueOf(id));

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
		
		List<GenTaskByTableViewSp> tableViewSpTasks = daoByTableViewSp.getTasksByProjectId(project_id);
		
		List<GenTaskBySqlBuilder> autoTasks = autoTask.getTasksByProjectId(Integer.valueOf(project_id));

		List<GenTaskByFreeSql> sqlTasks = sqlTask.getTasksByProjectId(Integer.valueOf(project_id));
		
		//在同一个project中，不同数据库下面不能存在相同的表名或者Dao类名
		if(tableViewSpTasks!=null && tableViewSpTasks.size()>0){
			for(GenTaskByTableViewSp task:tableViewSpTasks){
				if("1".equalsIgnoreCase(is_update) && task.getId() == dao_id)//修改操作，过滤掉修改的当前记录
					continue;
				String []daoClassName = daoName.split(",");
				for(String name:daoClassName){
					if(name.indexOf(prefix) == 0)
						name = name.replaceFirst(prefix, "");
					name = name + suffix;
					String []existTableName = task.getTable_names().split(",");
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
				if(task.getTable_name().equalsIgnoreCase(daoName) && 
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

}












