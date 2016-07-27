package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.utils.Configuration;
import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpDalGenerator;
import com.ctrip.platform.dal.daogen.generator.java.JavaDalGenerator;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Resource
@Singleton
@Path("project")
public class ProjectResource {
    private static final String JAVA = "java";
    private static final String CS = "csharp";

    private static Logger log = Logger.getLogger(ProjectResource.class);

    @GET
    @Path("users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LoginUser> getUsers() {
        return SpringBeanGetter.getDaoOfLoginUser().getAllUsers();
    }

    @GET
    @Path("userGroups")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroup> getUserGroups(@Context HttpServletRequest request, @QueryParam("root") boolean root) {
        String userNo = RequestUtil.getUserNo(request);
        LoginUser user = null;
        try {
            user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        } catch (Exception e) {
            log.warn("", e);
        }
        if (user == null) {
            user = new LoginUser();
            user.setUserNo(userNo);
            user.setUserName(CustomizedResource.getInstance().getName(null));
            user.setUserEmail(CustomizedResource.getInstance().getMail(null));
            try {
                SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
                user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            } catch (Exception e) {
                log.warn("", e);
            }
        }

        List<DalGroup> groups = new ArrayList<>();
        List<UserGroup> userGroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (userGroups != null && userGroups.size() >= 1) {
            Set<Integer> groupIds = new HashSet<>();
            for (UserGroup userGroup : userGroups) {
                groupIds.add(userGroup.getGroup_id());
            }
            groups = getAllJoinedDalGroup(groupIds);
        } else {
            DalGroup group = new DalGroup();
            group.setText("请先加入DAL Team");
            group.setIcon("glyphicon glyphicon-folder-open");
            group.setChildren(true);
            groups.add(group);
        }

        return groups;
    }

