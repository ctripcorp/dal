package com.ctrip.platform.dal.dao;

import java.util.Map;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.task.BulkTask;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.dao.task.DefaultTaskFactory;

import static com.ctrip.platform.dal.dao.task.DefaultTaskFactory.getDbCategory;

import com.ctrip.platform.dal.dao.task.DeleteSqlTask;
import com.ctrip.platform.dal.dao.task.SingleTask;
import com.ctrip.platform.dal.dao.task.UpdateSqlTask;
import com.ctrip.platform.dal.dao.vi.ConfigBeanFactory;

/**
 * This Factory is to unify Ctrip special MS Sql Server CUD case and common my sql case. Ctrip use SP3 or SPA to perform
 * CUD on MS Sql Server. The rules: 1. If there are both SP3 and SPA for the table, the batch CUD will use SP3, the
 * non-batch will use SPA. The reason is because a special setting in Ctrip Sql Server that prevent batch SPA CUD 2. If
 * there is only SP3 for the table, both batch and non-batch will using SP3 3. If there is only SPA for the table, only
 * non-batch CUD supported 4. If there is no SP3 or SPA, the original DalTableDao should be used. 5. For insert SP3 and
 * SPA, the auto incremental Id will be used as output parameter
 * 
 * For sharding support: it is confirmed from DBA that Ctrip has shard by DB case, but no shard by table case. For
 * inout, out parameter: only insert SP3/SPA has inout/out parameter
 * 
 * @author jhhe
 */
public class CtripTaskFactory implements DalTaskFactory {
    private DefaultTaskFactory defaultFactory;
    private static final String CALL_SP_BY_NAME = "callSpbyName";
    private static final String CALL_SP_BY_SQLSEVER = "callSpbySqlServerSyntax";
    private static final String CALL_SPT = "callSpt";
    
    private static final String INSERT_SPT_TPL = "spT_%s_i";
    private static final String DELETE_SPT_TPL = "spT_%s_d";
    private static final String UPDATE_SPT_TPL = "spT_%s_u";
    
    private static final String INSERT_SP3_TPL = "sp3_%s_i";
    private static final String DELETE_SP3_TPL = "sp3_%s_d";
    private static final String UPDATE_SP3_TPL = "sp3_%s_u";


    // Default enabled, to compatible with current behavior
    static boolean callSpbyName = true;
    static boolean callSpt = false;
    static boolean callSpbySqlServerSyntax = true;

    @Override
    public void initialize(Map<String, String> settings) {
        defaultFactory = new DefaultTaskFactory();
        defaultFactory.initialize(settings);
        // TO integrate VI here, it is not a good solution
        ConfigBeanFactory.init();
        String callSpbyNameOpt = defaultFactory.getProperty(CALL_SP_BY_NAME);
        if (callSpbyNameOpt != null)
            callSpbyName = Boolean.parseBoolean(callSpbyNameOpt);

        String callSptOpt = defaultFactory.getProperty(CALL_SPT);
        if (callSptOpt != null && callSptOpt.length() > 0)
            callSpt = Boolean.parseBoolean(callSptOpt);
        
        
        String callSptBySqlserver = defaultFactory.getProperty(CALL_SP_BY_SQLSEVER);
        if (callSptBySqlserver != null && callSptBySqlserver.length() > 0)
            callSpbySqlServerSyntax = Boolean.parseBoolean(callSptBySqlserver);
    }

    @Override
    public String getProperty(String key) {
        return defaultFactory.getProperty(key);
    }

    @Override
    public <T> SingleTask<T> createSingleInsertTask(DalParser<T> parser) {
        if (DatabaseCategory.MySql == getDbCategory(parser))
            return defaultFactory.createSingleInsertTask(parser);

        SingleTask<T> singleTask = new SingleInsertSpaTask<>();
        singleTask.initialize(parser);
        return singleTask;
    }

    @Override
    public <T> SingleTask<T> createSingleDeleteTask(DalParser<T> parser) {
        if (DatabaseCategory.MySql == getDbCategory(parser))
            return defaultFactory.createSingleDeleteTask(parser);

        SingleTask<T> singleTask = new SingleDeleteSpaTask<>();
        singleTask.initialize(parser);
        return singleTask;
    }

    @Override
    public <T> SingleTask<T> createSingleUpdateTask(DalParser<T> parser) {
        if (DatabaseCategory.MySql == getDbCategory(parser))
            return defaultFactory.createSingleUpdateTask(parser);

        SingleTask<T> singleTask = new SingleUpdateSpaTask<>();
        singleTask.initialize(parser);
        return singleTask;
    }

    @Override
    public <T> BulkTask<Integer, T> createCombinedInsertTask(DalParser<T> parser) {
        if (DatabaseCategory.MySql == getDbCategory(parser))
            return defaultFactory.createCombinedInsertTask(parser);

        // For sqlserver, this operation is not supported in ctrip
        return null;
    }

    @Override
    public <T> BulkTask<int[], T> createBatchInsertTask(DalParser<T> parser) {
        if (DatabaseCategory.MySql == getDbCategory(parser))
            return defaultFactory.createBatchInsertTask(parser);

        BulkTask<int[], T> bulkTask = null;
        if (!callSpt) {
            if(callSpbySqlServerSyntax)
                bulkTask = new BatchSp3Task<>(INSERT_SP3_TPL, parser.getColumnNames());
            else
                bulkTask = new BatchInsertSp3Task<>();
        } else {
            bulkTask = new CtripSptTask<>(INSERT_SPT_TPL);
        }
        bulkTask.initialize(parser);
        return bulkTask;
    }

    @Override
    public <T> BulkTask<int[], T> createBatchDeleteTask(DalParser<T> parser) {
        if (DatabaseCategory.MySql == getDbCategory(parser))
            return defaultFactory.createBatchDeleteTask(parser);

        BulkTask<int[], T> bulkTask = null;
        if (!callSpt) {
            if(callSpbySqlServerSyntax)
                bulkTask = new BatchSp3Task<>(DELETE_SP3_TPL, parser.getPrimaryKeyNames());
            else
                bulkTask = new BatchDeleteSp3Task<>();
        } else {
            bulkTask = new CtripSptTask<>(DELETE_SPT_TPL);
        }
        bulkTask.initialize(parser);
        return bulkTask;
    }

    @Override
    public <T> BulkTask<int[], T> createBatchUpdateTask(DalParser<T> parser) {
        if (DatabaseCategory.MySql == getDbCategory(parser))
            return defaultFactory.createBatchUpdateTask(parser);

        BulkTask<int[], T> bulkTask = null;
        if (!callSpt) {
            if(callSpbySqlServerSyntax)
                bulkTask = new BatchSp3Task<>(UPDATE_SP3_TPL, parser.getColumnNames());
            else
                bulkTask = new BatchUpdateSp3Task<>();
        } else {
            bulkTask = new CtripSptTask<>(UPDATE_SPT_TPL);
        }
        bulkTask.initialize(parser);
        return bulkTask;
    }

    @Override
    public <T> DeleteSqlTask<T> createDeleteSqlTask(DalParser<T> parser) {
        return defaultFactory.createDeleteSqlTask(parser);
    }

    @Override
    public <T> UpdateSqlTask<T> createUpdateSqlTask(DalParser<T> parser) {
        return defaultFactory.createUpdateSqlTask(parser);
    }
}
