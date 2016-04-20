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
import com.ctrip.platform.dal.dao.helper.DalRangedResultMerger;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultMerger;

public class BaseQueryBuilder implements QueryBuilder {
	private static final String ALL_COLUMNS = "*";
	private static final String SPACE = " ";
	private static final String ORDER_BY = "ORDER BY ";
	private static final String ASC = " ASC";
	private static final String DESC = " DESC";
	
	private static final String MYSQL_QUERY_TPL= "SELECT %s FROM %s WHERE %s";
	private static final String SQLSVR_QUERY_TPL= "SELECT %s FROM %s WITH (NOLOCK) WHERE %s";
	
	/**
	 * 对于select first，会在语句中追加limit 0,1(MySQL)或者top 1(SQL Server)：
	 * @return
	 */
	private static final String MYSQL_QUERY_TOP_TPL= "SELECT %s FROM %s WHERE %s limit %d";
	private static final String SQLSVR_QUERY_TOP_TPL= "SELECT TOP %d %s FROM %s WITH (NOLOCK) WHERE %s";
	
	private static final String MYSQL_QUERY_PAGE_TPL= "SELECT %s FROM %s WHERE %s limit %d,%d";
	private static final String SQLSVR_QUERY_PAGE_TPL= "SELECT %s FROM %s WITH (NOLOCK) WHERE %s OFFSET %d ROWS FETCH NEXT %d ROWS ONLY";
	
	private String tableName;
	private DatabaseCategory dbCategory;
	private String columns;
	private String whereClause;
	private String orderBy;
	private boolean ascending;

	private StatementParameters parameters;
	private DalRowMapper mapper;

	private boolean requireFirst = false;
	private boolean requireSingle = false;
	private boolean nullable = false;

	private int count;
	private int start;

	public BaseQueryBuilder(String tableName, DatabaseCategory dbCategory) throws SQLException {
		if(tableName ==null || tableName.isEmpty())
			throw new SQLException("table name is illegal.");
		
		this.tableName = tableName;
		this.dbCategory = dbCategory;
		selectAll();
	}
	
	public BaseQueryBuilder select(String columns) {
		this.columns = columns;
		return this;
	}
	
	public BaseQueryBuilder selectAll() {
		this.columns = ALL_COLUMNS;
		return this;
	}
	
	public BaseQueryBuilder where(String whereClause) {
		this.whereClause = whereClause;
		return this;
	}

	public BaseQueryBuilder orderBy(String orderBy, boolean ascending) {
		this.orderBy = orderBy;
		this.ascending = ascending;
		return this;
	}
	
	public BaseQueryBuilder with(StatementParameters parameters) {
		this.parameters = parameters;
		return this;
	}
	
	public <T> BaseQueryBuilder mapWith(DalRowMapper<T> mapper) {
		this.mapper = mapper;
		return this;
	}

	public String build() {
		return build(tableName);
	}

	@Override
	public String buildWith(String shardStr) {
		return build(tableName + shardStr);
	}

	private String build(String effectiveTableName) {
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
		String wrap = this.wrapField(orderBy);
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
		return wrapField(dbCategory, fieldName);
	}
	
	/**
	 * 对字段进行包裹，数据库是MySQL则用 `进行包裹，数据库是SqlServer则用[]进行包裹
	 * @param fieldName
	 * @return
	 */
	public static String wrapField(DatabaseCategory dbCategory, String fieldName){
		if("*".equalsIgnoreCase(fieldName) || fieldName.contains("ROW_NUMBER")){
			return fieldName;
		}else if(dbCategory == DatabaseCategory.MySql){
			return "`" + fieldName + "`";
		}else if(dbCategory == DatabaseCategory.SqlServer){
			return "[" + fieldName + "]";
		}
		return fieldName;
	}
	
	public <T> ResultMerger<T> getResultMerger(DalHints hints){
		if(hints.is(DalHintEnum.resultMerger))
			return (ResultMerger<T>)hints.get(DalHintEnum.resultMerger);
		
		if(isRequireSingle() || isRequireFirst())
			return isRequireSingle() ? new DalSingleResultMerger() : new DalFirstResultMerger((Comparator)hints.getSorter());

		return count > 0 ? new DalRangedResultMerger((Comparator)hints.getSorter(), count): new DalListMerger((Comparator)hints.getSorter());
	}

	public <T> DalResultSetExtractor<T> getResultExtractor(DalHints hints) {
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
			return String.format(SQLSVR_QUERY_TOP_TPL, count, effectiveTableName, columns, getCompleteWhereExp());
		else
			return String.format(MYSQL_QUERY_TOP_TPL, effectiveTableName, columns, getCompleteWhereExp(), count);
	}

	private String buildPage(String effectiveTableName){
		String tpl = DatabaseCategory.SqlServer == dbCategory ? SQLSVR_QUERY_PAGE_TPL : MYSQL_QUERY_PAGE_TPL;
		return String.format(tpl, effectiveTableName, columns, getCompleteWhereExp(), start, count);
	}
	
	private String buildList(String effectiveTableName){
		String tpl = DatabaseCategory.SqlServer == dbCategory ? SQLSVR_QUERY_TPL : MYSQL_QUERY_TPL;
		return String.format(tpl, columns, effectiveTableName, getCompleteWhereExp());
	}
	
	@Override
	public StatementParameters buildParameters() {
		return parameters;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	public QueryBuilder requireFirst() {
		requireFirst = true;
		return this;
	}
	
	public QueryBuilder requireSingle() {
		requireSingle = true;
		return this;
	}
	
	public QueryBuilder nullable() {
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
	
	public QueryBuilder top(int count) {
		this.count = count;
		return this;
	}	

	public QueryBuilder range(int start, int count) {
		this.start = start;
		this.count = count;
		return this;
	}

	public DatabaseCategory getDbCategory() {
		return dbCategory;
	}
}
