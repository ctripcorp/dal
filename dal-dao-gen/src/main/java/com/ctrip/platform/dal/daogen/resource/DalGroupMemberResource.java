package com.ctrip.platform.dal.daogen.resource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.GroupRelation;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.entity.UserGroup;
import com.ctrip.platform.dal.daogen.entity.UserProject;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

/**
 * DAL Member Manage.
 *
 * @author gzxia
 * @modified yn.wang
 */

@Resource
@Singleton
@Path("member")
public class DalGroupMemberResource {
    private static Logger log = Logger.getLogger(DalGroupMemberResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroup> getGroups(@Context HttpServletRequest request, @QueryParam("root") boolean root) {
        List<DalGroup> groups = SpringBeanGetter.getDaoOfDalGroup().getAllGroups();

        for (DalGroup group : groups) {
            group.setText(group.getGroup_name());
            group.setIcon("glyphicon glyphicon-th");
            group.setChildren(false);
        }

        String userNo = RequestUtil.getUserNo(request);
        return sortGroups(groups, userNo);
    }

    private List<DalGroup> sortGroups(List<DalGroup> groups, String userNo) {
        List<DalGroup> result = new ArrayList<>(groups.size());
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        List<UserGroup> joinedGroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (joinedGroups != null && joinedGroups.size() > 0) {
            for (UserGroup joinedGroup : joinedGroups) {
                Iterator<DalGroup> ite = groups.iterator();
                while (ite.hasNext()) {
                    DalGroup group = ite.next();
                    if (group.getId() == joinedGroup.getGroup_id()) {
                        result.add(group);
                        ite.remove();
                    }
                }
            }
            result.addAll(groups);
        }
        return result;
    }

    @GET
    @Path("groupuser")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LoginUser> getGroupUsers(@QueryParam("groupId") int currentGroupId) {
        List<LoginUser> users = SpringBeanGetter.getDaoOfLoginUser().getUserByGroupId(currentGroupId);
        if (users != null && users.size() > 0) {
            for (LoginUser user : users) {
                if ("1".equalsIgnoreCase(user.getRole())) {
                    user.setRole("Admin");
                } else if ("2".equalsIgnoreCase(user.getRole())) {
                    user.setRole("Limited");
                } else {
                    user.setRole("Unkown");
                }
                if ("1".equalsIgnoreCase(user.getAdduser())) {
                    user.setAdduser("允许");
                } else {
                    user.setAdduser("禁止");
                }
            }
        }

        List<GroupRelation> relations = SpringBeanGetter.getGroupRelationDao()
                .getAllGroupRelationByCurrentGroupId(currentGroupId);
        if (relations != null && relations.size() > 0) {
            for (GroupRelation relation : relations) {
                DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(relation.getChild_group_id());
                if (group != null) {
                    LoginUser user = new LoginUser();
                    user.setId(group.getId());
                    user.setUserName(group.getGroup_name());
                    user.setUserEmail(group.getGroup_comment());
                    if (1 == relation.getChild_group_role()) {
                        user.setRole("Admin");
                    } else if (2 == relation.getChild_group_role()) {
                        user.setRole("Limited");
                    } else {
                        user.setRole("Unkown");
                    }
                    if (1 == relation.getAdduser()) {
                        user.setAdduser("允许");
                    } else {
                        user.setAdduser("禁止");
                    }
                    user.setDalTeam(true);
                    users.add(user);
                }
            }
        }

        return users;
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LoginUser> getAllUsers() {
        List<LoginUser> users = SpringBeanGetter.getDaoOfLoginUser().getAllUsers();
        return users;
    }

    @POST
    @Path("addUser")
    public Status addUser(@Context HttpServletRequest request, @FormParam("groupId") int currentGroupId,
                          @FormParam("userId") int userID, @FormParam("user_role") int user_role,
                          @FormParam("allowAddUser") boolean allowAddUser) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null) {
            log.error(String.format("Add member failed, caused by illegal parameters:userNo=%s", userNo));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (!this.validatePermision(userNo, currentGroupId)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的组员操作权限.");
            return status;
        }

        if (!this.validatePermision(userNo, currentGroupId, user_role)) {
            Status status = Status.ERROR;
            status.setInfo("你所授予的权限大于你所拥有的权限.");
            return status;
        }

        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserById(userID);
        List<UserGroup> ugGroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        Iterator<UserGroup> ite = ugGroups.iterator();
        while (ite.hasNext()) {
            if (ite.next().getGroup_id() == currentGroupId) {
                Status status = Status.ERROR;
                status.setInfo("用户[" + user.getUserName() + "]已经加入当前DAL Team.");
                return status;
            }
        }
        int adduser = allowAddUser == true ? 1 : 2;
        int ret = SpringBeanGetter.getDalUserGroupDao().insertUserGroup(userID, currentGroupId, user_role, adduser);
        if (ret <= 0) {
            log.error("Add dal group member failed, caused by db operation failed, pls check the log.");
            Status status = Status.ERROR;
            status.setInfo("Add operation failed.");
            return status;
        } else {
            transferProjectToGroup(user.getUserNo(), currentGroupId);
        }
        return Status.OK;
    }

    @POST
    @Path("update")
    public Status update(@Context HttpServletRequest request, @FormParam("groupId") int currentGroupId,
                         @FormParam("userId") int userID, @FormParam("user_role") int user_role,
                         @FormParam("allowAddUser") boolean allowAddUser) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null) {
            log.error(String.format("Add member failed, caused by illegal parameters:userNo=%s", userNo));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (!this.validatePermision(userNo, currentGroupId)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的组员操作权限.");
            return status;
        }

        if (!this.validatePermision(userNo, currentGroupId, user_role)) {
            Status status = Status.ERROR;
            status.setInfo("你所授予的权限大于你所拥有的权限.");
            return status;
        }

        int adduser = allowAddUser == true ? 1 : 2;
        int ret = SpringBeanGetter.getDalUserGroupDao().updateUserPersimion(userID, currentGroupId, user_role, adduser);
        if (ret <= 0) {
            log.error("Update dal group user failed, caused by db operation failed, pls check the log.");
            Status status = Status.ERROR;
            status.setInfo("Update operation failed.");
            return status;
        }
        return Status.OK;
    }

