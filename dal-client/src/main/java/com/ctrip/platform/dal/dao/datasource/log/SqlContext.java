package com.ctrip.platform.dal.dao.datasource.log;

import com.ctrip.platform.dal.dao.datasource.ValidationResult;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface SqlContext {

    void populateDbName(String dbName);

    void populateDbZone(String dbZone);

    void populateCaller();

    void populateCaller(String callerClass, String callerMethod);

    void populateOperationType(OperationType operation);

    void populateTables(Set<String> tables);

    void startExecution();

    void populateValidationResult(ValidationResult result);

    void endExecution();

    void endExecution(Throwable errorIfAny);

    SqlContext fork();

    void populateReadStrategy(String readStrategy);

    void populateQueryRows(int rows);

    void populateDatabase(String database);

    void populateSql(String sql);

}
