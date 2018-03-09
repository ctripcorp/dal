package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.Comparator;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.helper.DalFirstResultMerger;
import com.ctrip.platform.dal.dao.helper.DalListMerger;
import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRangedResultMerger;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultMerger;

/**
 * A very flexible SQL builder that can build complete SQL alone with parameters for query purpose.
 * It allows user add nullable or conditional expressions. The null expression 
 * may be auto removed during build process. And it will also remove needed operator and bracket
 * to make the final sql correct. 
 * 
 * There are certain rules that you need to know about: 
 * if bracket has no content, bracket will be removed
 * expression can be evaluated and can be wrapped by bracket and connect to each other by and/or
 * expression should have no leading and tailing and/or, it there is, the and/or will be removed during validating
 * 
 * @author jhhe
 */
public class FreeSelectSqlBuilder<K> extends AbstractFreeSqlBuilder implements SelectBuilder {
	@SuppressWarnings("rawtypes")
    private DalRowMapper mapper;
	
	@SuppressWarnings("rawtypes")
    private ResultMerger merger;
	
	@SuppressWarnings("rawtypes")
    private DalResultSetExtractor extractor;

	private boolean requireFirst = false;
	private boolean requireSingle = false;
	private boolean nullable = false;

	private int count;
	private int start;

	
	/**
	 * @deprecated you should use FreeSelectSqlBuilder() instead
	 * @param dbCategory
	 */
	public FreeSelectSqlBuilder(DatabaseCategory dbCategory) {
		setDbCategory(dbCategory);
	}
	
	public FreeSelectSqlBuilder(){}
    
	/**
	 * If there is IN parameter, no matter how many values in the IN clause, the IN clause only need to 
	 * contain one "?".
	 * E.g. SELECT ... WHERE id IN (?)
	 * @param updateSqlTemplate
	 */
	public FreeSelectSqlBuilder<K> setTemplate(String selectSqlTemplate) {
		append(selectSqlTemplate);
		return this;
	}
	
	public String build(){
	    String query = super.build();

	    return count > 0 ? getDbCategory().buildPage(query, start, count) : query;
	}

	public <T> FreeSelectSqlBuilder<K> mapWith(DalRowMapper<T> mapper) {
		this.mapper = mapper;
		return this;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
    public <T> FreeSelectSqlBuilder<K> mapWith(Class<T> type) {
		return mapWith(new DalObjectRowMapper(type));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
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
	
	public FreeSelectSqlBuilder<K> setNullable(boolean nullable) {
		this.nullable = nullable;
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
	
	/**
	 * Indicate the final query will transfered to top count query based on the original query.
	 * 
	 * Note: This is applied to the entire sql statement.
	 *  
	 * @param count How many recorders to be fetched
	 * @return builder itself
	 */
	public FreeSelectSqlBuilder<K> top(int count) {
		this.count = count;
		return this;
	}	

	/**
	 * Indicate the final query will transfered to pagination query based on the original query.
	 * 
     * Note: This is applied to the entire sql statement.
     * 
     * @param start from where the recorder will be fetched
     * @param count How many recorders to be fetched
     * @return builder itself
	 */
	public FreeSelectSqlBuilder<K> range(int start, int count) {
		this.start = start;
		this.count = count;
		return this;
	}

	/**
	 * Indicate the final query will transfered to pagination query based on the original query.
	 * 
	 * Note: This is applied to the entire sql statement.
	 * 
	 * @param pageNo from which page will be recorder be fetched
	 * @param pageSize how many recorders to be fetched in a page
	 * @return builder itself
	 * @throws SQLException
	 */
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

	@SuppressWarnings({"unchecked", "rawtypes"})
    public <T> ResultMerger<T> getResultMerger(DalHints hints){
		if(hints.is(DalHintEnum.resultMerger))
			return (ResultMerger<T>)hints.get(DalHintEnum.resultMerger);
		
		if(merger != null)
			return merger;
		
		if(isRequireSingle() || isRequireFirst())
			return isRequireSingle() ? new DalSingleResultMerger() : new DalFirstResultMerger((Comparator)hints.getSorter());

		return count > 0 ? new DalRangedResultMerger((Comparator)hints.getSorter(), count): new DalListMerger((Comparator)hints.getSorter());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
    public <T> DalResultSetExtractor<T> getResultExtractor(DalHints hints) throws SQLException { 
		if(extractor != null)
			return extractor;

		if(isRequireSingle() || isRequireFirst())
			return new DalSingleResultExtractor<>(mapper, isRequireSingle());
			
		return count > 0 ? new DalRowMapperExtractor(mapper, count) : new DalRowMapperExtractor(mapper);
	}
}
