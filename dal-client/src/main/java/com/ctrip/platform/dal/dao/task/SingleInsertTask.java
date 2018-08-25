package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalContextClient;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class SingleInsertTask<T> extends InsertTaskAdapter<T> implements SingleTask<T>, KeyHolderAwaredTask {
	@Override
	public int execute(DalHints hints, Map<String, ?> fields, T rawPojo, DalTaskContext taskContext) throws SQLException {
		List<Map<String, ?>> pojoList = new ArrayList<Map<String,?>>();
		List<T> rawPojos = new ArrayList<>();
		
		pojoList.add(fields);
		rawPojos.add(rawPojo);
		
		Set<String> unqualifiedColumns = filterUnqualifiedColumns(hints, pojoList, rawPojos);
		
		removeUnqualifiedColumns(fields, unqualifiedColumns);

		String tableName = getRawTableName(hints, fields);
		if (taskContext instanceof DalTableNameConfigure)
			((DalTableNameConfigure) taskContext).addTables(tableName);

		/*
		 * In case fields is empty, the final sql will be like "insert into tableName () values()".
		 * We do not report error or simply return 0, but just let DB decide what to do.
		 * For MS Sql server, sql like this is illegal, but for mysql, this works however
		 */
		String insertSql = buildInsertSql(hints, fields, tableName);
		
		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, fields);

		if (client instanceof DalContextClient)
			return ((DalContextClient) client).update(insertSql, parameters, hints, taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
	}
	
	private String buildInsertSql(DalHints hints, Map<String, ?> fields, String effectiveTableName) throws SQLException {
		Set<String> remainedColumns = fields.keySet();
		String cloumns = combineColumns(remainedColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, remainedColumns.size(), COLUMN_SEPARATOR);

		return String.format(TMPL_SQL_INSERT, quote(effectiveTableName), cloumns, values);
	}

}
