package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;


/**
 * Base class for sql clause to build piece of the final statement.
 * 
 * Because certain information may not be ready during clause append,
 * the build process is separated into two phases. One is preparing: setBuilderCOntext(), this 
 * is invoked immediately after clause is been constructed and added. The other is build(), which 
 * actually build part of the final sql.
 * 
 * @author jhhe
 *
 */
public abstract class Clause {
    private BuilderContext context;
    
    /**
     * @return the final sql segment
     * @throws SQLException
     */
    public abstract String build() throws SQLException;
    
    public void setContext(BuilderContext context) {
        this.context = context;
    }
    
    public DatabaseCategory getDbCategory() {
        return context.getDbCategory();
    }

    public String getLogicDbName() {
        return context.getLogicDbName();
    }

    public DalHints getHints() {
        return context.getHints();
    }

    public StatementParameters getParameters() {
        return context.getParameters();
    }
    
    /**
     * Called when clause added to builder and setContext() is called
     */
    public void postAppend() {
    }
}
