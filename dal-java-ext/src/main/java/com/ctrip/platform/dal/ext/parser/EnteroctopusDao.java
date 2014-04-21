package com.ctrip.platform.dal.ext.parser;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public class EnteroctopusDao {

	private static final String INSERT_TEMP = "INSERT INTO %s VALUES";
	private static final String QUERY_PATTERN = "SELECT * FROM %s";
	
	private static ConcurrentHashMap<Class<?>, EnteroctopusDao> cache = null;
	private Loader loader = null;
	
	@SuppressWarnings("rawtypes")
	private DalParser parser = null;
	private DalClient client = null;
	private Set<String> permaryKeys = null;
	
	static{
		cache = new ConcurrentHashMap<Class<?>, EnteroctopusDao>();
	}
	
	private EnteroctopusDao(Class<?> clazz){ 
		this.loader = new EnteroctopusLoader();
		this.parser = EnteroctopusParser.create(clazz , this.loader);
		this.client = DalClientFactory.getClient(this.parser.getDatabaseName());
		this.permaryKeys = new TreeSet<String>();
		for(String key : this.parser.getPrimaryKeyNames()){
			this.permaryKeys.add(key);
		}
	} 
	
	public static EnteroctopusDao create(Class<?> clazz){
		if(!cache.contains(clazz)){
			cache.put(clazz, new EnteroctopusDao(clazz));
		}
		return cache.get(clazz);
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
	
	private int addParameters(int start, StatementParameters parameters,
			Map<String, ?> entries) {
		int count = 0;
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			Object value = this.permaryKeys.contains(entry.getKey()) ? null : entry.getValue();
			parameters.set(count + start, this.parser.getColumnTypes()[count], value);
			count++;
		}
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public <T> KeyHolder batchInsert(List<T> entities,  boolean replace) throws SQLException{
		KeyHolder generatedKeyHolder = new KeyHolder();
		if(null == entities || entities.size() < 1)
			return generatedKeyHolder;
		int count = entities.size();
		StatementParameters parameters = new StatementParameters();
		StringBuilder insertBuilder = new StringBuilder(
				String.format(INSERT_TEMP, this.parser.getTableName()));
		int startIndex = 1;
		for(int i = 0; i < count ; i++)
		{
			Map<String, ?> fields = parser.getFields(entities.get(i));
			int paramCount = addParameters(startIndex, parameters, fields);
			startIndex += paramCount;
			insertBuilder.append(
					String.format("(%s),", this.combine("?", paramCount, ",")));
		}
		String sql = insertBuilder.substring(0, insertBuilder.length() - 2) + ")";
		
		DalHints hints = new DalHints();
		this.client.update(sql, parameters, hints, generatedKeyHolder);
		return generatedKeyHolder;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> queryAll() throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		DalRowMapperExtractor<T> rowextractor = new DalRowMapperExtractor<T>(this.parser);
		List<T> result = this.client.query(String.format(QUERY_PATTERN, this.parser.getTableName()), 
				parameters, hints, rowextractor);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> queryByCondition(String condition, Object... params) throws SQLException{
		String sql = String.format(QUERY_PATTERN, this.parser.getTableName()) + " WHERE " + condition;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		for(int i = 0 ; i < params.length ; i++){
			parameters.set(i + 1, params[i]);
		}

		DalRowMapperExtractor<T> rowextractor = new DalRowMapperExtractor<T>(this.parser);
		List<T> result = this.client.query(sql, parameters, hints, rowextractor);
		return result;
	}
}
