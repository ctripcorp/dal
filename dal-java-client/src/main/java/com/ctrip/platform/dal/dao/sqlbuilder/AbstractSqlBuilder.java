package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;

public abstract class AbstractSqlBuilder {
	
	protected DatabaseCategory dBCategory = DatabaseCategory.MySql;
	
	protected StatementParameters parameters = new StatementParameters();
	
	protected int index = 1;
	
	protected List<FieldEntry> selectOrUpdataFieldEntrys =  new ArrayList<FieldEntry>();
	
	private StringBuilder whereExp = new StringBuilder();
	
	private List<WhereClauseEntry> whereClauseEntries = new ArrayList<>();
	
	private List<FieldEntry> whereFieldEntrys = new ArrayList<FieldEntry>();
	
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
		for(FieldEntry entry : selectOrUpdataFieldEntrys) {
			if (entry.isSensitive())
				parameters.setSensitive(index++, entry.getFieldName(), entry.getSqlType(), entry.getParamValue());
			else
				parameters.set(index++, entry.getFieldName(), entry.getSqlType(), entry.getParamValue());
		}
		for(FieldEntry entry : whereFieldEntrys){
			if (entry.isSensitive())
				parameters.setSensitive(index++, entry.getFieldName(), entry.getSqlType(), entry.getParamValue());
			else
				parameters.set(index++, entry.getFieldName(), entry.getSqlType(), entry.getParamValue());
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
	 * build sql.
	 * @return
	 */
	public abstract String build();
	
	/**
	 * build where expression
	 * @return
	 */
	public String getWhereExp(){
		return whereExp.toString().trim().isEmpty()? "": "WHERE"+ whereExp.toString();
	}
	
	/**
	 * 追加AND连接
	 * @return
	 */
	public AbstractSqlBuilder and(){
		add(OperatorClauseEntry.AND());
		return this;
	}
	
	/**
	 * 追加OR连接
	 * @return
	 */
	public AbstractSqlBuilder or(){
		add(OperatorClauseEntry.OR());
		return this;
	}
	
	private static final boolean DEFAULT_SENSITIVE = false;
	
	/**
	 *  等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder equal(String field, Object paramValue, int sqlType) throws SQLException {
		return equal(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder equal(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(field, "=", paramValue, sqlType, sensitive);
	}
	
	/**
	 *  等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder equalNullable(String field, Object paramValue, int sqlType) {
		return equalNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder equalNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(field, "=", paramValue, sqlType, sensitive);
	}

	/**
	 *  不等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder notEqual(String field, Object paramValue, int sqlType) throws SQLException {
		return notEqual(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder notEqual(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(field, "!=", paramValue, sqlType, sensitive);
	}
	
	/**
	 *  不等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder notEqualNullable(String field, Object paramValue, int sqlType) {
		return notEqualNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder notEqualNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(field, "!=", paramValue, sqlType, sensitive);
	}

	/**
	 *  大于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder greaterThan(String field, Object paramValue, int sqlType) throws SQLException {
		return greaterThan(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder greaterThan(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(field, ">", paramValue, sqlType, sensitive);
	}
	
	/**
	 *  大于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder greaterThanNullable(String field, Object paramValue, int sqlType) {
		return greaterThanNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder greaterThanNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(field, ">", paramValue, sqlType, sensitive);
	}

	/**
	 *  大于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder greaterThanEquals(String field, Object paramValue, int sqlType) throws SQLException {
		return greaterThanEquals(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder greaterThanEquals(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(field, ">=", paramValue, sqlType, sensitive);
	}
	
	/**
	 *  大于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder greaterThanEqualsNullable(String field, Object paramValue, int sqlType) {
		return greaterThanEqualsNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder greaterThanEqualsNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(field, ">=", paramValue, sqlType, sensitive);
	}

	/**
	 *  小于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder lessThan(String field, Object paramValue, int sqlType) throws SQLException {
		return lessThan(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder lessThan(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(field, "<", paramValue, sqlType, sensitive);
	}
	
	/**
	 *  小于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder lessThanNullable(String field, Object paramValue, int sqlType) {
		return lessThanNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder lessThanNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(field, "<", paramValue, sqlType, sensitive);
	}

	/**
	 *  小于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder lessThanEquals(String field, Object paramValue, int sqlType) throws SQLException {
		return lessThanEquals(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder lessThanEquals(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(field, "<=", paramValue, sqlType, sensitive);
	}
	
	/**
	 *  小于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder lessThanEqualsNullable(String field, Object paramValue, int sqlType) {
		return lessThanEqualsNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder lessThanEqualsNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(field, "<=", paramValue, sqlType, sensitive);
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
		return between(field, paramValue1, paramValue2, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder between(String field, Object paramValue1, Object paramValue2, int sqlType, boolean sensitive) throws SQLException {
		if (paramValue1 == null || paramValue2 == null)
			throw new SQLException(field + " is not support null value.");

		return add(new BetweenClauseEntry(field, paramValue1, paramValue2, sqlType, sensitive));
	}
	
	/**
	 *  Between操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue1 字段值1
	 * @param paramValue2 字段值2
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder betweenNullable(String field, Object paramValue1, Object paramValue2, int sqlType) {
		return betweenNullable(field, paramValue1, paramValue2, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder betweenNullable(String field, Object paramValue1, Object paramValue2, int sqlType, boolean sensitive) {
		//如果paramValue==null，则field不会作为条件加入到最终的SQL中。
		if(paramValue1 == null || paramValue2 == null)
			return this;

		return add(new BetweenClauseEntry(field, paramValue1, paramValue2, sqlType, sensitive));
	}

	/**
	 *  Like操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder like(String field, Object paramValue, int sqlType) throws SQLException {
		return like(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder like(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(field, "LIKE", paramValue, sqlType, sensitive);
	}
	
	/**
	 *  Like操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder likeNullable(String field, Object paramValue, int sqlType) {
		return likeNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder likeNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(field, "LIKE", paramValue, sqlType, sensitive);
	}

	/**
	 *  In操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValues 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractSqlBuilder in(String field, List<?> paramValues, int sqlType) throws SQLException {
		return in(field, paramValues, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder in(String field, List<?> paramValues, int sqlType, boolean sensitive) throws SQLException {
		if(null == paramValues || paramValues.size() == 0)
			throw new SQLException(field + " must have more than one value.");

		for(Object obj:paramValues)
			if(obj==null)
				throw new SQLException(field + " is not support null value.");

		return addInParam(field, paramValues, sqlType, sensitive);
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
		return inNullable(field, paramValues, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractSqlBuilder inNullable(String field, List<?> paramValues, int sqlType, boolean sensitive) throws SQLException {
		if(null == paramValues){
			return this;
		}
		
		if(paramValues.size() == 0){
			throw new SQLException(field + " must have more than one value.");
		}
		
		Iterator<?> ite = paramValues.iterator();
		while(ite.hasNext()){
			if(ite.next()==null){
				ite.remove();
			}
		}
		
		if(paramValues.size() == 0){
			return this;
		}
		
		return addInParam(field, paramValues, sqlType, sensitive);
	}
	
	/**
	 * Is null操作
	 * @param field 字段
	 * @return
	 */
	public AbstractSqlBuilder isNull(String field){
		return add(new NullClauseEntry(field, true));
	}
	
	/**
	 * Is not null操作
	 * @param field 字段
	 * @return
	 */
	public AbstractSqlBuilder isNotNull(String field){
		return add(new NullClauseEntry(field, false));
	}
	
	private AbstractSqlBuilder addInParam(String field, List<?> paramValues, int sqlType, boolean sensitive){
		return add(new InClauseEntry(field, paramValues, sqlType, sensitive));
	}
	
	private AbstractSqlBuilder addParam(String field, String condition, Object paramValue, int sqlType, boolean sensitive) throws SQLException{
		if(paramValue == null)
			throw new SQLException(field + " is not support null value.");	

		return add(new SingleClauseEntry(field, condition, paramValue, sqlType, sensitive));
	}
	
	private AbstractSqlBuilder addParamNullable(String field, String condition, Object paramValue, int sqlType, boolean sensitive){
		if(paramValue == null)
			return this;
		
		return add(new SingleClauseEntry(field, condition, paramValue, sqlType, sensitive));
	}
	
	private static abstract class WhereClauseEntry {
		private String clause; 
		
		public boolean isOperator() {
			return false;
		}
		
		public void setClause(String clause) {
			this.clause = clause;
		}
		
		public String getClause() {
			return clause;
		}
		
		public void addFieldEntry(List<FieldEntry> whereFieldEntrys){
			
		}
	}
	
	private static class SingleClauseEntry extends WhereClauseEntry {
		private FieldEntry entry;
		
		public SingleClauseEntry(String field, String condition, Object paramValue, int sqlType, boolean sensitive) {
			setClause(String.format("%s %s ?", field, condition));
			entry = new FieldEntry(field, paramValue, sqlType, sensitive);
		}

		@Override
		public void addFieldEntry(List<FieldEntry> whereFieldEntrys) {
			whereFieldEntrys.add(entry);
		}
	}
	
	private static class BetweenClauseEntry extends WhereClauseEntry {
		private FieldEntry entry1;
		private FieldEntry entry2;
		
		public BetweenClauseEntry(String field, Object paramValue1, Object paramValue2, int sqlType, boolean sensitive) {
			setClause(field + " BETWEEN ? AND ?");
			entry1 = new FieldEntry(field, paramValue1, sqlType, sensitive);
			entry2 = new FieldEntry(field, paramValue2, sqlType, sensitive);
		}

		@Override
		public void addFieldEntry(List<FieldEntry> whereFieldEntrys) {
			whereFieldEntrys.add(entry1);
			whereFieldEntrys.add(entry2);
		}
	}

	private static class InClauseEntry extends WhereClauseEntry {
		private List<FieldEntry> entries;
		
		public InClauseEntry(String field, List<?> paramValues, int sqlType, boolean sensitive){
			StringBuilder temp = new StringBuilder();
			temp.append(field).append(" in ( ");
			
			entries = new ArrayList<>(paramValues.size());
			for(int i=0,size=paramValues.size();i<size;i++){
				temp.append("?");
				if(i!=size-1){
					temp.append(", ");
				}
				FieldEntry entry = new FieldEntry(field, paramValues.get(i), sqlType, sensitive);
				entries.add(entry);
			}
			temp.append(" )");
			setClause(temp.toString());
		}

		@Override
		public void addFieldEntry(List<FieldEntry> whereFieldEntrys) {
			whereFieldEntrys.addAll(entries);
		}
	}
	
	private static class NullClauseEntry extends WhereClauseEntry {
		public NullClauseEntry(String field, boolean isNull) {
			setClause(field + (isNull ? " IS NULL" : " IS NOT NULL"));
		}
	}
	
	private static class OperatorClauseEntry extends WhereClauseEntry {
		public OperatorClauseEntry(String operator) {
			setClause(operator); 
		}
		
		@Override
		public boolean isOperator() {
			return true;
		}
		
		static OperatorClauseEntry AND() {
			return new OperatorClauseEntry("AND");
		}

		static OperatorClauseEntry OR() {
			return new OperatorClauseEntry("OR");
		}
	}
	
	private AbstractSqlBuilder add(WhereClauseEntry entry) {
		whereClauseEntries.add(entry);
		return this;
	}
}