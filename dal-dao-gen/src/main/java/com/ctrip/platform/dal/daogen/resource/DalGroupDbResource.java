package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
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
    private static Logger log = Logger.getLogger(DalGroupDbResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroup> getGroups(@Context HttpServletRequest request, @QueryParam("root") boolean root) {
        List<DalGroup> groups = SpringBeanGetter.getDaoOfDalGroup().getAllGroups();

        for (DalGroup group : groups) {
            group.setText(group.getGroup_name());
            group.setIcon("glyphicon glyphicon-folder-close");
            group.setChildren(false);
        }

        String userNo = RequestUtil.getUserNo(request);
        return sortGroups(groups, userNo);
    }

    private List<DalGroup> sortGroups(List<DalGroup> groups, String userNo) {
        List<DalGroup> result = new ArrayList<DalGroup>(groups.size());
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
    @Path("groupdb")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroupDB> getGroupUsers(@QueryParam("groupId") String id) {
        int groupId = -1;
        try {
            groupId = Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            log.error("get Group Users failed", ex);
            return null;
        }
        List<DalGroupDB> dbs = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(groupId);
        return dbs;
    }

    @GET
    @Path("allgroupdbs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroupDB> getAllGroupDbs() {
        List<DalGroupDB> dbs = SpringBeanGetter.getDaoOfDalGroupDB().getAllGroupDbs();
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
    public Status add(@Context HttpServletRequest request, @FormParam("groupId") String groupId, @FormParam("dbname") String dbname, @FormParam("comment") String comment, @FormParam("gen_default_dbset") boolean gen_default_dbset) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null || groupId == null || dbname == null) {
            log.error(String.format("Add member failed, caused by illegal parameters: " + "[groupId=%s, dbname=%s]", groupId, dbname));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        int groupID = -1;
        try {
            groupID = Integer.parseInt(groupId);
        } catch (NumberFormatException ex) {
            log.error("Add dal team database failed", ex);
            Status status = Status.ERROR;
            status.setInfo("Illegal group id");
            return status;
        }

        if (!this.validatePermision(userNo, groupID)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的操作权限.");
            return status;
        }

        DalGroupDB groupdb = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBByDbName(dbname);
        if (null != groupdb && groupdb.getDal_group_id() > 0) {
            DalGroup group = SpringBeanGetter.getDaoOfDalGroup().getDalGroupById(groupdb.getDal_group_id());
            Status status = Status.ERROR;
            status.setInfo(groupdb.getDbname() + " is already added in " + group.getGroup_name());
            return status;
        }

        int ret = -1;
        if (null != groupdb) {
            ret = SpringBeanGetter.getDaoOfDalGroupDB().updateGroupDB(groupdb.getId(), groupID);
            SpringBeanGetter.getDaoOfDalGroupDB().updateGroupDB(groupdb.getId(), comment);
        } else {
            Status status = Status.ERROR;
            status.setInfo(dbname + " 不存在，请先到数据库一览界面添加DB.");
            return status;
        }
        if (ret <= 0) {
            log.error("Add dal group db failed, caused by db operation failed, pls check the log.");
            Status status = Status.ERROR;
            status.setInfo("Add operation failed.");
            return status;
        }

        if (gen_default_dbset) {
            genDefaultDbset(groupID, dbname);
        }

        return Status.OK;
    }

    @POST
    @Path("update")
    public Status update(@Context HttpServletRequest request, @FormParam("groupId") String groupId, @FormParam("dbId") String dbId, @FormParam("comment") String comment) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null || groupId == null || dbId == null) {
            log.error(String.format("Add member failed, caused by illegal parameters: " + "[groupId=%s, dbId=%s]", groupId, dbId));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        int groupID = -1;
        int dbID = -1;
        try {
            dbID = Integer.parseInt(dbId);
            groupID = Integer.parseInt(groupId);
        } catch (NumberFormatException ex) {
            log.error("Update failed", ex);
            Status status = Status.ERROR;
            status.setInfo("Illegal group id");
            return status;
        }

        if (!this.validatePermision(userNo, groupID)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的操作权限.");
            return status;
        }

        int ret = SpringBeanGetter.getDaoOfDalGroupDB().updateGroupDB(dbID, comment);
        if (ret <= 0) {
            log.error("Update dal group db failed, caused by db operation failed, pls check the spring log");
            Status status = Status.ERROR;
            status.setInfo("Update operation failed.");
            return status;
        }

        return Status.OK;
    }

    @POST
    @Path("delete")
    public Status delete(@Context HttpServletRequest request, @FormParam("groupId") String groupId, @FormParam("dbId") String dbId) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null || groupId == null || dbId == null) {
            log.error(String.format("Delete db failed, caused by illegal parameters: " + "[groupId=%s, dbId=%s]", groupId, dbId));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        int groupID = -1;
        int dbID = -1;
        try {
            groupID = Integer.parseInt(groupId);
            dbID = Integer.parseInt(dbId);
        } catch (NumberFormatException ex) {
            log.error("Delete db failed", ex);
            Status status = Status.ERROR;
            status.setInfo("Illegal group id");
            return status;
        }

        if (!this.validatePermision(userNo, groupID)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DAL Team的操作权限.");
            return status;
        }

        int ret = SpringBeanGetter.getDaoOfDalGroupDB().updateGroupDB(dbID, -1);
        if (ret <= 0) {
            log.error("Delete db failed, caused by db operation failed, pls check the spring log");
            Status status = Status.ERROR;
            status.setInfo("Delete operation failed.");
            return status;
        }
        return Status.OK;
    }

    @POST
    @Path("transferdb")
    public Status transferdb(@Context HttpServletRequest request, @FormParam("groupId") String groupId, @FormParam("dbId") String dbId) {
        String userNo = RequestUtil.getUserNo(request);

        if (userNo == null || groupId == null || dbId == null) {
            log.error(String.format("transfer db failed, caused by illegal parameters: " + "[groupId=%s, dbId=%s]", groupId, dbId));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        int groupID = -1;
        int dbID = -1;
        try {
            groupID = Integer.parseInt(groupId);
            dbID = Integer.parseInt(dbId);
        } catch (NumberFormatException ex) {
            log.error("transfer db failed", ex);
            Status status = Status.ERROR;
            status.setInfo("Illegal group id or db id");
            return status;
        }

        if (!this.validateTransferPermision(userNo, dbID)) {
            Status status = Status.ERROR;
            status.setInfo("你没有当前DataBase的操作权限.");
            return status;
        }

        int ret = SpringBeanGetter.getDaoOfDalGroupDB().updateGroupDB(dbID, groupID);
        if (ret <= 0) {
            log.error("transfer db failed, caused by db operation failed, pls check the spring log");
            Status status = Status.ERROR;
            status.setInfo("transfer operation failed.");
            return status;
        }
        return Status.OK;
    }

    private boolean validatePermision(String userNo, int groupId) {
        boolean havaPermision = false;
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        List<UserGroup> urGroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (urGroups != null && urGroups.size() > 0) {
            for (UserGroup urGroup : urGroups) {
                if (urGroup.getGroup_id() == groupId) {
                    havaPermision = true;
                }
            }
        }
        return havaPermision;
    }

    private boolean validateTransferPermision(String userNo, int dbId) {
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        if (user == null) {
            return false;
        }
        List<UserGroup> urGroups = SpringBeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
        if (urGroups != null && urGroups.size() > 0) {
            for (UserGroup urGroup : urGroups) {
                List<DalGroupDB> groupDbs = SpringBeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(urGroup.getGroup_id());
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
    private void genDefaultDbset(int groupId, String dbname) {
        List<DatabaseSet> exist = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbname);
        if (exist != null && exist.size() > 0) {
            return;
        }
        DatabaseSet dbset = new DatabaseSet();
        dbset.setName(dbname);
        dbset.setProvider("sqlProvider");
        try {
            Connection connection = DataSourceUtil.getConnection(dbname);
            String dbType = connection.getMetaData().getDatabaseProductName();
            if (dbType != null && (!dbType.equals("Microsoft SQL Server"))) {
                dbset.setProvider("mySqlProvider");
            }
        } catch (Exception e) {
            log.warn("", e);
        }

        dbset.setGroupId(groupId);
        int ret = SpringBeanGetter.getDaoOfDatabaseSet().insertDatabaseSet(dbset);
        if (ret > 0) {
            dbset = SpringBeanGetter.getDaoOfDatabaseSet().getAllDatabaseSetByName(dbname).get(0);

            DatabaseSetEntry entry = new DatabaseSetEntry();
            entry.setDatabaseSet_Id(dbset.getId());
            entry.setDatabaseType("Master");
            entry.setName(dbname);
            entry.setConnectionString(dbname);

            SpringBeanGetter.getDaoOfDatabaseSet().insertDatabaseSetEntry(entry);
        }
    }

}
