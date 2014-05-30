
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

import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

/**
 * 生成模板(包含基础的增删改查操作)
 * @author gzxia
 *
 */
@Resource
@Singleton
@Path("task/table")
public class GenTaskByTableViewResource {

	private static DaoByTableViewSp daoByTableViewSp;

	static {
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addTask(@FormParam("id") int id,
			@FormParam("project_id") int project_id,
			@FormParam("db_name") String set_name,
			@FormParam("table_names") String table_names,
			@FormParam("view_names") String view_names,
			@FormParam("sp_names") String sp_names,
			@FormParam("prefix") String prefix,
			@FormParam("suffix") String suffix,
			@FormParam("cud_by_sp") boolean cud_by_sp,
			@FormParam("pagination") boolean pagination,
			@FormParam("version") int version,
			@FormParam("action") String action,
			@FormParam("comment") String comment) {
		GenTaskByTableViewSp task = new GenTaskByTableViewSp();

		if (action.equalsIgnoreCase("delete")) {
			task.setId(id);
			if (0 >= daoByTableViewSp.deleteTask(task)) {
				return Status.ERROR;
			}	
		}else{
			String userNo = AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("employee").toString();
			LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
			
			task.setProject_id(project_id);
			task.setDb_name(set_name);
			task.setTable_names(table_names);
			task.setView_names(view_names);
			task.setSp_names(sp_names);
			task.setPrefix(prefix);
			task.setSuffix(suffix);
			task.setCud_by_sp(cud_by_sp);
			task.setPagination(pagination);
			task.setUpdate_user_no(user.getUserName()+"("+userNo+")");
			task.setUpdate_time(new Timestamp(System.currentTimeMillis()));
			task.setComment(comment);
			
			if(action.equalsIgnoreCase("update")){
				task.setId(id);
				task.setVersion(daoByTableViewSp.getVersionById(id));
				if (0 >= daoByTableViewSp.updateTask(task)) {
					return Status.ERROR;
				}
			}else{
				task.setGenerated(false);
				task.setVersion(1);
				if (0 >= daoByTableViewSp.insertTask(task)) {
					return Status.ERROR;
				}
			}
		}

		return Status.OK;
	}

}
