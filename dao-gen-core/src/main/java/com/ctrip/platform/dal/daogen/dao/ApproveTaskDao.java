package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.ApproveTask;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ApproveTaskDao extends BaseDao {
    private DalTableDao<ApproveTask> client;
    private DalRowMapper<ApproveTask> approveTaskRowMapper = null;

    public ApproveTaskDao() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(ApproveTask.class));
        approveTaskRowMapper = new DalDefaultJpaMapper<>(ApproveTask.class);
    }

    public List<ApproveTask> getAllApproveTaskByApproverId(Integer approverId) throws SQLException {
        FreeSelectSqlBuilder<List<ApproveTask>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, task_id, task_type, create_time, create_user_id, approve_user_id FROM approve_task WHERE approve_user_id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "approve_user_id", Types.INTEGER, approverId);
        builder.mapWith(approveTaskRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<ApproveTask> list = queryDao.query(builder, parameters, hints);
        processList(list);
        return list;
    }

    private void processList(List<ApproveTask> list) {
        if (list == null || list.size() == 0)
            return;
        for (ApproveTask entity : list) {
            Date date = new Date(entity.getCreate_time().getTime());
            entity.setStr_create_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        }
    }

    public int insertApproveTask(ApproveTask task) throws SQLException {
        if (null == task)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.insert(hints, task);
    }

    public int deleteApproveTaskByTaskIdAndType(Integer id, String taskType) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("DELETE FROM approve_task WHERE task_id=? AND task_type=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "id", Types.INTEGER, id);
        parameters.set(i++, "task_type", Types.VARCHAR, taskType);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

}
