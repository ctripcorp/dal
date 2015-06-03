package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public class CombinedInsertTask<T> extends InsertTaskAdapter<T> implements BulkTask<Integer, T> {
	public static final String TMPL_SQL_MULTIPLE_INSERT = "INSERT INTO %s(%s) VALUES %s";

	@Override
	public Integer getEmptyValue() {
		return 0;
	}	

	@Override
	public Integer execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		StringBuilder values = new StringBuilder();

		int startIndex = 1;
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> vfields = daoPojos.get(index);
			
			removeAutoIncrementPrimaryFields(vfields);
			int paramCount = addParameters(startIndex, parameters, vfields, validColumnsForInsert);
			startIndex += paramCount;
			values.append(String.format("(%s),", combine("?", paramCount, ",")));
		}

		String sql = String.format(TMPL_SQL_MULTIPLE_INSERT,
				getTableName(hints), columnsForInsert,
				values.substring(0, values.length() - 2) + ")");

		KeyHolder keyHolder = hints.getKeyHolder();
		if(keyHolder == null) {
			return client.update(sql, parameters, hints);
		} else{
			KeyHolder tmpHolder = new KeyHolder();
			int count = client.update(sql, parameters, hints.setKeyHolder(tmpHolder));
			keyHolder.addPatial(daoPojos.keySet().toArray(new Integer[daoPojos.size()]), tmpHolder);
			return count;
		}
	}

	@Override
	public BulkTaskResultMerger<Integer> createMerger() {
		return new ShardedIntResultMerger();
	}
}
