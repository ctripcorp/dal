package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class BatchUpdateTask<T> extends AbstractIntArrayBulkTask<T> {
	public static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;

		Map<String, Boolean> pojoFieldStatus = filterUpdateColumnNames(hints, daoPojos);
		if(pojoFieldStatus.size() == 0)
			throw new DalException(ErrorCode.ValidateFieldCount);
		
		String[] updateColumnNames = pojoFieldStatus.keySet().toArray(new String[pojoFieldStatus.size()]);
		
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			StatementParameters parameters = new StatementParameters();

			addParameters(parameters, pojo, updateColumnNames);
			addParameters(parameters, pojo, parser.getPrimaryKeyNames());
			addVersion(parameters, pojo);
			
			parametersList[i++] = parameters;
		}
		
		String batchUpdateSql = buildBatchUpdateSql(getTableName(hints), pojoFieldStatus);
		
		int[] result = client.batchUpdate(batchUpdateSql, parametersList, hints);
		return result;
	}
	
	private Map<String, Boolean> filterUpdateColumnNames(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) throws DalException {
		if(hints.isUpdateNullField())
			return defaultUpdateColumnNames;
		
		String[] columnsToCheck = defaultUpdateColumnNames.keySet().toArray(new String[defaultUpdateColumnNames.size()]);
		Set<String> nullFields = new HashSet<>(defaultUpdateColumnNames.keySet());
		Set<String> notNullFields = new HashSet<>(nullFields);
		
		for (Integer index :daoPojos.keySet()) {
			if(notNullFields.isEmpty() && nullFields.isEmpty())
				break;
			
			Map<String, ?> pojo = daoPojos.get(index);
			for (int i = 0; i < columnsToCheck.length; i++) {
				String colName = columnsToCheck[i];
				boolean isNull = pojo.get(colName) == null;
				
				Set<String> check = isNull ? notNullFields : nullFields;
				
				if(!check.isEmpty() && check.contains(colName))
					check.remove(colName);
			}
		}
		
		Map<String, Boolean> updateColumnNames = new LinkedHashMap<>(defaultUpdateColumnNames);
		for(String nullField: nullFields)
			updateColumnNames.remove(nullField);
		
		Set<String> remain = new HashSet<>(updateColumnNames.keySet());
		remain.removeAll(notNullFields);
		
		for(String maybeNullField: remain)
			updateColumnNames.put(maybeNullField, true);

		return updateColumnNames;
	}

	private String buildBatchUpdateSql(String tableName, Map<String, Boolean> pojoFieldStatus) {
		List<String> updateColumnTmpls = new ArrayList<>(pojoFieldStatus.size());

		for(Map.Entry<String, Boolean> fieldStatus: pojoFieldStatus.entrySet()) {
			String columnName = fieldStatus.getKey();
			String quotedColumnName = quote(columnName);
			
			// If the field contains null value
			if(fieldStatus.getValue())
				updateColumnTmpls.add(String.format(setValueTmpl, quotedColumnName, quotedColumnName));
			else
				updateColumnTmpls.add(String.format(TMPL_SET_VALUE, quotedColumnName));
		}
		
		if(isVersionUpdatable)
			updateColumnTmpls.add(setVersionValueTmpl);
		
		String updateColumnsTmpl = StringUtils.join(updateColumnTmpls, COLUMN_SEPARATOR);
		return String.format(TMPL_SQL_UPDATE, tableName, updateColumnsTmpl, updateCriteriaTmpl);
	}
	
	private void addVersion(StatementParameters parameters, Map<String, ?> pojo) throws DalException {
		if(!hasVersion)
			return;
		
		Object version = pojo.get(parser.getVersionColumn());
		if(version == null)
			throw new DalException(ErrorCode.ValidateVersion);
		
		addParameter(parameters, parameters.size() + 1, parser.getVersionColumn(), version);
	}	
}
