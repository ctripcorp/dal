package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;

public class SelectSqlBuilder extends AbstractSqlBuilder implements QueryBuilder {
	private List<String> selectField =  new ArrayList<String>();
	
	private BaseQueryBuilder queryBuilder;
	
	private boolean isPagination = false;
	private static final String MYSQL_PAGE_SUFFIX_TPL= " limit ?, ?";
	private static final String SQLSVR_PAGE_SUFFIX_TPL= " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
	
	/**
	 * Important Note: In this case, the generated code with set page info into statement parameters.
	 * You are recommended to re-generate code using the code generator. The new code will use the other two constructor instead
	 * 
	 * @param tableName 表名
	 * @param dBCategory 数据库类型
	 * @param isPagination 是否分页. If it is true, it means the code is running with old generated code
	 * @throws SQLException
	 * @Deprecated
	 */
	public SelectSqlBuilder(String tableName,
			DatabaseCategory dbCategory, boolean isPagination)
			throws SQLException {
		this(tableName, dbCategory);
		this.isPagination = isPagination;
	}
	
	/**
	 * Construct build without pagenation
	 * @param tableName 表名
	 * @param dBCategory 数据库类型
	 * @throws SQLException
	 */
	public SelectSqlBuilder(String tableName,
			DatabaseCategory dbCategory)
			throws SQLException {
		super(tableName, dbCategory);
		queryBuilder = new BaseQueryBuilder(tableName, dbCategory);
		queryBuilder.nullable();
	}
	
	/**
	 * Construct with pagenation
	 * @param tableName
	 * @param dBCategory
	 * @param pageNo
	 * @param pageSize
	 * @throws SQLException
	 */
	public SelectSqlBuilder(String tableName,
			DatabaseCategory dbCategory, int pageNo, int pageSize)
			throws SQLException {
		this(tableName, dbCategory);
		
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, please check");	

		queryBuilder.range((pageNo - 1) * pageSize, pageSize);
	}
	
	/**
	 * 添加select字段
	 * @param fieldName
	 * @return
	 */
	public SelectSqlBuilder select(String ...fieldName){
		for(String field:fieldName){
			selectField.add(field);
		}
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
	public <T> ResultMerger<T> getResultMerger(DalHints hints) {
		return queryBuilder.getResultMerger(hints);
	}

	@Override
	public <T> QueryBuilder mapWith(DalRowMapper<T> mapper) {
		queryBuilder.mapWith(mapper);
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
		queryBuilder.where(getWhereExp()).select(buildSelectField());
	}
	
	private String buildSelectField(){
		StringBuilder selectFields = new StringBuilder();
		for(int i=0,count=selectField.size();i<count;i++){
			selectFields.append(this.wrapField(selectField.get(i)));
			if(i<count-1){
				selectFields.append(", ");
			}
		}
		return selectFields.toString();
	}
}