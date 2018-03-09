package com.ctrip.platform.dal.dao.sqlbuilder;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.buildShardStr;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isTableShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.locateTableShardId;
import static com.ctrip.platform.dal.dao.sqlbuilder.AbstractTableSqlBuilder.wrapField;
import static com.ctrip.platform.dal.dao.sqlbuilder.Expressions.expressions;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.Expressions.ColumnExpression;
import com.ctrip.platform.dal.dao.sqlbuilder.Expressions.Expression;

/**
 * A very flexible SQL builder that can build complete SQL alone with parameters.
 * It allows user add nullable or conditional expressions. The null expression 
 * may be auto removed during build process. And it will also remove needed operator and bracket
 * to make the final sql correct. 
 * 
 * There are certain rules that you need to know about: 
 * if bracket has no content, bracket will be removed
 * expression can be evaluated and can be wrapped by bracket and connect to each other by and/or
 * expression should have no leading and tailing and/or, it there is, the and/or will be removed during validating
 * 
 * @author jhhe
 *
 */
public class AbstractFreeSqlBuilder extends AbstractSqlBuilder {
    /**
     * Because builder will skip space for certain case, 
     * you can append EMPTY to bypass this restriction. 
     */
    public static final String EMPTY = "";
    public static final String PLACE_HOLDER = "?";
    
    /**
     * Builder will not insert space before COMMA.
     */
    public static final Text COMMA = text(",");
    
    public static final Text SPACE = text(" ");
    
    public static final Keyword SELECT = keyword("SELECT");
    public static final Keyword FROM = keyword("FROM");
    public static final Keyword WHERE= keyword("WHERE");
    public static final Keyword AS = keyword("AS");
    public static final Keyword ORDER_BY = keyword("ORDER BY");
    public static final Keyword ASC = keyword("ASC");
    public static final Keyword DESC = keyword("DESC");
    public static final Keyword GROUP_BY = keyword("GROUP BY");
    public static final Keyword HAVING = keyword("HAVING");
    
    private BuilderContext context;
    
    public AbstractFreeSqlBuilder() {
        super(new BuilderContext());
        context = getContext();
    }
    
    /**
     * In case there is Table clause appended, logic DB must be set to determine
     * if the table name can be sharded or not. Set this logic db name will also
     * set db category identified by the logic db name. So you don't need to set
     * db category again.
     * 
     * @param logicDbName
     * @return
     */
    public AbstractFreeSqlBuilder setLogicDbName(String logicDbName) {
        context.setLogicDbName(logicDbName);
        return this;
    }
    
    /**
     * If you already set logic db name, then you don't need to set this.
     *  
     * @param dbCategory
     * @return
     */
    public AbstractFreeSqlBuilder setDbCategory(DatabaseCategory dbCategory) {
        context.setDbCategory(dbCategory);
        return this;
    }
    
    public AbstractFreeSqlBuilder setHints(DalHints hints) {
        context.setHints(hints);
        return this;
    }
    
    /**
     * Specify parameters that come with this builder
     * @param parameters
     * @return
     */
    public AbstractFreeSqlBuilder with(StatementParameters parameters) {
        context.setParameters(parameters);
        return this;
    }
    
    @Override
    public StatementParameters buildParameters() {
        return context.getParameters().buildParameters();
    }
    
    public static Text text(String template) {
        return new Text(template);
    }
    
    public static ClauseList texts(String... templates) {
        ClauseList clauses = new ClauseList();
        for (int i = 0; i < templates.length; i++) {
            clauses.add(text(templates[i]));
        }
        
        return clauses;
    }
    
    public static Keyword keyword(String keyword) {
        return new Keyword(keyword);
    }
    
    /**
     * Create Column clause with given name
     * @param name
     * @return
     */
    public static Column column(String name) {
        return new Column(name);
    }
    
    /**
     * Turn string array into column clauses
     * 
     * @param names
     * @return
     */
    public static ClauseList columns(String... names) {
        ClauseList clauses = new ClauseList();
        for (int i = 0; i < names.length; i++) {
            clauses.add(column(names[i]));
        }
        
        return clauses;
    }
    
    /**
     * Create Table clause with given name
     * @param tableName
     * @return
     */
    public static Table table(String tableName) {
        return new Table(tableName);
    }
    
    /**
     * Add parameter methods definition
     */
    
