package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.sql.SQLException;

/**
 * Created by lilj on 2018/9/17.
 */
public class FreeSqlQueryTask<T> extends BaseTaskAdapter implements SqlTask<T>{
    private DalResultSetExtractor<T> extractor;

    public FreeSqlQueryTask(DalResultSetExtractor<T> extractor) {
        this.extractor = extractor;
    }

    @Override
    public T execute(DalClient client, String sql, StatementParameters parameters, DalHints hints, DalTaskContext taskContext) throws SQLException {
        if (client instanceof DalContextClient)
            return ((DalContextClient) client).query(sql, parameters, hints, extractor, taskContext);
        else
            throw new DalRuntimeException("The client is not instance of DalClient");
    }
}
