package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.entity.GroupRelation;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.entity.UserGroup;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class ApproveResource {
    public boolean needApproveTask(int projectId, int userId) throws SQLException {
        Project prj = BeanGetter.getDaoOfProject().getProjectByID(projectId);
        if (prj == null) {
            return true;
        }
        List<UserGroup> lst =
                BeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(prj.getDal_group_id(), userId);
        if (lst != null && lst.size() > 0 && lst.get(0).getRole() == 1) {
            return false;
        }
        // all child group
        List<GroupRelation> grs =
                BeanGetter.getGroupRelationDao().getAllGroupRelationByCurrentGroupId(prj.getDal_group_id());
        if (grs == null || grs.size() < 1) {
            return true;
        }
        // check user is or not in the child group which have admin role
        Iterator<GroupRelation> ite = grs.iterator();
        while (ite.hasNext()) {
            GroupRelation gr = ite.next();
            if (gr.getChild_group_role() == 1) {
                int groupId = gr.getChild_group_id();
                List<UserGroup> test = BeanGetter.getDalUserGroupDao().getUserGroupByGroupIdAndUserId(groupId, userId);
                if (test != null && test.size() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
