package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;

public class MultipleQueryTask implements SqlTask<List<?>>{
	private List<DalResultSetExtractor<?>> extractors;
	
	public MultipleQueryTask(List<DalResultSetExtractor<?>> extractors) {
		this.extractors = extractors;
	}
	
	@Override
	public List<?> execute(DalClient client, String sql, StatementParameters parameters, DalHints hints) throws SQLException {
		return client.query(sql, parameters, hints, extractors);
	}
}