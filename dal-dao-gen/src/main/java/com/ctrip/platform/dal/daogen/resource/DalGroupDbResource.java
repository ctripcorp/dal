package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
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
import java.util.*;

/**
 * DAL database of group manage.
 *
 * @author gzxia
 * @modified yn.wang
 */

@Resource
@Singleton
@Path("groupdb")
public class DalGroupDbResource {
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
        List<DalGroup> result = new ArrayList<DalGroup>(groups.size());
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
    @Path("groupdb")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroupDB> getGroupUsers(@QueryParam("groupId") String id) throws SQLException {
        try {
            int groupId = -1;
            groupId = Integer.parseInt(id);

            List<DalGroupDB> dbs = BeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(groupId);
            return dbs;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Path("allgroupdbs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroupDB> getAllGroupDbs() throws SQLException {
        List<DalGroupDB> dbs = BeanGetter.getDaoOfDalGroupDB().getAllGroupDbs();
        for (DalGroupDB db : dbs) {
            db.setDb_user("******");
            db.setDb_password("******");

            if (DatabaseType.SQLServer.getValue().equals(db.getDb_providerName())) {
                db.setDb_providerName("SQLServer");
            } else if (DatabaseType.MySQL.getValue().equals(db.getDb_providerName())) {
                db.setDb_providerName("MySQL");
            } else {
                db.setDb_providerName("unknown");
            }
        }
        Collections.sort(dbs, new Comparator<DalGroupDB>() {
            @Override
            public int compare(DalGroupDB o1, DalGroupDB o2) {
                return o1.getDbname().compareTo(o2.getDbname());
            }
        });
        return dbs;
    }

    @POST
    @Path("add")
    public Status add(@Context HttpServletRequest request, @FormParam("groupId") String groupId,
            @FormParam("dbname") String dbname, @FormParam("comment") String comment,
            @FormParam("gen_default_dbset") boolean gen_default_dbset) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null || groupId == null || dbname == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            int groupID = -1;
            groupID = Integer.parseInt(groupId);

            if (!this.validatePermision(userNo, groupID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限。");
                return status;
            }

            DalGroupDB groupdb = BeanGetter.getDaoOfDalGroupDB().getGroupDBByDbName(dbname);
            if (null != groupdb && groupdb.getDal_group_id() > 0) {
                DalGroup group = BeanGetter.getDaoOfDalGroup().getDalGroupById(groupdb.getDal_group_id());
                Status status = Status.ERROR();
                status.setInfo(groupdb.getDbname() + " is already added in " + group.getGroup_name());
                return status;
            }

            int ret = -1;
            if (null != groupdb) {
                ret = BeanGetter.getDaoOfDalGroupDB().updateGroupDB(groupdb.getId(), groupID);
                BeanGetter.getDaoOfDalGroupDB().updateGroupDB(groupdb.getId(), comment);
            } else {
                Status status = Status.ERROR();
                status.setInfo(dbname + " 不存在，请先到数据库一览界面添加DB。");
                return status;
            }
            if (ret <= 0) {
                Status status = Status.ERROR();
                status.setInfo("Add operation failed.");
                return status;
            }

            if (gen_default_dbset) {
                Status status = genDefaultDbset(groupID, dbname, null);
                if (status == Status.ERROR()) {
                    return status;
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
    @Path("update")
    public Status update(@Context HttpServletRequest request, @FormParam("groupId") String groupId,
            @FormParam("dbId") String dbId, @FormParam("comment") String comment) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null || groupId == null || dbId == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            int groupID = -1;
            int dbID = -1;
            dbID = Integer.parseInt(dbId);
            groupID = Integer.parseInt(groupId);

            if (!this.validatePermision(userNo, groupID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限。");
                return status;
            }

            int ret = BeanGetter.getDaoOfDalGroupDB().updateGroupDB(dbID, comment);
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
    @Path("delete")
    public Status delete(@Context HttpServletRequest request, @FormParam("groupId") String groupId,
            @FormParam("dbId") String dbId) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null || groupId == null || dbId == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            int groupID = -1;
            int dbID = -1;
            groupID = Integer.parseInt(groupId);
            dbID = Integer.parseInt(dbId);

            if (!this.validatePermision(userNo, groupID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DAL Team的操作权限。");
                return status;
            }

            int ret = BeanGetter.getDaoOfDalGroupDB().updateGroupDB(dbID, -1);
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
    @Path("transferdb")
    public Status transferdb(@Context HttpServletRequest request, @FormParam("groupId") String groupId,
            @FormParam("dbId") String dbId) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);

            if (userNo == null || groupId == null || dbId == null) {
                Status status = Status.ERROR();
                status.setInfo("Illegal parameters.");
                return status;
            }

            int groupID = -1;
            int dbID = -1;
            groupID = Integer.parseInt(groupId);
            dbID = Integer.parseInt(dbId);

            if (!this.validateTransferPermision(userNo, dbID)) {
                Status status = Status.ERROR();
                status.setInfo("你没有当前DataBase的操作权限。");
                return status;
            }

            int ret = BeanGetter.getDaoOfDalGroupDB().updateGroupDB(dbID, groupID);
            if (ret <= 0) {
                Status status = Status.ERROR();
                status.setInfo("transfer operation failed.");
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

    private boolean validatePermision(String userNo, int groupId) throws SQLException {
        boolean havaPermision = false;
        LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        List<UserGroup> urGroups = BeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (urGroups != null && urGroups.size() > 0) {
            for (UserGroup urGroup : urGroups) {
                if (urGroup.getGroup_id() == groupId) {
                    havaPermision = true;
                }
            }
        }
        return havaPermision;
    }

    private boolean validateTransferPermision(String userNo, int dbId) throws SQLException {
        LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        if (user == null) {
            return false;
        }
        List<UserGroup> urGroups = BeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (urGroups != null && urGroups.size() > 0) {
            for (UserGroup urGroup : urGroups) {
                List<DalGroupDB> groupDbs = BeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(urGroup.getGroup_id());
                if (groupDbs != null && groupDbs.size() > 0) {
                    for (DalGroupDB db : groupDbs) {
                        if (db.getId() == dbId) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 生成默认的databaseSet和databaseSet Entry
     *
     * @param dbname
     */
    public static Status genDefaultDbset(int groupId, String dbname, String dbProvider) throws Exception {
        Status status = Status.OK();
        List<DatabaseSet> exist = BeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbname);
        if (exist != null && exist.size() > 0) {
            status = Status.ERROR();
            status.setInfo("数据库" + dbname + "已添加成功。由于已存在名为" + dbname
                    + "的逻辑数据库，所以无法默认生成同名的逻辑库，请到逻辑数据库管理页面中手动添加不同名称的逻辑库。请点击关闭按钮以关闭窗口。");
            return status;
        }
        DatabaseSet dbset = new DatabaseSet();
        dbset.setName(dbname);
        dbset.setProvider("sqlProvider");

        if (dbProvider != null && dbProvider.length() > 0) {
            if (dbProvider.equals("SQLServer"))
                dbset.setProvider("sqlProvider");
            if (dbProvider.equals("MySQL"))
                dbset.setProvider("mySqlProvider");
        }

        dbset.setGroupId(groupId);
        int ret = BeanGetter.getDaoOfDatabaseSet().insertDatabaseSet(dbset);
        if (ret > 0) {
            dbset = BeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbname).get(0);

            DatabaseSetEntry entry = new DatabaseSetEntry();
            entry.setDatabaseSet_Id(dbset.getId());
            entry.setDatabaseType("Master");
            entry.setName(dbname);
            entry.setConnectionString(dbname);

            BeanGetter.getDaoOfDatabaseSet().insertDatabaseSetEntry(entry);
        }
        return status;
    }

}
