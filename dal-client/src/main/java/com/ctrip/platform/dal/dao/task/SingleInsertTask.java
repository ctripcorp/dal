package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SingleInsertTask<T> extends InsertTaskAdapter<T> implements SingleTask<T> {
	
	@Override
	public int execute(DalHints hints, Map<String, ?> fields) throws SQLException {
		if(hints.isIdentityInsertDisabled())
			removeAutoIncrementPrimaryFields(fields);
		
		String insertSql = buildInsertSql(hints, fields);

		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, fields);
		
		return client.update(insertSql, parameters, hints);
	}
	
	private String buildInsertSql(DalHints hints, Map<String, ?> fields) throws SQLException {
		filterNullFileds(fields);
		Set<String> remainedColumns = fields.keySet();
		String cloumns = combineColumns(remainedColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, remainedColumns.size(),
				COLUMN_SEPARATOR);

		return String.format(TMPL_SQL_INSERT, getTableName(hints, fields), cloumns,
				values);
	}
}
