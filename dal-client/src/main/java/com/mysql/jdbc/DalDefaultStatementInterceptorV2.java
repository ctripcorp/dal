package com.mysql.jdbc;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.Properties;

public class DalDefaultStatementInterceptorV2 implements StatementInterceptorV2 {


    @Override
    public void init(Connection conn, Properties props) throws SQLException {
    }

    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException) throws SQLException {
        if (!noGoodIndexUsed && isSocketTimeOutException(statementException)) {
            resetCancelState(interceptedStatement);
            throw statementException;
        }

        return null;
    }

    protected boolean isSocketTimeOutException(SQLException sqlEx) {
        Throwable t1 = sqlEx;
        while (t1 != null && !(t1 instanceof SQLException)) {
            t1 = t1.getCause();
        }

        return sqlEx instanceof CommunicationsException;
    }

    protected void resetCancelState(Statement interceptedStatement) throws SQLException {
        if (interceptedStatement == null)
            return;

        StatementImpl cancel = interceptedStatement.unwrap(StatementImpl.class);
        synchronized (cancel.cancelTimeoutMutex) {
            if (cancel.wasCancelled) {
                cancel.resetCancelledState();
            }
        }
    }

}
