package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalListMerger;
import com.ctrip.platform.dal.dao.helper.DalRowCallbackExtractor;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.EntityManager;
import com.ctrip.platform.dal.dao.helper.MultipleResultMerger;

public class MultipleSqlBuilder implements SqlBuilder {
	private List<QueryUnit> queryUnits = new ArrayList<>();
	private MultipleResultMerger mergers = new MultipleResultMerger();
	
	/**
	 * This extractor instance maybe used in more than one thread. Make sure it is thread safe.
	 * To be thread safe is easy, just not keep any changeable state inside the object.
	 */
	public <T> MultipleSqlBuilder addQuery(String sql, StatementParameters parameters, DalResultSetExtractor<T> extractor, ResultMerger<T> merger) {
		queryUnits.add(new QueryUnit(sql,parameters, extractor));
		mergers.add(merger);
		return this;
	}
		
	/**
	 * This mapper instance maybe used in more than one thread. Make sure it is thread safe.
	 * To be thread safe is easy, just not keep any changeable state inside the object.
	 * The default JPA parser is thread safe.
	 * @return
	 */
	public <T> MultipleSqlBuilder addQuery(String sql, StatementParameters parameters, DalRowMapper<T> mapper) {
		DalListMerger<T> defaultMerger = new DalListMerger<>();
		return addQuery(sql, parameters, new DalRowMapperExtractor<T>(mapper), defaultMerger);
	}
	
	/**
	 * This mapper instance maybe used in more than one thread. Make sure it is thread safe.
	 * To be thread safe is easy, just not keep any changeable state inside the object.
	 * The default JPA parser is thread safe.
	 */
	public <T> MultipleSqlBuilder addQuery(String sql, StatementParameters parameters, DalRowMapper<T> mapper, ResultMerger<List<T>> merger) {
		return addQuery(sql, parameters, new DalRowMapperExtractor<T>(mapper), merger);
	}
	
	/**
	 * This mapper instance maybe used in more than one thread. Make sure it is thread safe.
	 * To be thread safe is easy, just not keep any changeable state inside the object.
	 * The default JPA parser is thread safe.
	 */
	public <T> MultipleSqlBuilder addQuery(String sql, StatementParameters parameters, DalRowMapper<T> mapper, Comparator<T> sorter) {
		ResultMerger<List<T>> defaultMerger = new DalListMerger<>(sorter);
		return addQuery(sql, parameters, new DalRowMapperExtractor<T>(mapper), defaultMerger);
	}
	
	/**
	 * Query list of objects against type clazz with default list merger
	 * The class can be normal Object that can be get from result set or the class of annotated DAL POJO.
	 * The DalDefaultJpaMapper will be used as row mapper for later case.
	 * @param sql
	 * @param clazz
	 * @return
	 * @throws SQLException
	 */
	public <T> MultipleSqlBuilder addQuery(String sql, StatementParameters parameters, Class<T> clazz) throws SQLException {
		return addQuery(sql, parameters, clazz, new DalListMerger<T>());
	}
	
	/**
	 * Query list of objects against type clazz with given list merger
	 * The class can be normal Object that can be get from result set or the class of annotated DAL POJO.
	 * @param sql
	 * @param clazz
	 * @param merger
	 * @return
	 * @throws SQLException
	 */
	public <T> MultipleSqlBuilder addQuery(String sql, StatementParameters parameters, Class<T> clazz, ResultMerger<List<T>> merger) throws SQLException {
		return addQuery(sql, parameters, new DalRowMapperExtractor<T>(EntityManager.getMapper(clazz)), merger);
	}
	
	/**
	 * Query list of objects against type clazz with default list merger sort with given comparator.
	 * The class can be normal Object that can be get from result set or the class of annotated DAL POJO.
	 * @param sql
	 * @param clazz
	 * @param sorter
	 * @return
	 * @throws SQLException
	 */
	public <T> MultipleSqlBuilder addQuery(String sql, StatementParameters parameters, Class<T> clazz, Comparator<T> sorter) throws SQLException {
		return addQuery(sql, parameters, clazz, new DalListMerger<T>(sorter));
	}
	
	/**
	 * Work with callback. There will be null return value.
	 * @param sql
	 * @param callback
	 * @return
	 */
	public MultipleSqlBuilder addQuery(String sql, StatementParameters parameters, DalRowCallback callback) {
		DalListMerger<Object> defaultMerger = new DalListMerger<>();
		return addQuery(sql, parameters, new DalRowCallbackExtractor(callback), defaultMerger);
	}
	
	public List<DalResultSetExtractor<?>> getExtractors() {
		List<DalResultSetExtractor<?>> extractors = new ArrayList<>();
		for(QueryUnit unit: queryUnits)
			extractors.add(unit.extractor);
		return extractors;
	}
	
	public ResultMerger<List<?>> getMergers() {
		return mergers;
	}

	@Override
	public String build() {
		StringBuilder sb = new StringBuilder();
		List<DalResultSetExtractor<?>> extractors = new ArrayList<>();
		for(QueryUnit unit: queryUnits) {
			sb.append(unit.sql);
			if(!unit.sql.endsWith(";"))
				sb.append(';');
		}

		return sb.toString();
	}

	@Override
	public StatementParameters buildParameters() {
		StatementParameters parameters = new StatementParameters();
		for(QueryUnit unit: queryUnits) {
			parameters.addAll(unit.parameters);
		}
		return parameters;
	}
	
	private class QueryUnit {
		String sql;
		StatementParameters parameters;
		DalResultSetExtractor<?> extractor;
		
		<T> QueryUnit(String sql, StatementParameters parameters, DalResultSetExtractor<T> extractor) {
			this.sql = sql.trim();
			this.parameters = parameters;
			this.extractor = extractor;
		}
	}
}
