package com.ctrip.platform.dal.dao.sqlbuilder;

import static com.ctrip.platform.dal.dao.sqlbuilder.AbstractTableSqlBuilder.wrapField;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;

/**
 * A factory of static expression methods that can be sued with free sql builder, especially in bracket(...)
 * 
 * @author jhhe
 *
 */
public class Expressions {
    public static final NullExpression NULL = new NullExpression();
    
    public static final ImmutableExpression TRUE = new ImmutableExpression("TRUE");
    
    public static final ImmutableExpression FALSE = new ImmutableExpression("FALSE");
    
    public static final Operator AND = new Operator("AND");
    
    public static final Operator OR = new Operator("OR");
    
    public static final Operator NOT = new Operator("NOT");
    
    public static final Bracket leftBracket = new Bracket(true);

    public static final Bracket rightBracket = new Bracket(false);
    
    public static ColumnExpression columnExpression(String template, String name) {
        return new ColumnExpression(template, name);
    }
    
    /**
     * Create Expression clause with the given template
     * @param template
     * @return
     */
    public static Expression expression(String template) {
        return new Expression(template);
    }

    public static Clause expressions(String... templates) {
        ClauseList clauses = new ClauseList();
        for(String template: templates)
            clauses.add(expression(template));
        return clauses;
    }

    public static Expression expression(boolean condition, String template) {
        return condition ? new Expression(template) : NULL;
    }
    
    public static Expression expression(boolean condition, String template, String elseTemplate) {
        return condition ? expression(template) : expression(elseTemplate);
    }
    
    public static Clause bracket(String... templates) {
        return bracket(expressions(templates));
    }
    
    public static Clause bracket(Clause... clauses) {
        ClauseList list = new ClauseList();
        return list.add(leftBracket).add(clauses).add(rightBracket);
    }

    public static ColumnExpression equal(String name) {
        return columnExpression("%s = ?", name);
    }
    
    public static ColumnExpression equal(String name, Object value, int sqlType) {
        return equal(name).set(value, sqlType);
    }
    
    public static ColumnExpression notEqual(String name) {
        return columnExpression("%s <> ?", name);
    }
    
    public static ColumnExpression notEqual(String name, Object value, int sqlType) {
        return notEqual(name).set(value, sqlType);
    }
    
    public static ColumnExpression greaterThan(String name) {
        return columnExpression("%s > ?", name);
    }

    public static ColumnExpression greaterThan(String name, Object value, int sqlType) {
        return greaterThan(name).set(value, sqlType);
    }

    public static ColumnExpression greaterThanEquals(String name) {
        return columnExpression("%s >= ?", name);
    }

    public static ColumnExpression greaterThanEquals(String name, Object value, int sqlType) {
        return greaterThanEquals(name).set(value, sqlType);
    }

    public static ColumnExpression lessThan(String name) {
        return columnExpression("%s < ?", name);
    }

    public static ColumnExpression lessThan(String name, Object value, int sqlType) {
        return lessThan(name).set(value, sqlType);
    }

    public static ColumnExpression lessThanEquals(String name) {
        return columnExpression("%s <= ?", name);
    }

    public static ColumnExpression lessThanEquals(String name, Object value, int sqlType) {
        return lessThanEquals(name).set(value, sqlType);
    }

    public static BetweenExpression between(String name) {
        return new BetweenExpression(name);
    }
    
    public static BetweenExpression between(String name, Object lowerValue, Object upperValue, int sqlType) {
        BetweenExpression between = new BetweenExpression(name);
        between.set(lowerValue, sqlType);
        return between.setUpperValue(upperValue);
    }
    
    public static BetweenExpression notBetween(String name) {
        return new NotBetweenExpression(name);
    }
    
    public static BetweenExpression notBetween(String name, Object lowerValue, Object upperValue, int sqlType) {
        NotBetweenExpression notBetween = new NotBetweenExpression(name);
        notBetween.set(lowerValue, sqlType);
        return notBetween.setUpperValue(upperValue);
    }
    
    public static ColumnExpression like(String name) {
        return columnExpression("%s LIKE ?", name);
    }
    
    public static ColumnExpression like(String name, String value, int sqlType) {
        return like(name).set(value, sqlType);
    }
    
    public static ColumnExpression like(String name, String value, MatchPattern pattern, int sqlType) {
        return like(name).set(pattern.process(value), sqlType);
    }
    
    public static ColumnExpression notLike(String name) {
        return columnExpression("%s NOT LIKE ?", name);
    }
    
    public static ColumnExpression notLike(String name, String value, int sqlType) {
        return notLike(name).set(value, sqlType);
    }
    
    public static ColumnExpression notLike(String name, String value, MatchPattern pattern, int sqlType) {
        return notLike(name).set(pattern.process(value), sqlType);
    }
    
    public static ColumnExpression in(String name) {
        return new InExpression(name);
    }
    
    public static ColumnExpression in(String name, Collection<?> values, int sqlType) {
        return in(name).set(values, sqlType);
    }
    
    public static ColumnExpression notIn(String name) {
        return new NotInExpression(name);
    }
    
    public static ColumnExpression notIn(String name, Collection<?> values, int sqlType) {
        return notIn(name).set(values, sqlType);
    }
    
