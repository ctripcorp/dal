package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class SingleUpdateTask<T> extends TaskAdapter<T> implements SingleTask<T> {
	public static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";

	@Override
	public int execute(DalHints hints, Map<String, ?> fields) throws SQLException {
		if (fields.size() == 0)
			throw new DalException(ErrorCode.ValidateFieldCount);
		Map<String, ?> pks = getPrimaryKeys(fields);
		
		String updateSql = buildUpdateSql(getTableName(hints, fields), fields, hints);

		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, fields);
		addParameters(parameters, pks);
		
		return client.update(updateSql, parameters, hints);
	}

	private String buildUpdateSql(String tableName, Map<String, ?> fields, DalHints hints) {
		// Remove null value when hints is not DalHintEnum.updateNullField or
		// primary key
		for (String column : parser.getColumnNames()) {
			if ((fields.get(column) == null && !hints.is(DalHintEnum.updateNullField))
					|| isPrimaryKey(column))
				fields.remove(column);
		}

		String columns = String.format(
				combine(TMPL_SET_VALUE, fields.size(), COLUMN_SEPARATOR),
				quote(fields.keySet()));

		return String.format(TMPL_SQL_UPDATE, tableName, columns, pkSql);
	}
}
