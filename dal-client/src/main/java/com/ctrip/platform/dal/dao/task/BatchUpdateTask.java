package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.StatementParameters;

public class BatchUpdateTask<T> extends AbstractIntArrayBulkTask<T> {
	public static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";
	
	private String SET_VALUE_TMPL;

	private Map<String, Boolean> defaultUpdateColumnNames;
	public void initialize(DalParser<T> parser) {
		super.initialize(parser);
		initUpdateColumns();
		switch (dbCategory) {
		case MySql:
			SET_VALUE_TMPL = "%s=IFNULL(?,%s) ";
			break;
		case SqlServer:
			SET_VALUE_TMPL = "%s=ISNULL(?,%s) ";
			break;
		default:
			SET_VALUE_TMPL = "";
			break;
		}
	}

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;

		Map<String, Boolean> pojoFieldStatus = filterUpdateColumnNames(hints, daoPojos);
		
		String[] updateColumnNames = pojoFieldStatus.keySet().toArray(new String[pojoFieldStatus.size()]);
		
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			StatementParameters parameters = new StatementParameters();

			addParameters(parameters, pojo, updateColumnNames);
			addParameters(parameters, pojo, parser.getPrimaryKeyNames());
			
			parametersList[i++] = parameters;
		}
		
		String batchUpdateSql = buildBatchUpdateSql(getTableName(hints), pojoFieldStatus);
		
		int[] result = client.batchUpdate(batchUpdateSql, parametersList, hints);
		return result;
	}
	
	private void initUpdateColumns() {
		defaultUpdateColumnNames = new LinkedHashMap<>();
		
		for(String column: parser.getColumnNames()) {
			defaultUpdateColumnNames.put(column, false);
		}
		
		for (String column : parser.getPrimaryKeyNames()) {
			defaultUpdateColumnNames.remove(column);
		}
	}
	
	private Map<String, Boolean> filterUpdateColumnNames(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) {
		if(hints.is(DalHintEnum.updateNullField))
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
				Set<String> check = pojo.get(colName) == null ? notNullFields : nullFields;
				
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
			String columnName = quote(fieldStatus.getKey());
			// If the field contains null value
			if(fieldStatus.getValue())
				updateColumnTmpls.add(String.format(SET_VALUE_TMPL, columnName, columnName));
			else
				updateColumnTmpls.add(String.format(TMPL_SET_VALUE, columnName));
		}
		
		String updateColumnsTmpl = StringUtils.join(updateColumnTmpls, COLUMN_SEPARATOR);
		return String.format(TMPL_SQL_UPDATE, tableName, updateColumnsTmpl, pkSql);
	}
}
