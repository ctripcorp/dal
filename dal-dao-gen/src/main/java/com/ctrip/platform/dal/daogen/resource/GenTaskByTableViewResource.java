package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

/**
 * 生成模板(包含基础的增删改查操作)
 *
 * @author gzxia
 * @modified yn.wang
 */

@Resource
@Singleton
@Path("task/table")
public class GenTaskByTableViewResource extends ApproveResource {
    private static ObjectMapper mapper = new ObjectMapper();

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Status addTask(@Context HttpServletRequest request, @FormParam("id") int id,
            @FormParam("project_id") int project_id, @FormParam("db_name") String set_name,
            @FormParam("table_names") String table_names, @FormParam("view_names") String view_names,
            @FormParam("sp_names") String sp_names, @FormParam("prefix") String prefix,
            @FormParam("suffix") String suffix, @FormParam("cud_by_sp") boolean cud_by_sp,
            @FormParam("pagination") boolean pagination, @FormParam("length") boolean length,
            @FormParam("version") int version, @FormParam("action") String action, @FormParam("comment") String comment,
            @FormParam("sql_style") String sql_style, // C#风格或者Java风格
            @FormParam("api_list") String api_list) throws Exception {
        try {
            GenTaskByTableViewSp task = new GenTaskByTableViewSp();

            if (action.equalsIgnoreCase("delete")) {
                task.setId(id);
                if (0 >= BeanGetter.getDaoByTableViewSp().deleteTask(task)) {
                    return Status.ERROR();
                }
            } else {
                String userNo = RequestUtil.getUserNo(request);
                LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);

                task.setProject_id(project_id);
                task.setDatabaseSetName(set_name);
                task.setTable_names(table_names);
                task.setView_names(view_names);
                task.setSp_names(sp_names);
                task.setPrefix(prefix);
                task.setSuffix(suffix);
                task.setCud_by_sp(cud_by_sp);
                task.setPagination(pagination);
                task.setLength(length);
                task.setUpdate_user_no(user.getUserName() + "(" + userNo + ")");
                task.setUpdate_time(new Timestamp(System.currentTimeMillis()));
                task.setComment(comment);
                task.setSql_style(sql_style);
                task.setApi_list(api_list);
                if (needApproveTask(project_id, user.getId())) {
                    task.setApproved(1);
                } else {
                    task.setApproved(2);
                }
                task.setApproveMsg("");

                if (action.equalsIgnoreCase("update")) {
                    task.setId(id);
                    task.setVersion(BeanGetter.getDaoByTableViewSp().getVersionById(id));
                    if (0 >= BeanGetter.getDaoByTableViewSp().updateTask(task)) {
                        return Status.ERROR();
                    }
                } else {
                    task.setGenerated(false);
                    task.setVersion(1);
                    if (0 >= BeanGetter.getDaoByTableViewSp().insertTask(task)) {
                        return Status.ERROR();
                    }
                }
            }

            return Status.OK();
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getCause().getMessage());
            return status;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("apiList")
    public Status getApiList(@QueryParam("db_name") String db_set_name, @QueryParam("table_names") String table_names,
            @QueryParam("sql_style") String sql_style) {
        Status status = Status.OK();

        try {
            List<DalApi> apis = null;
            DatabaseSetEntry databaseSetEntry =
                    BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(db_set_name);
            DatabaseCategory dbCategory = DbUtils.getDatabaseCategory(databaseSetEntry.getConnectionString());

            if ("csharp".equalsIgnoreCase(sql_style)) {
                if (dbCategory == DatabaseCategory.MySql) {
                    apis = BeanGetter.getDalApiDao().getDalApiByLanguageAndDbtype("csharp", "MySQL");
                } else {
                    apis = BeanGetter.getDalApiDao().getDalApiByLanguageAndDbtype("csharp", "SQLServer");
                }
            } else {
                if (dbCategory == DatabaseCategory.MySql) {
                    apis = BeanGetter.getDalApiDao().getDalApiByLanguageAndDbtype("java", "MySQL");
                } else {
                    // SpType spType =
                    // spType(databaseSetEntry.getConnectionString(),
                    // table_names);
                    // apis =
                    // BeanGetter.getDalApiDao().getDalApiByLanguageAndDbtypeAndSptype("java",
                    // "SQLServer", spType.getValue());
                    apis = BeanGetter.getDalApiDao().getDalApiByLanguageAndDbtype("java", "SQLServer");
                }
            }

            for (DalApi api : apis) {
                String method_declaration = api.getMethod_declaration();
                method_declaration = method_declaration.replaceAll("<", "&lt;");
                method_declaration = method_declaration.replaceAll(">", "&gt;");
                api.setMethod_declaration(method_declaration);
            }

            java.util.Collections.sort(apis, new Comparator<DalApi>() {
                @Override
                public int compare(DalApi o1, DalApi o2) {
                    return o1.getMethod_declaration().compareToIgnoreCase(o2.getMethod_declaration());
                }
            });

            status.setInfo(mapper.writeValueAsString(apis));
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getCause().getMessage());
            return status;
        }
        return status;
    }

}
