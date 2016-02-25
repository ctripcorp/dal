package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Table;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;

public class MultipleQueryRequest {
	private StringBuilder sqls = new StringBuilder();
	private List<DalResultSetExtractor<?>> extractors = new ArrayList<>();
	private MultipleResultMerger mergers = new MultipleResultMerger();
	
	public <T> MultipleQueryRequest add(String sql, DalResultSetExtractor<T> extractor, ResultMerger<T> merger) {
		sql = sql.trim();
		sqls.append(sql);
		if(!sql.endsWith(";"))
			sqls.append(';');

		extractors.add(extractor);
		mergers.add(merger);
		
		return this;
	}
		
	public <T> MultipleQueryRequest add(String sql, DalRowMapper<T> mapper) {
		DalListMerger<T> defaultMerger = new DalListMerger<>();
		return add(sql, new DalRowMapperExtractor<T>(mapper), defaultMerger);
	}
	
	public <T> MultipleQueryRequest add(String sql, DalRowMapper<T> mapper, ResultMerger<List<T>> merger) {
		return add(sql, new DalRowMapperExtractor<T>(mapper), merger);
	}
	
	public <T> MultipleQueryRequest add(String sql, DalRowMapper<T> mapper, Comparator<T> sorter) {
		ResultMerger<List<T>> defaultMerger = new DalListMerger<>(sorter);
		return add(sql, new DalRowMapperExtractor<T>(mapper), defaultMerger);
	}
	
	public <T> MultipleQueryRequest add(String sql, Class<T> clazz) throws SQLException {
		DalListMerger<T> defaultMerger = new DalListMerger<>();
		
		Table table = clazz.getAnnotation(Table.class);
		if (table == null)
			return add(sql, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()), defaultMerger);
		
		return add(sql, new DalRowMapperExtractor<T>(new DalDefaultJpaMapper<T>(clazz)), defaultMerger);
	}
	
	public <T> MultipleQueryRequest add(String sql, Class<T> clazz, ResultMerger<List<T>> merger) {
		return add(sql, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()), merger);
	}
	
	public <T> MultipleQueryRequest add(String sql, Class<T> clazz, Comparator<T> sorter) {
		ResultMerger<List<T>> defaultMerger = new DalListMerger<>(sorter);
		return add(sql, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()), defaultMerger);
	}
	
	public MultipleQueryRequest add(String sql, DalRowCallback callback) {
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
}
