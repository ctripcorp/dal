package com.ctrip.platform.dal.dao.sqlbuilder;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

/**
 * A very flexible SQL builder that can build complete SQL alone with parameters for update purpose.
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
 */
public class FreeUpdateSqlBuilder extends AbstractFreeSqlBuilder {
    public static final String INSERT_INTO = "INSERT INTO";
    public static final String VALUES = "VALUES";

    public static final String DELETE_FROM = "DELETE FROM";
    public static final String UPDATE = "UPDATE";
    public static final String SET = "SET";
    
    /**
     * @deprecated you should use FreeUpdateSqlBuilder() instead
     * @param dbCategory
     */
	public FreeUpdateSqlBuilder(DatabaseCategory dbCategory) {
	    setDbCategory(dbCategory);
	}
	
	public FreeUpdateSqlBuilder(){}

	/**
	 * If there is IN parameter, no matter how many values in the IN clause, the IN clause only need to 
	 * contain one "?".
	 * E.g. UPDATE ... WHERE id IN ?
	 * 
	 * This method is the same with super.append(String);
	 * @param updateSqlTemplate
	 * @return
	 */
	public FreeUpdateSqlBuilder setTemplate(String updateSqlTemplate) {
		append(updateSqlTemplate);
		return this;
	}
	
	/**
	 * Append INSERT INTO + table name.
	 * @param tableName
	 * @return
	 */
	public FreeUpdateSqlBuilder insertInto(String tableName) {
	    return insertInto(table(tableName));
	}
	
    /**
     * Append INSERT INTO + table name.
     * @param tableName
     * @return FreeUpdateSqlBuilder instance. Because the following append will be values(...)
     */
	public FreeUpdateSqlBuilder insertInto(Table tableName) {
        append(INSERT_INTO);
        append(tableName);
        return this;
    }
	
	/**
	 * Append column names and "?" for INSERT statement
	 * @param columnNames
	 * @return
	 */
	public FreeUpdateSqlBuilder values(String...columnNames) {
	    append(Expressions.leftBracket);
        StringBuilder valueFields = new StringBuilder();
        
        for (int i = 0; i < columnNames.length; i++) {
            appendColumn(columnNames[i]);
            valueFields.append(PLACE_HOLDER);
            if(i != columnNames.length -1) {
                append(COMMA);
                valueFields.append(", ");
            }
        }
        
        append(Expressions.rightBracket, text(VALUES), Expressions.bracket(text(valueFields.toString())));
        
        return this;
    }
	
    /**
     * Append DELETE FROM + table name.
     * @param tableName
     * @return
     */
    public FreeUpdateSqlBuilder deleteFrom(String tableName) {
        return deleteFrom(table(tableName));
    }
    
    /**
     * Append DELETE FROM + table name.
     * @param tableName
     * @return
     */
    public FreeUpdateSqlBuilder deleteFrom(Table tableName) {
        append(DELETE_FROM);
        append(tableName);
        return this;
    }
    
    /**
     * Append UPDATE + table name.
     * @param tableName
     * @return
     */
    public FreeUpdateSqlBuilder update(String tableName) {
        return update(table(tableName));
    }

    /**
     * Append UPDATE + table name.
     * @param tableName
     * @return
     */
    public FreeUpdateSqlBuilder update(Table tableName) {
        append(UPDATE);
        append(tableName);
        return this;
    }
    
    /**
     * Append SET name1=?, name2=?... for UPDATE statement.
     * @param columnNames
     * @return
     */
    public FreeUpdateSqlBuilder set(String...columnNames) {
        append(SET);
        for (int i = 0; i < columnNames.length; i++) {
            append(Expressions.columnExpression("%s=?", columnNames[i]));
            if(i != columnNames.length -1)
                append(COMMA);
        }
        
        return this;
    }
}
