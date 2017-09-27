package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.Configuration;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Resource
@Singleton
@Path("group")
public class DalGroupResource {
    public static final int SUPER_GROUP_ID = 1; // The default supper user group

    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroup> getAllGroup() throws SQLException {
        try {
            List<DalGroup> groups = BeanGetter.getDaoOfDalGroup().getAllGroups();
            return groups;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Path("onegroup")
    @Produces(MediaType.APPLICATION_JSON)
    public DalGroup getProject(@QueryParam("id") String id) throws SQLException {
        try {
            return BeanGetter.getDaoOfDalGroup().getDalGroupById(Integer.valueOf(id));
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @POST
    @Path("keepSession")
    @Produces(MediaType.APPLICATION_JSON)
    public String keepSession(@FormParam("id") String id) {
        return "true";
    }

    @POST
    @Path("add")
    public Status add(@Context HttpServletRequest request, @FormParam("groupName") String groupName,
            @FormParam("groupComment") String groupComment) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null || groupName == null || groupName.isEmpty()) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            if (!this.validate(userNo)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限.");
                return status;
            }

            DalGroup group = new DalGroup();
            group.setGroup_name(groupName);
            group.setGroup_comment(groupComment);
            group.setCreate_user_no(userNo);
            group.setCreate_time(new Timestamp(System.currentTimeMillis()));

            int ret = BeanGetter.getDaoOfDalGroup().insertDalGroup(group);
            if (ret <= 0) {
                Status status = Status.ERROR();
                status.setInfo("Add operation failed.");
                return status;
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
    @Path("delete")
    public Status delete(@Context HttpServletRequest request, @FormParam("id") String id) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null || id == null || id.isEmpty()) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            if (!this.validate(userNo)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限.");
                return status;
            }
            int groupId = -1;
            try {
                groupId = Integer.parseInt(id);
            } catch (NumberFormatException ex) {
                Status status = Status.ERROR();
                status.setInfo("Illegal group id");
                return status;
            }

            List<Project> prjs = BeanGetter.getDaoOfProject().getProjectByGroupId(groupId);
            if (prjs != null && prjs.size() > 0) {
                Status status = Status.ERROR();
                status.setInfo("当前DAL Team中还有Project，请清空Project后再操作！");
                return status;
            }
            List<DalGroupDB> dbs = BeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(groupId);
            if (dbs != null && dbs.size() > 0) {
                Status status = Status.ERROR();
                status.setInfo("当前DAL Team中还有DataBase，请清空DataBase后再操作！");
                return status;
            }
            List<DatabaseSet> dbsets = BeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByGroupId(groupId);
            if (dbsets != null && dbsets.size() > 0) {
                Status status = Status.ERROR();
                status.setInfo("当前DAL Team中还有DataBaseSet，请清空DataBaseSet后再操作！");
                return status;
            }
            List<LoginUser> us = BeanGetter.getDaoOfLoginUser().getUserByGroupId(groupId);
            if (us != null && us.size() > 0) {
                Status status = Status.ERROR();
                status.setInfo("当前DAL Team中还有Member，请清空Member后再操作！");
                return status;
            }

            int ret = BeanGetter.getDaoOfDalGroup().deleteDalGroup(groupId);
            if (ret <= 0) {
                Status status = Status.ERROR();
                status.setInfo("Delete operation failed.");
                return status;
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
    @Path("update")
    public Status update(@Context HttpServletRequest request, @FormParam("groupId") String id,
            @FormParam("groupName") String groupName, @FormParam("groupComment") String groupComment) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null || id == null || id.isEmpty()) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            if (!this.validate(userNo)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限.");
                return status;
            }

            int groupId = -1;
            try {
                groupId = Integer.parseInt(id);
            } catch (NumberFormatException ex) {
                Status status = Status.ERROR();
                status.setInfo("Illegal group id");
                return status;
            }

            DalGroup group = BeanGetter.getDaoOfDalGroup().getDalGroupById(groupId);
            if (null == group) {
                Status status = Status.ERROR();
                status.setInfo("Group id not existed");
                return status;
            }
            if (null != groupName && !groupName.trim().isEmpty()) {
                group.setGroup_name(groupName);
            }
            if (null != groupComment && !groupComment.trim().isEmpty()) {
                group.setGroup_comment(groupComment);
            }

            group.setCreate_time(new Timestamp(System.currentTimeMillis()));

            int ret = BeanGetter.getDaoOfDalGroup().updateDalGroup(group);

            if (ret <= 0) {
                Status status = Status.ERROR();
                status.setInfo("update operation failed.");
                return status;
            }
            return Status.OK();
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    public static boolean validate(String userNo) throws SQLException {
        LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        List<UserGroup> groups = BeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (groups != null && groups.size() > 0) {
            for (UserGroup group : groups) {
                if (group.getGroup_id() == SUPER_GROUP_ID) {
                    return true;
                }
            }
        }
        return false;
    }

    @GET
    @Path("getDalTeamEmail")
    public String getDalTeamEmail() {
        return Configuration.get("dal_team_email");
    }

}
