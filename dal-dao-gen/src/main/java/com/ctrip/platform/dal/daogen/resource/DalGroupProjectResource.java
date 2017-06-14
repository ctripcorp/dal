package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
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
    private static Logger log = Logger.getLogger(DalGroupProjectResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DalGroup> getGroups(@QueryParam("root") boolean root) {
        try {
            List<DalGroup> groups = SpringBeanGetter.getDaoOfDalGroup().getAllGroups();
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
    public List<Project> getGroupProjects(@QueryParam("groupId") String groupId) {
        try {
            int groupID = -1;
            groupID = Integer.parseInt(groupId);

            return SpringBeanGetter.getDaoOfProject().getProjectByGroupId(groupID);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

}
