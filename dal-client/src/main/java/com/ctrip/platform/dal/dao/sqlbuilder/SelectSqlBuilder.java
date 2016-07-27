package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;

public class SelectSqlBuilder extends AbstractSqlBuilder implements TableSelectBuilder {
	private BaseTableSelectBuilder queryBuilder;
	
	private boolean isPagination = false;
	private static final String MYSQL_PAGE_SUFFIX_TPL= " limit ?, ?";
	private static final String SQLSVR_PAGE_SUFFIX_TPL= " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
	
	/**
	 * Important Note: In this case, the generated code with set page info into statement parameters.
	 * You are recommended to re-generate code using the code generator. The new code will use the other two constructor instead
	 * 
	 * @deprecated If you see this, please regenerate dal code with code gen
	 * @param tableName 表名
	 * @param dbCategory 数据库类型
	 * @param isPagination 是否分页. If it is true, it means the code is running with old generated code
	 * @throws SQLException
	 */
	public SelectSqlBuilder(String tableName,
			DatabaseCategory dbCategory, boolean isPagination)
			throws SQLException {
		this();
		from(tableName).setDatabaseCategory(dbCategory);
		this.isPagination = isPagination;
		setCompatible(true);
	}
	
	public SelectSqlBuilder() {
		queryBuilder = new BaseTableSelectBuilder();
		queryBuilder.nullable();
	}

	public SelectSqlBuilder from(String tableName) throws SQLException {
		super.from(tableName);
		queryBuilder.from(tableName);
		return this;
	}
	
	public SelectSqlBuilder setDatabaseCategory(DatabaseCategory dbCategory) throws SQLException {
		super.setDatabaseCategory(dbCategory);
		queryBuilder.setDatabaseCategory(dbCategory);
		return this;
	}

	/**
	 * 添加select字段
	 * @param fieldName
	 * @return
	 */
	public SelectSqlBuilder select(String ...fieldName){
		queryBuilder.select(fieldName);
		return this;
	}
	
	public SelectSqlBuilder selectAll() {
		queryBuilder.selectAll();
		return this;
	}
	
	public SelectSqlBuilder selectCount() {
		queryBuilder.selectCount();
		return this;
	}
	
	/**
	 * 追加order by字段
	 * @param fieldName 字段名
	 * @param ascending 是否升序
	 * @return
	 */
	public SelectSqlBuilder orderBy(String fieldName, boolean ascending){
		queryBuilder.orderBy(fieldName, ascending);
		return this;
	}
	
	/**
	 * Construct with pagenation
	 * @param pageNo
	 * @param pageSize
	 * @throws SQLException
	 */
	public SelectSqlBuilder atPage(int pageNo, int pageSize)
			throws SQLException {
		queryBuilder.atPage(pageNo, pageSize);
		return this;
	}

	@Override
	public SelectSqlBuilder range(int start, int count) {
		queryBuilder.range(start, count);
		return this;
	}
	
	public SelectSqlBuilder requireFirst() {
		queryBuilder.requireFirst();
		return this;
	}
	
	public SelectSqlBuilder requireSingle() {
		queryBuilder.requireSingle();
		return this;
	}
	
	public SelectSqlBuilder nullable() {
		queryBuilder.nullable();
		return this;
	}
	
	public boolean isRequireFirst () {
		return queryBuilder.isRequireFirst();
	}

	public boolean isRequireSingle() {
		return queryBuilder.isRequireSingle();
	}

	public boolean isNullable() {
		return queryBuilder.isNullable();
	}

	@Override
	public <T> SelectSqlBuilder mergerWith(ResultMerger<T> merger) {
		queryBuilder.mergerWith(merger);
		return this;
	}

	@Override
	public <T> SelectSqlBuilder extractorWith(DalResultSetExtractor<T> extractor) {
		queryBuilder.extractorWith(extractor);
		return this;
	}

	@Override
	public <T> ResultMerger<T> getResultMerger(DalHints hints) {
		return queryBuilder.getResultMerger(hints);
	}

	@Override
	public <T> SelectSqlBuilder mapWith(DalRowMapper<T> mapper) {
		queryBuilder.mapWith(mapper);
		return this;
	}
	
	public SelectSqlBuilder simpleType() {
		queryBuilder.simpleType();
		return this;
	}
	
	@Override
	public <T> DalResultSetExtractor<T> getResultExtractor(DalHints hints) {
		return queryBuilder.getResultExtractor(hints);
	}
	
	/**
	 * This method has to be backward compatible. The old generator will generated like
	 *      String sql = builder.build();
	 *      
	 *      For page:
	 *      StatementParameters parameters = builder.buildParameters();
	 *      int index =  builder.getStatementParameterIndex();
	 *      parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize + 1);
	 *      parameters.set(index++, Types.INTEGER, pageSize * pageNo);
	 *      return queryDao.query(sql, parameters, hints, parser);
	 *      
	 *      Or for first result
	 *      return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, parser);
	 *      
	 *      Or for single result
	 *      return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, parser);
	 * @return
	 */
	public String build(){
		preBuild();

		String sql = queryBuilder.build();
		String suffix = DatabaseCategory.SqlServer == queryBuilder.getDbCategory() ? SQLSVR_PAGE_SUFFIX_TPL : MYSQL_PAGE_SUFFIX_TPL;

		// If it is the old code gen case, we need to append page suffix
		return isPagination ? sql + suffix : sql;
	}
	
	/**
	 * For backward compatible
	 * 对于select first，会在语句中追加limit 0,1(MySQL)或者top 1(SQL Server)：
	 * @return
	 */
	public String buildFirst(){
		queryBuilder.requireFirst();
		return build();
	}
	
	/**
	 * Only the newly generated code will use this method
	 */
	public String build(String shardStr) {
		preBuild();
		return queryBuilder.build(shardStr);
	}
	
	private void preBuild() {
		queryBuilder.where(getWhereExp());
	}
}