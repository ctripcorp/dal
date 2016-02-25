package com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;

/**
 * This is the DalClient implementation for DAS mode.
 * 
 * @author jhhe
 * 
 */
public class DalDasClient implements DalClient {

	@Override
	public <T> T query(String sql, StatementParameters parameters,
			DalHints hints, DalResultSetExtractor<T> extractor)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<?> query(String sql, StatementParameters parameters,
			final DalHints hints,
			final List<DalResultSetExtractor<?>> extractors)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(String sql, StatementParameters parameters, DalHints hints)
			throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] batchUpdate(String[] sqls, DalHints hints) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] batchUpdate(String sql, StatementParameters[] parametersList,
			DalHints hints) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(DalCommand command, DalHints hints) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(List<DalCommand> commands, DalHints hints)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, ?> call(String callString,
			StatementParameters parameters, DalHints hints) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] batchCall(String callString,
			StatementParameters[] parametersList, DalHints hints)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
