package com.ctrip.platform.dal.dao.datasource.log;

import com.ctrip.platform.dal.dao.StatementParameters;
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

    @Override
    public void populateReadStrategy(String readStrategy) {

    }

    @Override
    public void populateQueryRows(int rows) {

    }

    @Override
    public void populateDatabase(String database) {

    }

    @Override
    public void populateSql(String sql) {

    }

    @Override
    public void populateSqlTransaction(long millionSeconds) {

    }

    @Override
    public void populateParameters(StatementParameters parameters) {

    }
}
