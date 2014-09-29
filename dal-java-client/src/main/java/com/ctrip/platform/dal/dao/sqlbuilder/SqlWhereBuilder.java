package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.dao.StatementParameters;

public class SqlWhereBuilder {
	
	private StringBuilder whereExp = new StringBuilder();
	
	private boolean and = false;
	
	private boolean or  = false;
	
	private List<FieldEntry> whereFieldEntrys = new ArrayList<FieldEntry>();
	
	public int buildWhereStatementParameters(StatementParameters parameters,
			int index){
		int paramIndex = index;
		for(FieldEntry entry : whereFieldEntrys){
			parameters.set(paramIndex++, entry.getSqlType(), entry.getParamValue());
		}
		return paramIndex;
	}
	
	public List<FieldEntry> getFieldEntry(){
		return whereFieldEntrys;
	}
	
	/**
	 * build where expression
	 * @return
	 */
	public String getWhereExp(){
		if(whereExp.toString().length()>0){
			return "WHERE"+ whereExp.toString();
		}else{
			return "";
		}
	}
	
	/**
	 * 追加AND连接
	 * @return
	 */
	public SqlWhereBuilder and(){
		and = true;
		return this;
	}
	
	/**
	 * 追加OR连接
	 * @return
	 */
	public SqlWhereBuilder or(){
		or = true;
		return this;
	}
	
	/**
	 *  等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder equal(String field, Object paramValue, int sqlType) throws SQLException {
		return addParam(field, "=", paramValue, sqlType);
	}
	
	/**
	 *  等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder equalNullable(String field, Object paramValue, int sqlType) {
		return addParamNullable(field, "=", paramValue, sqlType);
	}

	/**
	 *  不等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder notEqual(String field, Object paramValue, int sqlType) throws SQLException {
		return addParam(field, "!=", paramValue, sqlType);
	}
	
	/**
	 *  不等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder notEqualNullable(String field, Object paramValue, int sqlType) {
		return addParamNullable(field, "!=", paramValue, sqlType);
	}

	/**
	 *  大于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder greaterThan(String field, Object paramValue, int sqlType) throws SQLException {
		return addParam(field, ">", paramValue, sqlType);
	}
	
	/**
	 *  大于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder greaterThanNullable(String field, Object paramValue, int sqlType) {
		return addParamNullable(field, ">", paramValue, sqlType);
	}

	/**
	 *  大于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder greaterThanEquals(String field, Object paramValue, int sqlType) throws SQLException {
		return addParam(field, ">=", paramValue, sqlType);
	}
	
	/**
	 *  大于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder greaterThanEqualsNullable(String field, Object paramValue, int sqlType) {
		return addParamNullable(field, ">=", paramValue, sqlType);
	}

	/**
	 *  小于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder lessThan(String field, Object paramValue, int sqlType) throws SQLException {
		return addParam(field, "<", paramValue, sqlType);
	}
	
	/**
	 *  小于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder lessThanNullable(String field, Object paramValue, int sqlType) {
		return addParamNullable(field, "<", paramValue, sqlType);
	}

	/**
	 *  小于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder lessThanEquals(String field, Object paramValue, int sqlType) throws SQLException {
		return addParam(field, "<=", paramValue, sqlType);
	}
	
	/**
	 *  小于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder lessThanEqualsNullable(String field, Object paramValue, int sqlType) {
		return addParamNullable(field, "<=", paramValue, sqlType);
	}

	/**
	 *  Between操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue1 字段值1
	 * @param paramValue2 字段值2
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder between(String field, Object paramValue1, Object paramValue2, int sqlType) throws SQLException {
		if (paramValue1 == null || paramValue2 == null) {
			and = or = false;
			throw new SQLException(field + " is not support null value.");
		} else {
			appendConcate();
			whereExp.append(" ").append(field).append(" BETWEEN ? AND ?");
			FieldEntry entry1 = new FieldEntry(field, paramValue1, sqlType);
			whereFieldEntrys.add(entry1);
			FieldEntry entry2 = new FieldEntry(field, paramValue2, sqlType);
			whereFieldEntrys.add(entry2);
		}
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
	public SqlWhereBuilder betweenNullable( String field, Object paramValue1, Object paramValue2, int sqlType) {
		if(paramValue1 == null || paramValue2 == null){
			//如果paramValue==null，则field不会作为条件加入到最终的SQL中。
			and = or = false;
		}else{
			appendConcate();
			whereExp.append(" ").append(field).append(" BETWEEN ? AND ?");
			FieldEntry entry1 = new FieldEntry(field, paramValue1, sqlType);
			whereFieldEntrys.add(entry1);
			FieldEntry entry2 = new FieldEntry(field, paramValue2, sqlType);
			whereFieldEntrys.add(entry2);
		}
		return this;
	}

	/**
	 *  Like操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder like(String field, Object paramValue, int sqlType) throws SQLException {
		return addParam(field, "LIKE", paramValue, sqlType);
	}
	
	/**
	 *  Like操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder likeNullable(String field, Object paramValue, int sqlType) {
		return addParamNullable(field, "LIKE", paramValue, sqlType);
	}

	/**
	 *  In操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValues 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder in(String field, List<?> paramValues, int sqlType) throws SQLException {
		if(null == paramValues){
			and = or = false;
			throw new SQLException(field + " must have more than one value.");
		}
		if(paramValues.size() == 0){
			and = or = false;
			throw new SQLException(field + " must have more than one value.");
		}
		for(Object obj:paramValues){
			if(obj==null){
				and = or = false;
				throw new SQLException(field + " is not support null value.");
			}
		}
		return addInParam(field, paramValues, sqlType);
	}
	
	/**
	 *  In操作，允许字段值为NULL.
	 *  若传入的字段值数量为0，则抛出异常。
	 * @param field 字段
	 * @param paramValues 字段值
	 * @return
	 * @throws SQLException
	 */
	public SqlWhereBuilder inNullable(String field, List<?> paramValues, int sqlType) throws SQLException {
		if(null == paramValues){
			and = or = false;
			return this;
		}
		if(paramValues.size() == 0){
			and = or = false;
			throw new SQLException(field + " must have more than one value.");
		}
		return addInParam(field, paramValues, sqlType);
	}
	
