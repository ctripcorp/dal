package com.ctrip.platform.dal.dao.sqlbuilder;

import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.AND;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.NOT;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.NULL;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.OR;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.leftBracket;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.rightBracket;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.Expressions.ColumnExpression;
import com.ctrip.platform.dal.dao.sqlbuilder.Expressions.Expression;

/**
 * Sql builder for only one table
 * 
 * @author jhhe
 *
 */
public abstract class AbstractTableSqlBuilder extends AbstractSqlBuilder implements TableSqlBuilder {
	
	protected int index = 1;
	
	protected List<FieldEntry> selectOrUpdataFieldEntrys =  new ArrayList<FieldEntry>();
	
	private List<FieldEntry> whereFieldEntrys = new ArrayList<FieldEntry>();
	
	private String tableName;
	
    public AbstractTableSqlBuilder() {
        super(new BuilderContext());
        getContext().setDbCategory(DatabaseCategory.MySql);
        getContext().setParameters(new StatementParameters());
        
        // We disable space insertion by default to make sql like before?
        disableSpaceSkipping();
    }

	private boolean compatible = false;

	public boolean isCompatible() {
		return compatible;
	}

	public void setCompatible(boolean compatible) {
		this.compatible = compatible;
	}

	public AbstractTableSqlBuilder from(String tableName) throws SQLException {
		if(tableName ==null || tableName.isEmpty())
			throw new SQLException("table name is illegal.");
		
		this.tableName = tableName;
		return this;
	}
	
	public AbstractTableSqlBuilder setDatabaseCategory(DatabaseCategory dbCategory) throws SQLException {
		getContext().setDbCategory(dbCategory);
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
	    StatementParameters parameters = new StatementParameters();
		index = 1;
		for(FieldEntry entry : selectOrUpdataFieldEntrys) {
			parameters.add(new StatementParameter(index++, entry.getSqlType(), entry.getParamValue()).setSensitive(entry.isSensitive()).setName(entry.getFieldName()).setInParam(entry.isInParam()));
		}
		for(FieldEntry entry : whereFieldEntrys){
			parameters.add(new StatementParameter(index++, entry.getSqlType(), entry.getParamValue()).setSensitive(entry.isSensitive()).setName(entry.getFieldName()).setInParam(entry.isInParam()));
		}
		return parameters;
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
		return wrapField(getContext().getDbCategory(), fieldName);
	}
	
	/**
	 * 对字段进行包裹，数据库是MySQL则用 `进行包裹，数据库是SqlServer则用[]进行包裹
	 * @param fieldName
	 * @return
	 */
	public static String wrapField(DatabaseCategory dbCategory, String fieldName){
		if("*".equalsIgnoreCase(fieldName) || fieldName.contains("ROW_NUMBER") || fieldName.contains(",")){
			return fieldName;
		}

		return dbCategory.quote(fieldName);
	}
	
	/**
	 * Subclass must rewrite the default build process
	 * 
	 * @return
	 */
	public abstract String build();
	
	/**
	 * build where expression
	 * @return
	 */
	public String getWhereExp(){
	    return super.build();
	}
	
	/**
	 * 追加AND连接
	 * @return
	 */
	public AbstractTableSqlBuilder and(){
		return addInternal(AND);
	}
	
	/**
	 * 追加OR连接
	 * @return
	 */
	public AbstractTableSqlBuilder or(){
	    return addInternal(OR);
	}
	
	private static final boolean DEFAULT_SENSITIVE = false;
	
