package com.ctrip.platform.dal.common.enums;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.AbstractDalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalCallableStatement;
import com.ctrip.platform.dal.dao.markdown.ErrorContext;
import com.ctrip.platform.dal.exceptions.DalParameterException;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import org.apache.commons.lang.StringUtils;

public enum DatabaseCategory {
    MySql("%s=IFNULL(?,%s) ", "CURRENT_TIMESTAMP", new int[] {1043, 1159, 1161},
            new int[] {1021, 1037, 1038, 1039, 1040, 1041, 1154, 1158, 1160, 1189, 1190, 1205, 1218, 1219, 1220}) {

        private static final String READONLY_EXCEPTION_SQL_STATE = "HY000";
        private static final int READONLY_EXCEPTION_ERROR_CODE = 1290;
        private static final String READONLY_EXCEPTION_MESSAGE_KEYWORD = "--read-only";

        private AbstractDalPropertiesLocator mySqlDalPropertiesLocator =
                DalPropertiesManager.getInstance().getMySqlDalPropertiesLocator();

        public String quote(String fieldName) {
            return "`" + fieldName + "`";
        }

        public boolean isTimeOutException(ErrorContext ctx) {
            return ctx.getExType().toString().equalsIgnoreCase(MySQLTimeoutException.class.toString());
        }

        public String buildList(String effectiveTableName, String columns, String whereExp) {
            return String.format("SELECT %s FROM %s WHERE %s", columns, effectiveTableName, whereExp);
        }

        public String buildTop(String effectiveTableName, String columns, String whereExp, int count) {
            return String.format("SELECT %s FROM %s WHERE %s LIMIT %d", columns, effectiveTableName, whereExp, count);
        }

        public String buildPage(String effectiveTableName, String columns, String whereExp, int start, int count) {
            return String.format("SELECT %s FROM %s WHERE %s LIMIT %d, %d", columns, effectiveTableName, whereExp,
                    start, count);
        }

        public String buildPage(String selectSqlTemplate, int start, int count) {
            return selectSqlTemplate + " limit "+start+", "+count; //we cannot use String.format here because selectSqlTemplate may contains "%"
        }

        // SQLError.SQL_STATE_COMMUNICATION_LINK_FAILURE
        public boolean isSpecificException(SQLException exception) {
            Map<String, ErrorCodeInfo> map = mySqlDalPropertiesLocator.getErrorCodes();
            return matchSpecificError(exception, map) || isReadonlyException(exception);
        }

        private boolean isReadonlyException(SQLException exception) {
            if (exception != null && READONLY_EXCEPTION_SQL_STATE.equalsIgnoreCase(exception.getSQLState()) &&
                    READONLY_EXCEPTION_ERROR_CODE == exception.getErrorCode()) {
                String message = exception.getMessage();
                return message != null && message.toLowerCase().contains(READONLY_EXCEPTION_MESSAGE_KEYWORD);
            }
            return false;
        }
    },

    SqlServer("%s=ISNULL(?,%s) ", "getDate()", new int[] {-2, 233, 845, 846, 847, 1421},
            new int[] {2, 53, 701, 802, 945, 1204, 1222}) {

        private AbstractDalPropertiesLocator sqlServerDalPropertiesLocator =
                DalPropertiesManager.getInstance().getSqlServerDalPropertiesLocator();

        public String quote(String fieldName) {
            return "[" + fieldName + "]";
        }

        public String buildList(String effectiveTableName, String columns, String whereExp) {
            return String.format("SELECT %s FROM %s WITH (NOLOCK) WHERE %s", columns, effectiveTableName, whereExp);
        }

        public boolean isTimeOutException(ErrorContext ctx) {
            return ctx.getMsg().startsWith("The query has timed out") || ctx.getMsg().startsWith("查询超时");
        }

        public String buildTop(String effectiveTableName, String columns, String whereExp, int count) {
            return String.format("SELECT TOP %d %s FROM %s WITH (NOLOCK) WHERE %s", count, columns, effectiveTableName,
                    whereExp);
        }

        public String buildPage(String effectiveTableName, String columns, String whereExp, int start, int count) {
            return String.format("SELECT %s FROM %s WITH (NOLOCK) WHERE %s OFFSET %d ROWS FETCH NEXT %d ROWS ONLY",
                    columns, effectiveTableName, whereExp, start, count);
        }

        public String buildPage(String selectSqlTemplate, int start, int count) {
            return selectSqlTemplate + " OFFSET "+start+" ROWS FETCH NEXT "+count+" ROWS ONLY";//we cannot use String.format here because selectSqlTemplate may contains "%"
        }

        public void setObject(CallableStatement statement, StatementParameter parameter) throws SQLException {
            if (parameter.getValue() != null && parameter.getSqlType() == SQL_SERVER_TYPE_TVP) {
                CallableStatement callableStatement = statement;
                if (statement instanceof DalCallableStatement) {
                    callableStatement = ((DalCallableStatement) statement).getCallableStatement();
                }
                SQLServerCallableStatement sqlsvrStatement = (SQLServerCallableStatement) callableStatement;
                sqlsvrStatement.setStructured(parameter.getIndex(), parameter.getName(),
                        (SQLServerDataTable) parameter.getValue());
            } else {
                super.setObject(statement, parameter);
            }
        }

        // SQLServerException.EXCEPTION_XOPEN_CONNECTION_FAILURE
        public boolean isSpecificException(SQLException exception) {
            Map<String, ErrorCodeInfo> map = sqlServerDalPropertiesLocator.getErrorCodes();
            return matchSpecificError(exception, map);
        }
    },

