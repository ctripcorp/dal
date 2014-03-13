package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DalClient {
	public static enum OperationEnum {
		QUERY("query", 2001),
		UPDATE_SIMPLE("update", 2002),
		UPDATE_KH("update(KeyHolder)", 2003),
		BATCH_UPDATE("batchUpdate(sqls)", 2004),
		BATCH_UPDATE_PARAM("batchUpdate(params)", 2005),
		EXECUTE("execute", 2006),
		CALL("call", 2007);

		private String operation;
		private int eventId;
		private OperationEnum(String operation, int eventId) {
			this.operation = operation;
			this.eventId = eventId;
		}
		public int getEventId() {
			return eventId;
		}
		public String getOperation() {
			return operation;
		}
	}
	
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
