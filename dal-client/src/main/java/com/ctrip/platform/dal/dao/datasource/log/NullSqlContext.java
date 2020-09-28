package com.ctrip.platform.dal.dao.datasource.log;

import com.ctrip.platform.dal.dao.datasource.ValidationResult;

import java.util.Set;

/**
 * @author c7ch23en
 */
public class NullSqlContext implements SqlContext {

    @Override
    public void populateDbName(String dbName) {}

    @Override
    public void populateDbZone(String dbZone) {}

    @Override
    public void populateCaller() {}

    @Override
    public void populateCaller(String callerClass, String callerMethod) {}

    @Override
    public void populateOperationType(OperationType operation) {}

    @Override
    public void populateTables(Set<String> tables) {}

    @Override
    public void startExecution() {}

    @Override
    public void populateValidationResult(ValidationResult result) {}

    @Override
    public void endExecution() {}

    @Override
    public void endExecution(Throwable errorIfAny) {}

    @Override
    public SqlContext fork() {
        return this;
    }

}