    Oracle("%s=NVL(?,%s) ", "SYSTIMESTAMP", new int[] {-1}, new int[] {-1}) {

        public String quote(String fieldName) {
            return fieldName;// "\"" + fieldName + "\"";
        }

        public boolean isTimeOutException(ErrorContext ctx) {
            return false;
        }

        public String buildList(String effectiveTableName, String columns, String whereExp) {
            return String.format("SELECT %s FROM %s WHERE %s", columns, effectiveTableName, whereExp);
        }

        public String buildTop(String effectiveTableName, String columns, String whereExp, int count) {
            return String.format("SELECT * FROM (SELECT %s FROM %s WHERE %s) WHERE ROWNUM <= %d", columns,
                    effectiveTableName, whereExp, count);
        }

        public String buildPage(String effectiveTableName, String columns, String whereExp, int start, int count) {
            return String.format(
                    "SELECT * FROM (SELECT ROWNUM RN, T1.* FROM (SELECT %s FROM %s WHERE %s)T1 WHERE ROWNUM <= %d)T2 WHERE T2.RN >=%d",
                    columns, effectiveTableName, whereExp, start + count, start);
        }

        public String buildPage(String selectSqlTemplate, int start, int count) {
            return String.format(
                    "SELECT * FROM (SELECT ROWNUM RN, T1.* FROM (%s)T1 WHERE ROWNUM <= %d)T2 WHERE T2.RN >=%d",
                    selectSqlTemplate, start + count, start);
        }

        public boolean isSpecificException(SQLException exception) {
            if (exception == null)
                return false;

            String errorCode = Integer.toString(exception.getErrorCode());
            if (errorCode == null || errorCode.isEmpty())
                return false;

            return errorCode.startsWith("08");
        }
    };

    private String nullableUpdateTpl;
    private String timestampExp;
    private Set<Integer> retriableCodeSet;
    private Set<Integer> failOverableCodeSet;

    public static final String SQL_PROVIDER = "sqlProvider";
    public static final String MYSQL_PROVIDER = "mySqlProvider";
    public static final String ORACLE_PROVIDER = "oracleProvider";

    public static final String NORMAL_MYSQL_JDBC_URL_PREFIX = "jdbc:mysql";
    public static final String LOADBALANCE_MYSQL_JDBC_URL_PREFIX = "jdbc:mysql:loadbalance";
    public static final String REPLICATION_MYSQL_JDBC_URL_PREFIX = "jdbc:mysql:replication";
    public static final String X_MYSQL_JDBC_URL_PREFIX = "mysqlx";

    public static final String SQLSERVER_JDBC_URL_PREFIX_OLD = "jdbc:sqlserver";
    public static final String SQLSERVER_JDBC_URL_PREFIX_NEW = "jdbc:microsoft:sqlserver";

    public static final String ORACLE_JDBC_URL_PREFIX = "jdbc:oracle";

    public static final int SQL_SERVER_TYPE_TVP = -1000;

    public static DatabaseCategory matchWith(String provider) {
        if (provider == null || provider.trim().length() == 0)
            throw new RuntimeException("The provider value can not be NULL or empty!");

        provider = provider.trim();
        if (provider.equalsIgnoreCase(SQL_PROVIDER))
            return DatabaseCategory.SqlServer;

        if (provider.equalsIgnoreCase(MYSQL_PROVIDER))
            return DatabaseCategory.MySql;

        if (provider.equalsIgnoreCase(ORACLE_PROVIDER))
            return DatabaseCategory.Oracle;

        throw new RuntimeException("The provider: " + provider + " can not be recoganized");
    }

    public static DatabaseCategory matchWith(com.ctrip.platform.dal.cluster.database.DatabaseCategory category) {
        if (category == null)
            throw new RuntimeException("category can not be NULL!");
        if (category == com.ctrip.platform.dal.cluster.database.DatabaseCategory.MYSQL)
            return DatabaseCategory.MySql;
        else if (category == com.ctrip.platform.dal.cluster.database.DatabaseCategory.SQLSERVER)
            return DatabaseCategory.SqlServer;
        throw new RuntimeException("category unrecognized");
    }

