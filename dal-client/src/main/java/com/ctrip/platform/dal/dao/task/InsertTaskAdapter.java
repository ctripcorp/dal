package com.ctrip.platform.dal.dao.task;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
}
