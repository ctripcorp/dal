package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;

public abstract class AbstractSqlBuilder implements TableSqlBuilder {
	
	protected DatabaseCategory dbCategory = DatabaseCategory.MySql;
	
	protected StatementParameters parameters = new StatementParameters();
	
	protected int index = 1;
	
	protected List<FieldEntry> selectOrUpdataFieldEntrys =  new ArrayList<FieldEntry>();
	
	private LinkedList<WhereClauseEntry> whereClauseEntries = new LinkedList<>();
	
	private List<FieldEntry> whereFieldEntrys = new ArrayList<FieldEntry>();
	
	private String tableName;
	
	private boolean compatible = false;

	public boolean isCompatible() {
		return compatible;
	}

	public void setCompatible(boolean compatible) {
		this.compatible = compatible;
	}

	public AbstractSqlBuilder from(String tableName) throws SQLException {
		if(tableName ==null || tableName.isEmpty())
			throw new SQLException("table name is illegal.");
		
		this.tableName = tableName;
		return this;
	}
	
	public AbstractSqlBuilder setDatabaseCategory(DatabaseCategory dbCategory) throws SQLException {
		Objects.requireNonNull(dbCategory, "DatabaseCategory can't be null.");
		this.dbCategory = dbCategory;
		return this;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String getTableName(String shardStr) {
		return tableName + shardStr;
	}

	/**
	 * 获取StatementParameters
	 * @return
	 */
	public StatementParameters buildParameters(){
		parameters = new StatementParameters();
		index = 1;
		for(FieldEntry entry : selectOrUpdataFieldEntrys) {
			parameters.add(new StatementParameter(index++, entry.getSqlType(), entry.getParamValue()).setSensitive(entry.isSensitive()).setName(entry.getFieldName()).setInParam(entry.isInParam()));
		}
		for(FieldEntry entry : whereFieldEntrys){
			parameters.add(new StatementParameter(index++, entry.getSqlType(), entry.getParamValue()).setSensitive(entry.isSensitive()).setName(entry.getFieldName()).setInParam(entry.isInParam()));
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
		return wrapField(dbCategory, fieldName);
	}
	
	/**
	 * 对字段进行包裹，数据库是MySQL则用 `进行包裹，数据库是SqlServer则用[]进行包裹
	 * @param fieldName
	 * @return
	 */
	public static String wrapField(DatabaseCategory dbCategory, String fieldName){
		if("*".equalsIgnoreCase(fieldName) || fieldName.contains("ROW_NUMBER") || fieldName.contains(",")){
			return fieldName;
		}else if(dbCategory == DatabaseCategory.MySql){
			return "`" + fieldName + "`";
		}else if(dbCategory == DatabaseCategory.SqlServer){
			return "[" + fieldName + "]";
		}
		return fieldName;
	}
	
	/**
	 * build sql.
	 * @return
	 */
	public abstract String build();
	
	private static final String EMPTY = "";
	
	/**
	 * build where expression
	 * @return
	 */
	public String getWhereExp(){
//		return whereExp.toString().trim().isEmpty()? "": "WHERE"+ whereExp.toString();

		if(whereClauseEntries.size() == 0)
			return EMPTY;
		
		LinkedList<WhereClauseEntry> filtered = new LinkedList<>();
		
		for(WhereClauseEntry entry: whereClauseEntries) {
			if(entry.isClause() && entry.isNull()){
				meltDownNullValue(filtered);
				continue;
			}

			if(entry.isBracket() && !((BracketClauseEntry)entry).isLeft()){
				if(meltDownRightBracket(filtered))
					continue;
			}
			
			// AND/OR
			if(entry.isOperator() && !entry.isClause()) {
				if(meltDownAndOrOperator(filtered))
					continue;
			}
			
			filtered.add(entry);
		}
		
		StringBuilder sb = new StringBuilder();
		for(WhereClauseEntry entry: filtered) {
			sb.append(entry.getClause(dbCategory)).append(" ");
		}
		
		String whereClause = sb.toString().trim();
		if(whereClause.isEmpty())
			return "";
		
		return whereClause;
	}
	
	private boolean meltDownAndOrOperator(LinkedList<WhereClauseEntry> filtered) {
		// If it is the first element
		if(filtered.size() == 0)
			return true;

		WhereClauseEntry entry = filtered.getLast();
		// The last one is "("
		if(entry.isBracket() && ((BracketClauseEntry)entry).isLeft())
			return true;
			
		// AND/OR/NOT AND/OR
		if(entry.isOperator()) {
			return true;
		}
		return false;
	}
	
	private boolean meltDownRightBracket(LinkedList<WhereClauseEntry> filtered) {
		int bracketCount = 1;
		while(filtered.size() > 0) {
			WhereClauseEntry entry = filtered.getLast();
			// One ")" only remove one "("
			if(entry.isBracket() && ((BracketClauseEntry)entry).isLeft() && bracketCount == 1){
				filtered.removeLast();
				bracketCount--;
				continue;
			}
			
			// Remove any leading AND/OR/NOT (BOT is both operator and clause)
			if(entry.isOperator()) {
				filtered.removeLast();
				continue;
			}
			
			break;
		}
		
		return bracketCount == 0? true : false;
	}
	private void meltDownNullValue(LinkedList<WhereClauseEntry> filtered) {
		if(filtered.size() == 0)
			return;

		while(filtered.size() > 0) {
			WhereClauseEntry entry = filtered.getLast();
			// Remove any leading AND/OR/NOT (NOT is both operator and clause)
			if(entry.isOperator()) {
				filtered.removeLast();
				continue;
			}
			
			break;
		}
	}
	
	/**
	 * 追加AND连接
	 * @return
	 */
	public AbstractSqlBuilder and(){
		return add(OperatorClauseEntry.AND());
	}
	
	/**
	 * 追加OR连接
	 * @return
	 */
	public AbstractSqlBuilder or(){
		return add(OperatorClauseEntry.OR());
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

		return add(new BetweenClauseEntry(field, paramValue1, paramValue2, sqlType, sensitive, whereFieldEntrys));
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
			return add(new NullValueClauseEntry());

		return add(new BetweenClauseEntry(field, paramValue1, paramValue2, sqlType, sensitive, whereFieldEntrys));
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
			return add(new NullValueClauseEntry());
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
			return add(new NullValueClauseEntry());
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
	
	/**
	 * Add "("
	 */
	public AbstractSqlBuilder leftBracket(){
		return add(BracketClauseEntry.leftBracket());
	}
	
	/**
	 * Add ")"
	 */
	public AbstractSqlBuilder rightBracket(){
		return add(BracketClauseEntry.rightBracket());
	}

	/**
	 * Add "NOT"
	 */
	public AbstractSqlBuilder not(){
		return add(new NotClauseEntry());
	}
	
	private AbstractSqlBuilder addInParam(String field, List<?> paramValues, int sqlType, boolean sensitive){
		return add(new InClauseEntry(field, paramValues, sqlType, sensitive, whereFieldEntrys, compatible));
	}
	
	private AbstractSqlBuilder addParam(String field, String condition, Object paramValue, int sqlType, boolean sensitive) throws SQLException{
		if(paramValue == null)
			throw new SQLException(field + " is not support null value.");	

		return add(new SingleClauseEntry(field, condition, paramValue, sqlType, sensitive, whereFieldEntrys));
	}
	
	private AbstractSqlBuilder addParamNullable(String field, String condition, Object paramValue, int sqlType, boolean sensitive){
		if(paramValue == null)
			return add(new NullValueClauseEntry());
		
		return add(new SingleClauseEntry(field, condition, paramValue, sqlType, sensitive, whereFieldEntrys));
	}
	
	private static abstract class WhereClauseEntry {
		private String clause; 
		
		public boolean isOperator() {
			return false;
		}
		
		public boolean isBracket() {
			return false;
		}
		
		public boolean isClause() {
			return false;
		}
		
		public boolean isNull() {
			return false;
		}
		
		//To make it build late when DatabaseCategory is set
		public abstract String getClause(DatabaseCategory dbCategory);
		
		public String toString() {
			return clause;
		}
	}
	
	private static class NullValueClauseEntry extends WhereClauseEntry {
		public boolean isNull() {
			return true;
		}

		public boolean isClause() {
			return true;
		}
		
		public String getClause(DatabaseCategory dbCategory) {
			return "";
		}
	}
	
	private static class SingleClauseEntry extends WhereClauseEntry {
		private String condition;
		private FieldEntry entry;
		
		public SingleClauseEntry(String field, String condition, Object paramValue, int sqlType, boolean sensitive, List<FieldEntry> whereFieldEntrys) {
			this.condition = condition;
			entry = new FieldEntry(field, paramValue, sqlType, sensitive);
			whereFieldEntrys.add(entry);
		}

		public boolean isClause() {
			return true;
		}
		
		public String getClause(DatabaseCategory dbCategory) {
			return String.format("%s %s ?", wrapField(dbCategory, entry.getFieldName()), condition);
		}
	}
	
	private static class BetweenClauseEntry extends WhereClauseEntry {
		private FieldEntry entry1;
		private FieldEntry entry2;
		
		public BetweenClauseEntry(String field, Object paramValue1, Object paramValue2, int sqlType, boolean sensitive, List<FieldEntry> whereFieldEntrys) {
			entry1 = new FieldEntry(field, paramValue1, sqlType, sensitive);
			entry2 = new FieldEntry(field, paramValue2, sqlType, sensitive);
			whereFieldEntrys.add(entry1);
			whereFieldEntrys.add(entry2);
		}

		public boolean isClause() {
			return true;
		}

		public String getClause(DatabaseCategory dbCategory) {
			return wrapField(dbCategory, entry1.getFieldName()) + " BETWEEN ? AND ?";
		}
	}

	private static class InClauseEntry extends WhereClauseEntry {
		private String field;
		private String questionMarkList;
		private boolean compatible;
		private static final String IN_CLAUSE = " in ( ? )";
		private List<FieldEntry> entries;
		
		public InClauseEntry(String field, List<?> paramValues, int sqlType, boolean sensitive, List<FieldEntry> whereFieldEntrys, boolean compatible){
			this.field = field;
			this.compatible = compatible;
			
			if(compatible)
				create(field, paramValues, sqlType, sensitive, whereFieldEntrys);
			else{
				whereFieldEntrys.add(new FieldEntry(field, paramValues, sqlType, sensitive).setInParam(true));
			}
		}
		
		private void create(String field, List<?> paramValues, int sqlType, boolean sensitive, List<FieldEntry> whereFieldEntrys){
			StringBuilder temp = new StringBuilder();
			temp.append(" in ( ");
			
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
			questionMarkList = temp.toString();
			whereFieldEntrys.addAll(entries);
		}

		public boolean isClause() {
			return true;
		}

		public String getClause(DatabaseCategory dbCategory) {
			return compatible ?
					wrapField(dbCategory, field) + questionMarkList:
						wrapField(dbCategory, field) + IN_CLAUSE;
		}
	}
	
	private static class NullClauseEntry extends WhereClauseEntry {
		private String field;
		private boolean isNull;
		
		public NullClauseEntry(String field, boolean isNull) {
			this.field = field;
			this.isNull=  isNull;
		}

		public boolean isClause() {
			return true;
		}
		
		public String getClause(DatabaseCategory dbCategory) {
			return wrapField(dbCategory, field) + (isNull ? " IS NULL" : " IS NOT NULL");		
		}
	}
	
	private static class NotClauseEntry extends WhereClauseEntry {
		public NotClauseEntry() {
		}

		public boolean isClause() {
			return true;
		}
		
		public boolean isOperator() {
			return true;
		}
		
		public String getClause(DatabaseCategory dbCategory) {
			return "NOT";
		}
	}
	
	private static class OperatorClauseEntry extends WhereClauseEntry {
		private String operator;
		public OperatorClauseEntry(String operator) {
			this.operator = operator;
		}
		
		public String getClause(DatabaseCategory dbCategory) {
			return operator;
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
	
	private static class BracketClauseEntry extends WhereClauseEntry {
		private boolean left;
		public BracketClauseEntry(boolean isLeft) {
			left = isLeft;
		}
		
		public String getClause(DatabaseCategory dbCategory) {
			return left? "(" : ")";
		}
		
		public boolean isBracket() {
			return true;
		}

		public boolean isLeft() {
			return left;
		}

		static BracketClauseEntry leftBracket() {
			return new BracketClauseEntry(true);
		}

		static BracketClauseEntry rightBracket() {
			return new BracketClauseEntry(false);
		}
	}
	
	private AbstractSqlBuilder add(WhereClauseEntry entry) {
		whereClauseEntries.add(entry);
		return this;
	}
}