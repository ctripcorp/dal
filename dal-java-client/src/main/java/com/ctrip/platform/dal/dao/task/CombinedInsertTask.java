package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public class CombinedInsertTask<T> extends TaskAdapter<T> implements BulkTask<Integer> {
	public CombinedInsertTask(DalParser<T> parser) {
		super(parser);
	}
	
	@Override
	public Integer merge(List<Integer> results) {
		int value = 0;
		for(Integer i: results) value += i;
		return value;
	}

	@Override
	public Integer execute(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		StringBuilder values = new StringBuilder();

		int startIndex = 1;
		for (Map<String, ?> vfields: daoPojos) {
			removeAutoIncrementPrimaryFields(vfields);
			int paramCount = addParameters(startIndex, parameters, vfields, validColumnsForInsert);
			startIndex += paramCount;
			values.append(String.format("(%s),",
					combine("?", paramCount, ",")));
		}

		String sql = String.format(TMPL_SQL_MULTIPLE_INSERT,
				getTableName(hints), columnsForInsert,
				values.substring(0, values.length() - 2) + ")");

		KeyHolder keyHolder = hints.getKeyHolder();
		if(keyHolder == null) {
			return client.update(sql, parameters, hints);
		} else{
			KeyHolder tmpHolder = new KeyHolder();
			int count = client.update(sql, parameters, hints, tmpHolder);
			keyHolder.merge(tmpHolder);
			hints.addDetailResults(tmpHolder);
			return count;
		}
	}
}
