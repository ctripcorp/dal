package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DalClient {
	<T> T query(String sql, StatementParameters parameters, DalHints hints,
			DalResultSetExtractor<T> extractor) throws SQLException;

	int update(String sql, StatementParameters parameters, DalHints hints)
			throws SQLException;

	int update(String sql, StatementParameters parameters, DalHints hints,
			KeyHolder generatedKeyHolder) throws SQLException;

	int[] batchUpdate(String[] sql, DalHints hints) throws SQLException;

	int[] batchUpdate(String sql, StatementParameters[] parametersList,
			DalHints hints) throws SQLException;

	void execute(List<DalCommand> commands, DalHints hints) throws SQLException;

	Map<String, ?> call(String callString, StatementParameters parameters,
			DalHints hints) throws SQLException;
}
