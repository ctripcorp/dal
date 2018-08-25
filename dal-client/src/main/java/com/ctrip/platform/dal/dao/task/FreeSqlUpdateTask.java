package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalContextClient;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;


public class FreeSqlUpdateTask extends TaskAdapter implements SqlTask<Integer>{
	@Override
	public Integer execute(DalClient client, String sql, StatementParameters parameters, DalHints hints, DalTaskContext taskContext) throws SQLException {
		if (client instanceof DalContextClient)
			return ((DalContextClient)client).update(sql, parameters, hints, taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
	}
}
