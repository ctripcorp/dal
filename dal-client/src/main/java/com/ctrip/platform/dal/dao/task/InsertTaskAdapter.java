package com.ctrip.platform.dal.dao.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.UpdatableEntity;
import com.ctrip.platform.dal.exceptions.DalException;

public class InsertTaskAdapter<T> extends TaskAdapter<T> {
	public static final String TMPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES(%s)";

	protected Set<String> insertableColumns;
	protected Set<String> notInsertableColumns;
	protected String columnsForInsert;
	protected String columnsForInsertWithId;
	protected List<String> validColumnsForInsert;
	protected List<String> validColumnsForInsertWithId;
	
	public void initialize(DalParser<T> parser) {
		super.initialize(parser);
		
		insertableColumns = new HashSet<>();
		Collections.addAll(insertableColumns, parser.getInsertableColumnNames());
		
		notInsertableColumns = new HashSet<>();
		Collections.addAll(notInsertableColumns, parser.getColumnNames());
		notInsertableColumns.removeAll(insertableColumns);
		
		validColumnsForInsert = buildValidColumnsForInsert();
		validColumnsForInsertWithId = buildValidColumnsForInsertWithId();
		
		columnsForInsert = combineColumns(validColumnsForInsert, COLUMN_SEPARATOR);
		columnsForInsertWithId = combineColumns(validColumnsForInsertWithId, COLUMN_SEPARATOR);
	}
	
	private List<String> buildValidColumnsForInsert() {
		List<String> validColumns = new LinkedList<>(buildValidColumnsForInsertWithId());
		
		if(parser.isAutoIncrement())
			validColumns.remove(parser.getPrimaryKeyNames()[0]);
		
		return validColumns;
	}	

	private List<String> buildValidColumnsForInsertWithId() {
		return Arrays.asList(parser.getInsertableColumnNames());
	}
	
	public List<String> buildValidColumnsForInsert(Set<String> unqualifiedColumns) {
		List<String> finalalidColumnsForInsert = new ArrayList<>(Arrays.asList(parser.getInsertableColumnNames()));
		finalalidColumnsForInsert.removeAll(unqualifiedColumns);
		
		return finalalidColumnsForInsert;
	}
	
	public Set<String> filterUnqualifiedColumns(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, List<T> rawPojos) throws DalException {
		Set<String> unqualifiedColumns = new HashSet<>(notInsertableColumns);
		
		if(parser.isAutoIncrement() && hints.isIdentityInsertDisabled())
			unqualifiedColumns.add(parser.getPrimaryKeyNames()[0]);

		if(hints.isInsertUnchangedField() ||hints.isInsertNullField()) {
			return unqualifiedColumns;
		}

		if(rawPojos.get(0) instanceof UpdatableEntity)
			return filterUpdatableEntity(unqualifiedColumns, daoPojos, rawPojos);
		else
			return filterNullColumns(unqualifiedColumns, daoPojos);
	}
	
	private Set<String> filterUpdatableEntity(Set<String> unqualifiedColumns, Map<Integer, Map<String, ?>> daoPojos, List<T> rawPojos) throws DalException {
		Set<String> unchangedColumns = new HashSet<>(insertableColumns);
		String[] columnsToCheck = unchangedColumns.toArray(new String[unchangedColumns.size()]);
		boolean changed = false;		
		for (Integer index :daoPojos.keySet()) {
			if(unchangedColumns.isEmpty())
				break;

			// You may ask why I am doing the following, it is because of performance
			if(changed) {
				columnsToCheck = unchangedColumns.toArray(new String[unchangedColumns.size()]);
				changed = false;
			}
			
			UpdatableEntity rawPojo = (UpdatableEntity)rawPojos.get(index);
			Set<String> updatedColumns = rawPojo.getUpdatedColumns();
			for (String columnToCheck: columnsToCheck) {
				if(updatedColumns.contains(columnToCheck)) {
					unchangedColumns.remove(columnToCheck);
					changed = true;
				}
			}
		}

		unqualifiedColumns.addAll(unchangedColumns);
		
		return unqualifiedColumns;
	}
	
	private Set<String> filterNullColumns(Set<String> unqualifiedColumns, Map<Integer, Map<String, ?>> daoPojos) throws DalException {
		Set<String> nullColumns = new HashSet<>(insertableColumns);
		String[] columnsToCheck = nullColumns.toArray(new String[nullColumns.size()]);
		boolean changed = false;
		for (Integer index :daoPojos.keySet()) {
			if(nullColumns.isEmpty())
				break;

			if(changed) {
				columnsToCheck = nullColumns.toArray(new String[nullColumns.size()]);
				changed = false;
			}
			
			Map<String, ?> pojo = daoPojos.get(index);
			for (int i = 0; i < columnsToCheck.length; i++) {
				String colName = columnsToCheck[i];
				if(pojo.get(colName) != null) {
					nullColumns.remove(colName);
					changed = true;
				}
			}
		}		

		unqualifiedColumns.addAll(nullColumns);
		
		return unqualifiedColumns;
	}

	public void removeUnqualifiedColumns(Map<String, ?> pojo, Set<String> unqualifiedColumns) {
		if(unqualifiedColumns.size() == 0)
			return;
		
		for(String columName: unqualifiedColumns) {
			pojo.remove(columName);
		}
	}
}
