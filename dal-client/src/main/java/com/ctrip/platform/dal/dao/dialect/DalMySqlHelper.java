package com.ctrip.platform.dal.dao.dialect;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalMySqlHelper<T> {
	
	private static final String COLUMN_SEPARATOR = ", ";
	private static final String PLACE_HOLDER = "?";
	private static final String TMPL_SQL_MULTIPLE_INSERT = "REPLACE INTO %s(%s) VALUES %s";
	
	private DalClient client = null;
	private DalParser<T> parser = null;
	
	public DalMySqlHelper(DalParser<T> parser){
		this.parser = parser;
		this.client = DalClientFactory.getClient(this.parser.getDatabaseName());
	}

	public int replace(KeyHolder holder, DalHints hints, List<T> entities) throws SQLException{
		if(null == entities || entities.size() == 0)
			return 0;
		Map<String, ?> fields = this.parser.getFields(entities.get(0));
		Set<String> remainedColumns = fields.keySet();
		String cloumns = combine(remainedColumns, COLUMN_SEPARATOR);
		int count = entities.size();
		StatementParameters parameters = new StatementParameters();
		StringBuilder values = new StringBuilder();

		int startIndex = 1;
		for (int i = 0; i < count; i++) {
			Map<String, ?> vfields = parser.getFields(entities.get(i));
			int paramCount = addParameters(startIndex, parameters, vfields);
			startIndex += paramCount;
			values.append(String.format("(%s),",
					this.combine(PLACE_HOLDER, paramCount, COLUMN_SEPARATOR)));
		}

		String sql = String.format(TMPL_SQL_MULTIPLE_INSERT,
				this.parser.getTableName(), cloumns,
				values.substring(0, values.length() - 2) + ")");

		return null == holder ? this.client.update(sql, parameters, hints)
				: this.client.update(sql, parameters, hints.setKeyHolder(holder));
	}
	
	@SuppressWarnings("unchecked")
	public int replace(KeyHolder holder, DalHints hints, T... entities) throws SQLException{
		return replace(holder, hints, Arrays.asList(entities));
	}
	
	
	private int addParameters(int start, StatementParameters parameters,
			Map<String, ?> entries) {
		int count = 0;
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			parameters.set(count + start, this.parser.getColumnTypes()[count],
					entry.getValue());
			count++;
		}
		return count;
	}
	
	private String combine(Collection<String> values, String separator) {
		return combine(values.toArray(new String[values.size()]), separator);
	}
	
	private String combine(String[] values, String separator) {
		StringBuilder valuesSb = new StringBuilder();
		int i = 0;
		for (String value : values) {
			valuesSb.append(value);
			if (++i < values.length)
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}
	
	private String combine(String value, int count, String separator) {
		StringBuilder valuesSb = new StringBuilder();

		for (int i = 1; i <= count; i++) {
			valuesSb.append(value);
			if (i < count)
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}
}