    private int nextIndex() {
        return context.getParameters().nextIndex();
    }
    
    /**
     * Set parameter
     * 
     * @param sqlType java.sql.Types
     */
    public AbstractFreeSqlBuilder set(String name, Object value, int sqlType) {
        context.getParameters().set(nextIndex(), name, sqlType, value);
        return this;
    }
    
    /**
     * Set parameter when value is not null
     * 
     * @param sqlType java.sql.Types
     */
    public AbstractFreeSqlBuilder setNullable(String name, Object value, int sqlType) {
        return set(value != null, name, value, sqlType);
    }
    
    /**
     * Set parameter when condition is satisfied
     * 
     * @param sqlType java.sql.Types
     */
    public AbstractFreeSqlBuilder set(boolean condition, String name, Object value, int sqlType) {
        set(name, value, sqlType);
        getParameters().when(condition);
        return this;
    }
    
    /**
     * Set in parameter
     * 
     * @param sqlType java.sql.Types
     */
    public AbstractFreeSqlBuilder setIn(String name, List<?> values, int sqlType) throws SQLException {
        context.getParameters().setInParameter(nextIndex(), name, sqlType, values);
        return this;
    }
    
    /**
     * Set in parameter when value is not null and all its elements are not null
     * 
     * @param sqlType java.sql.Types
     */
    public AbstractFreeSqlBuilder setInNullable(String name, List<?> values, int sqlType) throws SQLException {
        setIn(name, values, sqlType);
        getParameters().nullable();
        return this;
    }
    
    /**
     * Set in parameter when condition is satisfied
     * 
     * @param sqlType java.sql.Types
     */
    public AbstractFreeSqlBuilder setIn(boolean condition, String name, List<?> values, int sqlType) throws SQLException {
        setIn(name, values, sqlType);
        getParameters().when(condition);
        return this;
    }
    
    /**
     * Basic append methods definition
     */
    
    /**
     * Basic append method. Parameter value can be String, Clause or Object. It will allow the maximal
     * flexibility for input parameter.
     * 
     * @param template String
     * @return builder itself
     */
    public AbstractFreeSqlBuilder append(String...templates) {
        return append(texts(templates));
    }
    
    /**
     * Append multiple template to the builder. Parameter of String will be append as Text.
     *  
     * If used with Expressions static methods, you can build
     * sql in a very flexible way. Usage like:
     * 
     * append(
     *          text("orderId > ?")
     *          AND,
     *          leftBracket,
     *          NOT, equals("Abc"),
     *          expression("count(1)"),
     *          rightBracket,
     *          OR,
     *          ...
     *       )
     * @param templates
     * @return
     */
    public AbstractFreeSqlBuilder append(Clause... templates) {
        for(Clause template: templates) {
            add(template);
        }

        return this;
    }
    
    /**
     * Append when the condition is met
     * @param condition
     * @param template
     * @return
     */
    public AbstractFreeSqlBuilder appendWhen(boolean condition, String template) {
        return appendWhen(condition, text(template));
    }
    
    /**
     * Append when the condition is met
     * @param condition
     * @param template
     * @return
     */
    public AbstractFreeSqlBuilder appendWhen(boolean condition, Clause template) {
        return condition ? append(template): this;
    }
    
    /**
     * Append template depends on whether the condition is met.
     * @param condition
     * @param template value to be appended when condition is true
     * @param elseTemplate value to be appended when condition is true
     * @return
     */
    public AbstractFreeSqlBuilder appendWhen(boolean condition, String template, String elseTemplate) {
        return appendWhen(condition, text(template), text(elseTemplate));
    }
    
    /**
     * Append template depends on whether the condition is met.
     * @param condition
     * @param template value to be appended when condition is true
     * @param elseTemplate value to be appended when condition is true
     * @return
     */
    public AbstractFreeSqlBuilder appendWhen(boolean condition, Clause template, Clause elseTemplate) {
        return condition ? append(template): append(elseTemplate);
    }
    
    /**
     * Append templates with separator
     * 
     * @param separator
     * @param templates
     * @return
     */
    public AbstractFreeSqlBuilder appendWith(Clause separator, Clause... templates) {
        ClauseList clauses = new ClauseList(templates);
        List<Clause> clauseList = clauses.getList();
        int size = clauseList.size();
        for (Clause clause: clauseList) {
            append(clause);
            if(--size > 0)
                append(separator);    
        }

        return this;
    }
    
