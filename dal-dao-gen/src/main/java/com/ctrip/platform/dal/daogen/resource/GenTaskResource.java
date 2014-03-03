package com.ctrip.platform.dal.daogen.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.FreeSqlClassPojoNames;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.pojo.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.TaskAggeragation;
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

		List<GenTaskBySqlBuilder> autoTasks = autoTask
				.getTasksByProjectId(Integer.valueOf(id));

		List<GenTaskByTableViewSp> tableViewSpTasks = daoByTableViewSp
				.getTasksByProjectId(id);

		List<GenTaskByFreeSql> sqlTasks = sqlTask.getTasksByProjectId(Integer
				.valueOf(id));

		TaskAggeragation allTasks = new TaskAggeragation();

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
			@QueryParam("server_id") int server,
			@QueryParam("db_name") String db_name) {
		List<GenTaskByFreeSql> sqlTasks = sqlTask.getTasksByProjectId(Integer
				.valueOf(id));

		FreeSqlClassPojoNames result = new FreeSqlClassPojoNames();

		Set<String> clazz = new HashSet<String>();
		Set<String> pojos = new HashSet<String>();

		for (GenTaskByFreeSql freesql : sqlTasks) {
			if (freesql.getServer_id() == server
					&& freesql.getDb_name().equals(db_name)) {
				clazz.add(freesql.getClass_name());
				pojos.add(freesql.getPojo_name());
			}
		}

		result.setClasses(clazz);
		result.setPojos(pojos);
		return result;

	}

}
