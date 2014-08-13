
package com.ctrip.platform.dal.daogen.resource;

import java.sql.Timestamp;
import java.util.Comparator;
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

import com.ctrip.platform.dal.daogen.dao.DalApiDao;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalApi;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	private static DalApiDao dalApiDao;
	
	private static ObjectMapper mapper = new ObjectMapper();

	static {
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();
		dalApiDao = SpringBeanGetter.getDalApiDao();
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
			@FormParam("comment") String comment,
			@FormParam("sql_style") String sql_style// C#风格或者Java风格
			) {
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
			task.setDatabaseSetName(set_name);
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
			task.setSql_style(sql_style);
			
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
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("apiList")
	public Status getTableSPNames(@QueryParam("db_name") String setName) {
		Status status = Status.OK;
		
		List<DalApi> apis = dalApiDao.getAllDalApi();
		
		java.util.Collections.sort(apis, new Comparator<DalApi>(){
			@Override
			public int compare(DalApi o1, DalApi o2) {
				return o1.getMethod_declaration().compareToIgnoreCase(o2.getMethod_declaration());
			}
		});
		
		try {
			status.setInfo(mapper.writeValueAsString(apis));
		} catch (JsonProcessingException e) {
			status = Status.ERROR;
			status.setInfo(e.getMessage());
			return status;
		}
		return status;
	}

}