    public static ColumnExpression isNull(String name) {
        return columnExpression("%s IS NULL", name);
    }
    
    public static ColumnExpression isNotNull(String name) {
        return columnExpression("%s IS NOT NULL", name);
    }
    
    public static class Operator extends Clause {
        private String operator;
        public Operator(String operator) {
            this.operator = operator;
        }
        
        @Override
        public String build() {
            return operator;
        }
    }
    
    public static class Bracket extends Clause {
        private boolean left;
        public Bracket(boolean isLeft) {
            left = isLeft;
        }

        public String build() {
            return left? "(" : ")";
        }
        
        public boolean isLeft() {
            return left;
        }
    }
    
    public static class Expression extends Clause {
        protected String template;
        private boolean invalid = false;
        
        public Expression(String template) {
            this.template = template;
        }
        
        public Expression nullable(Object o) {
            when(o != null);
            return this;
        }
        
        public Expression when(boolean condition) {
            invalid = !condition;
            return this;
        }
        
        
        public boolean isInvalid() {
            return invalid;
        }
        
        public boolean isValid() {
            return !invalid;
        }
        
        public String build() {
            if(invalid)
                throw new IllegalStateException("This expression is invalid and should be removed instead of build");
            
            return template;
        }
    }
    
    public static class ImmutableExpression extends Expression {
        public ImmutableExpression(String template) {
            super(template);
        }
        public Expression nullable(Object o) {
            return this;
        }
        
        public Expression when(boolean condition) {
            return this;
        }
        
        
        public boolean isInvalid() {
            return false;
        }
    }
    
    public static class ColumnExpression extends Expression {
        protected String columnName;
        // Used as a flag to decide if set() is called or not
        protected Integer sqlType;
        protected Object value;
        protected StatementParameter parameter;
        
        public ColumnExpression(String template, String columnName) {
            super(template);
            Objects.requireNonNull(columnName, "column name can not be null");
            this.columnName = columnName;
        }
        
        public String getColumnName() {
            return columnName;
        }

        public ColumnExpression set(Object value, int sqlType) {
            if(parameter != null)
                throw new IllegalStateException("An expression can not be set twice!");
            
            this.sqlType = sqlType;
            this.value = value;
            return this;
        }
        
        public void postAppend() {
            if(sqlType == null)
                return;
            
            StatementParameters parameters = getParameters();
            parameter = new StatementParameter(parameters.nextIndex(), sqlType, value).setName(columnName);
            parameter.when(isValid());
            parameters.add(parameter);            
        }
        
        public ColumnExpression nullable() {
            if(sqlType == null)
                throw new IllegalStateException("This operation is only avaliable when set(sqlType, value) is called!");
            
            nullable(value);
            return this;
        }
        
        public Expression when(boolean condition) {
            super.when(condition);
            if(parameter != null)
                parameter.when(condition);
            return this;
        }

        public String build() {
            String template = super.build();
            return columnName == null ? template : String.format(template, wrapField(getDbCategory(), columnName));
        }
    }
    
    public static class BetweenExpression extends ColumnExpression {
        private Object upperValue;
        private StatementParameter upperParameter;
        
        public BetweenExpression(String name) {
            super("%s BETWEEN ? AND ?", name);
        }
        
        public ColumnExpression nullable() {
            when(value != null && upperValue != null);
            return this;
        }
        
        public BetweenExpression setUpperValue(Object upperValue) {
            if(upperParameter != null)
                throw new IllegalStateException("An expression can not be set twice!");

            this.upperValue = upperValue;
            return this;
        }
        
        public void postAppend() {
            super.postAppend();
            if(sqlType == null)
                return;
            
            StatementParameters parameters = getParameters();
            upperParameter = new StatementParameter(parameters.nextIndex(), sqlType, value).setName(columnName);
            parameters.add(upperParameter);
            upperParameter.when(isValid());            
        }
        
        public Expression when(boolean condition) {
            super.when(condition);
            if(upperParameter != null)
                upperParameter.when(condition);
            return this;
        }
    }
    
    public static class NotBetweenExpression extends BetweenExpression {
        public NotBetweenExpression(String name) {
            super(name);
            template = "%s NOT BETWEEN ? AND ?";
        }
    }
    
    public static class InExpression extends ColumnExpression {
        public InExpression(String name) {
            super("%s IN ( ? )", name);
        }
        
        public ColumnExpression nullable() {
            when(!StatementParameter.isNullInParams((List<?>)value));
            return this;
        }
        
        public void postAppend() {
            super.postAppend();
            if(sqlType == null)
                return;

            getParameters().getLast().setInParam(true);
        }
    }
    
    public static class NotInExpression extends InExpression {
        public NotInExpression(String name) {
            super(name);
            template = "%s NOT IN ( ? )";
        }
    }
    
    /**
     * This clause is just a placeholder that can be removed from the expression clause list.
     * @author jhhe
     *
     */
    public static class NullExpression extends Expression {
        public NullExpression() {
            super("");
        }
        
        public boolean isInvalid() {
            return true;
        }
        
        @Override
        public String build() {
            return "";
        }
    }
}