    /**
     * Append as column. The column name will be quoted by database specific char.
     * 
     * @param names 
     * @return
     */
    public AbstractFreeSqlBuilder appendColumn(String name) {
        return append(column(name));
    }
    
    /**
     * Append as column with alias. The column name will be quoted by database specific char.
     * 
     * @param names 
     * @param alias
     * @return
     */
    public AbstractFreeSqlBuilder appendColumn(String name, String alias) {
        return append(column(name).as(alias));
    }
    
    /**
     * Append as Table. Same as append(table(tableName)).
     * 
     * The tableName will be replaced by true table name if it is a logic table that allow shard.
     * 
     * @param tableName table name. The table can be sharded
     * @return
     */
    public AbstractFreeSqlBuilder appendTable(String tableName) {
        return append(table(tableName));
    }
    
    /**
     * Append as Table with alias. Same as append(table(tableName)).
     * 
     * The tableName will be replaced by true table name if it is a logic table that allow shard.
     * 
     * @param tableName table name. The table can be sharded
     * @param alias
     * @return
     */
    public AbstractFreeSqlBuilder appendTable(String tableName, String alias) {
        return append(table(tableName).as(alias));
    }
    
    /**
     * Append as Expression. Same as append(expression(expression))
     * 
     * @param expression
     * @return
     */
    public AbstractFreeSqlBuilder appendExpression(String expression) {
        return append(new Expression(expression));
    }

    /**
     * Append a SELECT
     */
    public AbstractFreeSqlBuilder select() {
        return append(SELECT);
    }
    
    /**
     * Build a SELECT column1, column2,...using the giving names
     * 
     * Note: The text value will be wrapped by Column clause.
     * 
     * @param names
     * @param table
     * @return
     */
    public AbstractFreeSqlBuilder select(String... names) {
        return select(columns(names));
    }
    
    /**
     * Build a SELECT column1, column2,...using the giving names
     * 
     * @param columns The type of column can be Column or other clause
     * @param table
     * @return
     */
    public AbstractFreeSqlBuilder select(Clause... columns) {
        return append(SELECT).appendWith(COMMA, columns);
    }
    
    /**
     * Append SELECT *
     * 
     * @return
     */
    public AbstractFreeSqlBuilder selectAll() {
        return append(SELECT).append("*");
    }
    
    /**
     * Append FROM and table for SELECT statement. And if logic DB is sql server, it will 
     * append "WITH (NOLOCK)" by default 
     * 
     * @param columns The type of column can be Column or other clause
     * @param table table name string
     * @return
     */
    public AbstractFreeSqlBuilder from(String table) {
        return from(table(table));
    }
    
    /**
     * Append FROM and table for query. And if logic DB is MS Sql Server, it will 
     * append "WITH (NOLOCK)" by default 
     * 
     * @param columns The type of column can be Column or other clause
     * @param table table name clause
     * @return
     */
    public AbstractFreeSqlBuilder from(Table table) {
        return append(FROM).append(table).append(new SqlServerWithNoLock());
    }
    
    /**
     * Append WHERE
     * @return
     */
    public AbstractFreeSqlBuilder where() {
        return append(WHERE);
    }
            
    /**
     * Append WHERE alone with expressions. 
     * The text value will be treated as Expression
     * 
     * If you want to append 1=1 at the beginning, please use where(includeAll()) 
     * or if you want to select nothing if there is no valid record, please 
     * use where(excludeAll());
     * 
     * @param expressions
     * @return
     */
    public AbstractFreeSqlBuilder where(String... expressions) {
        return where(expressions(expressions));
    }
    
    /**
     * Append WHERE alone with expressions.
     * 
     * If you want to append 1=1 at the beginning, please use where(includeAll()) 
     * or if you want to select nothing if there is no valid record, please 
     * use where(excludeAll());
     * 
     * @param expressions
     * @return
     */
    public AbstractFreeSqlBuilder where(Clause... expressions) {
        return append(WHERE).append(expressions);
    }
    
    /**
     * @return "1=1" AND. To adapt to most batabase
     */
    public static Clause includeAll() {
        return new ClauseList().add(Expressions.expression("1=1"), Expressions.AND);
    }
    
    /**
     * @return "1<>1" OR. To adapt to most batabase
     */
    public static Clause excludeAll() {
        return new ClauseList().add(Expressions.expression("1<>1"), Expressions.OR);
    }
    