	/**
	 *  等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder equal(String field, Object paramValue, int sqlType) throws SQLException {
		return equal(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder equal(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(Expressions.equal(field), paramValue, sqlType, sensitive);
	}
	
	/**
	 *  等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder equalNullable(String field, Object paramValue, int sqlType) {
		return equalNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder equalNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(Expressions.equal(field), paramValue, sqlType, sensitive);
	}

	/**
	 *  不等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder notEqual(String field, Object paramValue, int sqlType) throws SQLException {
		return notEqual(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder notEqual(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(Expressions.notEqual(field), paramValue, sqlType, sensitive);
	}
	
	/**
	 *  不等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder notEqualNullable(String field, Object paramValue, int sqlType) {
		return notEqualNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder notEqualNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(Expressions.notEqual(field), paramValue, sqlType, sensitive);
	}

	/**
	 *  大于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder greaterThan(String field, Object paramValue, int sqlType) throws SQLException {
		return greaterThan(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder greaterThan(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(Expressions.greaterThan(field), paramValue, sqlType, sensitive);
	}
	
	/**
	 *  大于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder greaterThanNullable(String field, Object paramValue, int sqlType) {
		return greaterThanNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder greaterThanNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(Expressions.greaterThan(field), paramValue, sqlType, sensitive);
	}

	/**
	 *  大于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder greaterThanEquals(String field, Object paramValue, int sqlType) throws SQLException {
		return greaterThanEquals(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder greaterThanEquals(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(Expressions.greaterThanEquals(field), paramValue, sqlType, sensitive);
	}
	
	/**
	 *  大于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder greaterThanEqualsNullable(String field, Object paramValue, int sqlType) {
		return greaterThanEqualsNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder greaterThanEqualsNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(Expressions.greaterThanEquals(field), paramValue, sqlType, sensitive);
	}

	/**
	 *  小于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder lessThan(String field, Object paramValue, int sqlType) throws SQLException {
		return lessThan(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder lessThan(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(Expressions.lessThan(field), paramValue, sqlType, sensitive);
	}
	
	/**
	 *  小于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder lessThanNullable(String field, Object paramValue, int sqlType) {
		return lessThanNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder lessThanNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(Expressions.lessThan(field), paramValue, sqlType, sensitive);
	}

	/**
	 *  小于等于操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder lessThanEquals(String field, Object paramValue, int sqlType) throws SQLException {
		return lessThanEquals(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder lessThanEquals(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(Expressions.lessThanEquals(field), paramValue, sqlType, sensitive);
	}
	
	/**
	 *  小于等于操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder lessThanEqualsNullable(String field, Object paramValue, int sqlType) {
		return lessThanEqualsNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder lessThanEqualsNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(Expressions.lessThanEquals(field), paramValue, sqlType, sensitive);
	}

	/**
	 *  Between操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValue1 字段值1
	 * @param paramValue2 字段值2
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder between(String field, Object paramValue1, Object paramValue2, int sqlType) throws SQLException {
		return between(field, paramValue1, paramValue2, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder between(String field, Object paramValue1, Object paramValue2, int sqlType, boolean sensitive) throws SQLException {
		if (paramValue1 == null || paramValue2 == null)
			throw new SQLException(field + " is not support null value.");

		return addBetweenClause(field, paramValue1, paramValue2, sqlType, sensitive);
	}
	
	/**
	 *  Between操作，若字段值为NULL，则此条件不会加入SQL中
	 * @param field 字段
	 * @param paramValue1 字段值1
	 * @param paramValue2 字段值2
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder betweenNullable(String field, Object paramValue1, Object paramValue2, int sqlType) {
		return betweenNullable(field, paramValue1, paramValue2, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder betweenNullable(String field, Object paramValue1, Object paramValue2, int sqlType, boolean sensitive) {
	    if (paramValue1 == null || paramValue2 == null)
	        return addInternal(NULL);
	    
		return addBetweenClause(field, paramValue1, paramValue2, sqlType, sensitive);
	}
	
	private AbstractTableSqlBuilder addBetweenClause(String field, Object paramValue1, Object paramValue2, int sqlType, boolean sensitive) {
        whereFieldEntrys.add(new FieldEntry(field, paramValue1, sqlType, sensitive));
        whereFieldEntrys.add(new FieldEntry(field, paramValue2, sqlType, sensitive));

        return addInternal(Expressions.between(field));
	}

	/**
	 *  Like操作，且字段值不能为NULL，否则会抛出SQLException. 
	 *  
	 *  Please make sure there is "%" at certain place in the parameter. 
	 *  If there is no "%", DAL will not auto append any "%" in the original prameter.
	 *  In this case, like work exactly as equal expression.
	 *  
	 *  If you don't want to add "%" in the parameter by yourself, you can use the other 
	 *  like method with MatchPattern parameter.
	 *   
	 * @param field 字段
	 * @param paramValue 字段值, paramValue should contain "%" at the begining, end or in the middle.
	 * @return
	 * @throws SQLException
	 * @Deprecated just a marker to catch your eye about the usage notification
	 */
	public AbstractTableSqlBuilder like(String field, Object paramValue, int sqlType) throws SQLException {
		return like(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}
	
    /**
     *  Like操作，且字段值不能为NULL，否则会抛出SQLException. 
     *  
     *  Please make sure there is "%" at certain place in the parameter. 
     *  If there is no "%", DAL will not auto append any "%" in the original prameter.
     *  In this case, like work exactly as equal expression.
     *  
     *  If you don't want to add "%" in the parameter by yourself, you can use the other 
     *  like method with MatchPattern parameter.
     *   
     * @param field 字段
     * @param paramValue 字段值, paramValue should contain "%" at the begining, end or in the middle.
     * @param sensitive if the parameter will be replaced by "*" in the log output.
     * @return
     * @throws SQLException
     * @Deprecated just a marker to catch your eye about the usage notification
     */
	public AbstractTableSqlBuilder like(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException {
		return addParam(Expressions.like(field), paramValue, sqlType, sensitive);
	}
	
	/**
	 *  Like操作，若字段值为NULL，则此条件不会加入SQL中
	 *  
     *  Please make sure there is "%" at certain place in the parameter. 
     *  If there is no "%", DAL will not auto append any "%" in the original prameter.
     *  In this case, like work exactly as equal expression.
     *  
     *  If you don't want to add "%" in the parameter by yourself, you can use the other 
     *  like method with MatchPattern parameter.
     *  
	 * @param field 字段
	 * @param paramValue 字段值, paramValue should contain "%" at the begining, end or in the middle.
	 * @return
	 * @throws SQLException
     * @Deprecated just a marker to catch your eye about the usage notification
	 */
	public AbstractTableSqlBuilder likeNullable(String field, Object paramValue, int sqlType) {
		return likeNullable(field, paramValue, sqlType, DEFAULT_SENSITIVE);
	}

    /**
     *  Like操作，若字段值为NULL，则此条件不会加入SQL中
     *  
     *  Please make sure there is "%" at certain place in the parameter. 
     *  If there is no "%", DAL will not auto append any "%" in the original prameter.
     *  In this case, like work exactly as equal expression.
     *  
     *  If you don't want to add "%" in the parameter by yourself, you can use the other 
     *  like method with MatchPattern parameter.
     *  
     * @param field 字段
     * @param paramValue 字段值, paramValue should contain "%" at the begining, end or in the middle.
     * @param sensitive if the parameter will be replaced by "*" in the log output.
     * @return
     * @throws SQLException
     * @Deprecated just a marker to catch your eye about the usage notification
     */	
	public AbstractTableSqlBuilder likeNullable(String field, Object paramValue, int sqlType, boolean sensitive) {
		return addParamNullable(Expressions.like(field), paramValue, sqlType, sensitive);
	}

    /**
     *  Like操作，且字段值不能为NULL，否则会抛出SQLException
     *  
     *  Dal will append or insert "%" to the original parameter follow what is specified by pattern.
     *  
     *  If you want to control how "%" is added for maximal flexibility, you can use the other 
     *  like method without MatchPattern parameter.
     *  
     * @param field 字段
     * @param paramValue 字段值
     * @param pattern how DAL will append "%" for the input paramValue
     * @return
     * @throws SQLException
     */
    public AbstractTableSqlBuilder like(String field, String paramValue, MatchPattern pattern, int sqlType) throws SQLException {
        return like(field, paramValue, pattern, sqlType, DEFAULT_SENSITIVE);
    }
    
    /**
     *  Like操作，且字段值不能为NULL，否则会抛出SQLException
     *  
     *  Dal will append or insert "%" to the original parameter follow what is specified by pattern.
     *  
     *  If you want to control how "%" is added for maximal flexibility, you can use the other 
     *  like method without MatchPattern parameter.
     *  
     * @param field 字段
     * @param paramValue 字段值
     * @param pattern how DAL will append "%" for the input paramValue
     * @param sensitive if the parameter will be replaced by "*" in the log output.
     * @return
     * @throws SQLException
     */
    public AbstractTableSqlBuilder like(String field, String paramValue, MatchPattern pattern, int sqlType, boolean sensitive) throws SQLException {
        return addParam(Expressions.like(field), pattern.process(paramValue), sqlType, sensitive);
    }
    
    /**
     *  Like操作，若字段值为NULL，则此条件不会加入SQL中
     *  
     *  Dal will append or insert "%" to the original parameter follow what is specified by pattern.
     *  
     *  If you want to control how "%" is added for maximal flexibility, you can use the other 
     *  like method without MatchPattern parameter.
     *  
     * @param field 字段
     * @param paramValue 字段值
     * @param pattern how DAL will append "%" for the input paramValue
     * @return
     * @throws SQLException
     */
    public AbstractTableSqlBuilder likeNullable(String field, Object paramValue, MatchPattern pattern, int sqlType) {
        return likeNullable(field, paramValue, pattern, sqlType, DEFAULT_SENSITIVE);
    }
    
    /**
     *  Like操作，若字段值为NULL，则此条件不会加入SQL中
     *  
     *  Dal will append or insert "%" to the original parameter follow what is specified by pattern.
     *  
     *  If you want to control how "%" is added for maximal flexibility, you can use the other 
     *  like method without MatchPattern parameter.
     *  
     * @param field 字段
     * @param paramValue 字段值
     * @param pattern how DAL will append "%" for the input paramValue
     * @param sensitive if the parameter will be replaced by "*" in the log output.
     * @return
     * @throws SQLException
     */
    public AbstractTableSqlBuilder likeNullable(String field, Object paramValue, MatchPattern pattern, int sqlType, boolean sensitive) {
        return addParamNullable(Expressions.like(field), process(paramValue, pattern), sqlType, sensitive);
    }

	/**
	 *  In操作，且字段值不能为NULL，否则会抛出SQLException
	 * @param field 字段
	 * @param paramValues 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder in(String field, List<?> paramValues, int sqlType) throws SQLException {
		return in(field, paramValues, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder in(String field, List<?> paramValues, int sqlType, boolean sensitive) throws SQLException {
	    StatementParameter.validateInParams(field, paramValues);
		return addInParam(field, paramValues, sqlType, sensitive);
	}
	
	/**
	 *  In操作，允许参数为NULL，或者字段值为NULL, 或者传入的字段值数量为0。
	 * @param field 字段
	 * @param paramValues 字段值
	 * @return
	 * @throws SQLException
	 */
	public AbstractTableSqlBuilder inNullable(String field, List<?> paramValues, int sqlType) throws SQLException {
		return inNullable(field, paramValues, sqlType, DEFAULT_SENSITIVE);
	}
	
	public AbstractTableSqlBuilder inNullable(String field, List<?> paramValues, int sqlType, boolean sensitive) throws SQLException {
	    if(StatementParameter.isNullInParams(paramValues))
            return addInternal(NULL);
		return addInParam(field, paramValues, sqlType, sensitive);
	}
	
    /**
     *  In操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段
     * @param paramValues 字段值
     * @return
     * @throws SQLException
     */
    public AbstractTableSqlBuilder notIn(String field, List<?> paramValues, int sqlType) throws SQLException {
        return notIn(field, paramValues, sqlType, DEFAULT_SENSITIVE);
    }
    
    public AbstractTableSqlBuilder notIn(String field, List<?> paramValues, int sqlType, boolean sensitive) throws SQLException {
        StatementParameter.validateInParams(field, paramValues);
        return addNotInParam(field, paramValues, sqlType, sensitive);
    }
    
    /**
     *  In操作，允许参数为NULL，或者字段值为NULL, 或者传入的字段值数量为0。
     * @param field 字段
     * @param paramValues 字段值
     * @return
     * @throws SQLException
     */
    public AbstractTableSqlBuilder notInNullable(String field, List<?> paramValues, int sqlType) throws SQLException {
        return notInNullable(field, paramValues, sqlType, DEFAULT_SENSITIVE);
    }
    
    public AbstractTableSqlBuilder notInNullable(String field, List<?> paramValues, int sqlType, boolean sensitive) throws SQLException {
        if(null == paramValues || paramValues.size() == 0){
            return addInternal(NULL);
        }
        
        Iterator<?> ite = paramValues.iterator();
        while(ite.hasNext()){
            if(ite.next()==null){
                ite.remove();
            }
        }
        
        if(paramValues.size() == 0){
            return addInternal(NULL);
        }
        
        return addNotInParam(field, paramValues, sqlType, sensitive);
    }
    
    /**
	 * Is null操作
	 * @param field 字段
	 * @return
	 */
	public AbstractTableSqlBuilder isNull(String field){
		return addInternal(Expressions.isNull(field));
	}
	
	/**
	 * Is not null操作
	 * @param field 字段
	 * @return
	 */
	public AbstractTableSqlBuilder isNotNull(String field){
		return addInternal(Expressions.isNotNull(field));
	}
	
	/**
	 * Add "("
	 */
	public AbstractTableSqlBuilder leftBracket(){
		return addInternal(leftBracket);
	}
	
	/**
	 * Add ")"
	 */
	public AbstractTableSqlBuilder rightBracket(){
		return addInternal(rightBracket);
	}

	/**
	 * Add "NOT"
	 */
	public AbstractTableSqlBuilder not(){
	    return addInternal(NOT);
	}
	
	private AbstractTableSqlBuilder addInParam(String field, List<?> paramValues, int sqlType, boolean sensitive){
		return addInternal(new InClauseEntry(field, paramValues, sqlType, sensitive, whereFieldEntrys, compatible));
	}
	
    private AbstractTableSqlBuilder addNotInParam(String field, List<?> paramValues, int sqlType, boolean sensitive){
        return addInternal(new InClauseEntry(field, paramValues, sqlType, sensitive, whereFieldEntrys, compatible).setNotIn());
    }
    
	private AbstractTableSqlBuilder addParam(Expression expr, Object paramValue, int sqlType, boolean sensitive) throws SQLException{
	    ColumnExpression colExpr = (ColumnExpression )expr;
		if(paramValue == null)
			throw new SQLException(colExpr.getColumnName() + " is not support null value.");	

		return addSingleClause(colExpr, paramValue, sqlType, sensitive);
	}
	
	private AbstractTableSqlBuilder addParamNullable(Expression expr, Object paramValue, int sqlType, boolean sensitive){
		if(paramValue == null)
			return addInternal(NULL);
		
		return addSingleClause((ColumnExpression)expr, paramValue, sqlType, sensitive);
	}
	
	private AbstractTableSqlBuilder addSingleClause(ColumnExpression expr, Object paramValue, int sqlType, boolean sensitive){
        whereFieldEntrys.add(new FieldEntry(expr.getColumnName(), paramValue, sqlType, sensitive));
        return addInternal(expr);
	}
	
	private String process(Object value, MatchPattern pattern) {
	    if(value == null)
	        return null;
	    
	    String valueStr = value instanceof String ? (String)value : value.toString();
	    
	    switch (pattern) {
            case END_WITH:
                return "%" + valueStr;
            case BEGIN_WITH:
                return valueStr + "%";
            case CONTAINS:
                return "%" + valueStr + "%";
            case USER_DEFINED:
                return valueStr;
            default:
                throw new IllegalStateException("Not supported yet");
        }
	}
	
	private static class InClauseEntry extends Expression {
		private String field;
		private String questionMarkList;
		private boolean compatible;
		private boolean isNot = false;
		private static final String IN_CLAUSE = " in ( ? )";
		private static final String NOT_IN_CLAUSE = " not in ( ? )";
		private List<FieldEntry> entries;
		
		public InClauseEntry(String field, List<?> paramValues, int sqlType, boolean sensitive, List<FieldEntry> whereFieldEntrys, boolean compatible){
		    super("");
			this.field = field;
			this.compatible = compatible;
			
			if(compatible)
				create(field, paramValues, sqlType, sensitive, whereFieldEntrys);
			else{
				whereFieldEntrys.add(new FieldEntry(field, paramValues, sqlType, sensitive).setInParam(true));
			}
		}
		
		public InClauseEntry setNotIn() {
		    isNot = true;
		    return this;
		}
		
		private void create(String field, List<?> paramValues, int sqlType, boolean sensitive, List<FieldEntry> whereFieldEntrys){
			StringBuilder temp = new StringBuilder();
			temp.append(isNot ? " not in ( ":" in ( ");
			
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
		
		private String wrap(String field) {
            return wrapField(getDbCategory(), field);
        }

		public String build() {
			return compatible ?
					wrap(field) + questionMarkList:
						wrap(field) + (isNot ? NOT_IN_CLAUSE:IN_CLAUSE);
		}
	}
	
	private AbstractTableSqlBuilder addInternal(Clause entry) {
		super.add(entry);
		return this;
	}
}