	/**
	 * Is null操作
	 * @param field 字段
	 * @return
	 */
	public SqlWhereBuilder isNull(String field){
		appendConcate();
		whereExp.append(" ").append(field).append(" IS NULL");
		return this;
	}
	
	/**
	 * Is not null操作
	 * @param field 字段
	 * @return
	 */
	public SqlWhereBuilder isNotNull(String field){
		appendConcate();
		whereExp.append(" ").append(field).append(" IS NOT NULL");
		return this;
	}
	
	private SqlWhereBuilder addInParam(String field, List<?> paramValues, int sqlType){
		StringBuilder temp = new StringBuilder();
		temp.append(" in ( ");
		for(int i=0,size=paramValues.size();i<size;i++){
			temp.append("?");
			if(i!=size-1){
				temp.append(", ");
			}
			FieldEntry entry = new FieldEntry(field, paramValues.get(i), sqlType);
			whereFieldEntrys.add(entry);
		}
		temp.append(" )");
		appendConcate();
		whereExp.append(" ").append(field).append(temp);
		return this;
	}
	
	private SqlWhereBuilder addParam(String field, String condition, Object paramValue, int sqlType) throws SQLException{
		if(paramValue != null){
			appendConcate();
			whereExp.append(" ").append(field).append(" ").append(condition).append(" ?");
			FieldEntry entry = new FieldEntry(field, paramValue, sqlType);
			whereFieldEntrys.add(entry);
		}else{
			and = or = false;
			throw new SQLException(field + " is not support null value.");
		}
		return this;
	}
	
	private SqlWhereBuilder addParamNullable(String field, String condition, Object paramValue, int sqlType){
		if(paramValue != null){
			appendConcate();
			whereExp.append(" ").append(field).append(" ").append(condition).append(" ?");
			FieldEntry entry = new FieldEntry(field, paramValue, sqlType);
			whereFieldEntrys.add(entry);
		}else{
			//如果paramValue==null，则field不会作为条件加入到最终的SQL中。
			and = or = false;
		}
		return this;
	}
	
	private void appendConcate(){
		if(and){
			whereExp.append(" AND ");
		}
		if(or){
			whereExp.append(" OR ");
		}
		and = or = false;
	}
	
}
