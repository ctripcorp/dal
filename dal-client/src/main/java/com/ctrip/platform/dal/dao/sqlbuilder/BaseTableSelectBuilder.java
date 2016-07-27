package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Objects;

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

/**
 * This builder is only for internal use of DalTableDao
 * @author jhhe
 *
 */
public class BaseTableSelectBuilder implements TableSelectBuilder {
	private static final String ALL_COLUMNS = "*";
	private static final String COUNT = "COUNT(1)";
	private static final String SPACE = " ";
	private static final String ORDER_BY = "ORDER BY ";
	private static final String ASC = " ASC";
	private static final String DESC = " DESC";
	private static final String QUERY_ALL_CRITERIA = "1=1";
	
	private static final String MYSQL_QUERY_TPL= "SELECT %s FROM %s WHERE %s";
	private static final String SQLSVR_QUERY_TPL= "SELECT %s FROM %s WITH (NOLOCK) WHERE %s";
	
	/**
	 * 对于select first，会在语句中追加limit 0,1(MySQL)或者top 1(SQL Server)：
	 * @return
	 */
	private static final String MYSQL_QUERY_TOP_TPL= "SELECT %s FROM %s WHERE %s LIMIT %d";
	private static final String SQLSVR_QUERY_TOP_TPL= "SELECT TOP %d %s FROM %s WITH (NOLOCK) WHERE %s";
	
	private static final String MYSQL_QUERY_PAGE_TPL= "SELECT %s FROM %s WHERE %s LIMIT %d, %d";
	private static final String SQLSVR_QUERY_PAGE_TPL= "SELECT %s FROM %s WITH (NOLOCK) WHERE %s OFFSET %d ROWS FETCH NEXT %d ROWS ONLY";
	
	private String tableName;
	private DatabaseCategory dbCategory;
	
	private String[] selectedColumns;
	private String customized;
	
	private String whereClause;
	private String orderBy;
	private boolean ascending;

	private StatementParameters parameters;
	private DalRowMapper mapper;
	private ResultMerger merger;
	private DalResultSetExtractor extractor;
	
	private boolean requireFirst = false;
	private boolean requireSingle = false;
	private boolean nullable = false;

	private int count;
	private int start;

	public BaseTableSelectBuilder() {
		selectAll();
	}
	
	public BaseTableSelectBuilder(String tableName, DatabaseCategory dbCategory) throws SQLException {
		this();
		from(tableName).setDatabaseCategory(dbCategory);
	}
	
	public BaseTableSelectBuilder from(String tableName) throws SQLException {
		if(tableName ==null || tableName.isEmpty())
			throw new SQLException("table name is illegal.");
		
		this.tableName = tableName;
		return this;
	}
	
	public BaseTableSelectBuilder setDatabaseCategory(DatabaseCategory dbCategory) throws SQLException {
		Objects.requireNonNull(dbCategory, "DatabaseCategory can't be null.");
		this.dbCategory = dbCategory;
		return this;
	}

	public BaseTableSelectBuilder select(String... selectedColumns) {
		this.selectedColumns = selectedColumns;
		customized = null;
		return this;
	}
	
	public BaseTableSelectBuilder selectAll() {
		this.customized = ALL_COLUMNS;
		selectedColumns = null;
		return this;
	}
	
	public BaseTableSelectBuilder selectCount() {
		this.customized = COUNT;
		selectedColumns = null;
		mergerWith(new LongNumberSummary());
		requireSingle();
		return this.simpleType();
	}
	
	public BaseTableSelectBuilder where(String whereClause) {
		whereClause = whereClause.trim();
		this.whereClause = whereClause.length() == 0 ? QUERY_ALL_CRITERIA : whereClause;
		return this;
	}

	public BaseTableSelectBuilder orderBy(String orderBy, boolean ascending) {
		this.orderBy = orderBy;
		this.ascending = ascending;
		return this;
	}
	
	public BaseTableSelectBuilder with(StatementParameters parameters) {
		this.parameters = parameters;
		return this;
	}
	
	public <T> BaseTableSelectBuilder mapWith(DalRowMapper<T> mapper) {
		this.mapper = mapper;
		return this;
	}
	
	public BaseTableSelectBuilder simpleType() {
		return mapWith(new DalObjectRowMapper());
	}

