package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class MultipleQueryTask extends TaskAdapter implements SqlTask<List<?>>{
	private List<DalResultSetExtractor<?>> extractors;
	
	public MultipleQueryTask(List<DalResultSetExtractor<?>> extractors) {
		this.extractors = extractors;
	}

	@Override
	public List<?> execute(DalClient client, String sql, StatementParameters parameters, DalHints hints, DalTaskContext taskContext) throws SQLException {
		if (client instanceof DalContextClient)
			return ((DalContextClient) client).query(sql, parameters, hints, extractors, taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
	}
}