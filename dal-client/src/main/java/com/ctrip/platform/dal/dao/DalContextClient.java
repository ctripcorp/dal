package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.task.DalTaskContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by lilj on 2018/7/27.
 */
public interface DalContextClient extends DalClient {
    /**
     * Query against the given sql and parameters. The result set will be
     * processed by the given extractor
     *
     * @param sql The sql statement to be executed
     * @param parameters A container that holds all the necessary parameters
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param extractor helper used to convert result to desired type
     * @return the extracted result from the result set
     * @throws SQLException when things going wrong during the execution
     */
    <T> T query(String sql, StatementParameters parameters, DalHints hints,
                DalResultSetExtractor<T> extractor, DalTaskContext dalTaskContext) throws SQLException;

    /**
     * Query against the given sql and parameters. The sql is combined by multiple select clause,
     * the result will be extracted one by one by the given extractors list
     *
     * @param sql The sql statement to be executed
     * @param parameters A container that holds all the necessary parameters
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param extractors helper used to convert result to desired type
     * @return the extracted result from the result set
     * @throws SQLException when things going wrong during the execution
     */
    List<?> query(String sql, StatementParameters parameters, DalHints hints,
                  List<DalResultSetExtractor<?>> extractors, DalTaskContext dalTaskContext) throws SQLException;
    /**
     * Update against the given sql and parameters.
     *
     * @param sql The sql statement to be executed
     * @param parameters A container that holds all the necessary parameters
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @return how many rows been affected
     * @throws SQLException when things going wrong during the execution
     */
    int update(String sql, StatementParameters parameters, DalHints hints, DalTaskContext dalTaskContext)
            throws SQLException;

    /**
     * Batch update for given sqls.The default behavior is execute in transaction.
     * You can overwrite this by set forceAutoCommit in hints.
     *
     * @param sqls List of sql statement to be executed
     * @param hints
     * 			Additional parameters that instruct how DAL Client perform database operation.
     * 			when hints set forceAutoCommit the connection auto commit will be true.
     * @return how many rows been affected for each of the sql
     * @throws SQLException when things going wrong during the execution
     */
    int[] batchUpdate(String[] sqls, DalHints hints, DalTaskContext dalTask) throws SQLException;
    /**
     * Batch update for the given sql with all the given parameters in parametersList.
     * The default behavior is execute in transaction.
     * You can overwrite this by set forceAutoCommit in hints.
     * @param sql The sql statement to be executed
     * @param parametersList Container that holds parameters
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @return how many rows been affected for each of parameters
     * @throws SQLException when things going wrong during the execution
     */
    int[] batchUpdate(String sql, StatementParameters[] parametersList,
                      DalHints hints, DalTaskContext dalTask) throws SQLException;

    /**
     * Call stored procedure.
     *
     * @param callString sql statement represent the stored procedure
     * @param parameters A container that holds all the necessary parameters
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @return the returned update count and result set in order
     * @throws SQLException when things going wrong during the execution
     */
    Map<String, ?> call(String callString, StatementParameters parameters,
                        DalHints hints, DalTaskContext dalTask) throws SQLException;

    /**
     * Call stored procedure in batch mode.
     * The default behavior is execute in transaction.
     * You can overwrite this by set forceAutoCommit in hints.
     * @param callString sql statement represent the stored procedure
     * @param parametersList Container that holds parameters
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @return how many rows been affected for each of parameters
     * @throws SQLException when things going wrong during the execution
     */
    int[] batchCall(String callString, StatementParameters[] parametersList,
                    DalHints hints, DalTaskContext dalTask) throws SQLException;
}
