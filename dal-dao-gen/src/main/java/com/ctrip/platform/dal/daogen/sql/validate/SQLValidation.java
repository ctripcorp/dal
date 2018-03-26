package com.ctrip.platform.dal.daogen.sql.validate;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;
import com.ctrip.platform.dal.daogen.utils.ORMUtils;
import com.ctrip.platform.dal.daogen.utils.ResourceUtils;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import microsoft.sql.DateTimeOffset;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SQLValidation {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Mock a series of String value for SQL Types
     *
     * @param sqlTypes The SQL Types
     * @return Mocked String values.
     */
    public static String[] mockStringValues(int[] sqlTypes) {
        if (sqlTypes == null || sqlTypes.length == 0)
            return new String[] {};
        String[] mockedVals = new String[sqlTypes.length];
        for (int i = 0; i < mockedVals.length; i++) {
            Object obj = mockSQLValue(sqlTypes[i]);
            if (null == obj)
                mockedVals[i] = "null";
            else
                mockedVals[i] = obj.toString();
            // mockedVals[i] = obj instanceof String && sqlTypes[i] != 10001 ? //10001 <---> uniqueidentifier
            // "'" + obj.toString() + "'" : obj.toString();
        }
        return mockedVals;
    }

    /**
     * Mock a series of Java Object value for SQL Types
     *
     * @param sqlTypes The SQL Types
     * @return Mocked Java Object values.
     */
    public static Object[] mockObjectValues(int[] sqlTypes) {
        if (sqlTypes == null || sqlTypes.length == 0)
            return new Object[] {};
        Object[] mockedVals = new Object[sqlTypes.length];
        for (int i = 0; i < mockedVals.length; i++) {
            mockedVals[i] = mockSQLValue(sqlTypes[i]);
        }
        return mockedVals;
    }

    /**
     * Validate the SQL Statement Parameters will be auto-mocked according to the specified SQL Types
     *
     * @param dbName The database name
     * @param sql SQL Statement
     * @param paramsTypes SQL Types of parameters
     * @return Validate Result
     */
    public static ValidateResult validate(String dbName, String sql, int[] paramsTypes) {
        Object[] mockedVals = mockObjectValues(paramsTypes);
        return validate(dbName, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the SQL Statement Parameters will be parsed form specified String values.
     *
     * @param dbName The database name
     * @param sql SQL Statement
     * @param paramsTypes SQL Types of parameters
     * @param vals Parameter String values
     * @return Validate Result
     */
    public static ValidateResult validate(String dbName, String sql, int[] paramsTypes, String[] vals) {
        Object[] mockedVals = parseSQLValue(paramsTypes, vals);
        return validate(dbName, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the SQL Statement
     *
     * @param dbName The database name
     * @param sql SQL Statement
     * @param paramsTypes SQL Types of parameters
     * @param vals Parameter values
     * @return Validate Result
     */
    private static ValidateResult validate(String dbName, String sql, int[] paramsTypes, Object[] vals) {
        if (StringUtils.startsWithIgnoreCase(sql, "SELECT")) {
            return queryValidate(dbName, sql, paramsTypes, vals);
        } else {
            return updateValidate(dbName, sql, paramsTypes, vals);
        }
    }

    /**
     * Validate the Non-Query SQL Statement
     *
     * @param dbName The database name
     * @param sql SQL Statement
     * @param paramsTypes SQL Types of parameters
     * @param vals Parameter String values
     * @return Validate Result
     */
    public static ValidateResult updateValidate(String dbName, String sql, int[] paramsTypes, String[] vals) {
        Object[] mockedVals = parseSQLValue(paramsTypes, vals);
        return updateValidate(dbName, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the Non-Query SQL Statement Parameters will be auto-mocked according to the specified SQL Types
     *
     * @param dbName The database name
     * @param sql SQL Statement
     * @param paramsTypes SQL Types of parameters
     * @return Validate Result
     */
    public static ValidateResult updateValidate(String dbName, String sql, int[] paramsTypes) {
        Object[] mockedVals = mockObjectValues(paramsTypes);
        return updateValidate(dbName, sql, paramsTypes, mockedVals);
    }


    /**
     * Validate the Non-Query SQL Statement
     *
     * @param dbName The database name
     * @param sql SQL Statement
     * @param paramsTypes SQL Types of parameters
     * @param mockedVals Parameter values
     * @return Validate Result
     */
    private static ValidateResult updateValidate(String dbName, String sql, int[] paramsTypes, Object[] mockedVals) {
        ValidateResult status = new ValidateResult(sql);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DataSourceUtil.getConnection(dbName);
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(SqlBuilder.net2Java(sql));
            if (paramsTypes != null) {
                for (int i = 1; i <= paramsTypes.length; i++) {
                    if (paramsTypes[i - 1] == 10001) {
                        preparedStatement.setObject(i, mockedVals[i - 1], Types.CHAR);
                    } else {
                        preparedStatement.setObject(i, mockedVals[i - 1], paramsTypes[i - 1]);
                    }
                }
            }
            int rows = preparedStatement.executeUpdate();
            status.setAffectRows(rows);
            status.setPassed(true).append("Validate Successfully");
        } catch (Exception e) {
            status.append(e.getMessage());
        } finally {
            ResourceUtils.close(preparedStatement);
            ResourceUtils.rollback(connection);
            ResourceUtils.close(connection);
        }

        return status;
    }

    /**
     * Validate the Query SQL Statement. Parameters will be auto-mocked according to the specified SQL Types
     *
     * @param dbName The database name
     * @param sql SQL Statement
     * @param paramsTypes SQL Types of parameters
     * @return Validate Result
     */

    public static ValidateResult queryValidate(String dbName, String sql, int[] paramsTypes) {
        Object[] mockedVals = mockObjectValues(paramsTypes);
        return queryValidate(dbName, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the Query SQL Statement. Parameters will be be parsed from specified String values
     *
     * @param dbName The database name
     * @param sql SQL Statement
     * @param paramsTypes SQL Types of parameters
     * @param vals The parameter values
     * @return Validate Result
     */

    public static ValidateResult queryValidate(String dbName, String sql, int[] paramsTypes, String[] vals) {
        Object[] mockedVals = parseSQLValue(paramsTypes, vals);
        return queryValidate(dbName, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the Query SQL Statement.
     *
     * @param dbName The database name
     * @param sql SQL Statement
     * @param paramsTypes SQL Types of parameters
     * @param mockedVals The parameter values
     * @return Validate Result
     */

    private static ValidateResult queryValidate(String dbName, String sql, int[] paramsTypes, Object[] mockedVals) {
        ValidateResult status = new ValidateResult(sql);
        Connection connection = null;
        try {
            connection = DataSourceUtil.getConnection(dbName);
            String dbType = getDBType(connection, dbName);
            if (dbType == "MySQL") {
                mysqlQuery(connection, sql, status, paramsTypes, mockedVals);
            } else if (dbType.equals("Microsoft SQL Server")) {
                sqlserverQueryWithoutExplain(connection, sql, status, paramsTypes, mockedVals);
            }
        } catch (Exception e) {
            status.clearAppend(e.getMessage());
        } finally {
            ResourceUtils.close(connection);
        }
        return status;
    }

    /**
     * Validate the SQL Server Query SQL Statement.
     *
     * @param connection SQL Connection
     * @param sql SQL Statement
     * @param status Result to be updated
     * @param paramsTypes SQL Types of parameters
     */

    private static void sqlserverQueryWithoutExplain(Connection connection, String sql, ValidateResult status,
            int[] paramsTypes, Object[] vals) {
        sqlserverExplain(connection, sql, status, paramsTypes, vals);
        if (status.isPassed()) {
            ResultSet rs = null;
            PreparedStatement stat = null;
            try {
                stat = connection.prepareStatement(SqlBuilder.net2Java(sql));
                for (int i = 1; i <= paramsTypes.length; i++) {
                    if (paramsTypes[i - 1] == 10001)
                        stat.setObject(i, vals[i - 1], Types.CHAR);
                    else
                        stat.setObject(i, vals[i - 1], paramsTypes[i - 1]);
                }
                rs = stat.executeQuery();
                int affectRows = 0;
                while (rs.next()) {
                    affectRows++;
                }
                status.setAffectRows(affectRows);
            } catch (SQLException e) {
                status.setPassed(false);
                status.append(e.getMessage());
            } catch (Exception e) {
                status.setPassed(false);
                status.append(e.getMessage());
            } finally {
                ResourceUtils.close(rs);
                ResourceUtils.close(stat);
            }
        }
    }

    private static void sqlserverExplain(Connection connection, String sql, ValidateResult status, int[] paramsTypes,
            Object[] vals) {
        status.append("The SQL Server explain is not supported!");
        status.setPassed(true);
    }

    /**
     * Validate the MySQL Query SQL Statement.
     *
     * @param connection SQL Connection
     * @param sql SQL Statement
     * @param status Result to be updated
     * @param paramsTypes SQL Types of parameters
     */

    private static void mysqlQuery(Connection connection, String sql, ValidateResult status, int[] paramsTypes,
            Object[] vals) {
        mysqlExplain(connection, sql, status, paramsTypes, vals);

        if (status.isPassed()) {
            ResultSet rs = null;
            PreparedStatement stat = null;
            try {
                String sql_content = SqlBuilder.net2Java(sql);
                stat = connection.prepareStatement(sql_content);

                for (int i = 1; i <= paramsTypes.length; i++) {
                    stat.setObject(i, vals[i - 1], paramsTypes[i - 1]);
                }

                rs = stat.executeQuery();
                int affectRows = 0;
                while (rs.next()) {
                    affectRows++;
                }
                status.setAffectRows(affectRows);
            } catch (SQLException e) {
                status.setPassed(false);
                status.append(e.getMessage());
            } catch (Exception e) {
                status.setPassed(false);
                status.append(e.getMessage());
            } finally {
                ResourceUtils.close(rs);
                ResourceUtils.close(stat);
            }
        }
    }


    private static void mysqlExplain(Connection connection, String sql, ValidateResult status, int[] paramsTypes,
            Object[] vals) {
        ResultSet rs = null;
        PreparedStatement stat = null;
        try {
            String sql_content = "EXPLAIN " + SqlBuilder.net2Java(sql);
            stat = connection.prepareStatement(sql_content);

            for (int i = 1; i <= paramsTypes.length; i++) {
                stat.setObject(i, vals[i - 1], paramsTypes[i - 1]);
            }

            rs = stat.executeQuery();
            List<MySQLExplain> explains = new ArrayList<MySQLExplain>();
            while (rs.next()) {
                explains.add(ORMUtils.map(rs, MySQLExplain.class));
            }
            status.append(objectMapper.writeValueAsString(explains));
            status.setPassed(true);
        } catch (SQLException e) {
            status.append(e.getMessage());
        } catch (JsonProcessingException e) {
            status.append(e.getMessage());
        } catch (Exception e) {
            status.append(e.getMessage());
        } finally {
            ResourceUtils.close(rs);
            ResourceUtils.close(stat);
        }
    }

    /**
     * Get the database category, which is SQL Server or MySQL
     *
     * @param conn Connection
     * @param dbName Database Name
     * @return The category name
     * @throws SQLException
     */

    private static String getDBType(Connection conn, String dbName) throws SQLException {
        String dbType = null;
        if (Consts.databaseType.containsKey(dbName)) {
            dbType = Consts.databaseType.get(dbName);
        } else {
            dbType = conn.getMetaData().getDatabaseProductName();
            Consts.databaseType.put(dbName, dbType);
        }
        return dbType;
    }

    /**
     * Mock a object according to the SQL Type
     *
     * @param javaSqlTypes The specified SQL Type @see java.sql.Types
     * @return Mocked object
     */

    private static Object mockSQLValue(int javaSqlTypes) {
        switch (javaSqlTypes) {
            case java.sql.Types.BIT:
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT:
                return 0;
            case java.sql.Types.REAL:
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.DECIMAL:
                return 0.0;
            case java.sql.Types.NUMERIC:
                return BigDecimal.ZERO;
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.NULL:
            case java.sql.Types.OTHER:
                return null;
            case java.sql.Types.CHAR:
                return "X";
            case java.sql.Types.DATE:
                return java.sql.Date.valueOf("2012-01-01");
            case java.sql.Types.TIME:
                return Time.valueOf("10:00:00");
            case java.sql.Types.TIMESTAMP:
                return Timestamp.valueOf("2012-01-01 10:00:00");
            case microsoft.sql.Types.DATETIMEOFFSET:
                return DateTimeOffset.valueOf(Timestamp.valueOf("2012-01-01 10:00:00"), 0);
            case java.sql.Types.VARCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.LONGNVARCHAR:
            case java.sql.Types.LONGVARCHAR:
                return "TT";
            case 10001: // uniqueidentifier
                return "C4AECF65-1D5C-47B6-BFFC-0C9550C4E158";
            default:
                return null;

        }
    }

    private static Object[] parseSQLValue(int[] sqlTypes, String[] vals) {
        if (sqlTypes == null || vals == null || sqlTypes.length != vals.length || sqlTypes.length == 0)
            return new Object[] {};
        else {
            Object[] objs = new Object[sqlTypes.length];
            for (int i = 0; i < objs.length; i++) {
                objs[i] = parseSQLValue(sqlTypes[i], vals[i]);
            }
            return objs;
        }
    }

    /**
     * Parse the String value to java Object according to different SQL Type
     *
     * @param javaSqlTypes The SQL Types @see java.sql.Types
     * @param val The string value
     * @return Java Object
     */
    private static Object parseSQLValue(int javaSqlTypes, String val) {
        if (null == val || val.equalsIgnoreCase("null"))
            return null;
        switch (javaSqlTypes) {
            case java.sql.Types.BIT:
                return Integer.parseInt(val) == 0 ? 0 : 1;
            case java.sql.Types.TINYINT:
                return Byte.parseByte(val);
            case java.sql.Types.SMALLINT:
                return Short.parseShort(val);
            case java.sql.Types.INTEGER:
                return Integer.parseInt(val);
            case java.sql.Types.BIGINT:
                return Long.parseLong(val);
            case java.sql.Types.REAL:
                return Float.parseFloat(val);
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
                return Double.parseDouble(val);
            case java.sql.Types.DECIMAL:
            case java.sql.Types.NUMERIC:
                return BigDecimal.valueOf(Double.parseDouble(val));
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
                return val.getBytes();
            case java.sql.Types.NULL:
            case java.sql.Types.OTHER:
                return null;
            case java.sql.Types.CHAR:
                return val;
            case java.sql.Types.DATE:
                // return Date.valueOf(val);
                return parseDate(val);
            case java.sql.Types.TIME:
                return Time.valueOf(val);
            case java.sql.Types.TIMESTAMP:
                return Timestamp.valueOf(val);
            case microsoft.sql.Types.DATETIMEOFFSET:
                return DateTimeOffset.valueOf(Timestamp.valueOf(val), 0);
            case java.sql.Types.VARCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.LONGNVARCHAR:
            case java.sql.Types.LONGVARCHAR:
                return val;
            case 10001: // uniqueidentifier
                return val;
            default:
                return null;

        }
    }

    private static Date parseDate(String val) {
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date cur = format1.parse(val);
            return new Date(cur.getTime());
        } catch (ParseException e) {
        }
        return null;
    }
}