    /**
     * Append ORDER BY with column name
     * 
     * @param name
     * @param ascending
     * @return
     */
    public AbstractFreeSqlBuilder orderBy(String name, boolean ascending){
        return append(ORDER_BY).append(column(name)).appendWhen(ascending, ASC, DESC);
    }
    
    /**
     * Append GROUP BY with column name
     * 
     * @param name
     * @return
     */
    public AbstractFreeSqlBuilder groupBy(String name) {
        return append(GROUP_BY).append(column(name));
    }
    
    public AbstractFreeSqlBuilder groupBy(Clause condition) {
        return append(GROUP_BY).append(condition);
    }
    
    /**
     * Append HAVING with condition
     * 
     * @param condition
     * @return
     */
    public AbstractFreeSqlBuilder having(String condition) {
        return append(HAVING).append(condition);
    }
    
    /**
     * Append "("
     * 
     * @return
     */
    public AbstractFreeSqlBuilder leftBracket() {
        return append(Expressions.leftBracket);
    }

    /** Append ")"
     * 
     * @return
     */
    public AbstractFreeSqlBuilder rightBracket() {
        return append(Expressions.rightBracket);
    }
    
    /**
     * Append multiple text as expressions into ().
     * 
     * The text value will be treated as Expression
     * 
     * @param expressions
     * @return
     */
    public AbstractFreeSqlBuilder bracket(String... expressions) {
        return bracket(expressions(expressions));
    }
    
    /**
     * Append multiple expression into ().
     * 
     * @param expressions
     * @return
     */
    public AbstractFreeSqlBuilder bracket(Clause... expressions) {
        return leftBracket().append(expressions).rightBracket();
    }
    
    public AbstractFreeSqlBuilder and() {
        return append(Expressions.AND);
    }
    
    public AbstractFreeSqlBuilder or() {
        return append(Expressions.OR);
    }
    
    public AbstractFreeSqlBuilder not() {
        return append(Expressions.NOT);
    }
    
    /**
     * Join multiple expression with AND.
     * 
     * The text value will be treated as Expression
     * 
     * @param expressions
     * @return
     */
    public AbstractFreeSqlBuilder and(String... expressions) {
        return and(expressions(expressions));
    }

    /**
     * Join multiple expression with AND.
     * 
     * @param expressions
     * @return
     */
    public AbstractFreeSqlBuilder and(Clause... expressions) {
        return appendWith(Expressions.AND, expressions);
    }

    /**
     * Join multiple expression with OR.
     * The text value will be treated as Expression
     * 
     * @param expressions
     * @return
     */
    public AbstractFreeSqlBuilder or(String... expressions) {
        return or(expressions(expressions));
    }
    
    /**
     * Join multiple expression with OR.
     * 
     * @param expressions
     * @return
     */
    public AbstractFreeSqlBuilder or(Clause... expressions) {
        return appendWith(Expressions.OR, expressions);
    }
    
    /**
     * Mark last expression as valid expression if given value is not null.
     * 
     * @param value
     * @return
     */
    public AbstractFreeSqlBuilder nullable(Object value) {
        getLastExpression().nullable(value);
        return this;
    }
    
    /**
     * Mark last ColumnExpression as valid expression if expression's value is not null.
     * 
     * @param value
     * @return
     */
    public AbstractFreeSqlBuilder nullable() {
        Expression last = getLastExpression();

        if(!(last instanceof ColumnExpression))
            throw new IllegalStateException("The last sql segement is not an ColumnExpression.");
        
        ((ColumnExpression)last).nullable();
        return this;    
    }

    /**
     * Mark last expression as valid expression that is to be used in builder when the condition is met.
     * 
     * @param value
     * @return
     */
    public AbstractFreeSqlBuilder when(boolean condition) {
        getLastExpression().when(condition);
        return this;
    }
    
    private Expression getLastExpression() {
        List<Clause> list = getClauseList().getList();
        
        if(list.isEmpty())
            throw new IllegalStateException("There is no exitsing sql segement.");
        
        Clause last = list.get(list.size() - 1);
        
        if(!(last instanceof Expression))
            throw new IllegalStateException("The last sql segement is not an expression.");
        
        return ((Expression)last);
    }

