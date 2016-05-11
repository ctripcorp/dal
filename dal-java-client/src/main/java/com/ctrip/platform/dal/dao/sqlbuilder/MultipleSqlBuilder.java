package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Table;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalListMerger;
import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowCallbackExtractor;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.MultipleResultMerger;

public class MultipleSqlBuilder implements SqlBuilder {
	private StringBuilder sqls = new StringBuilder();
	private StatementParameters parameters;
	private List<DalResultSetExtractor<?>> extractors = new ArrayList<>();
	private MultipleResultMerger mergers = new MultipleResultMerger();
	
	/**
	 * This extractor instance maybe used in more than one thread. Make sure it is thread safe.
	 * To be thread safe is easy, just not keep any changeable state inside the object.
	 */
	public <T> MultipleSqlBuilder add(String sql, DalResultSetExtractor<T> extractor, ResultMerger<T> merger) {
		sql = sql.trim();
		sqls.append(sql);
		if(!sql.endsWith(";"))
			sqls.append(';');

		extractors.add(extractor);
		mergers.add(merger);
		
		return this;
	}
		
	/**
	 * This mapper instance maybe used in more than one thread. Make sure it is thread safe.
	 * To be thread safe is easy, just not keep any changeable state inside the object.
	 * The default JPA parser is thread safe.
	 * @param sql
	 * @param mapper
	 * @return
	 */
	public <T> MultipleSqlBuilder add(String sql, DalRowMapper<T> mapper) {
		DalListMerger<T> defaultMerger = new DalListMerger<>();
		return add(sql, new DalRowMapperExtractor<T>(mapper), defaultMerger);
	}
	
	/**
	 * This mapper instance maybe used in more than one thread. Make sure it is thread safe.
	 * To be thread safe is easy, just not keep any changeable state inside the object.
	 * The default JPA parser is thread safe.
	 */
	public <T> MultipleSqlBuilder add(String sql, DalRowMapper<T> mapper, ResultMerger<List<T>> merger) {
		return add(sql, new DalRowMapperExtractor<T>(mapper), merger);
	}
	
	/**
	 * This mapper instance maybe used in more than one thread. Make sure it is thread safe.
	 * To be thread safe is easy, just not keep any changeable state inside the object.
	 * The default JPA parser is thread safe.
	 */
	public <T> MultipleSqlBuilder add(String sql, DalRowMapper<T> mapper, Comparator<T> sorter) {
		ResultMerger<List<T>> defaultMerger = new DalListMerger<>(sorter);
		return add(sql, new DalRowMapperExtractor<T>(mapper), defaultMerger);
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
	public <T> MultipleSqlBuilder add(String sql, Class<T> clazz) throws SQLException {
		return add(sql, clazz, new DalListMerger<T>());
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
	public <T> MultipleSqlBuilder add(String sql, Class<T> clazz, ResultMerger<List<T>> merger) throws SQLException {
		Table table = clazz.getAnnotation(Table.class);
		// If it is annotated DAL POJO
		if (table != null)
			return add(sql, new DalRowMapperExtractor<T>(new DalDefaultJpaMapper<T>(clazz)), merger);
		
		return add(sql, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()), merger);
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
	public <T> MultipleSqlBuilder add(String sql, Class<T> clazz, Comparator<T> sorter) throws SQLException {
		return add(sql, clazz, new DalListMerger<T>(sorter));
	}
	
	/**
	 * Work with callback. There will be null return value.
	 * @param sql
	 * @param callback
	 * @return
	 */
	public MultipleSqlBuilder add(String sql, DalRowCallback callback) {
		DalListMerger<Object> defaultMerger = new DalListMerger<>();
		return add(sql, new DalRowCallbackExtractor(callback), defaultMerger);
	}
	
	public List<DalResultSetExtractor<?>> getExtractors() {
		return extractors;
	}
	
	public String getSqls() {
		return sqls.toString();
	}

	public ResultMerger<List<?>> getMergers() {
		return mergers;
	}

	@Override
	public String build() {
		return sqls.toString();
	}

	public MultipleSqlBuilder with(StatementParameters parameters) {
		this.parameters = parameters;
		return this;
	}
	
	@Override
	public StatementParameters buildParameters() {
		return parameters;
	}
}
