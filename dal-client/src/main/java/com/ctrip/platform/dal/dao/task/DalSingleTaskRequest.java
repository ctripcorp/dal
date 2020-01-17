package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.KeyHolder.mergePartial;
import static com.ctrip.platform.dal.dao.KeyHolder.prepareLocalHints;
import static com.ctrip.platform.dal.dao.KeyHolder.setGeneratedKeyBack;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.detectDistributedTransaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.helper.DalRequestContext;
import com.ctrip.platform.dal.dao.helper.RequestContext;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalSingleTaskRequest<T> implements DalRequest<int[]> {
    private String caller;
    private String logicDbName;
    private DalHints hints;
    private boolean isList;
    private T rawPojo;
    private List<T> rawPojos;
    private List<Map<String, ?>> daoPojos;
    private SingleTask<T> task;
    private DalTaskContext dalTaskContext;

    private DalSingleTaskRequest(String logicDbName, DalHints hints, SingleTask<T> task) {
        this.logicDbName = logicDbName;
        this.task = task;
        this.hints = hints != null ? hints.clone() : new DalHints();
        this.caller = LogContext.getRequestCaller();
        prepareRequestContext();
    }

    public DalSingleTaskRequest(String logicDbName, DalHints hints, T rawPojo, SingleTask<T> task) {
        this(logicDbName, hints, task);

        this.rawPojo = rawPojo;
        isList = false;
    }

    public DalSingleTaskRequest(String logicDbName, DalHints hints, List<T> rawPojos, SingleTask<T> task) {
        this(logicDbName, hints, task);
        this.rawPojos = rawPojos;
        isList = true;
    }

    @Override
    public String getCaller() {
        return caller;
    }

    @Override
    public boolean isAsynExecution() {
        return hints.isAsyncExecution();
    }

    @Override
    public void validateAndPrepare() throws SQLException {
        if (isList && null == rawPojos)
            throw new DalException(ErrorCode.ValidatePojoList);

        if (!isList && null == rawPojo)
            throw new DalException(ErrorCode.ValidatePojo);

        if (task == null)
            throw new DalException(ErrorCode.ValidateTask);

        if (!isList) {
            rawPojos = new ArrayList<>(1);
            rawPojos.add(rawPojo);
        }

        daoPojos = task.getPojosFields(rawPojos);

        if (task instanceof InsertTaskAdapter) {
            ((InsertTaskAdapter) task).processIdentityField(hints, daoPojos);
        }

        detectDistributedTransaction(logicDbName, hints, daoPojos);
        dalTaskContext = task.createTaskContext();
    }

    private void prepareRequestContext() {
        hints.setRequestContext(null);
        if (task instanceof TaskAdapter) {
            RequestContext ctx = new DalRequestContext().setLogicTableName(((TaskAdapter) task).rawTableName);
            hints.setRequestContext(ctx);
        } else if (hints.getSpecifiedTableName() != null) {
            RequestContext ctx = new DalRequestContext().setLogicTableName(hints.getSpecifiedTableName());
            hints.setRequestContext(ctx);
        }
    }

    @Override
    public boolean isCrossShard() {
        // The single task request is always executed as if the pojos are not corss shard even they really are.
        return false;
    }

    @Override
    public TaskCallable<int[]> createTask() {
        return new SingleTaskCallable<>(hints, daoPojos, rawPojos, task, dalTaskContext);
    }

    @Override
    public Map<String, TaskCallable<int[]>> createTasks() throws SQLException {
        throw new DalException(ErrorCode.NotSupported);
    }

    @Override
    public ResultMerger<int[]> getMerger() {
        // Not support for now. Maybe support for new hints in the future
        return null;
    }

    @Override
    public void endExecution() throws SQLException {
        setGeneratedKeyBack(task, hints, rawPojos);
    }

    private static class SingleTaskCallable<T> implements TaskCallable<int[]> {
        private DalHints hints;
        private List<Map<String, ?>> daoPojos;
        private List<T> rawPojos;
        private SingleTask<T> task;
        private DalTaskContext taskContext;

        public SingleTaskCallable(DalHints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos, SingleTask<T> task, DalTaskContext taskContext) {
            this.hints = hints;
            this.daoPojos = daoPojos;
            this.rawPojos = rawPojos;
            this.task = task;
            this.taskContext = taskContext;
        }

        @Override
        public int[] call() throws Exception {
            int[] counts = new int[daoPojos.size()];
            for (int i = 0; i < daoPojos.size(); i++) {
                Throwable error = null;
                DalHints localHints = prepareLocalHints(task, hints);

                try {
                    counts[i] = task.execute(localHints, daoPojos.get(i), rawPojos.get(i), taskContext);
                } catch (Throwable e) {
                    error = e;
                }

                mergePartial(task, hints.getKeyHolder(), localHints.getKeyHolder(), error);
                hints.handleError("Error when execute single pojo operation", error);
            }

            return counts;
        }

        @Override
        public DalTaskContext getDalTaskContext() {
            return this.taskContext;
        }
    }
}