    /**
     * Append = expression using the giving name
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder equal(String name) {
        return append(Expressions.equal(name));
    }
    
    /**
     * Append = expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param value the value of the expression
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder equal(String name, Object value, int sqlType) {
        return append(Expressions.equal(name, value, sqlType));
    }
    
    /**
     * Append <> expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder notEqual(String name) {
        return append(Expressions.notEqual(name));
    }
    
    /**
     * Append <> expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param value the value of the expression@return
     * @param sqlType corresponding sql type of the value
     */
    public AbstractFreeSqlBuilder notEqual(String name, Object value, int sqlType) {
        return append(Expressions.notEqual(name, value, sqlType));
    }
    
    /**
     * Append > expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder greaterThan(String name) {
        return append(Expressions.greaterThan(name));
    }

    /**
     * Append > expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param value the value of the expression@return
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder greaterThan(String name, Object value, int sqlType) {
        return append(Expressions.greaterThan(name, value, sqlType));
    }

    /**
     * Append >= expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder greaterThanEquals(String name) {
        return append(Expressions.greaterThanEquals(name));
    }

    /**
     * Append >= expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param value the value of the expression
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder greaterThanEquals(String name, Object value, int sqlType) {
        return append(Expressions.greaterThanEquals(name, value, sqlType));
    }

    /**
     * Append < expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder lessThan(String name) {
        return append(Expressions.lessThan(name));
    }

    /**
     * Append < expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param value the value of the expression
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder lessThan(String name, Object value, int sqlType) {
        return append(Expressions.lessThan(name, value, sqlType));
    }

    /**
     * Append <= expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder lessThanEquals(String name) {
        return append(Expressions.lessThanEquals(name));
    }

    /**
     * Append <= expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param value the value of the expression
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder lessThanEquals(String name, Object value, int sqlType) {
        return append(Expressions.lessThanEquals(name, value, sqlType));
    }

    /**
     * Append BETWEEN expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder between(String name) {
        return append(Expressions.between(name));
    }
    
    /**
     * Append BETWEEN expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param lowerValue the lower value of the expression
     * @param upperValue the upper value of the expression
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder between(String name, Object lowerValue, Object upperValue, int sqlType) {
        return append(Expressions.between(name, lowerValue, upperValue, sqlType));
    }
    
    /**
     * Append NOT BETWEEN expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder notBetween(String name) {
        return append(Expressions.notBetween(name));
    }
    
    /**
     * Append NOT BETWEEN expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param lowerValue the lower value of the expression
     * @param upperValue the upper value of the expression
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder notBetween(String name, Object lowerValue, Object upperValue, int sqlType) {
        return append(Expressions.notBetween(name, lowerValue, upperValue, sqlType));
    }
    
    /**
     * Append LIKE expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder like(String name) {
        return append(Expressions.like(name));
    }
    
    /**
     * Append LIKE expression using the giving name, type and value.
     * 
     * IMPORTANT NOTE: This method does not add % to any position of value, you need to add % manually in the value
     * You can use like with MatchPattern to indicate dal add % for you.
     * 
     * @param name column name, can not be expression.
     * @param sqlType corresponding sql type of the value
     * @param value the value of the expression
     * @return
     */
    public AbstractFreeSqlBuilder like(String name, String value, int sqlType) {
        return append(Expressions.like(name, value, sqlType));
    }
    
    /**
     * Append LIKE expression using the giving name, , pattern, value and type
     * 
     * @param name column name, can not be expression.
     * @param pattern match pattern
     * @param value the value of the expression
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder like(String name, String value, MatchPattern pattern, int sqlType) {
        return append(Expressions.like(name, value, pattern, sqlType));
    }

    /**
     * Append NOT LIKE expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder notLike(String name) {
        return append(Expressions.notLike(name));
    }
    
    /**
     * Append NOT LIKE expression using the giving name, type and value
     * 
     * IMPORTANT NOTE: This method does not add % to any position of value, you need to add % manually in the value
     * You can use notLike with MatchPattern to indicate dal add % for you.
     * 
     * @param name column name, can not be expression.
     * @param sqlType corresponding sql type of the value
     * @param value the value of the expression
     * @return
     */
    public AbstractFreeSqlBuilder notLike(String name, String value, int sqlType) {
        return append(Expressions.notLike(name, value, sqlType));
    }
    
