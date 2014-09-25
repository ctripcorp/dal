package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.StatementParameters;

public class SqlWhereBuilder {
	
	private StringBuilder whereExp = new StringBuilder();
	
	private boolean and = false;
	private boolean or  = false;
	
	
	/**
	 * build wehre expression
	 * @return
	 */
	protected String getWhereExp(){
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
	public int equal(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		return addParam(field, "=", paramValue, parameters, index, sqlType);
	}
	
	/**
	 *  等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int equalNullable(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) {
		return addParamNullable(field, "=", paramValue, parameters, index, sqlType);
	}

	/**
	 *  不等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int notEqual(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		return addParam(field, "!=", paramValue, parameters, index, sqlType);
	}
	
	/**
	 *  不等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int notEqualNullable(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) {
		return addParamNullable(field, "!=", paramValue, parameters, index, sqlType);
	}

	/**
	 *  大于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int greaterThan(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		return addParam(field, ">", paramValue, parameters, index, sqlType);
	}
	
	/**
	 *  大于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int greaterThanNullable(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) {
		return addParamNullable(field, ">", paramValue, parameters, index, sqlType);
	}

	/**
	 *  大于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int greaterThanEquals(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		return addParam(field, ">=", paramValue, parameters, index, sqlType);
	}
	
	/**
	 *  大于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int greaterThanEqualsNullable(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) {
		return addParamNullable(field, ">=", paramValue, parameters, index, sqlType);
	}

	/**
	 *  小于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int lessThan(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		return addParam(field, "<", paramValue, parameters, index, sqlType);
	}
	
	/**
	 *  小于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int lessThanNullable(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) {
		return addParamNullable(field, "<", paramValue, parameters, index, sqlType);
	}

	/**
	 *  小于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int lessThanEquals(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		return addParam(field, "<=", paramValue, parameters, index, sqlType);
	}
	
	/**
	 *  小于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int lessThanEqualsNullable(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) {
		return addParamNullable(field, "<=", paramValue, parameters, index, sqlType);
	}

	/**
	 *  Between操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue1 字段值1
	 * @param paramValue2 字段值2
	 * @return
	 * @throws SQLException
	 */
	public int between(String field, String paramValue1, String paramValue2,
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		if (paramValue1 == null || paramValue2 == null) {
			and = or = false;
			throw new SQLException(field + " is not support null value.");
		} else {
			appendConcate();
			whereExp.append(" ").append(field).append(" BETWEEN ? AND ?");
			parameters.set(index++, sqlType, paramValue1);
			parameters.set(index++, sqlType, paramValue2);
		}
		return index;
	}
	
	/**
	 *  Between操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue1 字段值1
	 * @param paramValue2 字段值2
	 * @return
	 * @throws SQLException
	 */
	public int betweenNullable( String field, String paramValue1, String paramValue2,
			StatementParameters parameters, int index, int sqlType) {
		if(paramValue1 == null || paramValue2 == null){
			//如果paramValue==null，则field不会作为条件加入到最终的SQL中。
			and = or = false;
		}else{
			appendConcate();
			whereExp.append(" ").append(field).append(" BETWEEN ? AND ?");
			parameters.set(index++, sqlType, paramValue1);
			parameters.set(index++, sqlType, paramValue2);
		}
		return index;
	}

	/**
	 *  Like操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int like(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		return addParam(field, "LIKE", paramValue, parameters, index, sqlType);
	}
	
	/**
	 *  Like操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public int likeNullable(String field, String paramValue, 
			StatementParameters parameters, int index, int sqlType) {
		return addParamNullable(field, "LIKE", paramValue, parameters, index, sqlType);
	}

	/**
	 *  In操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValues 字段值
	 * @return
	 * @throws SQLException
	 */
	public int in(String field, List<?> paramValues, 
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		if(null == paramValues){
			and = or = false;
			throw new SQLException(field + " is not support null value.");
		}
		if(paramValues.size() == 0){
			and = or = false;
			throw new SQLException(field + " must have more than one value.");
		}
		return addInParam(field, paramValues, parameters, index, sqlType);
	}
	
	/**
	 *  In操作，若字段值为NULL，则此条件不会加入SQL中.
	 *  若传入的字段值数量为0，则抛出异常。
	 * @param field 字段
	 * @param paramValues 字段值
	 * @return
	 * @throws SQLException
	 */
	public int inNullable(String field, List<Object> paramValues, 
			StatementParameters parameters, int index, int sqlType) throws SQLException {
		if(null == paramValues){
			and = or = false;
			return index;
		}
		if(paramValues.size() == 0){
			and = or = false;
			throw new SQLException(field + " must have more than one value.");
		}
		return addInParam(field, paramValues, parameters, index, sqlType);
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
	
	private int addInParam(String field, List<?> paramValues,
			StatementParameters parameters, int index, int sqlType){
		StringBuilder temp = new StringBuilder();
		temp.append(" in ( ");
		for(int i=0,size=paramValues.size();i<size;i++){
			temp.append("?");
			if(i!=size-1){
				temp.append(", ");
			}
		}
		temp.append(" )");
		appendConcate();
		whereExp.append(" ").append(field).append(temp);
		return parameters.setInParameter(index, sqlType, paramValues);
	}
	
	private int addParam(String field, String condition, String paramValue, 
			StatementParameters parameters, int index, int sqlType) throws SQLException{
		if(paramValue != null){
			appendConcate();
			whereExp.append(" ").append(field).append(" ").append(condition).append(" ?");
			parameters.set(index++, sqlType, paramValue);
		}else{
			and = or = false;
			throw new SQLException(field + " is not support null value.");
		}
		return index;
	}
	
	private int addParamNullable(String field, String condition, String paramValue,
			StatementParameters parameters, int index, int sqlType){
		if(paramValue != null){
			appendConcate();
			whereExp.append(" ").append(field).append(" ").append(condition).append(" ?");
			parameters.set(index++, sqlType, paramValue);
		}else{
			//如果paramValue==null，则field不会作为条件加入到最终的SQL中。
			and = or = false;
		}
		return index;
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
