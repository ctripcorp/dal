package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;

public class AbstractSqlBuilder {
	
	private SqlWhereBuilder whereBuilder = new SqlWhereBuilder();
	
	protected DatabaseCategory dBCategory = DatabaseCategory.MySql;
	
	protected StatementParameters parameters = new StatementParameters();
	
	protected List<FieldEntry> fieldEntrys =  new ArrayList<FieldEntry>();
	
	protected int index = 1;
	
	public AbstractSqlBuilder(DatabaseCategory dBCategory) throws SQLException{
		if(dBCategory==null){
			throw new SQLException("DatabaseCategory can't be null.");
		}
		this.dBCategory = dBCategory;
	}
	
	/**
	 * 获取StatementParameters
	 * @return
	 */
	public StatementParameters buildParameters(){
		parameters = new StatementParameters();
		index = 1;
		for(FieldEntry entry : fieldEntrys){
			parameters.set(index++, entry.getSqlType(), entry.getParamValue());
		}
		List<FieldEntry> whereFieldEntrys = whereBuilder.getFieldEntry();
		for(FieldEntry entry : whereFieldEntrys){
			parameters.set(index++, entry.getSqlType(), entry.getParamValue());
		}
		return this.parameters;
	}
	
	/**
	 * 获取设置StatementParameters的index，返回值为构建后的sql中需要传值的个数加1
	 * @return
	 */
	public int getStatementParameterIndex(){
		return this.index;
	}
	
	/**
	 * 对表名进行处理，如果数据库是SqlServer，则在表名称后追加关键字 WITH (NOLOCK)，其余均不作处理
	 * @param tableName
	 * @return
	 */
	public String wrapTableName(String tableName){
		if(dBCategory == DatabaseCategory.MySql){
			return tableName;
		}else if(dBCategory == DatabaseCategory.SqlServer){
			return tableName+" WITH (NOLOCK)";
		}
		return tableName;
	}
	
	/**
	 * 对字段进行包裹，数据库是MySQL则用 `进行包裹，数据库是SqlServer则用[]进行包裹
	 * @param fieldName
	 * @return
	 */
	public String wrapField(String fieldName){
		if("*".equalsIgnoreCase(fieldName) || fieldName.contains("ROW_NUMBER")){
			return fieldName;
		}else if(dBCategory == DatabaseCategory.MySql){
			return "`" + fieldName + "`";
		}else if(dBCategory == DatabaseCategory.SqlServer){
			return "[" + fieldName + "]";
		}
		return fieldName;
	}
	
	/**
	 * build wehre expression
	 * @return
	 */
	public String getWhereExp(){
		return whereBuilder.getWhereExp();
	}
	
	/**
	 * 追加AND连接
	 * @return
	 */
	public AbstractSqlBuilder and(){
		whereBuilder.and();
		return this;
	}
	
	/**
	 * 追加OR连接
	 * @return
	 */
	public AbstractSqlBuilder or(){
		whereBuilder.or();
		return this;
	}
	
	/**
	 *  等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder equal(String field, Object paramValue, int sqlType) throws SQLException {
		whereBuilder.equal(field, paramValue, sqlType);
		return this;
	}
	
	/**
	 *  等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder equalNullable(String field, Object paramValue, int sqlType) {
		whereBuilder.equalNullable(field, paramValue, sqlType);
		return this;
	}

	/**
	 *  不等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder notEqual(String field, Object paramValue, int sqlType) throws SQLException {
		whereBuilder.notEqual(field, paramValue, sqlType);
		return this;
	}
	
	/**
	 *  不等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder notEqualNullable(String field, Object paramValue, int sqlType) {
		whereBuilder.notEqualNullable(field, paramValue, sqlType);
		return this;
	}

	/**
	 *  大于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder greaterThan(String field, Object paramValue, int sqlType) throws SQLException {
		 whereBuilder.greaterThan(field, paramValue, sqlType);
		return this;
	}
	
	/**
	 *  大于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder greaterThanNullable(String field, Object paramValue, int sqlType) {
		whereBuilder.greaterThanNullable(field, paramValue, sqlType);
		return this;
	}

	/**
	 *  大于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder greaterThanEquals(String field, Object paramValue, int sqlType) throws SQLException {
		whereBuilder.greaterThanEquals(field, paramValue, sqlType);
		return this;
	}
	
	/**
	 *  大于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder greaterThanEqualsNullable(String field, Object paramValue, int sqlType) {
		whereBuilder.greaterThanEqualsNullable(field, paramValue, sqlType);
		return this;
	}

	/**
	 *  小于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder lessThan(String field, Object paramValue, int sqlType) throws SQLException {
		whereBuilder.lessThan(field, paramValue, sqlType);
		return this;
	}
	
	/**
	 *  小于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder lessThanNullable(String field, Object paramValue, int sqlType) {
		whereBuilder.lessThanNullable(field, paramValue, sqlType);
		return this;
	}

	/**
	 *  小于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder lessThanEquals(String field, Object paramValue, int sqlType) throws SQLException {
		whereBuilder.lessThanEquals(field, paramValue, sqlType);
		return this;
	}
	
	/**
	 *  小于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder lessThanEqualsNullable(String field, Object paramValue, int sqlType) {
		whereBuilder.lessThanEqualsNullable(field, paramValue, sqlType);
		return this;
	}

	/**
	 *  Between操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue1 字段值1
	 * @param paramValue2 字段值2
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder between(String field, Object paramValue1, Object paramValue2, int sqlType) throws SQLException {
		whereBuilder.between(field, paramValue1, paramValue2, sqlType);
		return this;
	}
	
	/**
	 *  Between操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue1 字段值1
	 * @param paramValue2 字段值2
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder betweenNullable( String field, Object paramValue1, Object paramValue2, int sqlType) {
		whereBuilder.betweenNullable(field, paramValue1, paramValue2, sqlType);
		return this;
	}

	/**
	 *  Like操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder like(String field, Object paramValue, int sqlType) throws SQLException {
		whereBuilder.like(field, paramValue, sqlType);
		return this;
	}
	
	/**
	 *  Like操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder likeNullable(String field, Object paramValue, int sqlType) {
		whereBuilder.likeNullable(field, paramValue, sqlType);
		return this;
	}

	/**
	 *  In操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValues 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder in(String field, List<?> paramValues, int sqlType) throws SQLException {
		whereBuilder.in(field, paramValues, sqlType);
		return this;
	}
	
	/**
	 *  In操作，允许字段值为NULL.
	 *  若传入的字段值数量为0，则抛出异常。
	 * @param field 字段
	 * @param paramValues 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder inNullable(String field, List<?> paramValues, int sqlType) throws SQLException {
		whereBuilder.inNullable(field, paramValues, sqlType);
		return this;
	}
	
	/**
	 * Is null操作
	 * @param field 字段
	 * @return
	 */
	public AbstractSqlBuilder isNull(String field){
		whereBuilder.isNull(field);
		return this;
	}
	
	/**
	 * Is not null操作
	 * @param field 字段
	 * @return
	 */
	public AbstractSqlBuilder isNotNull(String field){
		whereBuilder.isNotNull(field);
		return this;
	}

}