    /**
     * Append NOT LIKE expression using the giving name, pattern, value and type
     * 
     * @param name column name, can not be expression.
     * @param sqlType corresponding sql type of the value
     * @param pattern match pattern
     * @param value the value of the expression
     * @return
     */
    public AbstractFreeSqlBuilder notLike(String name, String value, MatchPattern pattern, int sqlType) {
        return append(Expressions.notLike(name, value, pattern, sqlType));
    }
    
    /**
     * Append IN expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder in(String name) {
        return append(Expressions.in(name));
    }
    
    /**
     * Append IN expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param values the value of the expression
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder in(String name, Collection<?> values, int sqlType) {
        return append(Expressions.in(name, values, sqlType));
    }
    
    /**
     * Append NOT IN expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder notIn(String name) {
        return append(Expressions.notIn(name));
    }
    
    /**
     * Append NOT IN expression using the giving name, type and value
     * 
     * @param name column name, can not be expression.
     * @param values the value of the expression
     * @param sqlType corresponding sql type of the value
     * @return
     */
    public AbstractFreeSqlBuilder notIn(String name, Collection<?> values, int sqlType) {
        return append(Expressions.notIn(name, values, sqlType));
    }
    
    /**
     * Append IS NULL expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder isNull(String name) {
        return append(Expressions.isNull(name));
    }
    
    /**
     * Append IS NOT NULL expression using the giving name
     * 
     * @param name column name, can not be expression.
     * @return
     */
    public AbstractFreeSqlBuilder isNotNull(String name) {
        return append(Expressions.isNotNull(name));
    }
    
    public static class Text extends Clause {
        private String template;
        public Text(String template) {
            this.template =template;
        }
        
        public String build() {
            return template;
        }
        
        public String toString() {
            return build();
        }
    }
    
    public static class Keyword extends Text {
        public Keyword(String keyword) {
            super(keyword);
        }
    }

    public static class Column extends Clause {
        private String name;
        private String alias;
        public Column(String name) {
            this.name = name;
        }
        
        public Column(String name, String alias) {
            this(name);
            this.alias = alias;
        }
        
        public Column as(String alias) {
            this.alias = alias;
            return this;
        }
        
        public String build() {
            return alias == null ? wrapField(getDbCategory(), name): wrapField(getDbCategory(), name) + " AS " + alias;
        }
    }
    
    public static class Table extends Clause{
        private String rawTableName;
        private String alias;
        private String tableShardId;
        private Object tableShardValue;
        
        public Table(String rawTableName) {
            this.rawTableName = rawTableName;
        }
        
        public Table(String rawTableName, String alias) {
            this(rawTableName);
            this.alias = alias;
        }
        
        public Table inShard(String tableShardId) {
            this.tableShardId = tableShardId;
            return this;
        }
        
        public Table shardValue(String tableShardValue) {
            this.tableShardValue = tableShardValue;
            return this;
        }
        
        public Table as(String alias) {
            this.alias = alias;
            return this;
        }
        
        @Override
        public String build() throws SQLException {
            String logicDbName = getLogicDbName();
            DatabaseCategory dbCategory = getDbCategory();
            String tableName = null;

            if(!isTableShardingEnabled(logicDbName, rawTableName))
                tableName = wrapField(dbCategory, rawTableName);
            else if(tableShardId!= null)
                tableName = wrapField(dbCategory, rawTableName + buildShardStr(logicDbName, tableShardId));
            else if(tableShardValue != null) {
                tableName = wrapField(dbCategory, rawTableName + buildShardStr(logicDbName, locateTableShardId(logicDbName, rawTableName, new DalHints().setTableShardValue(tableShardValue), null, null)));
            }else
                tableName = wrapField(dbCategory, rawTableName + buildShardStr(logicDbName, locateTableShardId(logicDbName, rawTableName, getHints(), getParameters(), null)));
            
            return alias == null ? tableName : tableName + " AS " + alias;
        }
    }
    
    /**
     * Special Clause that only works when DB is sql server. It will append WITH (NOLOCK) after table
     * name against guideline.
     * @author jhhe
     *
     */
    private static class SqlServerWithNoLock extends Text {
        private static final String SQL_SERVER_NOLOCK = "WITH (NOLOCK)";
        
        public SqlServerWithNoLock() {
            super(SQL_SERVER_NOLOCK);
        }

        public String build() {
            return getDbCategory() == DatabaseCategory.SqlServer ? super.build() : EMPTY;
        }
    }
}
