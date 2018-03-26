package com.ctrip.platform.dal.dao.sqlbuilder;

import java.util.Objects;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

/**
 * Context information for sql building
 * 
 * @author jhhe
 */
public class BuilderContext {
    private String logicDbName;
    private DatabaseCategory dbCategory;
    private DalHints hints;
    private StatementParameters parameters = new StatementParameters();

    public DatabaseCategory getDbCategory() {
        return dbCategory;
    }

    public String getLogicDbName() {
        return logicDbName;
    }

    public DalHints getHints() {
        return hints;
    }

    public StatementParameters getParameters() {
        return parameters;
    }

    public void setLogicDbName(String logicDbName) {
        Objects.requireNonNull(logicDbName, "logicDbName can not be NULL");
        // Check if exist
        this.dbCategory = DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getDatabaseCategory();
        this.logicDbName = logicDbName;
    }

    public void setDbCategory(DatabaseCategory dbCategory) {
        Objects.requireNonNull(dbCategory, "dbCategory can not be NULL");
        if (logicDbName == null)
            this.dbCategory = dbCategory;
        else {
            if (this.dbCategory != dbCategory)
                throw new IllegalArgumentException("The dbCategory does not match logic DB " + logicDbName);
        }
    }

    public void setHints(DalHints hints) {
        Objects.requireNonNull(hints, "DalHints can't be null.");
        this.hints = hints.clone();
    }

    public void setParameters(StatementParameters parameters) {
        Objects.requireNonNull(parameters, "parameters can't be null.");
        if (this.parameters == parameters)
            return;

        if (this.parameters != null && this.parameters.size() > 0)
            throw new IllegalStateException("The parameters has already be set and processed. "
                    + "You can only set parameters at the begining of build");

        this.parameters = parameters;
    }
}
