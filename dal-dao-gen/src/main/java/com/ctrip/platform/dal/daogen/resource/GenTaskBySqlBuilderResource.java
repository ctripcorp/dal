
package com.ctrip.platform.dal.daogen.resource;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;

@Resource
@Singleton
@Path("task/auto")
public class GenTaskBySqlBuilderResource {
	
	private static DaoBySqlBuilder daoBySqlBuilder;

	static {
		daoBySqlBuilder = SpringBeanGetter.getDaoBySqlBuilder();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addTask(@FormParam("id") int id,
			@FormParam("project_id") int project_id,
			@FormParam("db_name") String db_name,
			@FormParam("table_name") String table_name,
			@FormParam("method_name") String method_name,
			@FormParam("sql_style") String sql_style, // C#风格或者Java风格
			@FormParam("crud_type") String crud_type,
			@FormParam("fields") String fields,
			@FormParam("condition") String condition,
			@FormParam("sql_content") String sql_content,
			@FormParam("version") int version,
			@FormParam("action") String action) {
		GenTaskBySqlBuilder task = new GenTaskBySqlBuilder();

		if (action.equalsIgnoreCase("delete")) {
			task.setId(id);
			if (0 >= daoBySqlBuilder.deleteTask(task)) {
				return Status.ERROR;
			}	
		}else{
			task.setProject_id(project_id);
			task.setDb_name(db_name);
			task.setTable_name(table_name);
			task.setMethod_name(method_name);
			task.setSql_style(sql_style);
			task.setCrud_type(crud_type);
			task.setFields(fields);
			task.setCondition(condition);
			
			if(action.equalsIgnoreCase("update")){
				task.setId(id);
				task.setVersion(version);
				task.setSql_content(sql_content);
				if (0 >= daoBySqlBuilder.updateTask(task)) {
					Status status = Status.ERROR;
					status.setInfo("更新出错，数据是否合法？或者已经有同名方法？");
					return status;

				}
			}else{
				task.setGenerated(false);
				task.setVersion(1);
				task.setSql_content(sql_content);
				if (0 >= daoBySqlBuilder.insertTask(task)) {
					Status status = Status.ERROR;
					status.setInfo("新增出错，数据是否合法？或者已经有同名方法？");
					return status;
				}
			}
		}

		return Status.OK;
	}


}
