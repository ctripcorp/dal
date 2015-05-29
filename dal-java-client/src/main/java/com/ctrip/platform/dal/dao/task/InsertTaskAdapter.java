package com.ctrip.platform.dal.dao.task;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.dao.DalParser;

public class InsertTaskAdapter<T> extends TaskAdapter<T> {
	public static final String TMPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES(%s)";

	protected String columnsForInsert;
	protected List<String> validColumnsForInsert;

	public void initialize(DalParser<T> parser) {
		super.initialize(parser);
		validColumnsForInsert = buildValidColumnsForInsert();
		columnsForInsert = combineColumns(validColumnsForInsert, COLUMN_SEPARATOR);
	}
	
	private List<String> buildValidColumnsForInsert() {
		List<String> validColumns = new ArrayList<String>();
		for(String s : parser.getColumnNames()){
			if(!(parser.isAutoIncrement() && isPrimaryKey(s)))
				validColumns.add(s);
		}
		
		return validColumns;
	}	
}