    private List<DalGroup> getAllJoinedDalGroup(Set<Integer> groupIds) {
        Set<Integer> parentGroupIds = new HashSet<>();
        for (Integer childGroupId : groupIds) {
            List<GroupRelation> relations = SpringBeanGetter.getGroupRelationDao().getAllGroupRelationByChildGroupId(childGroupId);
            if (relations == null || relations.size() < 1) {
                continue;
            }
            for (GroupRelation relation : relations) {
                parentGroupIds.add(relation.getCurrent_group_id());
            }
        }
        // the group that user have joined
        groupIds.addAll(parentGroupIds);
        List<DalGroup> groups = new ArrayList<>();
        for (Integer groupId : groupIds) {
            DalGroup dalGroup = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupId);
            dalGroup.setText(dalGroup.getGroup_name());
            dalGroup.setIcon("glyphicon glyphicon-folder-open");
            dalGroup.setChildren(true);
            groups.add(dalGroup);
        }
        return groups;
    }

    @GET
    @Path("groupprojects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Project> getGroupProjects(@QueryParam("groupId") String groupId) {
        int groupID = -1;
        try {
            groupID = Integer.parseInt(groupId);
        } catch (NumberFormatException ex) {
            log.error("Add member failed", ex);
            Status status = Status.ERROR;
            status.setInfo("Illegal group id");
            return null;
        }

        List<Project> list = SpringBeanGetter.getDaoOfProject().getProjectByGroupId(groupID);
        return list;
    }

    @GET
    @Path("project")
    @Produces(MediaType.APPLICATION_JSON)
    public Project getProject(@QueryParam("id") String id) {
        return SpringBeanGetter.getDaoOfProject().getProjectByID(Integer.valueOf(id));
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Status addProject(@Context HttpServletRequest request, @FormParam("id") int id, @FormParam("name") String name, @FormParam("namespace") String namespace, @FormParam("dalconfigname") String dalconfigname, @FormParam("action") String action, @FormParam("project_group_id") int project_group_id) {
        Project proj = new Project();
        String userNo = RequestUtil.getUserNo(request);
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);

        if (user == null) {
            Status status = Status.ERROR;
            status.setInfo("You have not login.");
            return status;
        }

        List<UserGroup> urGroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());

        if (urGroups == null || urGroups.size() < 1) {
            Status status = Status.ERROR;
            status.setInfo("请先加入某个DAL Team.");
            return status;
        }

        if (action.equals("insert")) {
            List<Project> pjs = SpringBeanGetter.getDaoOfProject().getProjectByConfigname(dalconfigname);
            if (null != pjs && pjs.size() > 0) {
                Status status = Status.ERROR;
                status.setInfo("Dal.config Name --> " + dalconfigname + " 已经存在，请重新命名!");
                return status;
            }
            proj.setName(name);
            proj.setNamespace(namespace);
            proj.setDal_config_name(dalconfigname);
            proj.setDal_group_id(project_group_id);
            proj.setUpdate_user_no(user.getUserName() + "(" + userNo + ")");
            proj.setUpdate_time(new Timestamp(System.currentTimeMillis()));
            SpringBeanGetter.getDaoOfProject().insertProject(proj);
            return Status.OK;
        }

        if (!validateProjectUpdatePermision(userNo, id, project_group_id)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前Project的操作权限.");
            return status;
        }

        if (action.equals("update")) {
            List<Project> pjs = SpringBeanGetter.getDaoOfProject().getProjectByConfigname(dalconfigname);
            if (null != pjs && pjs.size() > 0) {
                for (Project temp : pjs) {
                    if (temp.getId() != id) {
                        Status status = Status.ERROR;
                        status.setInfo("Dal.config Name --> " + dalconfigname + " 已经存在，请重新命名!");
                        return status;
                    }
                }
            }
            proj.setId(id);
            proj.setName(name);
            proj.setNamespace(namespace);
            proj.setDal_config_name(dalconfigname);
            proj.setUpdate_user_no(user.getUserName() + "(" + userNo + ")");
            proj.setUpdate_time(new Timestamp(System.currentTimeMillis()));
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

    @POST
    @Path("projectPermisionCheck")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Status projectPermisionCheck(@Context HttpServletRequest request, @FormParam("prjId") int prjId) {
        String userNo = RequestUtil.getUserNo(request);

        if (!validateProjectUpdatePermision(userNo, prjId, -1)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAO的操作权限.");
            return status;
        }
        return Status.OK;
    }

    @POST
    @Path("eraseFiles")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Status eraseFiles(@FormParam("prjId") int prjId) {
        String path = Configuration.get("gen_code_path");
        File dir = new File(String.format("%s/%s", path, prjId));
        if (dir.exists())
            try {
                FileUtils.forceDelete(dir);
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        return Status.OK;
    }

    private boolean validateProjectUpdatePermision(String userNo, int prjId, int project_group_id) {
        boolean havePermision = false;
        project_group_id = SpringBeanGetter.getDaoOfProject().getProjectByID(prjId).getDal_group_id();
        havePermision = validateProjectUpdatePermisionInCurrentGroup(userNo, prjId, project_group_id);
        if (havePermision) {
            return havePermision;
        }
        havePermision = validateProjectUpdatePermisionInChildGroup(userNo, prjId, project_group_id);
        return havePermision;
    }

    private boolean validateProjectUpdatePermisionInCurrentGroup(String userNo, int prjId, int project_group_id) {
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        List<UserGroup> userGroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(project_group_id, user.getId());
        for (UserGroup userGroup : userGroups) {
            if (userGroup.getRole() == 1) {// the user is the original team user, at
                // the same time is also the admin of
                // current team
                return true;
            }
        }

        Project prj = SpringBeanGetter.getDaoOfProject().getProjectByID(prjId);
        String update_user_no = user.getUserName() + "(" + userNo + ")";
        // the user is the original team user, but is not admin, so can only
        // modify prj which is created by himself
        if (update_user_no.equalsIgnoreCase(prj.getUpdate_user_no())) {
            return true;
        }

        return false;
    }

    private boolean validateProjectUpdatePermisionInChildGroup(String userNo, int prjId, int project_group_id) {
        List<GroupRelation> relations = SpringBeanGetter.getGroupRelationDao().getAllGroupRelationByCurrentGroupId(project_group_id);
        if (relations == null || relations.size() < 1) {
            return false;
        }

        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);

        for (GroupRelation relation : relations) {
            if (relation.getChild_group_role() == 1) {// the child group, but
                // have admin role
                // permision
                List<UserGroup> exists = SpringBeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(relation.getChild_group_id(), user.getId());
                if (exists != null && exists.size() > 0) { // the user is in the
                    // specific group
                    // whic have
                    // permison to
                    // modify the prj
                    return true;
                }
            } else { // the child group which only have limited permison
                Project prj = SpringBeanGetter.getDaoOfProject().getProjectByID(prjId);
                String updateUserNo = prj.getUpdate_user_no();
                if (updateUserNo == null || updateUserNo.isEmpty()) { // the prj have no
                    // update user
                    // info
                    // check the user is or not in the current group
                    int userId = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
                    List<UserGroup> check = SpringBeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(project_group_id, userId);
                    if (check != null && check.size() > 0) {
                        return true;
                    }
                    return false;
                }

                Pattern pattern = Pattern.compile(".+\\((\\w+)\\).*");
                Matcher m = pattern.matcher(updateUserNo);
                String upNo = "";
                if (m.find()) {
                    upNo = m.group(1);
                }
                if (upNo.equalsIgnoreCase(userNo)) {
                    return true;
                }
                // the owner of the current project
                LoginUser currentPrjUser = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(upNo);
                if (currentPrjUser == null) {
                    return false;
                }
                // the group that the owner of the current prj have been joined
                // in
                List<UserGroup> userGroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(currentPrjUser.getId());
                if (userGroups == null || userGroups.size() < 1) {
                    return false;
                }

                // now check, the user who want to modify the prj is or not in
                // the same group compare with the current prj owner
                int userId = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
                Set<Integer> childGroupIds = getChildGroupId(project_group_id);
                for (UserGroup userGroup : userGroups) {
                    if (!childGroupIds.contains(userGroup.getGroup_id())) {
                        continue;
                    }
                    List<UserGroup> exists = SpringBeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(userGroup.getGroup_id(), userId);
                    if (exists != null && exists.size() > 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Set<Integer> getChildGroupId(int currentGroupId) {
        Set<Integer> sets = new HashSet<>();
        List<GroupRelation> relations = SpringBeanGetter.getGroupRelationDao().getAllGroupRelationByCurrentGroupId(currentGroupId);
        if (relations == null) {
            return sets;
        }

        for (GroupRelation relation : relations) {
            sets.add(relation.getChild_group_id());
        }
        return sets;
    }

    /**
     * 一键添加project缺失的databaseSet
     *
     * @param project_id
     * @return
     */
    @POST
    @Path("addLackDbset")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Status addLackDbset(@FormParam("project_id") int project_id) {
        int groupId = SpringBeanGetter.getDaoOfProject().getProjectByID(project_id).getDal_group_id();
        String info = addLackDb(project_id, groupId);

        Set<String> notExistDbset = getLackDbset(groupId, project_id);
        for (String dbsetName : notExistDbset) {
            List<DatabaseSet> dbsets = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbsetName);
            if (null != dbsets && dbsets.size() > 0) {
                info += "<span style='color:red;'>databaseSet Name --> " + dbsetName + " 已经存在，请重新命名，再手动添加!" + "</span><br/>";
            } else {
                List<String> dbAllinOneNames = SpringBeanGetter.getDaoOfDalGroupDB().getAllDbAllinOneNames();
                Set<String> allInOneDbnames = new HashSet<String>(dbAllinOneNames);
                if (allInOneDbnames.contains(dbsetName)) {
                    info += genDefaultDbset(groupId, dbsetName);
                } else {
                    info += "<span style='color:red;'>databaseSet Name --> " + dbsetName + "在数据库中不存在，请手动添加!" + "</span><br/>";
                }
            }
        }
        if (!"".equals(info)) {
            info += "点击此处添加databaseSet ： <a href='dbsetsmanage.jsp' target='_blank'>逻辑数据库管理</a><br/>";
            info += "点击此处添加组内database ： <a href='dbmanage.jsp' target='_blank'>数据库管理</a><br/>";
            Status status = Status.ERROR;
            status.setInfo(info);
            return status;
        } else {
            Status status = Status.OK;
            status.setInfo("一键补全成功！");
            return status;
        }
    }

    /**
     * 生成默认的databaseSet和databaseSet Entry
     *
     * @param dbname
     */
    private String genDefaultDbset(int groupId, String dbname) {
        DatabaseSet dbset = new DatabaseSet();
        dbset.setName(dbname);
        dbset.setProvider("sqlProvider");
        dbset.setGroupId(groupId);
        String info = "";
        int ret = SpringBeanGetter.getDaoOfDatabaseSet().insertDatabaseSet(dbset);
        if (ret > 0) {
            info += "databaseSet-->" + dbname + "一键补全成功!<br/>";
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

    private String addLackDb(int project_id, int groupId) {
        Set<String> notExistDb = getLackDatabase(groupId, project_id);
        List<String> dbAllinOneNames = SpringBeanGetter.getDaoOfDalGroupDB().getAllDbAllinOneNames();
        Set<String> allInOneDbnames = new HashSet<>(dbAllinOneNames);

        String info = "";
        for (String dbname : notExistDb) {
            DalGroupDB groupdb = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBByDbName(dbname);
            if (null != groupdb && groupdb.getDal_group_id() > 0) {
                DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupdb.getDal_group_id());
                info += "<span style='color:red;'>数据库" + groupdb.getDbname() + " 已经加入 " + group.getGroup_comment() + "，一键补全失败</span><br/>";
            } else if (allInOneDbnames.contains(dbname)) {
                DalGroupDB groupDb = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBByDbName(dbname);
                int ret = SpringBeanGetter.getDaoOfDalGroupDB().updateGroupDB(groupDb.getId(), groupId);
                if (ret <= 0) {
                    info += "<span style='color:red;'>数据库" + dbname + "一键补全失败，请手动添加。" + "</span><br/>";
                } else {
                    info += "数据库" + dbname + "一键补全成功。" + "<br/>";
                }
            } else {
                info += "<span style='color:red;'>数据库" + dbname + "一键补全失败，请手动添加。" + "</span><br/>";
            }
        }
        return info;
    }

    @POST
    @Path("generate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Status generateProject(@Context HttpServletRequest request, @FormParam("project_id") int id, @FormParam("regenerate") boolean regen, @FormParam("language") String language, @FormParam("newPojo") boolean newPojo, @FormParam("random") String random) {
        Status status = null;
        String userNo = RequestUtil.getUserNo(request);
        Progress progress = ProgressResource.getProgress(userNo, id, random);

        status = validatePermision(userNo, id);
        if (status.getCode().equals(Status.ERROR.getCode())) {
            progress.setStatus(ProgressResource.FINISH);
            return status;
        }

        try {
            log.info(String.format("begain generate project: [id=%s; regen=%s; language=%s]", id, true, language));

            DalGenerator generator = null;
            CodeGenContext context = null;
            HashSet<String> hashSet = getProjectSqlStyles(id);
            String code = "";
            if (hashSet.contains(JAVA)) {
                code = JAVA;
                generator = new JavaDalGenerator();
                context = generator.createContext(id, true, progress, newPojo, false);
                generateLanguageProject(generator, context);
            }
            if (hashSet.contains(CS)) { // cs
                code = "cs";
                generator = new CSharpDalGenerator();
                context = generator.createContext(id, true, progress, newPojo, false);
                generateLanguageProject(generator, context);
            }
            status = Status.OK;
            status.setInfo(code);
            log.info(String.format("generate project[%s] completed.", id));
        } catch (Exception e) {
            status = Status.ERROR;
            status.setInfo(e.getMessage());
            progress.setOtherMessage(e.getMessage());
            log.error(String.format("generate project[%s] failed.", id), e);
        } finally {
            progress.setStatus(ProgressResource.FINISH);
        }

        return status;
    }

    private void generateLanguageProject(DalGenerator generator, CodeGenContext context) throws Exception {
        if (generator == null || context == null) {
            return;
        }
        generator.prepareDirectory(context);
        generator.prepareData(context);
        generator.generateCode(context);
    }

    private HashSet<String> getProjectSqlStyles(int projectId) {
        HashSet<String> hashSet = new HashSet<>();
        List<GenTaskBySqlBuilder> autoTasks = SpringBeanGetter.getDaoBySqlBuilder().getTasksByProjectId(projectId);

        if (autoTasks != null && autoTasks.size() > 0) {
            for (GenTaskBySqlBuilder genTaskBySqlBuilder : autoTasks) {
                hashSet.add(genTaskBySqlBuilder.getSql_style());
            }
        }

        List<GenTaskByTableViewSp> tableViewSpTasks = SpringBeanGetter.getDaoByTableViewSp().getTasksByProjectId(projectId);

        if (tableViewSpTasks != null && tableViewSpTasks.size() > 0) {
            for (GenTaskByTableViewSp genTaskByTableViewSp : tableViewSpTasks) {
                hashSet.add(genTaskByTableViewSp.getSql_style());
            }
        }

        List<GenTaskByFreeSql> sqlTasks = SpringBeanGetter.getDaoByFreeSql().getTasksByProjectId(projectId);

        if (sqlTasks != null && sqlTasks.size() > 0) {
            for (GenTaskByFreeSql genTaskByFreeSql : sqlTasks) {
                hashSet.add(genTaskByFreeSql.getSql_style());
            }
        }

        return hashSet;
    }

    private Status validatePermision(String userNo, int project_id) {
        Status status = Status.ERROR;
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        List<UserGroup> urGroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (urGroups == null) {
            status.setInfo("你没有权限生成代码.请先加入一个 DAL Team.");
            return status;
        }
        if (urGroups.size() < 1) {
            status.setInfo("你没有权限生成代码.请先加入一个 DAL Team.");
            return status;
        }
        int groupId = -1;
        groupId = SpringBeanGetter.getDaoOfProject().getProjectByID(project_id).getDal_group_id();
        String info = "";
        // 验证project的task所需要的databaseSet在组内是否存在
        status = validateDbsetPermision(groupId, project_id);
        if (status.getCode().equals(Status.ERROR.getCode())) {
            info = status.getInfo();
        }
        // 验证project的task所需要的database在组内是否存在
        status = validateDbPermision(groupId, project_id);
        if (status.getCode().equals(Status.ERROR.getCode())) {
            info += "</br>" + status.getInfo();
        }
        if (!"".equals(info)) {
            status = Status.ERROR;
            status.setInfo(info);
            return status;
        }

        return Status.OK;
    }

    private Status validateDbsetPermision(int groupId, int project_id) {
        Status status = Status.ERROR;
        Set<String> notExistDbset = getLackDbset(groupId, project_id);

        if (notExistDbset == null || notExistDbset.size() <= 0) {
            return Status.OK;
        } else {
            String info = "<ul>";
            for (String temp : notExistDbset) {
                info += "<li>" + temp + "</li>";
            }
            info += "</ul>";
            DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupId);
            info = "你所在" + group.getGroup_name() + "中不存在以下逻辑数据库(databaseSet)：</br>" + info
                    + "请先添加逻辑数据库(databaseSet)到你所在DAL Team!</br>"
                    + "点击此处添加逻辑数据库(databaseSet) ： <a href='dbsetsmanage.jsp' target='_blank'>逻辑数据库管理</a>";
            status.setInfo(info);
            return status;
        }
    }

    private Set<String> getLackDbset(int groupId, int project_id) {
        List<DatabaseSet> groupDbsets = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByGroupId(groupId);
        Set<String> group_dbset_names = new HashSet<>();
        for (DatabaseSet dbset : groupDbsets) {
            group_dbset_names.add(dbset.getName());
        }

        Set<String> notExistDbset = new HashSet<>();
        List<GenTaskBySqlBuilder> autoTasks = SpringBeanGetter.getDaoBySqlBuilder().getTasksByProjectId(project_id);
        for (GenTaskBySqlBuilder task : autoTasks) {
            String databaseSet_name = task.getDatabaseSetName();
            if (!group_dbset_names.contains(databaseSet_name)) {
                notExistDbset.add(databaseSet_name);
            }
        }

        List<GenTaskByTableViewSp> tableViewSpTasks = SpringBeanGetter.getDaoByTableViewSp().getTasksByProjectId(project_id);
        for (GenTaskByTableViewSp task : tableViewSpTasks) {
            String databaseSet_name = task.getDatabaseSetName();
            if (!group_dbset_names.contains(databaseSet_name)) {
                notExistDbset.add(databaseSet_name);
            }
        }

        List<GenTaskByFreeSql> sqlTasks = SpringBeanGetter.getDaoByFreeSql().getTasksByProjectId(project_id);
        for (GenTaskByFreeSql task : sqlTasks) {
            String databaseSet_name = task.getDatabaseSetName();
            if (!group_dbset_names.contains(databaseSet_name)) {
                notExistDbset.add(databaseSet_name);
            }
        }

        return notExistDbset;
    }

    private Status validateDbPermision(int groupId, int project_id) {
        Status status = Status.ERROR;
        Set<String> notExistDb = getLackDatabase(groupId, project_id);

        if (notExistDb == null || notExistDb.size() <= 0) {
            return Status.OK;
        } else {
            String info = "<ul>";
            for (String temp : notExistDb) {
                info += "<li>" + temp + "</li>";
            }
            info += "</ul>";
            DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupId);
            info = "你所在" + group.getGroup_name() + "中不存在以下数据库(database)：</br>" + info
                    + "请先添加数据库(database)到你所在DAL Team!</br>"
                    + "点击此处添加组内数据库(database) ： <a href='dbmanage.jsp' target='_blank'>数据库管理</a>";
            status.setInfo(info);
            return status;
        }
    }

    private Set<String> getLackDatabase(int groupId, int project_id) {
        List<DalGroupDB> groupDbs = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(groupId);
        Set<String> group_db_names = new HashSet<>();
        for (DalGroupDB db : groupDbs) {
            group_db_names.add(db.getDbname());
        }

        Set<String> allRequiredDb = getProjectAllRequireDbname(project_id);
        Set<String> notExistDb = new HashSet<>();
        for (String db : allRequiredDb) {
            if (!group_db_names.contains(db)) {
                notExistDb.add(db);
            }
        }
        return notExistDb;
    }

    private Set<String> getProjectAllRequireDbname(int project_id) {
        List<GenTaskBySqlBuilder> autoTasks = SpringBeanGetter.getDaoBySqlBuilder().getTasksByProjectId(project_id);
        List<GenTaskByTableViewSp> tableViewSpTasks = SpringBeanGetter.getDaoByTableViewSp().getTasksByProjectId(project_id);
        List<GenTaskByFreeSql> sqlTasks = SpringBeanGetter.getDaoByFreeSql().getTasksByProjectId(project_id);
        List<String> allRequireDbset = new ArrayList<>();

        for (GenTaskBySqlBuilder temp : autoTasks) {
            allRequireDbset.add(temp.getDatabaseSetName());
        }
        for (GenTaskByTableViewSp temp : tableViewSpTasks) {
            allRequireDbset.add(temp.getDatabaseSetName());
        }
        for (GenTaskByFreeSql temp : sqlTasks) {
            allRequireDbset.add(temp.getDatabaseSetName());
        }
        Set<String> result = new HashSet<>();
        for (String dbsetName : allRequireDbset) {
            List<DatabaseSet> dbset = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbsetName);
            if (dbset != null && dbset.size() > 0) {
                List<DatabaseSetEntry> dbentrys = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetEntryByDbsetid(dbset.get(0).getId());
                for (DatabaseSetEntry entry : dbentrys) {
                    result.add(entry.getConnectionString());
                }
            } else {
                List<String> dbAllinOneNames = SpringBeanGetter.getDaoOfDalGroupDB().getAllDbAllinOneNames();
                Set<String> allInOneDbnames = new HashSet<>(dbAllinOneNames);
                if (allInOneDbnames.contains(dbsetName)) {
                    result.add(dbsetName);
                }
            }
        }
        return result;
    }

}
