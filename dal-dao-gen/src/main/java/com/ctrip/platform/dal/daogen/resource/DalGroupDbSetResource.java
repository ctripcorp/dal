package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DAL databaseSet of group manage.
 *
 * @author gzxia
 * @modified yn.wang
 */

@Resource
@Singleton
@Path("groupdbset")
public class DalGroupDbSetResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroup> getGroups(@Context HttpServletRequest request, @QueryParam("root") boolean root)
            throws Exception {
        try {
            List<DalGroup> groups = BeanGetter.getDaoOfDalGroup().getAllGroups();

            for (DalGroup group : groups) {
                group.setText(group.getGroup_name());
                group.setIcon("glyphicon glyphicon-folder-close");
                group.setChildren(false);
            }

            String userNo = RequestUtil.getUserNo(request);
            return sortGroups(groups, userNo);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    private List<DalGroup> sortGroups(List<DalGroup> groups, String userNo) throws SQLException {
        List<DalGroup> result = new ArrayList<>(groups.size());
        LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        List<UserGroup> joinedGroups = BeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
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
    @Path("getDbset")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatabaseSet> getDatabaseSetByGroupId(@QueryParam("groupId") int groupId,
            @QueryParam("daoFlag") boolean daoFlag) throws SQLException {
        try {
            List<DatabaseSet> dbsets = BeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByGroupId(groupId);
            if (!daoFlag)
                return dbsets;

            List<DatabaseSet> result = new ArrayList<>();
            for (DatabaseSet dbset : dbsets) { // 排除没有entry的dbset
                List<DatabaseSetEntry> entrys =
                        BeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetEntryByDbsetid(dbset.getId());
                if (entrys != null && entrys.size() > 0)
                    result.add(dbset);
            }
            return result;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Path("getDbsetEntry")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatabaseSetEntry> getDatabaseSetEntryByDbsetid(@QueryParam("dbsetId") String dbsetId)
            throws SQLException {
        try {
            int databaseSet_Id = -1;
            databaseSet_Id = Integer.parseInt(dbsetId);

            List<DatabaseSetEntry> dbsetEntry =
                    BeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetEntryByDbsetid(databaseSet_Id);
            return dbsetEntry;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @POST
    @Path("addDbset")
    public Status addDbset(@Context HttpServletRequest request, @FormParam("name") String name,
            @FormParam("provider") String provider, @FormParam("shardingStrategy") String shardingStrategy,
            @FormParam("groupId") int groupID) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            if (!this.validatePermision(userNo, groupID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限.");
                return status;
            }

            List<DatabaseSet> dbsets = BeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(name);
            if (null != dbsets && dbsets.size() > 0) {
                Status status = Status.ERROR();
                status.setInfo("databaseSet Name --> " + name + " 已经存在，请重新命名!");
                return status;
            }

            int ret = -1;
            DatabaseSet dbset = new DatabaseSet();
            dbset.setName(name);
            dbset.setProvider(provider);
            dbset.setShardingStrategy(shardingStrategy);
            dbset.setGroupId(groupID);
            dbset.setUpdate_time(new Timestamp(System.currentTimeMillis()));
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            String upNo = user.getUserName() + "(" + userNo + ")";
            dbset.setUpdate_user_no(upNo);
            ret = BeanGetter.getDaoOfDatabaseSet().insertDatabaseSet(dbset);
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
    @Path("updateDbset")
    public Status updateDbset(@Context HttpServletRequest request, @FormParam("id") int iD,
            @FormParam("name") String name, @FormParam("provider") String provider,
            @FormParam("shardingStrategy") String shardingStrategy, @FormParam("groupId") int groupID)
            throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            if (!this.validatePermision(userNo, groupID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限.");
                return status;
            }

            if (!validatePermision(userNo, groupID, iD)) {
                Status status = Status.ERROR();
                status.setInfo("你只能操作你们组创建的逻辑数据库.");
                return status;
            }

            List<DatabaseSet> dbsets = BeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(name);
            if (null != dbsets && dbsets.size() > 0) {
                for (DatabaseSet dbset : dbsets) {
                    if (dbset.getId() != iD) {
                        Status status = Status.ERROR();
                        status.setInfo("databaseSet Name --> " + name + "已经存在，请重新命名!");
                        return status;
                    }
                }
            }

            int ret = -1;
            DatabaseSet dbset = new DatabaseSet();
            dbset.setId(iD);
            dbset.setName(name);
            dbset.setProvider(provider);
            dbset.setShardingStrategy(shardingStrategy);
            dbset.setGroupId(groupID);
            dbset.setUpdate_time(new Timestamp(System.currentTimeMillis()));
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            String upNo = user.getUserName() + "(" + userNo + ")";
            dbset.setUpdate_user_no(upNo);
            ret = BeanGetter.getDaoOfDatabaseSet().updateDatabaseSet(dbset);
            if (ret <= 0) {
                Status status = Status.ERROR();
                status.setInfo("Update operation failed.");
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
    @Path("deletedbset")
    public Status deleteDbset(@Context HttpServletRequest request, @FormParam("groupId") int groupID,
            @FormParam("dbsetId") int dbsetID) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            if (!this.validatePermision(userNo, groupID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限.");
                return status;
            }

            if (!validatePermision(userNo, groupID, dbsetID)) {
                Status status = Status.ERROR();
                status.setInfo("你只能操作你们组创建的逻辑数据库.");
                return status;
            }

            int ret1 = BeanGetter.getDaoOfDatabaseSet().deleteDatabaseSetEntryByDbsetId(dbsetID);
            int ret2 = BeanGetter.getDaoOfDatabaseSet().deleteDatabaseSetById(dbsetID);
            if (ret1 < 0 || ret2 < 0) {
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
    @Path("addDbsetEntry")
    public Status addDbsetEntry(@Context HttpServletRequest request, @FormParam("name") String name,
            @FormParam("databaseType") String databaseType, @FormParam("sharding") String sharding,
            @FormParam("connectionString") String connectionString, @FormParam("dbsetId") int dbsetID,
            @FormParam("groupId") int groupID) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            if (!this.validatePermision(userNo, groupID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限.");
                return status;
            }

            if (!validatePermision(userNo, groupID, dbsetID)) {
                Status status = Status.ERROR();
                status.setInfo("你只能操作你们组创建的逻辑数据库.");
                return status;
            }

            int ret = -1;
            DatabaseSetEntry dbsetEntry = new DatabaseSetEntry();
            dbsetEntry.setName(name);
            dbsetEntry.setDatabaseType(databaseType);
            dbsetEntry.setSharding(sharding);
            dbsetEntry.setConnectionString(connectionString);
            dbsetEntry.setDatabaseSet_Id(dbsetID);
            dbsetEntry.setUpdate_time(new Timestamp(System.currentTimeMillis()));
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            String upNo = user.getUserName() + "(" + userNo + ")";
            dbsetEntry.setUpdate_user_no(upNo);
            ret = BeanGetter.getDaoOfDatabaseSet().insertDatabaseSetEntry(dbsetEntry);
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
    @Path("updateDbsetEntry")
    public Status updateDbsetEntry(@Context HttpServletRequest request, @FormParam("id") int dbsetEntyID,
            @FormParam("name") String name, @FormParam("databaseType") String databaseType,
            @FormParam("sharding") String sharding, @FormParam("connectionString") String connectionString,
            @FormParam("dbsetId") int dbsetID, @FormParam("groupId") int groupID) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            if (!this.validatePermision(userNo, groupID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限.");
                return status;
            }

            if (!validatePermision(userNo, groupID, dbsetID)) {
                Status status = Status.ERROR();
                status.setInfo("你只能操作你们组创建的逻辑数据库.");
                return status;
            }

            int ret = -1;
            DatabaseSetEntry dbsetEntry = new DatabaseSetEntry();
            dbsetEntry.setId(dbsetEntyID);
            dbsetEntry.setName(name);
            dbsetEntry.setDatabaseType(databaseType);
            dbsetEntry.setSharding(sharding);
            dbsetEntry.setConnectionString(connectionString);
            dbsetEntry.setDatabaseSet_Id(dbsetID);
            dbsetEntry.setUpdate_time(new Timestamp(System.currentTimeMillis()));
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            String upNo = user.getUserName() + "(" + userNo + ")";
            dbsetEntry.setUpdate_user_no(upNo);
            ret = BeanGetter.getDaoOfDatabaseSet().updateDatabaseSetEntry(dbsetEntry);
            if (ret <= 0) {
                Status status = Status.ERROR();
                status.setInfo("Update operation failed.");
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
    @Path("deletedbsetEntry")
    public Status deleteDbsetEntry(@Context HttpServletRequest request, @FormParam("groupId") int groupID,
            @FormParam("dbsetEntryId") int dbsetEntryID, @FormParam("dbsetId") int dbsetID) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            if (!this.validatePermision(userNo, groupID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限.");
                return status;
            }

            if (!validatePermision(userNo, groupID, dbsetID)) {
                Status status = Status.ERROR();
                status.setInfo("你只能操作你们组创建的逻辑数据库.");
                return status;
            }

            int ret = BeanGetter.getDaoOfDatabaseSet().deleteDatabaseSetEntryById(dbsetEntryID);
            if (ret < 0) {
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

    private boolean validatePermision(String userNo, int currentGroupId) throws SQLException {
        boolean havePermision = false;
        havePermision = validateUserPermisionInCurrentGroup(userNo, currentGroupId);
        if (havePermision) {
            return havePermision;
        }
        havePermision = validateUserPermisionInChildGroup(userNo, currentGroupId);
        return havePermision;
    }

    private boolean validateUserPermisionInCurrentGroup(String userNo, int currentGroupId) throws SQLException {
        LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        // 用户加入的所有组
        List<UserGroup> urgroups = BeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (urgroups == null) {
            return false;
        }
        for (UserGroup ug : urgroups) {
            if (ug.getGroup_id() == currentGroupId) {
                return true;
            }
        }
        return false;
    }

    private boolean validateUserPermisionInChildGroup(String userNo, int currentGroupId) throws SQLException {
        boolean havePermison = false;
        int userId = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
        List<GroupRelation> relations =
                BeanGetter.getGroupRelationDao().getAllGroupRelationByCurrentGroupId(currentGroupId);
        Iterator<GroupRelation> ite = relations.iterator();
        while (ite.hasNext()) {
            GroupRelation relation = ite.next();
            // then check the user whether or not exist in this child group
            List<UserGroup> ugs = BeanGetter.getDalUserGroupDao()
                    .getUserGroupByGroupIdAndUserId(relation.getChild_group_id(), userId);
            if (ugs != null && ugs.size() > 0) {
                havePermison = true;
            }
        }
        return havePermison;
    }

    private boolean validatePermision(String userNo, int currentGroupId, int pk_DbSetId) throws SQLException {
        boolean havePermision = false;
        havePermision = validateUserPermisionInCurrentGroup(userNo, currentGroupId);
        if (havePermision) {
            return havePermision;
        }
        havePermision = validateUserPermisionInChildGroup(userNo, currentGroupId, pk_DbSetId);
        return havePermision;
    }

    private boolean validateUserPermisionInChildGroup(String userNo, int currentGroupId, int pk_DbSetId)
            throws SQLException {
        DatabaseSet dbset = BeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetById(pk_DbSetId);
        if (dbset == null) {
            return false;
        }
        String updateUN = dbset.getUpdate_user_no();
        if (updateUN == null || updateUN.isEmpty()) { // the dbset have no
            // update user info
            // check the user is or not in the current group
            int userId = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
            List<UserGroup> check =
                    BeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(currentGroupId, userId);
            if (check != null && check.size() > 0) {
                return true;
            }
            return false;
        }
        Pattern pattern = Pattern.compile(".+\\((\\w+)\\).*");
        Matcher m = pattern.matcher(updateUN);
        String upNo = "";
        if (m.find()) {
            upNo = m.group(1);
        }
        if (upNo.equalsIgnoreCase(userNo)) {
            return true;
        }
        // the owner of the current database set
        LoginUser currentDbSetUser = BeanGetter.getDaoOfLoginUser().getUserByNo(upNo);
        if (currentDbSetUser == null) {
            return false;
        }
        // the group that the owner of the current database set have been joined
        // in
        List<UserGroup> currentDbSetUserGroup =
                BeanGetter.getDalUserGroupDao().getUserGroupByUserId(currentDbSetUser.getId());
        if (currentDbSetUserGroup == null || currentDbSetUserGroup.size() < 1) {
            return false;
        }

        // now check, the user who want to modify the dbset is or not in the
        // same group compare with the current dbset owner
        int userId = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo).getId();
        Set<Integer> childGroupIds = getChildGroupId(currentGroupId);
        Iterator<UserGroup> ite = currentDbSetUserGroup.iterator();
        while (ite.hasNext()) {
            UserGroup ug = ite.next();
            if (!childGroupIds.contains(ug.getGroup_id())) {
                continue;
            }
            List<UserGroup> exists =
                    BeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(ug.getGroup_id(), userId);
            if (exists != null && exists.size() > 0) {
                return true;
            }
        }

        return false;
    }

    private Set<Integer> getChildGroupId(int currentGroupId) throws SQLException {
        Set<Integer> sets = new HashSet<>();
        List<GroupRelation> relations =
                BeanGetter.getGroupRelationDao().getAllGroupRelationByCurrentGroupId(currentGroupId);
        if (relations == null) {
            return sets;
        }

        for (GroupRelation relation : relations) {
            sets.add(relation.getChild_group_id());
        }
        return sets;
    }

}
