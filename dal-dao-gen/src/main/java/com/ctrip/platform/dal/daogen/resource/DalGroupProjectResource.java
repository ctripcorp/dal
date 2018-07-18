package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

/**
 * DAL Project & Code 一览
 *
 * @author gzxia
 */
@Resource
@Singleton
@Path("projectview")
public class DalGroupProjectResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroup> getGroups(@QueryParam("root") boolean root) throws SQLException {
        try {
            List<DalGroup> groups = BeanGetter.getDaoOfDalGroup().getAllGroups();
            for (DalGroup group : groups) {
                group.setText(group.getGroup_name());
                group.setIcon("glyphicon glyphicon-folder-open");
                group.setChildren(true);
            }
            return groups;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Path("groupprojects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Project> getGroupProjects(@QueryParam("groupId") String groupId) throws SQLException {
        try {
            int groupID = -1;
            groupID = Integer.parseInt(groupId);

            return BeanGetter.getDaoOfProject().getProjectByGroupId(groupID);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

}
