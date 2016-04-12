package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.enums.DbType;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.mysql.jdbc.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbUtils {
    private static Logger log = Logger.getLogger(DbUtils.class);
    public static List<Integer> validMode = new ArrayList<>();
    private static Pattern inRegxPattern = Pattern.compile("in\\s(@\\w+)", java.util.regex.Pattern.CASE_INSENSITIVE);

    static {
        validMode.add(DatabaseMetaData.procedureColumnIn);
        validMode.add(DatabaseMetaData.procedureColumnInOut);
        validMode.add(DatabaseMetaData.procedureColumnOut);
    }

    public static boolean tableExists(String allInOneName, String tableName) {
        try {
            return objectExist(allInOneName, "u", tableName);
        } catch (Exception e) {
            log.error(String.format("get table exists error: [allInOneName=%s;tableName=%s]", allInOneName, tableName), e);
        }
        return false;
    }

    private static boolean objectExist(String allInOneName, String objectType, String objectName) throws Exception {
        String dbType = getDbType(allInOneName);
        if (dbType.equals("Microsoft SQL Server")) {
            return mssqlObjectExist(allInOneName, objectType, objectName);
        } else {
            return mysqlObjectExist(allInOneName, objectType, objectName);
        }
    }

    private static boolean mssqlObjectExist(String allInOneName, String objectType, String objectName) throws Exception {
        String sql = "select Name from sysobjects where xtype = ? and status>=0 and Name=?";
        return query(allInOneName, sql, new Object[]{objectType, objectName}, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet rs) throws SQLException {
                return rs.next();
            }
        });
    }

    private static boolean mysqlObjectExist(String allInOneName, final String objectType, final String objectName) {
        try {
            return execute(allInOneName, new ConnectionCallback<Boolean>() {
                @Override
                public Boolean doInConnection(Connection connection) throws SQLException, DataAccessException {
                    String type = "u".equalsIgnoreCase(objectType) ? "TABLE" : "VIEW";
                    ResultSet rs = connection.getMetaData().getTables(null, null, objectName, new String[]{type});
                    return rs.next();
                }
            });
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        return false;
    }

    private static JdbcTemplate createJdbcTemplate(String allInOneName) throws Exception {
        return new JdbcTemplate(DataSourceUtil.getDataSource(allInOneName));
    }

    private static <T> T query(String allInOneName, String sql, Object[] params, ResultSetExtractor<T> extractor) throws Exception {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(allInOneName);
        return jdbcTemplate.query(sql, params, extractor);
    }

    private static <T> T execute(String allInOneName, ConnectionCallback<T> action) throws Exception {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(allInOneName);
        return jdbcTemplate.execute(action);
    }

    /**
     * 获取所有表名
     */
    public static List<String> getAllTableNames(String allInOneName) throws Exception {
        return execute(allInOneName, new ConnectionCallback<List<String>>() {
            @Override
            public List<String> doInConnection(Connection connection) throws SQLException, DataAccessException {
                List<String> results = new ArrayList<>();
                ResultSet rs = connection.getMetaData().getTables(null, "dbo", "%", new String[]{"TABLE"});
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if ("sysdiagrams".equals(tableName.toLowerCase()))
                        continue;
                    results.add(rs.getString("TABLE_NAME"));
                }
                return results;
            }
        });
    }

    public static boolean viewExists(String allInOneName, String viewName) {
        try {
            return objectExist(allInOneName, "v", viewName);
        } catch (Exception e) {
            log.error(String.format("get view exists error: [allInOneName=%s;viewName=%s]", allInOneName, viewName), e);
        }
        return false;
    }

    /**
     * 获取所有视图
     */
    public static List<String> getAllViewNames(String allInOneName) throws Exception {
        return execute(allInOneName, new ConnectionCallback<List<String>>() {
            @Override
            public List<String> doInConnection(Connection connection) throws SQLException, DataAccessException {
                List<String> results = new ArrayList<>();
                ResultSet rs = connection.getMetaData().getTables(null, "dbo", "%", new String[]{"VIEW"});
                while (rs.next())
                    results.add(rs.getString("TABLE_NAME"));
                return results;
            }
        });
    }

    public static boolean spExists(String allInOneName, final StoredProcedure sp) {
        try {
            // 如果是Sql Server，通过Sql语句获取所有表和视图的名称
            if (!isMySqlServer(allInOneName)) {
                String sql = "select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE' and SPECIFIC_SCHEMA=? and SPECIFIC_NAME=?";
                return query(allInOneName, sql, new Object[]{sp.getSchema(), sp.getName()},
                        new ResultSetExtractor<Boolean>() {
                            @Override
                            public Boolean extractData(ResultSet rs) throws SQLException {
                                return rs.next();
                            }
                        });
            }
        } catch (Exception e) {
            log.error(String.format("get sp exists error: [allInOneName=%s;spName=%s]", allInOneName, sp.getName()), e);
        }
        return false;
    }

    public static List<StoredProcedure> getAllSpNames(String allInOneName) throws Exception {
        try {
            // 如果是Sql Server，通过Sql语句获取所有视图的名称
            if (!isMySqlServer(allInOneName)) {
                String sql = "select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'";
                return query(allInOneName, sql, null, new ResultSetExtractor<List<StoredProcedure>>() {
                    @Override
                    public List<StoredProcedure> extractData(ResultSet rs) throws SQLException {
                        List<StoredProcedure> results = new ArrayList<>();
                        while (rs.next()) {
                            StoredProcedure sp = new StoredProcedure();
                            sp.setSchema(rs.getString(1));
                            sp.setName(rs.getString(2));
                            results.add(sp);
                        }
                        return results;
                    }
                });
            }
        } catch (SQLException e) {
            handleException(String.format("get all sp names error: [allInOneName=%s]", allInOneName), e);
        }
        return new ArrayList<>();
    }

    /**
     * 获取存储过程的所有参数
     */
    public static <T> T getSpParams(String allInOneName, final StoredProcedure sp, final ResultSetExtractor<T> extractor) {
        try {
            return execute(allInOneName, new ConnectionCallback<T>() {
                @Override
                public T doInConnection(Connection connection) throws SQLException, DataAccessException {
                    ResultSet spParamRs = connection.getMetaData().getProcedureColumns(null, sp.getSchema(), sp.getName(), null);
                    return extractor.extractData(spParamRs);
                }
            });
        } catch (Exception e) {
            log.error(String.format("get sp params error: [allInOneName=%s;spName=%s;]", allInOneName, sp.getName()), e);
        }
        return null;
    }

    public static List<String> getPrimaryKeyNames(String allInOneName, final String tableName) {
        try {
            return execute(allInOneName, new ConnectionCallback<List<String>>() {
                @Override
                public List<String> doInConnection(Connection connection) throws SQLException, DataAccessException {
                    List<String> primaryKeys = new ArrayList<>();
                    // 获取所有主键
                    ResultSet primaryKeyRs = connection.getMetaData().getPrimaryKeys(null, null, tableName);
                    while (primaryKeyRs.next())
                        primaryKeys.add(primaryKeyRs.getString("COLUMN_NAME"));
                    return primaryKeys;
                }

            });
        } catch (Exception e) {
            log.error(String.format("get primary key names error: [allInOneName=%s;tableName=%s]", allInOneName, tableName), e);
        }
        return new ArrayList<>();
    }

    public static DbType getDotNetDbType(String typeName, int dataType, int length, boolean isUnsigned, DatabaseCategory dbCategory) {
        DbType dbType;
        if (typeName != null && typeName.equalsIgnoreCase("year")) {
            dbType = DbType.Int16;
        } else if (typeName != null && typeName.equalsIgnoreCase("uniqueidentifier")) {
            dbType = DbType.Guid;
        } else if (typeName != null && typeName.equalsIgnoreCase("sql_variant")) {
            dbType = DbType.Object;
        } else if (typeName != null && typeName.equalsIgnoreCase("datetime")) {
            dbType = DbType.DateTime;
        } else if (typeName != null && typeName.equalsIgnoreCase("datetime2")) {
            dbType = DbType.DateTime2;
        } else if (typeName != null && typeName.equalsIgnoreCase("smalldatetime")) {
            dbType = DbType.DateTime;
        } else if (typeName != null && typeName.equalsIgnoreCase("xml")) {
            dbType = DbType.Xml;
        } else if (dataType == java.sql.Types.CHAR && length > 1) {
            dbType = DbType.AnsiString;
        } else if (dataType == -155) {
            dbType = DbType.DateTimeOffset;
        } else if (dataType == Types.BIT && length > 1) {
            dbType = DbType.UInt64;
        } else if (dataType == Types.SMALLINT && isUnsigned) {
            dbType = DbType.UInt16;
        } else if (dataType == Types.INTEGER && isUnsigned) {
            dbType = DbType.UInt32;
        } else if (dataType == Types.BIGINT && isUnsigned) {
            dbType = DbType.UInt64;
        } else if (dataType == Types.TIMESTAMP && dbCategory != null && dbCategory != DatabaseCategory.SqlServer) {
            dbType = DbType.DateTime;
        } else {
            dbType = DbType.getDbTypeFromJdbcType(dataType);
        }
        return dbType;
    }

    public static boolean isColumnUnsigned(String columnType) {
        boolean result = false;
        if (columnType == null || columnType.length() == 0) {
            return result;
        }
        if (columnType.toLowerCase().indexOf("unsigned") > -1) {
            result = true;
        }
        return result;
    }

    public static <T> T getAllColumnNames(String allInOneName, final String tableName, final ResultSetExtractor<T> extractor) {
        try {
            return execute(allInOneName, new ConnectionCallback<T>() {
                @Override
                public T doInConnection(Connection connection) throws SQLException, DataAccessException {
                    ResultSet allColumnsRs = connection.getMetaData().getColumns(null, null, tableName, null);
                    return extractor.extractData(allColumnsRs);
                }
            });
        } catch (Exception e) {
            log.error(String.format("get all column names error: [allInOneName=%s;tableName=%s;]", allInOneName, tableName), e);
        }
        return null;
    }

    public static Map<String, Class<?>> getSqlType2JavaTypeMaper(String allInOneName, String tableViewName) {
        try {
            String sql = buildColumnSql(allInOneName, tableViewName);
            return query(allInOneName, sql, null, new ResultSetExtractor<Map<String, Class<?>>>() {
                @Override
                public Map<String, Class<?>> extractData(ResultSet rs) throws SQLException {
                    Map<String, Class<?>> map = new HashMap<>();
                    ResultSetMetaData rsMeta = rs.getMetaData();
                    for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                        String columnName = rsMeta.getColumnName(i);
                        Integer sqlType = rsMeta.getColumnType(i);
                        Class<?> javaType = null;
                        try {
                            javaType = Class.forName(rsMeta.getColumnClassName(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                            javaType = Consts.jdbcSqlTypeToJavaClass.get(sqlType);
                        }
                        if (!map.containsKey(columnName) && null != javaType)
                            map.put(columnName, javaType);
                    }
                    return map;
                }
            });
        } catch (Exception e) {
            log.error(String.format("get sql-type to java-type maper error: [allInOneName=%s;tableViewName=%s]", allInOneName, tableViewName), e);
        }
        return new HashMap<>();
    }

    private static String buildColumnSql(String allInOneName, String tableViewName) throws Exception {
        if (isMySqlServer(allInOneName)) {
            return "select * from " + tableViewName + " limit 1";
        } else {
            return "select top 1 * from " + tableViewName;
        }
    }

    public static Map<String, Integer> getColumnSqlType(String allInOneName, String tableViewName) {
        try {
            String sql = buildColumnSql(allInOneName, tableViewName);
            return query(allInOneName, sql, null, new ResultSetExtractor<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> extractData(ResultSet rs) throws SQLException {
                    ResultSetMetaData rsMeta = rs.getMetaData();
                    Map<String, Integer> map = new HashMap<>();
                    for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                        String columnName = rsMeta.getColumnName(i);
                        Integer sqlType = rsMeta.getColumnType(i);
                        if ("uniqueidentifier".equals(rsMeta.getColumnTypeName(i))) {
                            sqlType = 10001;
                        }
                        if (!map.containsKey(columnName) && null != sqlType) {
                            map.put(columnName, sqlType);
                        }
                    }
                    return map;
                }
            });
        } catch (Exception e) {
            log.error(String.format("get sql-type to java-type maper error: [allInOneName=%s;tableViewName=%s]", allInOneName, tableViewName), e);
        }
        return new HashMap<>();
    }

    private static boolean isMySqlServer(String allInOneName) throws Exception {
        String dbType = getDbType(allInOneName);
        if (dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            return false;
        } else {
            return true;
        }
    }

    public static List<AbstractParameterHost> getSelectFieldHosts(String allInOneName, String sql, ResultSetExtractor<List<AbstractParameterHost>> extractor) {
        String testSql = sql;
        int whereIndex = StringUtils.indexOfIgnoreCase(testSql, "where");
        if (whereIndex > 0)
            testSql = sql.substring(0, whereIndex);
        try {
            if (isMySqlServer(allInOneName)) {
                testSql = testSql + " limit 1";
            } else {
                testSql = testSql.replace("select", "select top(1)");
            }
            return query(allInOneName, testSql, null, extractor);
        } catch (Exception e) {
            log.error(String.format("get select field error: [allInOneName=%s;sql=%s;]", allInOneName, sql), e);
        }
        return new ArrayList<>();
    }

    public static List<AbstractParameterHost> testAQuerySql(String allInOneName, final String sql, final String params, final ResultSetExtractor<List<AbstractParameterHost>> extractor) throws Exception {
        return execute(allInOneName, new ConnectionCallback<List<AbstractParameterHost>>() {
            @Override
            public List<AbstractParameterHost> doInConnection(Connection connection) throws SQLException, DataAccessException {
                String[] parameters = params.split(";");
                Matcher m = inRegxPattern.matcher(sql);
                String temp = sql;
                while (m.find()) {
                    temp = temp.replace(m.group(1), String.format("(?) "));
                }
                String replacedSql = temp.replaceAll("[@:]\\w+", "?");
                PreparedStatement ps = connection.prepareStatement(replacedSql);
                int index = 0;
                for (String param : parameters) {
                    if (param != null && !param.isEmpty()) {
                        String[] tuple = param.split(",");
                        try {
                            index = Integer.valueOf(tuple[0]);
                        } catch (NumberFormatException ex) {
                            index++;
                        }
                        if (Integer.valueOf(tuple[1]) == 10001)
                            ps.setObject(index, mockATest(Integer.valueOf(tuple[1])), Types.BINARY);
                        else
                            ps.setObject(index, mockATest(Integer.valueOf(tuple[1])), Integer.valueOf(tuple[1]));
                    }
                }
                ResultSet rs = ps.executeQuery();
                return extractor.extractData(rs);
            }
        });
    }

    public static Object mockATest(int javaSqlTypes) {
        switch (javaSqlTypes) {
            case java.sql.Types.BIT:
                return true;
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT:
                return 1;
            case java.sql.Types.REAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.DECIMAL:
                return 1.0;
            case java.sql.Types.CHAR:
                return 't';
            case java.sql.Types.DATE:
                return "2012-01-01";
            case java.sql.Types.TIME:
                return "10:00:00";
            case java.sql.Types.TIMESTAMP:
                return "2012-01-01 10:00:00";
            case 10001:// uniqueidentifier
                return new byte[]{};
            default:
                return "test";
        }
    }

    public static String getDbType(final String allInOneName) throws Exception {
        if (Consts.databaseType.containsKey(allInOneName)) {
            return Consts.databaseType.get(allInOneName);
        } else {
            return execute(allInOneName, new ConnectionCallback<String>() {
                @Override
                public String doInConnection(Connection connection) throws SQLException, DataAccessException {
                    String dbType = connection.getMetaData().getDatabaseProductName();
                    Consts.databaseType.put(allInOneName, dbType);
                    return dbType;
                }
            });
        }
    }

    public static DatabaseCategory getDatabaseCategory(String allInOneName) throws Exception {
        DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
        String dbType = getDbType(allInOneName);
        if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            dbCategory = DatabaseCategory.MySql;
        }
        return dbCategory;
    }

    public static Map<String, String> getSqlserverColumnComment(String allInOneName, String tableName) throws Exception {
        Map<String, String> map = new HashMap<>();
        if (isMySqlServer(allInOneName)) {
            return map;
        }
        String sql = "" + "SELECT sys.columns.name as name, " + "       CONVERT(VARCHAR(1000), (SELECT VALUE "
                + "                              FROM   sys.extended_properties "
                + "                              WHERE  sys.extended_properties.major_id = sys.columns.object_id "
                + "                                     AND sys.extended_properties.minor_id = sys.columns.column_id)) AS description "
                + "FROM   sys.columns, " + "       sys.tables " + "WHERE  sys.columns.object_id = sys.tables.object_id "
                + "       AND sys.tables.name = ? " + "ORDER  BY sys.columns.column_id ";
        return query(allInOneName, sql, new Object[]{tableName}, new ResultSetExtractor<Map<String, String>>() {
            @Override
            public Map<String, String> extractData(ResultSet rs) throws SQLException {
                Map<String, String> map = new HashMap<>();
                while (rs.next())
                    map.put(rs.getString("name").toLowerCase(), rs.getString("description"));
                return map;
            }
        });
    }

    private static void handleException(String msg, Exception e) throws Exception {
        log.warn(msg == null ? e.getMessage() : msg, e);
        throw e;
    }

}
