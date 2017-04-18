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
	
	public Set<String> filterUnqualifiedColumns(DalHints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) {
		Set<String> unqualifiedColumns = new HashSet<>(notInsertableColumns);
		
		if(parser.isAutoIncrement() && hints.isIdentityInsertDisabled())
			unqualifiedColumns.add(parser.getPrimaryKeyNames()[0]);

		if(hints.isInsertNullField()) {
			return unqualifiedColumns;
		}

		Set<String> nullColumns = new HashSet<>(insertableColumns);
		String[] columnsToCheck = nullColumns.toArray(new String[nullColumns.size()]);
		boolean changed = false;
		for (Map<String, ?> pojo: daoPojos) {
			if(nullColumns.isEmpty())
				break;

			if(changed) {
				columnsToCheck = nullColumns.toArray(new String[nullColumns.size()]);
				changed = false;
			}
			
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