    public static DatabaseCategory matchWithConnectionUrl(String connectionUrl) {
        if (StringUtils.isEmpty(connectionUrl)) {
            throw new RuntimeException("connection url not be null");
        }
        if (connectionUrl.startsWith(NORMAL_MYSQL_JDBC_URL_PREFIX) ||
                connectionUrl.startsWith(LOADBALANCE_MYSQL_JDBC_URL_PREFIX) ||
                connectionUrl.startsWith(REPLICATION_MYSQL_JDBC_URL_PREFIX) ||
                connectionUrl.startsWith(X_MYSQL_JDBC_URL_PREFIX)) {
            return DatabaseCategory.MySql;
        }
        else if (connectionUrl.startsWith(SQLSERVER_JDBC_URL_PREFIX_OLD) ||
        connectionUrl.startsWith(SQLSERVER_JDBC_URL_PREFIX_NEW)) {
            return DatabaseCategory.SqlServer;
        }
        else if (connectionUrl.startsWith(ORACLE_JDBC_URL_PREFIX)) {
            return DatabaseCategory.Oracle;
        }
        else {
            throw new RuntimeException("connection url unrecognized");
        }
    }

    public Set<Integer> getDefaultRetriableErrorCodes() {
        return new TreeSet<Integer>(retriableCodeSet);
    }

    public Set<Integer> getDefaultFailOverableErrorCodes() {
        return new TreeSet<Integer>(failOverableCodeSet);
    }

    public Set<Integer> getDefaultErrorCodes() {
        Set<Integer> errorCodes = getDefaultRetriableErrorCodes();
        errorCodes.addAll(retriableCodeSet);
        errorCodes.addAll(failOverableCodeSet);
        return errorCodes;
    }

    public String getTimestampExp() {
        return timestampExp;
    }

    /**
     * This is for compatible with code generated for dal 1.4.1 and previouse version. Such code is like:
     *
     * SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, true); ... int index =
     * builder.getStatementParameterIndex(); parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize);
     * parameters.set(index++, Types.INTEGER, pageSize); return queryDao.query(sql, parameters, hints, parser);
     */
    public String getPageSuffixTpl() {
        switch (this) {
            case MySql:
                return " limit ?, ?";
            case SqlServer:
                return " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            default:
                return null;
        }
    }

    public abstract boolean isTimeOutException(ErrorContext ctx);

    public abstract String quote(String fieldName);

    public abstract String buildList(String effectiveTableName, String columns, String whereExp);

    public abstract String buildTop(String effectiveTableName, String columns, String whereExp, int count);

    public abstract String buildPage(String effectiveTableName, String columns, String whereExp, int start, int count);

    public abstract String buildPage(String selectSqlTemplate, int start, int count);

    public abstract boolean isSpecificException(SQLException exception);

    public boolean matchSpecificError(SQLException exception, Map<String, ErrorCodeInfo> map) {
        boolean result = false;

        if (exception == null)
            return result;

        if (map == null || map.isEmpty())
            return result;

        String sqlState = exception.getSQLState();
        if (sqlState != null && !sqlState.isEmpty()) {
            if (map.containsKey(sqlState))
                result = true;
        }

        String errorCode = Integer.toString(exception.getErrorCode());
        if (errorCode != null && !errorCode.isEmpty()) {
            if (map.containsKey(errorCode))
                result = true;
        }

        return result;
    }

    public void setObject(PreparedStatement statement, StatementParameter parameter) throws SQLException {
        try {
            if (parameter.isDefaultType()) {
                statement.setObject(parameter.getIndex(), parameter.getValue());
            } else {
                statement.setObject(parameter.getIndex(), parameter.getValue(), parameter.getSqlType());
            }
        } catch (Throwable e) {
            throw new DalParameterException(e, parameter);
        }
    }

    public void setObject(CallableStatement statement, StatementParameter parameter) throws SQLException {
        try {
            if (parameter.getValue() == null) {
                if (parameter.isDefaultType()) {
                    statement.setObject(parameter.getIndex(), null);
                } else {
                    if (parameter.getName() == null)
                        statement.setNull(parameter.getIndex(), parameter.getSqlType());
                    else
                        statement.setNull(parameter.getName(), parameter.getSqlType());
                }
            } else {
                if (parameter.isDefaultType()) {
                    statement.setObject(parameter.getIndex(), parameter.getValue());
                } else {
                    if (parameter.getName() == null)
                        statement.setObject(parameter.getIndex(), parameter.getValue(), parameter.getSqlType());
                    else
                        statement.setObject(parameter.getName(), parameter.getValue(), parameter.getSqlType());
                }
            }
        } catch (Throwable e) {
            throw new DalParameterException(e, parameter);
        }
    }

    public String getNullableUpdateTpl() {
        return nullableUpdateTpl;
    }

    DatabaseCategory(String nullableUpdateTpl, String timestampExp, int[] retriableCodes, int[] failOverableCodes) {
        this.nullableUpdateTpl = nullableUpdateTpl;
        this.timestampExp = timestampExp;
        this.retriableCodeSet = parseErrorCodes(retriableCodes);
        this.failOverableCodeSet = parseErrorCodes(failOverableCodes);
    }

    private Set<Integer> parseErrorCodes(int[] codes) {
        Set<Integer> temp = new TreeSet<Integer>();
        for (int value : codes)
            temp.add(value);

        return temp;
    }

}
