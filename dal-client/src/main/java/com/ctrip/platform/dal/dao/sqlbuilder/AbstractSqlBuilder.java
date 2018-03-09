package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.Expressions.Expression;

/**
 * Parent of AbstractFreeSqlBuilder and AbstractTableSqlBuilder
 * 
 * @author jhhe
 */
public abstract class AbstractSqlBuilder implements SqlBuilder {
    private static final String SPACE = " ";
    
    private BuilderContext context;
    private ClauseList clauses = new ClauseList();
    private boolean enableSmartSpaceSkipping= true;

    public AbstractSqlBuilder(BuilderContext context) {
        clauses.setContext(context);
        this.context = context;
    }
    
    protected BuilderContext getContext() {
        return context;
    }
    
    protected DatabaseCategory getDbCategory() {
        return context.getDbCategory();
    }
    
    protected void setParameters(StatementParameters parameters) {
        context.setParameters(parameters);
    }

    protected StatementParameters getParameters() {
        return context.getParameters();
    }

    /**
     * Default logic for building the sql statement.
     * 
     * It will append where and check if the value is start of "and" or "or", of so, the leading 
     * "and" or "or" will be removed.
     */
    @Override
    public String build() {
        try {
            List<Clause> clauseList = clauses.getList();
            
            clauseList = meltdown(clauseList);
            
            return concat(clauseList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Disable the auto space removing around bracket and before COMMA
     */
    public AbstractSqlBuilder disableSpaceSkipping() {
        enableSmartSpaceSkipping = false;
        return this;
    }
    
    /**
     * Enable the auto space removing around bracket and before COMMA
     */
    public AbstractSqlBuilder enableSpaceSkipping() {
        enableSmartSpaceSkipping = true;
        return this;
    }
    
    public void add(Clause clause) {
        clauses.add(clause);
    }
    
    protected ClauseList getClauseList() {
        return clauses;
    }
    
    /**
     * If there is COMMA, then the leading space will not be appended.
     * If there is bracket, then both leading and trailing space will be omitted.
     * 
     * @param clauseList
     * @return
     * @throws SQLException
     */
    private String concat(List<Clause> clauseList) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < clauseList.size(); i ++) {
            Clause curClause = clauseList.get(i);
            Clause nextClause = (i == clauseList.size() - 1) ? null: clauseList.get(i+1);
            
            sb.append(curClause.build());
            
            if(skipSpaceInsertion(curClause, nextClause))
                continue;
            
            sb.append(SPACE);
        }
        
        return sb.toString().trim();
    }
    
    private List<Clause> meltdown(List<Clause> clauseList) {
        LinkedList<Clause> filtered = new LinkedList<>();
        
        for(Clause entry: clauseList) {
            if(isExpression(entry) && isNull(entry)){
                meltDownNullValue(filtered);
                continue;
            }

            if(isBracket(entry) && !isLeft(entry)){
                if(meltDownRightBracket(filtered))
                    continue;
            }
            
            // AND/OR
            if(isOperator(entry) && !isNot(entry)) {
                if(meltDownAndOrOperator(filtered))
                    continue;
            }
            
            filtered.add(entry);
        }
        
        return filtered;
    }
    
    /**
     * Builder will not insert space if enableSmartSpaceSkipping is enabled and:
     * 1. current cuase is operator(AND, OR, NOT)
     * 2. current cuase is left bracket
     * 3. next clause is right bracket or COMMA
     */
    private boolean skipSpaceInsertion(Clause curClause, Clause nextClause) {
        if(!enableSmartSpaceSkipping)
            return false;
        
        if(isOperator(curClause))
            return false;
        // if after "("
        if(isBracket(curClause) && isLeft(curClause))
            return true;
        
        // reach the end
        if(nextClause == null)
            return true;

        if(isBracket(nextClause) && !isLeft(nextClause))
            return true;
        
        return isComma(nextClause);
    }
    
    private void meltDownNullValue(LinkedList<Clause> filtered) {
        if(filtered.isEmpty())
            return;

        while(!filtered.isEmpty()) {
            Clause entry = filtered.getLast();
            // Remove any leading AND/OR/NOT (NOT is both operator and clause)
            if(isOperator(entry)) {
                filtered.removeLast();
            }else
                break;
        }
    }

    private boolean meltDownRightBracket(LinkedList<Clause> filtered) {
        int bracketCount = 1;
        while(!filtered.isEmpty()) {
            Clause entry = filtered.getLast();
            // One ")" only remove one "("
            if(isBracket(entry) && isLeft(entry) && bracketCount == 1){
                filtered.removeLast();
                bracketCount--;
            } else if(isOperator(entry)) {// Remove any leading AND/OR/NOT (NOT is both operator and clause)
                filtered.removeLast();
            } else
                break;
        }
        
        return bracketCount == 0? true : false;
    }

    private boolean meltDownAndOrOperator(LinkedList<Clause> filtered) {
        // If it is the first element
        if(filtered.isEmpty())
            return true;

        Clause entry = filtered.getLast();

        // The last one is "("
        if(isBracket(entry))
            return isLeft(entry);
            
        // AND/OR/NOT AND/OR
        if(isOperator(entry)) {
            return true;
        }
        
        // If it is expression. 
        if(isExpression(entry))
            return false;

        // Reach the beginning of the meltdown section
        return true;
    }
    /**
     * @return if current clause is comma
     */
    private boolean isComma(Clause clause) {
        return clause == AbstractFreeSqlBuilder.COMMA;
    }

    /**
     * @return if current clause is an expression
     */
    private boolean isExpression(Clause clause) {
        return clause instanceof Expressions.Expression;
    }

    /**
     * @return if current clause is null
     */
    private boolean isNull(Clause clause) {
        Expression exp = (Expression)clause;
        return exp.isInvalid();
    }
    
    /**
     * @return if current clause is a bracket
     */
    private boolean isBracket(Clause clause) {
        return clause instanceof Expressions.Bracket;
    }
    
    /**
     * @return if current clause is left bracket
     */
    private boolean isLeft(Clause clause) {
        return clause == Expressions.leftBracket;
    }
    
    /**
     * @return if current clause is an operator
     */
    private boolean isOperator(Clause clause) {
        return clause instanceof Expressions.Operator;
    }
    
    /**
     * @return if current clause is NOT operator
     */
    private boolean isNot(Clause clause) {
        return clause == Expressions.NOT;
    }    
}
