
package com.ctrip.platform.dal.daogen.resource;

import java.sql.Timestamp;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jasig.cas.client.util.AssertionHolder;

import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

/**
 * 复杂查询（额外生成实体类）
 * @author gzxia
 *
 */
@Resource
@Singleton
@Path("task/sql")
public class GenTaskByFreeSqlResource {
	
	private static DaoByFreeSql daoByFreeSql;

	static {
		daoByFreeSql = SpringBeanGetter.getDaoByFreeSql();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addTask(@FormParam("id") int id,
			@FormParam("project_id") int project_id,
			@FormParam("db_name") String set_name,
			@FormParam("class_name") String class_name,
			@FormParam("pojo_name") String pojo_name,
			@FormParam("method_name") String method_name,
			@FormParam("crud_type") String crud_type,
			@FormParam("sql_content") String sql_content,
			@FormParam("params") String params,
			@FormParam("version") int version,
			@FormParam("action") String action,
			@FormParam("comment") String comment) {
		GenTaskByFreeSql task = new GenTaskByFreeSql();

		if (action.equalsIgnoreCase("delete")) {
			task.setId(id);
			if (0 >= daoByFreeSql.deleteTask(task)) {
				return Status.ERROR;
			}	
		}else{
			String userNo = AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("employee").toString();
			LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		
			task.setProject_id(project_id);
			task.setDatabaseSetName(set_name);
			task.setClass_name(class_name);
			task.setPojo_name(pojo_name);
			task.setMethod_name(method_name);
			task.setCrud_type(crud_type);
			task.setSql_content(sql_content);
			task.setParameters(params);
			task.setUpdate_user_no(user.getUserName()+"("+userNo+")");
			task.setUpdate_time(new Timestamp(System.currentTimeMillis()));
			task.setComment(comment);
			
			if(action.equalsIgnoreCase("update")){
				task.setId(id);
				task.setVersion(daoByFreeSql.getVersionById(id));
				if (0 >= daoByFreeSql.updateTask(task)) {
					Status status = Status.ERROR;
					status.setInfo("更新出错，数据是否合法？或者已经有同名方法？");
					return status;
				}
			}else{
				task.setGenerated(false);
				task.setVersion(1);
				if (0 >= daoByFreeSql.insertTask(task)) {
					Status status = Status.ERROR;
					status.setInfo("新增出错，数据是否合法？或者已经有同名方法？");
					return status;
				}
			}
		}

		return Status.OK;
	}

}
