package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalContextClient;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.UpdatableEntity;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class BatchUpdateTask<T> extends AbstractIntArrayBulkTask<T> {
	public static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";

	@Override
	public BulkTaskContext<T> createTaskContext(DalHints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) throws DalException {
		BulkTaskContext<T> taskContext = new BulkTaskContext<T>(rawPojos);
		
		Map<String, Boolean> pojoFieldStatus = taskContext.isUpdatableEntity() ?
				filterUpdatableEntity(hints, rawPojos) :
					filterNullColumns(hints, daoPojos);

		if(pojoFieldStatus.size() == 0)
			throw new DalException(ErrorCode.ValidateFieldCount);

		taskContext.setPojoFieldStatus(pojoFieldStatus);

		return taskContext;
	}

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, DalBulkTaskContext<T> taskContext) throws SQLException {
		List<T> rawPojos = taskContext.getRawPojos();
		boolean isUpdatableEntity = taskContext.isUpdatableEntity();
		Map<String, Boolean> pojoFieldStatus = taskContext.getPojoFieldStatus();

		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;
		String[] updateColumnNames = pojoFieldStatus.keySet().toArray(new String[pojoFieldStatus.size()]);
		
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			StatementParameters parameters = new StatementParameters();

			if(isUpdatableEntity && !hints.isUpdateUnchangedField())
				addParameters(parameters, pojo, updateColumnNames, ((UpdatableEntity)rawPojos.get(index)).getUpdatedColumns());
			else
				addParameters(parameters, pojo, updateColumnNames);
			
			addParameters(parameters, pojo, parser.getPrimaryKeyNames());
			addVersion(parameters, pojo);
			
			parametersList[i++] = parameters;
		}

		String tableName = getRawTableName(hints);
		if (taskContext instanceof DalTableNameConfigure)
			((DalTableNameConfigure) taskContext).addTables(tableName);

		String batchUpdateSql = buildBatchUpdateSql(quote(tableName), pojoFieldStatus);
		int[] result;
		if (client instanceof DalContextClient)
			result = ((DalContextClient) client).batchUpdate(batchUpdateSql, parametersList, hints, taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
		return result;
	}
	
	public void addParameters(StatementParameters parameters,
			Map<String, ?> entries, String[] validColumns, Set<String> updatedColumns) {
		int index = parameters.size() + 1;
		for(String column : validColumns){
			Object value = updatedColumns.contains(column) ? entries.get(column) : null;
			addParameter(parameters, index++, column, value);
		}
	}
	
	/**
	 * Find out all columns that are not changed to reduce the batch update sql size
	 * E.g
	 * 				C1	C2	C3	C4	C5	C6
	 * E1					x	x	x
	 * E2				x	x	x
	 * 
	 * final not changed columns: C1 and C2
	 * final always changed columns: C3 and C4
	 * final may changed columns: C2 and C5
	 * 
	 * So C1 and C2 will be removed from final update sql
	 * C3 and C4 will using set value
	 * C2 and C5 will use set ifnull
	 */
	private Map<String, Boolean> filterUpdatableEntity(DalHints hints, List<T> rawPojos) {
		Set<String> qualifiedColumns = filterColumns(hints);
		Map<String, Boolean> columnStatus = new HashMap<String, Boolean>();
		for(String column: qualifiedColumns)
			columnStatus.put(column, false);
		
		if(hints.isUpdateUnchangedField()) {
			return columnStatus;
		}
		
		Set<String> unChangedFields = new HashSet<>(qualifiedColumns);
		Set<String> changedFields = new HashSet<>(qualifiedColumns);
		
		for (T pojo: rawPojos) {
			if(unChangedFields.isEmpty())
				break;
			
			Set<String> updatedColumns = getUpdatedColumns(pojo);
			if(updatedColumns.size() == 0)
				continue;
			
			unChangedFields.removeAll(updatedColumns);
			changedFields.retainAll(updatedColumns);
		}
		
		for(String unChangedField: unChangedFields)
			columnStatus.remove(unChangedField);
		
		Set<String> remain = new HashSet<>(columnStatus.keySet());
		remain.removeAll(changedFields);
		
		for(String maybeChangedField: remain)
			columnStatus.put(maybeChangedField, true);

		return columnStatus;
	}

	private Map<String, Boolean> filterNullColumns(DalHints hints, List<Map<String, ?>> daoPojos) {
		Set<String> qualifiedColumns = filterColumns(hints);
		Map<String, Boolean> columnStatus = new HashMap<String, Boolean>();
		for(String column: qualifiedColumns)
			columnStatus.put(column, false);
		
		if(hints.isUpdateNullField()) {
			return columnStatus;
		}
		
		String[] columnsToCheck = qualifiedColumns.toArray(new String[qualifiedColumns.size()]);
		Set<String> nullFields = new HashSet<>(qualifiedColumns);
		Set<String> notNullFields = new HashSet<>(nullFields);
		
		for (Map<String, ?> pojo: daoPojos) {
			if(notNullFields.isEmpty() && nullFields.isEmpty())
				break;
			
			for (int i = 0; i < columnsToCheck.length; i++) {
				String colName = columnsToCheck[i];
				boolean isNull = pojo.get(colName) == null;
				
				Set<String> check = isNull ? notNullFields : nullFields;
				
				if(!check.isEmpty() && check.contains(colName))
					check.remove(colName);
			}
		}
		
		for(String nullField: nullFields)
			columnStatus.remove(nullField);
		
		Set<String> remain = new HashSet<>(columnStatus.keySet());
		remain.removeAll(notNullFields);
		
		for(String maybeNullField: remain)
			columnStatus.put(maybeNullField, true);

		return columnStatus;
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
