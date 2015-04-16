package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
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
	
	protected String buildInsertSql(DalHints hints, Map<String, ?> fields) throws SQLException {
		filterNullFileds(fields);
		Set<String> remainedColumns = fields.keySet();
		String cloumns = combineColumns(remainedColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, remainedColumns.size(),
				COLUMN_SEPARATOR);

		return String.format(TMPL_SQL_INSERT, getTableName(hints, fields), cloumns,
				values);
	}
	
	protected List<String> buildValidColumnsForInsert() {
		List<String> validColumns = new ArrayList<String>();
		for(String s : parser.getColumnNames()){
			if(!(parser.isAutoIncrement() && isPrimaryKey(s)))
				validColumns.add(s);
		}
		
		return validColumns;
	}	
}
