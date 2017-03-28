package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.UpdatableEntity;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class SingleUpdateTask<T> extends TaskAdapter<T> implements SingleTask<T> {
	public static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";
	
	@Override
	public int execute(DalHints hints, Map<String, ?> fields, T rawPojo) throws SQLException {
		if (fields.size() == 0)
			throw new DalException(ErrorCode.ValidateFieldCount);
		Map<String, ?> pks = getPrimaryKeys(fields);
		
		Object version = getVersion(fields);
		
		if(rawPojo instanceof UpdatableEntity)
			filterNullColumns(hints, fields);
		else
			filterUpdatableEntity(hints, fields, getUpdatedColumns(rawPojo));
		
		String updateSql = buildUpdateSql(getTableName(hints, fields), fields, hints);

		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, fields);
		addParameters(parameters, pks);
		addVersion(parameters, version);
		
		return client.update(updateSql, parameters, hints);
	}

	private Object getVersion(Map<String, ?> fields) throws DalException {
		if(!hasVersion)
			return null;

		Object version = fields.get(parser.getVersionColumn());
		if(version == null)
			throw new DalException(ErrorCode.ValidateVersion);

		return version;
	}
	
	private void filterNullColumns(DalHints hints, Map<String, ?> fields) throws DalException {
		Set<String> updatableColumns = filterColumns(hints);
			
		// Remove null value when hints is not DalHintEnum.updateNullField or
		// primary key or not updatable
		for (String column : parser.getColumnNames()) {
			if ((fields.get(column) == null && !hints.isUpdateNullField())
					|| isPrimaryKey(column) || !updatableColumns.contains(column))
				fields.remove(column);
		}
	}
	
	private void filterUpdatableEntity(DalHints hints, Map<String, ?> fields, Set<String> updatedColumns) throws DalException {
		Set<String> updatableColumns = filterColumns(hints);
			
		// Dirty flag is not set but need to update
		// primary key or not updatable
		for (String column : parser.getColumnNames()) {
			if (!updatedColumns.contains(column) && !hints.isUpdateUnchangedField() || 
					isPrimaryKey(column) || !updatableColumns.contains(column))
				fields.remove(column);
		}
	}
	
	private String buildUpdateSql(String tableName, Map<String, ?> fields, DalHints hints) {
		String columns = String.format(
				combine(TMPL_SET_VALUE, fields.size(), COLUMN_SEPARATOR),
				quote(fields.keySet()));

		if(isVersionUpdatable)
			columns += COLUMN_SEPARATOR + setVersionValueTmpl;

		return String.format(TMPL_SQL_UPDATE, tableName, columns, updateCriteriaTmpl);
	}
	
	private void addVersion(StatementParameters parameters, Object version) throws DalException {
		if(!hasVersion)
			return;
		
		addParameter(parameters, parameters.size() + 1, parser.getVersionColumn(), version);
	}
}