    @POST
    @Path("addGroup")
    public Status addGroup(@Context HttpServletRequest request, @FormParam("currentGroupId") int currentGroupId,
                           @FormParam("childGroupId") int childGroupId, @FormParam("child_group_role") int child_group_role,
                           @FormParam("allowGroupAddUser") boolean allowGroupAddUser) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null) {
            log.error(String.format("Add group failed, caused by illegal parameters:[userNo=%s]", userNo));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (currentGroupId == childGroupId) {
            Status status = Status.ERROR;
            status.setInfo("不能将当前组加入当前组.");
            return status;
        }

        if (!this.validatePermision(userNo, currentGroupId)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的组员操作权限.");
            return status;
        }

        if (!this.validatePermision(userNo, currentGroupId, child_group_role)) {
            Status status = Status.ERROR;
            status.setInfo("你所授予的权限大于你所拥有的权限.");
            return status;
        }

        GroupRelation relation = SpringBeanGetter.getGroupRelationDao()
                .getGroupRelationByCurrentGroupIdAndChildGroupId(currentGroupId, childGroupId);
        if (relation != null) {
            DalGroup dalGroup = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(childGroupId);
            Status status = Status.ERROR;
            status.setInfo("DAL Team[" + dalGroup.getGroup_name() + "]已经加入当前DAL Team.");
            return status;
        }

        int adduser = allowGroupAddUser == true ? 1 : 2;
        relation = new GroupRelation();
        relation.setAdduser(adduser);
        relation.setChild_group_id(childGroupId);
        relation.setChild_group_role(child_group_role);
        relation.setCurrent_group_id(currentGroupId);
        relation.setUpdate_time(new Timestamp(System.currentTimeMillis()));
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        String upNo = user.getUserName() + "(" + userNo + ")";
        relation.setUpdate_user_no(upNo);
        int ret = SpringBeanGetter.getGroupRelationDao().insertChildGroup(relation);
        if (ret <= 0) {
            log.error("Add dal group failed, caused by db operation failed, pls check the log.");
            Status status = Status.ERROR;
            status.setInfo("Add operation failed.");
            return status;
        }
        return Status.OK;
    }

    @POST
    @Path("updateGroup")
    public Status updateGroup(@Context HttpServletRequest request, @FormParam("currentGroupId") int currentGroupId,
                              @FormParam("child_group_id") int childGroupId, @FormParam("child_group_role") int childGroupRole,
                              @FormParam("allowGroupAddUser") boolean allowGroupAddUser) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null) {
            log.error(String.format("Add member failed, caused by illegal parameters:[userNo=%s]", userNo));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (!this.validatePermision(userNo, currentGroupId)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的组员操作权限.");
            return status;
        }

        if (!this.validatePermision(userNo, currentGroupId, childGroupRole)) {
            Status status = Status.ERROR;
            status.setInfo("你所授予的权限大于你所拥有的权限.");
            return status;
        }

        int adduser = allowGroupAddUser == true ? 1 : 2;
        String updateUserNo = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getUserName();
        int ret = SpringBeanGetter.getGroupRelationDao().updateGroupRelation(currentGroupId, childGroupId,
                childGroupRole, adduser, updateUserNo, new Timestamp(System.currentTimeMillis()));
        if (ret <= 0) {
            log.error("Update dal group failed, caused by db operation failed, pls check the log.");
            Status status = Status.ERROR;
            status.setInfo("Update operation failed.");
            return status;
        }
        return Status.OK;
    }

    @POST
    @Path("delete")
    public Status delete(@Context HttpServletRequest request, @FormParam("groupId") int currentGroupId,
                         @FormParam("userId") int userId, @FormParam("isDalTeam") boolean isDalTeam) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null) {
            log.error(String.format("Add member failed, caused by illegal parameters: [userNo=%s]", userNo));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (!this.validatePermision(userNo, currentGroupId)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的组员操作权限.");
            return status;
        }

        if (isDalTeam) {
            int childGroupId = userId;
            int ret = SpringBeanGetter.getGroupRelationDao()
                    .deleteChildGroupByCurrentGroupIdAndChildGroupId(currentGroupId, childGroupId);
            if (ret <= 0) {
                log.error("Delete dal team failed, caused by db operation failed, pls check the log.");
                Status status = Status.ERROR;
                status.setInfo("Delete operation failed.");
                return status;
            }
            return Status.OK;
        }

        int ret = SpringBeanGetter.getDalUserGroupDao().deleteUserFromGroup(userId, currentGroupId);
        if (ret <= 0) {
            log.error("Delete user failed, caused by db operation failed, pls check the log.");
            Status status = Status.ERROR;
            status.setInfo("Delete operation failed.");
            return status;
        }
        return Status.OK;
    }

    @GET
    @Path("approveuser")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LoginUser> getApproveUsers(@QueryParam("projectId") int projectId) {
        Project prj = SpringBeanGetter.getDaoOfProject().getProjectByID(projectId);
        if (prj == null) {
            return null;
        }
        DalGroup dalGroup = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(prj.getDal_group_id());
        if (dalGroup == null) {
            return null;
        }
        List<LoginUser> users = SpringBeanGetter.getDaoOfLoginUser().getUserByGroupId(dalGroup.getId());
        List<LoginUser> result = new ArrayList<LoginUser>();
        for (LoginUser user : users) {
            if ("1".equalsIgnoreCase(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }

    private boolean validatePermision(String userNo, int currentGroupId) {
        boolean havePermision = false;
        havePermision = validateUserPermisionInCurrentGroup(userNo, currentGroupId);
        if (havePermision) {
            return havePermision;
        }
        havePermision = validateUserPermisionInChildGroup(userNo, currentGroupId);
        return havePermision;
    }

    private boolean validateUserPermisionInCurrentGroup(String userNo, int currentGroupId) {
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        // 用户加入的所有组
        List<UserGroup> urgroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (urgroups == null) {
            return false;
        }
        for (UserGroup ug : urgroups) {
            if (ug.getGroup_id() == DalGroupResource.SUPER_GROUP_ID && ug.getAdduser() == 1) {
                return true;
            }
            if (ug.getGroup_id() == currentGroupId && ug.getAdduser() == 1) {
                return true;
            }
        }
        return false;
    }

    private boolean validateUserPermisionInChildGroup(String userNo, int currentGroupId) {
        boolean havePermison = false;
        int userId = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
        List<GroupRelation> relations = SpringBeanGetter.getGroupRelationDao()
                .getAllGroupRelationByCurrentGroupId(currentGroupId);
        Iterator<GroupRelation> ite = relations.iterator();
        while (ite.hasNext()) {
            GroupRelation relation = ite.next();
            if (relation.getAdduser() == 1) { // the child group can manage the
                // current parent group user
                // then check the user whether or not exist in this child group
                List<UserGroup> ugs = SpringBeanGetter.getDalUserGroupDao()
                        .getUserGroupByGroupIdAndUserId(relation.getChild_group_id(), userId);
                if (ugs != null && ugs.size() > 0) {
                    havePermison = true;
                }
            }
        }
        return havePermison;
    }

    private boolean validatePermision(String userNo, int currentGroupId, int user_role) {
        boolean havePermision = false;
        havePermision = validateUserPermisionInCurrentGroup(userNo, currentGroupId, user_role);
        if (havePermision) {
            return havePermision;
        }
        havePermision = validateUserPermisionInChildGroup(userNo, currentGroupId, user_role);
        return havePermision;
    }

    private boolean validateUserPermisionInCurrentGroup(String userNo, int currentGroupId, int user_role) {
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        // 用户加入的所有组
        List<UserGroup> urgroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (urgroups == null) {
            return false;
        }
        for (UserGroup ug : urgroups) {
            if (ug.getGroup_id() == DalGroupResource.SUPER_GROUP_ID && ug.getAdduser() == 1
                    && ug.getRole() <= user_role) {
                return true;
            }
            if (ug.getGroup_id() == currentGroupId && ug.getAdduser() == 1 && ug.getRole() <= user_role) {
                return true;
            }
            if (ug.getGroup_id() == currentGroupId && ug.getAdduser() == 1 && ug.getRole() <= user_role) {
                return true;
            }
        }
        return false;
    }

    private boolean validateUserPermisionInChildGroup(String userNo, int currentGroupId, int user_role) {
        boolean havePermison = false;
        int userId = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
        List<GroupRelation> relations = SpringBeanGetter.getGroupRelationDao()
                .getAllGroupRelationByCurrentGroupId(currentGroupId);
        Iterator<GroupRelation> ite = relations.iterator();
        while (ite.hasNext()) {
            GroupRelation relation = ite.next();
            if (relation.getAdduser() == 1) { // the child group can manage the
                // current parent group user
                // then check the user whether or not exist in this child group
                List<UserGroup> ugs = SpringBeanGetter.getDalUserGroupDao()
                        .getUserGroupByGroupIdAndUserId(relation.getChild_group_id(), userId);
                if (ugs != null && ugs.size() > 0) {// user is in the child
                    // group
                    if (relation.getChild_group_role() <= user_role) { // check
                        // the
                        // child
                        // group
                        // role,
                        // which
                        // must
                        // greater
                        // than
                        // the
                        // given
                        // role
                        havePermison = true;
                    }
                }
            }
        }
        return havePermison;
    }

    private void transferProjectToGroup(String userNo, int groupId) {
        // 当前用户的所有Project
        List<UserProject> userProjects = SpringBeanGetter.getDaoOfUserProject().getUserProjectsByUser(userNo);
        for (UserProject proj : userProjects) {
            int project_id = proj.getProject_id();
            // project_id符合当前迭代的Project，且在user_project中id最小
            UserProject project = SpringBeanGetter.getDaoOfUserProject().getMinUserProjectByProjectId(project_id);
            // 验证当前project是否是由当前user创建
            if (proj.getId() == project.getId()) {
                // 更新Project表的groupId为当前用户的gourpId
                SpringBeanGetter.getDaoOfProject().updateProjectGroupById(groupId, project_id);
                // 删除user_project表中所有project_id符合当前迭代的Project
                SpringBeanGetter.getDaoOfUserProject().deleteUserProject(project_id);
            }

        }
    }

}