	public String build() {
		return internalBuild(tableName);
	}

	@Override
	public String build(String shardStr) {
		return internalBuild(tableName + shardStr);
	}

	private String internalBuild(String effectiveTableName) {
		effectiveTableName = wrapField(effectiveTableName);
		
		if(requireFirst)
			return buildFirst(effectiveTableName);
		
		if(start == 0 && count > 0)
			return buildTop(effectiveTableName);
		
		if(start > 0 && count > 0)
			return buildPage(effectiveTableName);
		
		return buildList(effectiveTableName);
	}

	private String getCompleteWhereExp() {
		return orderBy == null ? whereClause : whereClause + SPACE + buildOrderbyExp();
	}
	
	private String buildOrderbyExp(){
		StringBuilder orderbyExp = new StringBuilder();

		orderbyExp.append(ORDER_BY);
		String wrap = wrapField(orderBy);
		wrap += ascending? ASC: DESC;
		orderbyExp.append(wrap);

		return orderbyExp.toString();
	}

	/**
	 * 对字段进行包裹，数据库是MySQL则用 `进行包裹，数据库是SqlServer则用[]进行包裹
	 * @param fieldName
	 * @return
	 */
	public String wrapField(String fieldName){
		return AbstractSqlBuilder.wrapField(dbCategory, fieldName);
	}
	
	@Override
	public <T> BaseTableSelectBuilder mergerWith(ResultMerger<T> merger) {
		this.merger = merger;
		return this;
	}

	@Override
	public <T> BaseTableSelectBuilder extractorWith(DalResultSetExtractor<T> extractor) {
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
	
	private String buildFirst(String effectiveTableName){
		count = 1;
		return buildTop(effectiveTableName);
	}
	
	private String buildTop(String effectiveTableName){
		if(DatabaseCategory.SqlServer == dbCategory)
			return String.format(SQLSVR_QUERY_TOP_TPL, count, buildColumns(), effectiveTableName, getCompleteWhereExp());
		else
			return String.format(MYSQL_QUERY_TOP_TPL, buildColumns(), effectiveTableName, getCompleteWhereExp(), count);
	}

	private String buildPage(String effectiveTableName){
		String tpl = DatabaseCategory.SqlServer == dbCategory ? SQLSVR_QUERY_PAGE_TPL : MYSQL_QUERY_PAGE_TPL;
		return String.format(tpl, buildColumns(), effectiveTableName, getCompleteWhereExp(), start, count);
	}
	
	private String buildList(String effectiveTableName){
		String tpl = DatabaseCategory.SqlServer == dbCategory ? SQLSVR_QUERY_TPL : MYSQL_QUERY_TPL;
		return String.format(tpl, buildColumns(), effectiveTableName, getCompleteWhereExp());
	}
	
	private String buildColumns() {
		if(customized != null)
			return customized;
		
		if(selectedColumns != null) {
			StringBuilder fieldBuf = new StringBuilder();
			for(int i=0, count= selectedColumns.length; i < count; i++){
				fieldBuf.append(this.wrapField(selectedColumns[i]));
				if(i<count-1){
					fieldBuf.append(", ");
				}
			}
			
			return fieldBuf.toString();
		}
		
		// This will be an exceptional case
		return SPACE;
	}
	
	@Override
	public StatementParameters buildParameters() {
		return parameters;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	public BaseTableSelectBuilder requireFirst() {
		requireFirst = true;
		return this;
	}
	
	public BaseTableSelectBuilder requireSingle() {
		requireSingle = true;
		return this;
	}
	
	public BaseTableSelectBuilder nullable() {
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
	
	public BaseTableSelectBuilder top(int count) {
		this.count = count;
		return this;
	}	

	public BaseTableSelectBuilder range(int start, int count) {
		this.start = start;
		this.count = count;
		return this;
	}

	public DatabaseCategory getDbCategory() {
		return dbCategory;
	}

	public BaseTableSelectBuilder atPage(int pageNo, int pageSize) throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, please check");	

		range((pageNo - 1) * pageSize, pageSize);
		
		return this;
	}

	private static class LongNumberSummary implements ResultMerger<Number>{
		private long total;
		@Override
		public void addPartial(String shard, Number partial) {
			total += partial.longValue();
		}

		@Override
		public Number merge() {
			return total;
		}
	}
}
