package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.Comparator;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalFirstResultMerger;
import com.ctrip.platform.dal.dao.helper.DalListMerger;
import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRangedResultMerger;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultMerger;

public class FreeSelectSqlBuilder<K> implements SqlBuilder, SelectBuilder {
	private static final String MYSQL_PAGE_SUFFIX_TPL= " limit %d, %d";
	private static final String SQLSVR_PAGE_SUFFIX_TPL= " OFFSET %d ROWS FETCH NEXT %d ROWS ONLY";

	private String selectSqlTemplate;
	private DatabaseCategory dbCategory;
	private StatementParameters parameters;
	
	private DalRowMapper mapper;
	private ResultMerger merger;
	private DalResultSetExtractor extractor;

	private boolean requireFirst = false;
	private boolean requireSingle = false;
	private boolean nullable = false;

	private int count;
	private int start;

	public FreeSelectSqlBuilder(DatabaseCategory dbCategory) {
		this.dbCategory = dbCategory;
	}
	
	/**
	 * If there is IN parameter, no matter how many values in the IN clause, the IN clause only need to 
	 * contain one "?".
	 * E.g. SELECT ... WHERE id IN (?)
	 * @param updateSqlTemplate
	 */
	public FreeSelectSqlBuilder<K> setTemplate(String selectSqlTemplate) {
		this.selectSqlTemplate= selectSqlTemplate;
		return this;
	}
	
	public FreeSelectSqlBuilder<K> with(StatementParameters parameters) {
		this.parameters = parameters;
		return this;
	}
	
	@Override
	public StatementParameters buildParameters() {
		return parameters;
	}
	
	public String build(){
		if(count  == 0)
			return selectSqlTemplate;
		
		String suffix = DatabaseCategory.SqlServer == dbCategory ? SQLSVR_PAGE_SUFFIX_TPL : MYSQL_PAGE_SUFFIX_TPL;
		String sql = selectSqlTemplate + suffix;
		
		return String.format(sql, start, count);
	}

	public <T> FreeSelectSqlBuilder<K> mapWith(DalRowMapper<T> mapper) {
		this.mapper = mapper;
		return this;
	}

	public FreeSelectSqlBuilder<K> simpleType() {
		return mapWith(new DalObjectRowMapper());
	}

	public FreeSelectSqlBuilder<K> requireFirst() {
		requireFirst = true;
		return this;
	}
	
	public FreeSelectSqlBuilder<K> requireSingle() {
		requireSingle = true;
		return this;
	}
	
	public FreeSelectSqlBuilder<K> nullable() {
		nullable = true;
		return this;
	}
	
	public boolean isRequireFirst () {
		return requireFirst;
	}

	public boolean isRequireSingle() {
		return requireSingle;
	}

	public boolean isNullable() {
		return nullable;
	}
	
	public FreeSelectSqlBuilder<K> top(int count) {
		this.count = count;
		return this;
	}	

	public FreeSelectSqlBuilder<K> range(int start, int count) {
		this.start = start;
		this.count = count;
		return this;
	}

	public FreeSelectSqlBuilder<K> atPage(int pageNo, int pageSize) throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, please check");	

		range((pageNo - 1) * pageSize, pageSize);
		
		return this;
	}

	@Override
	public <T> FreeSelectSqlBuilder<K> mergerWith(ResultMerger<T> merger) {
		this.merger = merger;
		return this;
	}

	@Override
	public <T> FreeSelectSqlBuilder<K> extractorWith(DalResultSetExtractor<T> extractor) {
		this.extractor = extractor;
		return this;
	}

	public <T> ResultMerger<T> getResultMerger(DalHints hints){
		if(hints.is(DalHintEnum.resultMerger))
			return (ResultMerger<T>)hints.get(DalHintEnum.resultMerger);
		
		if(merger != null)
			return merger;
		
		if(isRequireSingle() || isRequireFirst())
			return isRequireSingle() ? new DalSingleResultMerger() : new DalFirstResultMerger((Comparator)hints.getSorter());

		return count > 0 ? new DalRangedResultMerger((Comparator)hints.getSorter(), count): new DalListMerger((Comparator)hints.getSorter());
	}

	public <T> DalResultSetExtractor<T> getResultExtractor(DalHints hints) {
		if(extractor != null)
			return extractor;

		if(isRequireSingle() || isRequireFirst())
			return new DalSingleResultExtractor<>(mapper, isRequireSingle());
			
		return count > 0 ? new DalRowMapperExtractor(mapper, count) : new DalRowMapperExtractor(mapper);
	}
}
