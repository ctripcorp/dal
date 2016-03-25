package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.List;

@Resource
@Singleton
@Path("group")
public class DalGroupResource {
    private static Logger log = Logger.getLogger(DalGroupResource.class);
    public static final int SUPER_GROUP_ID = 1; // The default supper user group

    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroup> getAllGroup() {
        List<DalGroup> groups = SpringBeanGetter.getDaoOfDalGroup().getAllGroups();
        return groups;
    }

    @GET
    @Path("onegroup")
    @Produces(MediaType.APPLICATION_JSON)
    public DalGroup getProject(@QueryParam("id") String id) {
        return SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(Integer.valueOf(id));
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
                      @FormParam("groupComment") String groupComment) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null || groupName == null || groupName.isEmpty()) {
            log.error(String.format(
                    "Add dal group failed, caused by illegal parameters: " + "[groupName=%s, groupComment=%s]",
                    groupName, groupComment));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (!this.validate(userNo)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的操作权限.");
            return status;
        }

        DalGroup group = new DalGroup();
        group.setGroup_name(groupName);
        group.setGroup_comment(groupComment);
        group.setCreate_user_no(userNo);
        group.setCreate_time(new Timestamp(System.currentTimeMillis()));

        int ret = SpringBeanGetter.getDaoOfDalGroup().insertDalGroup(group);
        if (ret <= 0) {
            log.error("Add dal group failed, caused by db operation failed, pls check the spring log");
            Status status = Status.ERROR;
            status.setInfo("Add operation failed.");
            return status;
        }
        return Status.OK;
    }

    @POST
    @Path("delete")
    public Status delete(@Context HttpServletRequest request, @FormParam("id") String id) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null || id == null || id.isEmpty()) {
            log.error(String.format("Delete dal group failed, caused by illegal parameters " + "[ids=%s]", id));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (!this.validate(userNo)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的操作权限.");
            return status;
        }
        int groupId = -1;
        try {
            groupId = Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            log.error("Delete dal group failed", ex);
            Status status = Status.ERROR;
            status.setInfo("Illegal group id");
            return status;
        }

        List<Project> prjs = SpringBeanGetter.getDaoOfProject().getProjectByGroupId(groupId);
        if (prjs != null && prjs.size() > 0) {
            Status status = Status.ERROR;
            status.setInfo("当前DAL Team中还有Project，请清空Project后再操作！");
            return status;
        }
        List<DalGroupDB> dbs = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(groupId);
        if (dbs != null && dbs.size() > 0) {
            Status status = Status.ERROR;
            status.setInfo("当前DAL Team中还有DataBase，请清空DataBase后再操作！");
            return status;
        }
        List<DatabaseSet> dbsets = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByGroupId(groupId);
        if (dbsets != null && dbsets.size() > 0) {
            Status status = Status.ERROR;
            status.setInfo("当前DAL Team中还有DataBaseSet，请清空DataBaseSet后再操作！");
            return status;
        }
        List<LoginUser> us = SpringBeanGetter.getDaoOfLoginUser().getUserByGroupId(groupId);
        if (us != null && us.size() > 0) {
            Status status = Status.ERROR;
            status.setInfo("当前DAL Team中还有Member，请清空Member后再操作！");
            return status;
        }

        int ret = SpringBeanGetter.getDaoOfDalGroup().deleteDalGroup(groupId);
        if (ret <= 0) {
            log.error("Delete dal group failed, caused by db operation failed, pls check the spring log");
            Status status = Status.ERROR;
            status.setInfo("Delete operation failed.");
            return status;
        }
        return Status.OK;
    }

    @POST
    @Path("update")
    public Status update(@Context HttpServletRequest request, @FormParam("groupId") String id,
                         @FormParam("groupName") String groupName, @FormParam("groupComment") String groupComment) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null || id == null || id.isEmpty()) {
            log.error(String.format("Update dal group failed, caused by illegal parameters, "
                    + "[id=%s, groupName=%s, groupComment=%s]", id, groupName, groupComment));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (!this.validate(userNo)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的操作权限.");
            return status;
        }

        int groupId = -1;
        try {
            groupId = Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            log.error("Update dal group failed", ex);
            Status status = Status.ERROR;
            status.setInfo("Illegal group id");
            return status;
        }

        DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupId);
        if (null == group) {
            log.error("Update dal group failed, caused by group_id specifed not existed.");
            Status status = Status.ERROR;
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

        int ret = SpringBeanGetter.getDaoOfDalGroup().updateDalGroup(group);

        if (ret <= 0) {
            log.error("Delete dal group failed, caused by db operation failed, pls check the spring log");
            Status status = Status.ERROR;
            status.setInfo("update operation failed.");
            return status;
        }
        return Status.OK;
    }

    public static boolean validate(String userNo) {
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        List<UserGroup> groups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (groups != null && groups.size() > 0) {
            for (UserGroup group : groups) {
                if (group.getGroup_id() == SUPER_GROUP_ID) {
                    return true;
                }
            }
        }
        return false;
    }
}
