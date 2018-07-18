package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.domain.FreeSqlClassPojoNames;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.domain.TaskAggeragation;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.LanguageType;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpDalGenerator;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.generator.java.JavaDalGenerator;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpMethodHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpTableHost;
import com.ctrip.platform.dal.daogen.host.java.FreeSqlHost;
import com.ctrip.platform.dal.daogen.host.java.JavaMethodHost;
import com.ctrip.platform.dal.daogen.host.java.JavaTableHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.Configuration;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Resource
@Singleton
@Path("task")
public class GenTaskResource {
    private static final String CSHARP = "csharp";
    private static final String JAVA = "java";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TaskAggeragation getTasks(@QueryParam("project_id") int id) throws SQLException {
        try {
            List<GenTaskBySqlBuilder> autoTasks = BeanGetter.getDaoBySqlBuilder().getTasksByProjectId(id);
            List<GenTaskByTableViewSp> tableViewSpTasks = BeanGetter.getDaoByTableViewSp().getTasksByProjectId(id);
            List<GenTaskByFreeSql> sqlTasks = BeanGetter.getDaoByFreeSql().getTasksByProjectId(id);
            TaskAggeragation allTasks = new TaskAggeragation();

            if (autoTasks != null && autoTasks.size() > 0) {
                java.util.Collections.sort(autoTasks);
            }
            if (tableViewSpTasks != null && tableViewSpTasks.size() > 0) {
                java.util.Collections.sort(tableViewSpTasks);
            }
            if (sqlTasks != null && sqlTasks.size() > 0) {
                java.util.Collections.sort(sqlTasks);
            }

            allTasks.setAutoTasks(autoTasks);
            allTasks.setTableViewSpTasks(tableViewSpTasks);
            allTasks.setSqlTasks(sqlTasks);

            return allTasks;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Path("getLanguageType")
    @Produces(MediaType.APPLICATION_JSON)
    public Status getLanguageType(@QueryParam("project_id") int id) throws Exception {
        Status status = Status.ERROR();
        TaskAggeragation task = getTasks(id);
        List<GenTaskByTableViewSp> tableTasks = task.getTableViewSpTasks();
        if (tableTasks != null && tableTasks.size() > 0) {
            for (GenTaskByTableViewSp table : tableTasks) {
                if (table.getSql_style().equals(CSHARP)) {
                    status.setInfo(CSHARP);
                } else if (table.getSql_style().equals(JAVA)) {
                    status.setInfo(JAVA);
                }
                break;
            }
            status.setCode("OK");
            return status;
        }

        List<GenTaskBySqlBuilder> buildTasks = task.getAutoTasks();
        if (buildTasks != null && buildTasks.size() > 0) {
            for (GenTaskBySqlBuilder build : buildTasks) {
                if (build.getSql_style().equals(CSHARP)) {
                    status.setInfo(CSHARP);
                } else if (build.getSql_style().equals(JAVA)) {
                    status.setInfo(JAVA);
                }
                break;
            }
            status.setCode("OK");
            return status;
        }

        List<GenTaskByFreeSql> sqlTasks = task.getSqlTasks();
        if (sqlTasks != null && sqlTasks.size() > 0) {
            for (GenTaskByFreeSql sql : sqlTasks) {
                if (sql.getSql_style().equals(CSHARP)) {
                    status.setInfo(CSHARP);
                } else if (sql.getSql_style().equals(JAVA)) {
                    status.setInfo(JAVA);
                }
                break;
            }
            status.setCode("OK");
            return status;
        }

        return status;
    }

    @GET
    @Path("sql_class")
    @Produces(MediaType.APPLICATION_JSON)
    public FreeSqlClassPojoNames getClassPojoNames(@QueryParam("project_id") int id,
            @QueryParam("db_name") String db_name) throws SQLException {
        try {
            List<GenTaskByFreeSql> sqlTasks = BeanGetter.getDaoByFreeSql().getTasksByProjectId(Integer.valueOf(id));
            FreeSqlClassPojoNames result = new FreeSqlClassPojoNames();
            Set<String> clazz = new HashSet<>();
            Set<String> pojos = new HashSet<>();

            for (GenTaskByFreeSql freesql : sqlTasks) {
                if (freesql.getDatabaseSetName().trim().equals(db_name.trim())) {
                    clazz.add(freesql.getClass_name());
                    pojos.add(freesql.getPojo_name());
                }
            }

            result.setClasses(clazz);
            result.setPojos(pojos);
            return result;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @POST
    @Path("checkDaoNameConflict")
    @Produces(MediaType.APPLICATION_JSON)
    public Status checkDaoNameConflict(@FormParam("project_id") int project_id,
            @FormParam("db_set_name") String db_set_name, @FormParam("daoName") String daoName,
            @FormParam("is_update") String is_update, @FormParam("dao_id") int dao_id,
            @FormParam("prefix") String prefix, @FormParam("suffix") String suffix) {
        try {
            Status status = Status.ERROR();
            daoName = daoName.replaceAll("_", "");
            List<GenTaskByTableViewSp> tableViewSpTasks =
                    BeanGetter.getDaoByTableViewSp().getTasksByProjectId(project_id);
            List<GenTaskBySqlBuilder> autoTasks =
                    BeanGetter.getDaoBySqlBuilder().getTasksByProjectId(Integer.valueOf(project_id));
            List<GenTaskByFreeSql> sqlTasks =
                    BeanGetter.getDaoByFreeSql().getTasksByProjectId(Integer.valueOf(project_id));

            // 在同一个project中，不同数据库下面不能存在相同的表名或者Dao类名
            if (tableViewSpTasks != null && tableViewSpTasks.size() > 0) {
                for (GenTaskByTableViewSp task : tableViewSpTasks) {
                    if ("1".equalsIgnoreCase(is_update) && task.getId() == dao_id)// 修改操作，过滤掉修改的当前记录
                        continue;
                    String[] daoClassName = daoName.split(",");
                    for (String name : daoClassName) {
                        if (name.indexOf(prefix) == 0)
                            name = name.replaceFirst(prefix, "");
                        name = name + suffix;
                        String[] existTableName = task.getTable_names().replaceAll("_", "").split(",");
                        for (String tableName : existTableName) {
                            if (tableName.indexOf(task.getPrefix()) == 0)
                                tableName = tableName.replaceFirst(task.getPrefix(), "");
                            String existDaoName = tableName + task.getSuffix();
                            if (existDaoName.equalsIgnoreCase(name)
                                    && !task.getDatabaseSetName().equalsIgnoreCase(db_set_name)) {
                                status.setInfo("在同一个project中，不同数据库下面不能定义相同的表名或者DAO类名.<br/>" + "逻辑数据库"
                                        + task.getDatabaseSetName() + "下已经存在名为" + name + "的DAO.");
                                return status;
                            }
                        }
                    }
                }
            }

            if (autoTasks != null && autoTasks.size() > 0) {
                for (GenTaskBySqlBuilder task : autoTasks) {
                    if ("1".equalsIgnoreCase(is_update) && task.getId() == dao_id)// 修改操作，过滤掉修改的当前记录
                        continue;
                    String existBuildSqlTableName = task.getTable_name().replaceAll("_", "");
                    String existBuildSqlDaoName = existBuildSqlTableName;

                    if (tableViewSpTasks != null && tableViewSpTasks.size() > 0) {
                        for (GenTaskByTableViewSp tableTask : tableViewSpTasks) {
                            String[] tableNames = tableTask.getTable_names().replaceAll("_", "").split(",");
                            for (String tableName : tableNames) {
                                if (tableName.equalsIgnoreCase(existBuildSqlTableName)) {
                                    if (tableName.indexOf(tableTask.getPrefix()) == 0)
                                        tableName = tableName.replaceFirst(tableTask.getPrefix(), "");
                                    tableName = tableName + tableTask.getSuffix();
                                    existBuildSqlDaoName = tableName;
                                    break;
                                }
                            }
                        }
                    }

                    if (existBuildSqlDaoName.equalsIgnoreCase(daoName)
                            && !task.getDatabaseSetName().equalsIgnoreCase(db_set_name)) {
                        status.setInfo("在同一个project中，不同数据库下面不能定义相同的表名.<br/>" + "逻辑数据库" + task.getDatabaseSetName()
                                + "下已经存在名为" + daoName + "的DAO.");
                        return status;
                    }
                }
            }

            if (sqlTasks != null && sqlTasks.size() > 0) {
                for (GenTaskByFreeSql task : sqlTasks) {
                    if ("1".equalsIgnoreCase(is_update) && task.getId() == dao_id)// 修改操作，过滤掉修改的当前记录
                        continue;
                    if (task.getClass_name().equalsIgnoreCase(daoName)
                            && !task.getDatabaseSetName().equalsIgnoreCase(db_set_name)) {
                        status.setInfo("在同一个project中，不同数据库下面不能定义相同的DAO类名.<br/>" + "逻辑数据库" + task.getDatabaseSetName()
                                + "下已经存在名为" + daoName + "的DAO.");
                        return status;
                    }
                }
            }

            return Status.OK();
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    @POST
    @Path("approveTask")
    @Produces(MediaType.APPLICATION_JSON)
    public Status approveTask(@Context HttpServletRequest request, @FormParam("taskId") String taskId,
            @FormParam("taskType") String taskType, @FormParam("userId") int userId) throws Exception {
        try {
            Status status = Status.ERROR();
            LoginUser approver = BeanGetter.getDaoOfLoginUser().getUserById(userId);
            if (approver == null) {
                return status;
            }

            int len = request.getRequestURI().length();
            String host = request.getRequestURL().toString();
            host = host.substring(0, len);
            String approveUrl = host + "rest/task/taskApproveOperationForEmail?";
            String myApprovelTaskUrl = host + "eventmanage.jsp";

            String[] taskIds = taskId.split(",");
            String[] taskTypes = taskType.split(",");

            List<GenTaskByTableViewSp> tableViewSpTasks = new ArrayList<>();
            List<GenTaskBySqlBuilder> autoTasks = new ArrayList<>();
            List<GenTaskByFreeSql> sqlTasks = new ArrayList<>();

            String userNo = RequestUtil.getUserNo(request);
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);

            ApproveTask at = new ApproveTask();
            at.setApprove_user_id(approver.getId());
            at.setCreate_user_id(user.getId());
            at.setCreate_time(new Timestamp(System.currentTimeMillis()));

            for (int i = 0; i < taskIds.length; i++) {
                int id = Integer.parseInt(taskIds[i]);
                String type = taskTypes[i].trim();
                if ("table_view_sp".equalsIgnoreCase(type)) {
                    GenTaskByTableViewSp task = BeanGetter.getDaoByTableViewSp().getTasksByTaskId(id);
                    tableViewSpTasks.add(task);
                    at.setTask_id(task.getId());
                    at.setTask_type("table_view_sp");
                    BeanGetter.getApproveTaskDao().insertApproveTask(at);
                } else if ("auto".equalsIgnoreCase(type)) {
                    GenTaskBySqlBuilder task = BeanGetter.getDaoBySqlBuilder().getTasksByTaskId(id);
                    autoTasks.add(task);
                    at.setTask_id(task.getId());
                    at.setTask_type("auto");
                    BeanGetter.getApproveTaskDao().insertApproveTask(at);
                } else if ("sql".equalsIgnoreCase(type)) {
                    GenTaskByFreeSql task = BeanGetter.getDaoByFreeSql().getTasksByTaskId(id);
                    sqlTasks.add(task);
                    at.setTask_id(task.getId());
                    at.setTask_type("sql");
                    BeanGetter.getApproveTaskDao().insertApproveTask(at);
                }
            }

            java.util.Collections.sort(tableViewSpTasks);
            java.util.Collections.sort(autoTasks);
            java.util.Collections.sort(sqlTasks);

            VelocityContext context = GenUtils.buildDefaultVelocityContext();
            context.put("standardDao", tableViewSpTasks);
            context.put("autoDao", autoTasks);
            context.put("sqlDao", sqlTasks);
            context.put("approveUrl", approveUrl);
            context.put("myApprovelTaskUrl", myApprovelTaskUrl);
            context.put("approveUser", approver.getUserName());
            String msg = GenUtils.mergeVelocityContext(context, "templates/approval/approveDao.tpl");

            HtmlEmail email = new HtmlEmail();
            email.setHostName(Configuration.get("email_host_name"));
            email.setAuthentication(Configuration.get("email_user_name"), Configuration.get("email_password"));

            try {
                email.addTo(approver.getUserEmail());
                email.setFrom(user.getUserEmail(), user.getUserName());
                email.setSubject("Codegen DAO 审批");
                email.setHtmlMsg(msg);
                email.send();
            } catch (EmailException e) {
                e.printStackTrace();
            }

            return Status.OK();
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    @GET
    @Path("taskApproveOperationForEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public String taskApproveOperationForEmail(@Context HttpServletRequest request, @QueryParam("taskId") int taskId,
            @QueryParam("taskType") String taskType, @QueryParam("approveFlag") int approveFlag,
            @QueryParam("approveMsg") String approveMsg) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            if (user == null) {
                return "please login fisrt.";
            }

            ApproveTask task = haveApprovePermision(user.getId(), taskId, taskType);
            if (task == null) {
                return "Dao have been approved.";
            }

            List<GenTaskByTableViewSp> tableViewSpTasks = new ArrayList<>();
            List<GenTaskBySqlBuilder> autoTasks = new ArrayList<>();
            List<GenTaskByFreeSql> sqlTasks = new ArrayList<>();

            if ("table_view_sp".equalsIgnoreCase(taskType)) {
                BeanGetter.getDaoByTableViewSp().updateTask(taskId, approveFlag, approveMsg);
                BeanGetter.getApproveTaskDao().deleteApproveTaskByTaskIdAndType(taskId, taskType);
                tableViewSpTasks.add(BeanGetter.getDaoByTableViewSp().getTasksByTaskId(taskId));
            } else if ("auto".equalsIgnoreCase(taskType)) {
                BeanGetter.getDaoBySqlBuilder().updateTask(taskId, approveFlag, approveMsg);
                BeanGetter.getApproveTaskDao().deleteApproveTaskByTaskIdAndType(taskId, taskType);
                autoTasks.add(BeanGetter.getDaoBySqlBuilder().getTasksByTaskId(taskId));
            } else if ("sql".equalsIgnoreCase(taskType)) {
                BeanGetter.getDaoByFreeSql().updateTask(taskId, approveFlag, approveMsg);
                BeanGetter.getApproveTaskDao().deleteApproveTaskByTaskIdAndType(taskId, taskType);
                sqlTasks.add(BeanGetter.getDaoByFreeSql().getTasksByTaskId(taskId));
            }

            java.util.Collections.sort(tableViewSpTasks);
            java.util.Collections.sort(autoTasks);
            java.util.Collections.sort(sqlTasks);

            LoginUser noticeUsr = BeanGetter.getDaoOfLoginUser().getUserById(task.getCreate_user_id());

            VelocityContext context = GenUtils.buildDefaultVelocityContext();
            context.put("standardDao", tableViewSpTasks);
            context.put("autoDao", autoTasks);
            context.put("sqlDao", sqlTasks);
            String msg = "你好，" + noticeUsr.getUserName() + ":<br/>&nbsp;&nbsp;你提交的DAO已审批，审批";
            if (approveFlag == 2) {
                msg += "通过。";
            } else {
                msg += "未通过。";
            }
            if (approveMsg != null) {
                msg += "<br/>&nbsp;&nbsp;审批意见：" + approveMsg;
            }
            context.put("msg", msg);
            String mailMsg = GenUtils.mergeVelocityContext(context, "templates/approval/approveResult.tpl");

            HtmlEmail email = new HtmlEmail();
            email.setHostName(Configuration.get("email_host_name"));
            email.setAuthentication(Configuration.get("email_user_name"), Configuration.get("email_password"));

            try {
                email.addTo(noticeUsr.getUserEmail());
                email.setFrom(user.getUserEmail(), user.getUserName());
                email.setSubject("Codegen DAO 审批结果通知");
                email.setHtmlMsg(mailMsg);
                email.send();
            } catch (EmailException e) {
                e.printStackTrace();
            }

            return "Success Approved.";
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Path("taskApproveOperation")
    @Produces(MediaType.APPLICATION_JSON)
    public Status taskApproveOperation(@Context HttpServletRequest request, @QueryParam("taskId") int taskId,
            @QueryParam("taskType") String taskType, @QueryParam("approveFlag") int approveFlag,
            @QueryParam("approveMsg") String approveMsg) throws Exception {
        try {
            Status status = Status.ERROR();
            String userNo = RequestUtil.getUserNo(request);
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            if (user == null) {
                status.setInfo("please login fisrt.");
                return status;
            }

            ApproveTask task = haveApprovePermision(user.getId(), taskId, taskType);
            if (task == null) {
                status.setInfo("you don't have permision to approve this task.");
                return status;
            }

            List<GenTaskByTableViewSp> tableViewSpTasks = new ArrayList<>();
            List<GenTaskBySqlBuilder> autoTasks = new ArrayList<>();
            List<GenTaskByFreeSql> sqlTasks = new ArrayList<>();

            if ("table_view_sp".equalsIgnoreCase(taskType)) {
                BeanGetter.getDaoByTableViewSp().updateTask(taskId, approveFlag, approveMsg);
                BeanGetter.getApproveTaskDao().deleteApproveTaskByTaskIdAndType(taskId, taskType);
                tableViewSpTasks.add(BeanGetter.getDaoByTableViewSp().getTasksByTaskId(taskId));
            } else if ("auto".equalsIgnoreCase(taskType)) {
                BeanGetter.getDaoBySqlBuilder().updateTask(taskId, approveFlag, approveMsg);
                BeanGetter.getApproveTaskDao().deleteApproveTaskByTaskIdAndType(taskId, taskType);
                autoTasks.add(BeanGetter.getDaoBySqlBuilder().getTasksByTaskId(taskId));
            } else if ("sql".equalsIgnoreCase(taskType)) {
                BeanGetter.getDaoByFreeSql().updateTask(taskId, approveFlag, approveMsg);
                BeanGetter.getApproveTaskDao().deleteApproveTaskByTaskIdAndType(taskId, taskType);
                sqlTasks.add(BeanGetter.getDaoByFreeSql().getTasksByTaskId(taskId));
            }

            java.util.Collections.sort(tableViewSpTasks);
            java.util.Collections.sort(autoTasks);
            java.util.Collections.sort(sqlTasks);

            LoginUser noticeUsr = BeanGetter.getDaoOfLoginUser().getUserById(task.getCreate_user_id());

            VelocityContext context = GenUtils.buildDefaultVelocityContext();
            context.put("standardDao", tableViewSpTasks);
            context.put("autoDao", autoTasks);
            context.put("sqlDao", sqlTasks);
            String msg = "你好，" + noticeUsr.getUserName() + ":<br/>&nbsp;&nbsp;你提交的DAO已审批，审批";
            if (approveFlag == 2) {
                msg += "通过。";
            } else {
                msg += "未通过。";
            }
            if (approveMsg != null) {
                msg += "<br/>&nbsp;&nbsp;审批意见：" + approveMsg;
            }
            context.put("msg", msg);
            String mailMsg = GenUtils.mergeVelocityContext(context, "templates/approval/approveResult.tpl");

            HtmlEmail email = new HtmlEmail();
            email.setHostName(Configuration.get("email_host_name"));
            email.setAuthentication(Configuration.get("email_user_name"), Configuration.get("email_password"));

            try {
                email.addTo(noticeUsr.getUserEmail());
                email.setFrom(user.getUserEmail(), user.getUserName());
                email.setSubject("Codegen DAO 审批结果通知");
                email.setHtmlMsg(mailMsg);
                email.send();
            } catch (EmailException e) {
                e.printStackTrace();
            }

            return Status.OK();
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    private ApproveTask haveApprovePermision(int approverId, int taskId, String taskType) throws SQLException {
        List<ApproveTask> list = BeanGetter.getApproveTaskDao().getAllApproveTaskByApproverId(approverId);
        Iterator<ApproveTask> ite = list.iterator();
        while (ite.hasNext()) {
            ApproveTask task = ite.next();
            if (taskId == task.getTask_id() && task.getTask_type().equalsIgnoreCase(taskType)) {
                return task;
            }
        }
        return null;
    }

    @GET
    @Path("getMyApproveTask")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApproveTask> getMyApproveTask(@Context HttpServletRequest request, @QueryParam("rand") String rand)
            throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            List<ApproveTask> result = BeanGetter.getApproveTaskDao().getAllApproveTaskByApproverId(user.getId());
            Iterator<ApproveTask> ite = result.iterator();
            while (ite.hasNext()) {
                ApproveTask task = ite.next();
                String create_user_name =
                        BeanGetter.getDaoOfLoginUser().getUserById(task.getCreate_user_id()).getUserName();
                task.setCreate_user_name(create_user_name);
            }
            return result;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Path("getMyApproveTaskDetail")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApproveTaskDetail> getMyApproveTaskDetail(@QueryParam("rand") String rand,
            @QueryParam("taskId") String taskId, @QueryParam("taskType") String taskType) throws SQLException {
        try {
            int id = Integer.parseInt(taskId);
            List<ApproveTaskDetail> detail = null;
            if ("table_view_sp".equalsIgnoreCase(taskType)) {
                GenTaskByTableViewSp task = BeanGetter.getDaoByTableViewSp().getTasksByTaskId(id);
                detail = buildStandardTaskDetail(task);
            } else if ("auto".equalsIgnoreCase(taskType)) {
                GenTaskBySqlBuilder task = BeanGetter.getDaoBySqlBuilder().getTasksByTaskId(id);
                detail = buildAutoTaskDetail(task);
            } else if ("sql".equalsIgnoreCase(taskType)) {
                GenTaskByFreeSql task = BeanGetter.getDaoByFreeSql().getTasksByTaskId(id);
                detail = buildFreesqlTaskDetail(task);
            }

            return detail;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    private List<ApproveTaskDetail> buildStandardTaskDetail(GenTaskByTableViewSp task) {
        List<ApproveTaskDetail> detail = new ArrayList<>();
        detail.add(createOneAttr("Database Set Name:", task.getDatabaseSetName()));
        detail.add(createOneAttr("All Select Table:", task.getTable_names()));
        if (task.getView_names() != null && !task.getView_names().isEmpty()) {
            detail.add(createOneAttr("All Select View:", task.getView_names()));
        }
        if (task.getSp_names() != null && !task.getSp_names().isEmpty()) {
            detail.add(createOneAttr("All Select SP:", task.getSp_names()));
        }
        detail.add(createOneAttr("Have Pagination Fun:", String.valueOf(task.getPagination())));
        detail.add(createOneAttr("Comment:", task.getComment()));
        detail.add(createOneAttr("SQL Style:", task.getSql_style()));
        StringBuilder funs = new StringBuilder();
        if (task.getApi_list() != null && !task.getApi_list().isEmpty()) {
            String api_list = task.getApi_list().replaceAll("dal_api_", "");
            String[] apis = api_list.split(",");
            for (int i = 0; i < apis.length; i++) {
                try {
                    DalApi api = BeanGetter.getDalApiDao().getDalApiById(Integer.parseInt(apis[i]));
                    funs.append(api.getMethod_description()).append("<br/><br/>");
                } catch (Exception e) {
                }
            }
        }
        detail.add(createOneAttr("All Select Method:", funs.toString()));
        detail.add(createOneAttr("Last Update User:", task.getUpdate_user_no()));
        detail.add(createOneAttr("Last Update Time:", task.getStr_update_time()));
        return detail;
    }

    private List<ApproveTaskDetail> buildAutoTaskDetail(GenTaskBySqlBuilder task) {
        List<ApproveTaskDetail> detail = new ArrayList<>();
        detail.add(createOneAttr("Database Set Name:", task.getDatabaseSetName()));
        detail.add(createOneAttr("Select Table:", task.getTable_name()));
        detail.add(createOneAttr("SQL Style:", task.getSql_style()));
        String sql = task.getSql_content();
        if (sql.length() > 200) {
            sql = sql.replaceAll(",", "  <br/><br/>, ");
            sql = sql.replaceAll("(?i)from", "<br/><br/>from");
            sql = sql.replaceAll("(?i)where", "<br/><br/>where");
            sql = sql.replaceAll("(?i)and", "<br/><br/>	and");
        }
        detail.add(createOneAttr("SQL Preview:", sql));
        detail.add(createOneAttr("Method Name:", task.getMethod_name()));
        detail.add(createOneAttr("Method Param:", buildAutoTaskMethodParam(task)));
        detail.add(createOneAttr("Last Update User:", task.getUpdate_user_no()));
        detail.add(createOneAttr("Last Update Time:", task.getStr_update_time()));
        return detail;
    }

    private List<ApproveTaskDetail> buildFreesqlTaskDetail(GenTaskByFreeSql task) {
        List<ApproveTaskDetail> detail = new ArrayList<>();
        detail.add(createOneAttr("Database Set Name:", task.getDatabaseSetName()));
        detail.add(createOneAttr("SQL Style:", task.getSql_style()));
        String sql = task.getSql_content();
        if (sql.length() > 200) {
            sql = sql.replaceAll(",", "  <br/><br/>, ");
            sql = sql.replaceAll("(?i)from", "<br/><br/>from");
            sql = sql.replaceAll("(?i)where", "<br/><br/>where");
            sql = sql.replaceAll("(?i)and", "<br/><br/>	and");
        }
        detail.add(createOneAttr("SQL Preview:", sql));
        detail.add(createOneAttr("Method Name:", task.getMethod_name()));
        detail.add(createOneAttr("Method Param:", buildFreesqlTaskMethodParam(task)));
        detail.add(createOneAttr("Last Update User:", task.getUpdate_user_no()));
        detail.add(createOneAttr("Last Update Time:", task.getStr_update_time()));
        return detail;
    }

    private ApproveTaskDetail createOneAttr(String attrName, String attrValue) {
        ApproveTaskDetail detail = new ApproveTaskDetail(attrName, attrValue);
        return detail;
    }

    private String buildAutoTaskMethodParam(GenTaskBySqlBuilder task) {
        try {
            DalGenerator generator = null;
            CodeGenContext context = null;
            Progress progress = new Progress();
            if ("java".equalsIgnoreCase(task.getSql_style())) {
                generator = new JavaDalGenerator();
                context = generator.createContext(task.getProject_id(), true, progress, true, true);
                generator.prepareData(context);
                JavaCodeGenContext ctx = (JavaCodeGenContext) context;
                Queue<JavaTableHost> tableHosts = ctx.getTableHosts();
                JavaTableHost tableHost = null;
                while ((tableHost = tableHosts.poll()) != null) {
                    List<JavaMethodHost> methods = tableHost.getMethods();
                    for (JavaMethodHost method : methods) {
                        if (task.getMethod_name().equalsIgnoreCase(method.getName())) {
                            String param = method.getParameterDeclaration();
                            param = param.replaceAll("<", "&lt;");
                            param = param.replaceAll(">", "&gt;");
                            return param;
                        }
                    }
                }
            } else {
                generator = new CSharpDalGenerator();
                context = generator.createContext(task.getProject_id(), true, progress, true, true);
                generator.prepareData(context);
                CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
                Queue<CSharpTableHost> tableHosts = ctx.getTableViewHosts();
                CSharpTableHost tableHost = null;
                while ((tableHost = tableHosts.poll()) != null) {
                    List<CSharpMethodHost> methods = tableHost.getExtraMethods();
                    for (CSharpMethodHost method : methods) {
                        if (task.getMethod_name().equalsIgnoreCase(method.getName())) {
                            String param = method.getParameterDeclaration();
                            param = param.replaceAll("<", "&lt;");
                            param = param.replaceAll(">", "&gt;");
                            return param;
                        }
                    }
                }
            }

        } catch (Exception e) {
        }
        return null;
    }

    private String buildFreesqlTaskMethodParam(GenTaskByFreeSql task) {
        try {
            DalGenerator generator = null;
            CodeGenContext context = null;
            Progress progress = new Progress();
            if ("java".equalsIgnoreCase(task.getSql_style())) {
                generator = new JavaDalGenerator();
                context = generator.createContext(task.getProject_id(), true, progress, true, true);
                generator.prepareData(context);
                JavaCodeGenContext ctx = (JavaCodeGenContext) context;
                Queue<FreeSqlHost> freeSqlHosts = ctx.getFreeSqlHosts();
                FreeSqlHost freeSqlHost = null;
                while ((freeSqlHost = freeSqlHosts.poll()) != null) {
                    List<JavaMethodHost> methods = freeSqlHost.getMethods();
                    for (JavaMethodHost method : methods) {
                        if (task.getMethod_name().equalsIgnoreCase(method.getName())) {
                            String param = method.getParameterDeclaration();
                            param = param.replaceAll("<", "&lt;");
                            param = param.replaceAll(">", "&gt;");
                            return param;
                        }
                    }
                }
            } else {
                generator = new CSharpDalGenerator();
                context = generator.createContext(task.getProject_id(), true, progress, true, true);
                generator.prepareData(context);
                CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
                Queue<CSharpFreeSqlHost> freeSqlHosts = ctx.getFreeSqlHosts();
                CSharpFreeSqlHost freeSqlHost = null;
                while ((freeSqlHost = freeSqlHosts.poll()) != null) {
                    List<CSharpMethodHost> methods = freeSqlHost.getMethods();
                    for (CSharpMethodHost method : methods) {
                        if (task.getMethod_name().equalsIgnoreCase(method.getName())) {
                            String param = method.getParameterDeclaration();
                            param = param.replaceAll("<", "&lt;");
                            param = param.replaceAll(">", "&gt;");
                            return param;
                        }
                    }
                }
            }

        } catch (Exception e) {
        }
        return null;
    }

    static class ApproveTaskDetail {
        private String attrName;
        private String attrValue;

        public ApproveTaskDetail() {
            super();
        }

        public ApproveTaskDetail(String attrName, String attrValue) {
            super();
            this.attrName = attrName;
            this.attrValue = attrValue;
        }

        public String getAttrName() {
            return attrName;
        }

        public void setAttrName(String attrName) {
            this.attrName = attrName;
        }

        public String getAttrValue() {
            return attrValue;
        }

        public void setAttrValue(String attrValue) {
            this.attrValue = attrValue;
        }
    }

}
