package com.ctrip.platform.dal.dao;

import static com.ctrip.platform.dal.common.enums.ParameterDirection.InputOutput;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.task.DalTableNameConfigure;
import com.ctrip.platform.dal.dao.task.DalTaskContext;
import com.ctrip.platform.dal.dao.task.KeyHolderAwaredTask;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class SingleInsertSpaTask<T> extends CtripSpaTask<T> implements KeyHolderAwaredTask {
    private static final String INSERT_SPA_TPL = "spA_%s_i";

    private String outputIdName;
    private int outputIdIndex;

    private static final String RET_CODE = "retcode";

    public SingleInsertSpaTask() {}

    public void initialize(DalParser<T> parser) {
        super.initialize(parser);
        outputIdName = parser.isAutoIncrement() ? parser.getPrimaryKeyNames()[0] : null;
        outputIdIndex = 0;
        for (String name : parser.getColumnNames()) {
            if (name.equals(outputIdName))
                break;
            outputIdIndex++;
        }
    }

    @Override
    public int execute(DalHints hints, Map<String, ?> fields, T rawPojos, DalTaskContext taskContext)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        String tableName = getRawTableName(hints, fields);
        String insertSPA = String.format(INSERT_SPA_TPL, tableName);

        StatementParameters parameters = new StatementParameters();
        String callSql = prepareSpCall(insertSPA, parameters, fields);

        register(parameters, fields);

        if (taskContext instanceof DalTableNameConfigure)
            ((DalTableNameConfigure) taskContext).addTables(tableName);

        if (client instanceof DalContextClient) {
            Map<String, ?> results =
                    ((DalContextClient) client).call(callSql, parameters, hints.setFields(fields), taskContext);
            extract(parameters, hints.getKeyHolder());
            return (Integer) results.get(RET_CODE);
        } else
            throw new DalRuntimeException("The client is not instance of DalClient");
    }

    private void register(StatementParameters parameters, Map<String, ?> fields) {
        if (!CtripTaskFactory.callSpbySqlServerSyntax && CtripTaskFactory.callSpbyName) {
            if (outputIdName != null) {
                parameters.registerInOut(outputIdName, getColumnType(outputIdName), fields.get(outputIdName));
            }
        } else {
            /**
             * Must to be first one
             */
            if (outputIdName != null) {
                parameters.get(outputIdIndex).setDirection(InputOutput);
            }
        }
    }

    private void extract(StatementParameters parameters, KeyHolder holder) {
        if (holder == null)
            return;

        Map<String, Object> map = new LinkedHashMap<String, Object>();

        if (!CtripTaskFactory.callSpbySqlServerSyntax && CtripTaskFactory.callSpbyName) {
            if (outputIdName != null) {
                map.put(outputIdName, parameters.get(outputIdName, ParameterDirection.InputOutput).getValue());
            }
        } else {
            /**
             * Must to be first one
             */
            if (outputIdName != null) {
                map.put(outputIdName, parameters.get(outputIdIndex).getValue());
            }
        }

        if (map.isEmpty()) {
            holder.addEmptyKeys(1);
        } else {
            holder.addKey(map);
        }

    }
}
