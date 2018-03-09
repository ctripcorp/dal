package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalStatementCreator;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.Parameter;
import com.ctrip.platform.dal.daogen.enums.DbType;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.mysql.jdbc.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbUtils {
    private static DalStatementCreator statementCreator = null;

    public static List<Integer> validMode = new ArrayList<>();
    private static Pattern inRegxPattern = Pattern.compile("in\\s(@\\w+)", Pattern.CASE_INSENSITIVE);
    private static Set<Integer> stringTypeSet = null;
    private static final int UNIQUE_IDENTIFIER_TYPE = 10001;


    static {
        statementCreator = new DalStatementCreator(com.ctrip.platform.dal.common.enums.DatabaseCategory.MySql);
        validMode.add(DatabaseMetaData.procedureColumnIn);
        validMode.add(DatabaseMetaData.procedureColumnInOut);
        validMode.add(DatabaseMetaData.procedureColumnOut);
        stringTypeSet = getStringTypeSet();
    }

    public static boolean tableExists(String allInOneName, String tableName) throws Exception {
        try {
            return objectExist(allInOneName, "u", tableName);
        } catch (Throwable e) {
            throw e;
        }
    }

    private static boolean objectExist(String allInOneName, String objectType, String objectName) throws Exception {
        String dbType = getDbType(allInOneName);
        if (dbType.equals("Microsoft SQL Server")) {
            return mssqlObjectExist(allInOneName, objectType, objectName);
        } else {
            return mysqlObjectExist(allInOneName, objectType, objectName);
        }
    }

    private static boolean mssqlObjectExist(String allInOneName, String objectType, String objectName)
            throws Exception {
        String sql = "select Name from sysobjects where xtype = ? and status>=0 and Name=?";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "xtype", Types.VARCHAR, objectType);
        parameters.set(i++, "Name", Types.VARCHAR, objectName);
        DalHints hints = DalHints.createIfAbsent(null);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean result = false;
        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            preparedStatement = statementCreator.createPreparedStatement(connection, sql, parameters, hints);
            resultSet = preparedStatement.executeQuery();
            result = resultSet.next();
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(preparedStatement);
            ResourceUtils.close(connection);
        }
        return result;
    }

    private static boolean mysqlObjectExist(String allInOneName, String objectType, String objectName)
            throws Exception {
        Connection connection = null;
        ResultSet resultSet = null;
        String type = "u".equalsIgnoreCase(objectType) ? "TABLE" : "VIEW";
        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            resultSet = connection.getMetaData().getTables(null, null, objectName, new String[] {type});
            return resultSet.next();
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(connection);
        }
    }

    /**
     * 获取所有表名
     */
    public static List<String> getAllTableNames(String allInOneName) throws Exception {
        List<String> list = new ArrayList<>();
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            resultSet = connection.getMetaData().getTables(null, "dbo", "%", new String[] {"TABLE"});
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                if ("sysdiagrams".equals(tableName.toLowerCase()))
                    continue;
                list.add(resultSet.getString("TABLE_NAME"));
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(connection);
        }
        return list;
    }

    public static boolean viewExists(String allInOneName, String viewName) throws Exception {
        try {
            return objectExist(allInOneName, "v", viewName);
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * 获取所有视图
     */
    public static List<String> getAllViewNames(String allInOneName) throws Exception {
        List<String> list = new ArrayList<>();
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            resultSet = connection.getMetaData().getTables(null, "dbo", "%", new String[] {"VIEW"});
            while (resultSet.next()) {
                list.add(resultSet.getString("TABLE_NAME"));
            }
        } catch (Throwable e) {
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(connection);
        }

        return list;
    }

    public static boolean spExists(String allInOneName, StoredProcedure sp) throws Exception {
        boolean result = false;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "SPECIFIC_SCHEMA", Types.VARCHAR, sp.getSchema());
        parameters.set(i++, "SPECIFIC_NAME", Types.VARCHAR, sp.getName());
        DalHints hints = DalHints.createIfAbsent(null);

        try {
            // 如果是Sql Server，通过Sql语句获取所有表和视图的名称
            if (!isMySqlServer(allInOneName)) {
                String sql =
                        "select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE' and SPECIFIC_SCHEMA=? and SPECIFIC_NAME=?";
                connection = DataSourceUtil.getConnection(allInOneName);
                preparedStatement = statementCreator.createPreparedStatement(connection, sql, parameters, hints);
                resultSet = preparedStatement.executeQuery();
                result = resultSet.next();
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(preparedStatement);
            ResourceUtils.close(connection);
        }
        return result;
    }

    public static List<StoredProcedure> getAllSpNames(String allInOneName) throws Exception {
        List<StoredProcedure> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        StatementParameters parameters = new StatementParameters();
        DalHints hints = DalHints.createIfAbsent(null);

        try {
            // 如果是Sql Server，通过Sql语句获取所有视图的名称
            if (!isMySqlServer(allInOneName)) {
                String sql =
                        "select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'";
                connection = DataSourceUtil.getConnection(allInOneName);
                preparedStatement = statementCreator.createPreparedStatement(connection, sql, parameters, hints);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    StoredProcedure sp = new StoredProcedure();
                    sp.setSchema(resultSet.getString(1));
                    sp.setName(resultSet.getString(2));
                    list.add(sp);
                }
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(preparedStatement);
            ResourceUtils.close(connection);
        }
        return list;
    }

    /**
     * 获取存储过程的所有参数
     */
    public static <T> T getSpParams(String allInOneName, StoredProcedure sp, DalResultSetExtractor<T> extractor)
            throws Exception {
        Connection connection = null;
        ResultSet resultSet = null;
        T result = null;

        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            resultSet = connection.getMetaData().getProcedureColumns(null, sp.getSchema(), sp.getName(), null);
            result = extractor.extract(resultSet);
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(connection);
        }
        return result;
    }

    public static List<String> getPrimaryKeyNames(String allInOneName, String tableName) throws Exception {
        List<String> list = new ArrayList<>();
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            resultSet = connection.getMetaData().getPrimaryKeys(null, null, tableName);
            while (resultSet.next()) {
                list.add(resultSet.getString("COLUMN_NAME"));
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(connection);
        }
        return list;
    }

    public static DbType getDotNetDbType(String typeName, int dataType, int length, boolean isUnsigned,
            DatabaseCategory dbCategory) {
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
        } else if (dataType == Types.CHAR && length > 1) {
            dbType = DbType.AnsiString;
        } else if (dataType == -155) {
            dbType = DbType.DateTimeOffset;
        } else if (dataType == Types.BIT && length > 1) {
            dbType = DbType.UInt64;
        } else if (dataType == Types.TINYINT && !isUnsigned && dbCategory == DatabaseCategory.MySql) {
            dbType = DbType.SByte;
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
        if (columnType == null || columnType.length() == 0)
            return result;

        if (columnType.toLowerCase().indexOf("unsigned") > -1)
            result = true;

        return result;
    }

    public static <T> T getAllColumnNames(String allInOneName, String tableName, DalResultSetExtractor<T> extractor)
            throws Exception {
        Connection connection = null;
        ResultSet resultSet = null;
        T result = null;

        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            resultSet = connection.getMetaData().getColumns(null, null, tableName, null);
            result = extractor.extract(resultSet);
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(connection);
        }
        return result;
    }

    public static Map<String, Class<?>> getSqlType2JavaTypeMaper(String allInOneName, String tableViewName)
            throws Exception {
        Map<String, Class<?>> map = new HashMap<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = buildColumnSql(allInOneName, tableViewName);
            StatementParameters parameters = new StatementParameters();
            DalHints hints = DalHints.createIfAbsent(null);
            connection = DataSourceUtil.getConnection(allInOneName);
            preparedStatement = statementCreator.createPreparedStatement(connection, sql, parameters, hints);
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Integer sqlType = metaData.getColumnType(i);
                Class<?> javaType = null;
                try {
                    javaType = Class.forName(metaData.getColumnClassName(i));
                } catch (Throwable e) {
                    javaType = Consts.jdbcSqlTypeToJavaClass.get(sqlType);
                }
                if (!map.containsKey(columnName) && null != javaType)
                    map.put(columnName, javaType);
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(preparedStatement);
            ResourceUtils.close(connection);
        }
        return map;
    }

    private static String buildColumnSql(String allInOneName, String tableViewName) throws Exception {
        if (isMySqlServer(allInOneName)) {
            return "select * from `" + tableViewName + "` limit 1";
        } else {
            return "select top 1 * from [" + tableViewName + "]";
        }
    }

    public static Map<String, Integer> getColumnSqlType(String allInOneName, String tableViewName) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = buildColumnSql(allInOneName, tableViewName);
            StatementParameters parameters = new StatementParameters();
            DalHints hints = DalHints.createIfAbsent(null);
            connection = DataSourceUtil.getConnection(allInOneName);
            preparedStatement = statementCreator.createPreparedStatement(connection, sql, parameters, hints);
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Integer sqlType = metaData.getColumnType(i);
                if ("uniqueidentifier".equals(metaData.getColumnTypeName(i)))
                    sqlType = UNIQUE_IDENTIFIER_TYPE;
                if (!map.containsKey(columnName) && null != sqlType)
                    map.put(columnName, sqlType);
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(preparedStatement);
            ResourceUtils.close(connection);
        }
        return map;
    }

    public static boolean isMySqlServer(String allInOneName) throws SQLException {
        String dbType = getDbType(allInOneName);
        if (dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            return false;
        } else {
            return true;
        }
    }

    public static List<AbstractParameterHost> getSelectFieldHosts(String allInOneName, String sql,
            DalResultSetExtractor<List<AbstractParameterHost>> extractor) throws Exception {
        List<AbstractParameterHost> list = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        StatementParameters parameters = new StatementParameters();
        DalHints hints = DalHints.createIfAbsent(null);
        String testSql = sql;
        int whereIndex = StringUtils.indexOfIgnoreCase(testSql, "where");
        if (whereIndex > 0)
            testSql = sql.substring(0, whereIndex);
        if (isMySqlServer(allInOneName)) {
            testSql = testSql + " limit 1";
        } else {
            testSql = testSql.replace("select", "select top(1)");
        }
        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            preparedStatement = statementCreator.createPreparedStatement(connection, testSql, parameters, hints);
            resultSet = preparedStatement.executeQuery();
            list = extractor.extract(resultSet);
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(preparedStatement);
            ResourceUtils.close(connection);
        }
        return list;
    }

    public static List<AbstractParameterHost> testAQuerySql(String allInOneName, String sql, String params,
            DalResultSetExtractor<List<AbstractParameterHost>> extractor) throws Exception {
        List<AbstractParameterHost> result = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            List<Parameter> list = new ArrayList<>();
            String[] parameters = params.split(";");
            if (parameters != null && parameters.length > 0) {
                for (String p : parameters) {
                    if (p.isEmpty()) {
                        continue;
                    }
                    String[] tuple = p.split(",");
                    if (tuple != null && tuple.length > 0) {
                        Parameter parameter = new Parameter();
                        parameter.setName(tuple[0]);
                        parameter.setType(Integer.valueOf(tuple[1]));
                        list.add(parameter);
                    }
                }
            }

            Matcher matcher = pattern.matcher(sql);
            // Match C# parameters
            if (matcher.find()) {
                list = getActualParameters(sql, list);
            }

            Matcher m = inRegxPattern.matcher(sql);
            String temp = sql;
            while (m.find()) {
                temp = temp.replace(m.group(1), String.format("(?) "));
            }
            String replacedSql = temp.replaceAll(expression, "?");
            preparedStatement = connection.prepareStatement(replacedSql);
            int index = 0;
            for (Parameter parameter : list) {
                String name = parameter.getName();
                int type = parameter.getType();
                try {
                    index = Integer.valueOf(name);
                } catch (NumberFormatException ex) {
                    index++;
                }
                if (type == UNIQUE_IDENTIFIER_TYPE) {
                    preparedStatement.setObject(index, mockATest(type), Types.BINARY);
                } else {
                    preparedStatement.setObject(index, mockATest(type), type);
                }
            }
            ResultSet rs = preparedStatement.executeQuery();
            result = extractor.extract(rs);
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(preparedStatement);
            ResourceUtils.close(connection);
        }
        return result;
    }

    public static int testUpdateSql(String allInOneName, String sql, String params) throws Exception {
        int result;
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            connection.setAutoCommit(false);
            List<Parameter> list = new ArrayList<>();
            String[] parameters = params.split(";");
            if (parameters != null && parameters.length > 0) {
                for (String p : parameters) {
                    if (p.isEmpty()) {
                        continue;
                    }
                    String[] tuple = p.split(",");
                    if (tuple != null && tuple.length > 0) {
                        Parameter parameter = new Parameter();
                        parameter.setName(tuple[0]);
                        parameter.setType(Integer.valueOf(tuple[1]));
                        list.add(parameter);
                    }
                }
            }

            Matcher matcher = pattern.matcher(sql);
            // Match C# parameters
            if (matcher.find()) {
                list = getActualParameters(sql, list);
            }

            Matcher m = inRegxPattern.matcher(sql);
            String temp = sql;
            while (m.find()) {
                temp = temp.replace(m.group(1), String.format("(?) "));
            }
            String replacedSql = temp.replaceAll(expression, "?");
            preparedStatement = connection.prepareStatement(replacedSql);
            int index = 0;
            for (Parameter parameter : list) {
                String name = parameter.getName();
                int type = parameter.getType();
                try {
                    index = Integer.valueOf(name);
                } catch (NumberFormatException ex) {
                    index++;
                }
                if (type == 10001) {
                    preparedStatement.setObject(index, mockATest(type), Types.BINARY);
                } else {
                    preparedStatement.setObject(index, mockATest(type), type);
                }
            }
            result = preparedStatement.executeUpdate();
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(preparedStatement);
            ResourceUtils.rollback(connection);
            ResourceUtils.close(connection);
        }
        return result;
    }

    private static final String expression = "[@:]\\w+";
    private static final Pattern pattern = Pattern.compile(expression);

    private static List<Parameter> getActualParameters(final String sql, List<Parameter> parameters) {
        List<Parameter> list = new ArrayList<>();
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String parameter = matcher.group();
            Parameter p = new Parameter();
            p.setName(parameter.substring(1));
            list.add(p);
        }

        Map<String, Parameter> map = new HashMap<>();
        if (parameters != null && parameters.size() > 0) {
            for (Parameter p : parameters) {
                String name = p.getName();
                if (!map.containsKey(name)) {
                    map.put(name, p);
                }
            }
        }

        for (Parameter p : list) {
            String name = p.getName();
            Parameter temp = map.get(name);
            if (temp != null) {
                p.setType(temp.getType());
            }
        }

        return list;
    }

    public static Object mockATest(int javaSqlTypes) {
        switch (javaSqlTypes) {
            case Types.BIT:
                return true;
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
                return 1;
            case Types.REAL:
            case Types.DOUBLE:
            case Types.DECIMAL:
                return 1.0;
            case Types.CHAR:
                return "t";
            // return 't';
            case Types.DATE:
                return "2012-01-01";
            case Types.TIME:
                return "10:00:00";
            case Types.TIMESTAMP:
                return "2012-01-01 10:00:00";
            case UNIQUE_IDENTIFIER_TYPE:// uniqueidentifier
                return new byte[] {};
            default:
                return "test";
        }
    }

    public static String getDbType(final String allInOneName) throws SQLException {
        if (Consts.databaseType.containsKey(allInOneName)) {
            return Consts.databaseType.get(allInOneName);
        } else {
            Connection connection = null;
            String dbType = null;
            try {
                connection = DataSourceUtil.getConnection(allInOneName);
                dbType = connection.getMetaData().getDatabaseProductName();
                Consts.databaseType.put(allInOneName, dbType);
            } catch (Throwable e) {
                throw new SQLException(String.format("getDbType error,allInOneName is:%s", allInOneName), e);
            } finally {
                ResourceUtils.close(connection);
            }
            return dbType;
        }
    }

    public static DatabaseCategory getDatabaseCategory(String allInOneName) throws Exception {
        DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
        String dbType = getDbType(allInOneName);
        if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server"))
            dbCategory = DatabaseCategory.MySql;

        return dbCategory;
    }

    public static Map<String, String> getSqlserverColumnComment(String allInOneName, String tableName)
            throws Exception {
        Map<String, String> map = new HashMap<>();
        if (isMySqlServer(allInOneName))
            return map;

        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT sys.columns.name as name, CONVERT(VARCHAR(1000), (SELECT VALUE FROM sys.extended_properties ");
        sb.append("WHERE  sys.extended_properties.major_id = sys.columns.object_id ");
        sb.append("AND sys.extended_properties.minor_id = sys.columns.column_id)) AS description ");
        sb.append("FROM sys.columns, sys.tables WHERE sys.columns.object_id = sys.tables.object_id ");
        sb.append("AND sys.tables.name = ? ORDER BY sys.columns.column_id ");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "name", Types.VARCHAR, tableName);
        DalHints hints = DalHints.createIfAbsent(null);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DataSourceUtil.getConnection(allInOneName);
            preparedStatement = statementCreator.createPreparedStatement(connection, sb.toString(), parameters, hints);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                map.put(resultSet.getString("name").toLowerCase(), resultSet.getString("description"));
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(preparedStatement);
            ResourceUtils.close(connection);
        }

        return map;
    }

    private static Set<Integer> getStringTypeSet() {
        Set<Integer> set = new HashSet<>();
        set.add(Types.CHAR);
        set.add(Types.VARCHAR);
        set.add(Types.LONGVARCHAR);
        set.add(Types.NCHAR);
        set.add(Types.NVARCHAR);
        set.add(Types.LONGNVARCHAR);
        set.add(UNIQUE_IDENTIFIER_TYPE);
        return set;
    }

    public static boolean isStringType(int sqlType) {
        return stringTypeSet.contains(sqlType);
    }

}
