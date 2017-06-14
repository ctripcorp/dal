package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import microsoft.sql.DateTimeOffset;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavaColumnNameResultSetExtractor implements ResultSetExtractor<List<AbstractParameterHost>> {
    private final String TYPE_NAME = "TYPE_NAME";
    private final String COLUMN_NAME = "COLUMN_NAME";
    private final String ORDINAL_POSITION = "ORDINAL_POSITION";
    private final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";
    private final String REMARKS = "REMARKS";
    private final String COLUMN_DEF = "COLUMN_DEF";
    private final String DATA_TYPE = "DATA_TYPE";

    private static Logger log = Logger.getLogger(JavaColumnNameResultSetExtractor.class);

    private String allInOneName;
    private String tableName;
    private DatabaseCategory dbCategory;

    public JavaColumnNameResultSetExtractor(String allInOneName, String tableName, DatabaseCategory dbCategory) {
        super();
        this.allInOneName = allInOneName;
        this.tableName = tableName;
        this.dbCategory = dbCategory;
    }

    @Override
    public List<AbstractParameterHost> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<AbstractParameterHost> allColumns = new ArrayList<>();
        try {
            Map<String, Integer> columnSqlType = DbUtils.getColumnSqlType(allInOneName, tableName);
            Map<String, Class<?>> typeMapper = DbUtils.getSqlType2JavaTypeMaper(allInOneName, tableName);
            Map<String, String> columnComment;
            columnComment = DbUtils.getSqlserverColumnComment(allInOneName, tableName);

            if (columnSqlType != null && columnSqlType.size() > 0) {
                while (rs.next()) {
                    JavaParameterHost host = new JavaParameterHost();
                    String typeName = rs.getString(TYPE_NAME);
                    String columnName = rs.getString(COLUMN_NAME);
                    host.setName(columnName);
                    host.setSqlType(columnSqlType.get(host.getName()));
                    Class<?> javaClass = null;
                    if (null != typeMapper && typeMapper.containsKey(host.getName())) {
                        javaClass = typeMapper.get(host.getName());
                    } else {
                        javaClass = Consts.jdbcSqlTypeToJavaClass.get(host.getSqlType());
                    }
                    if (null == javaClass) {
                        if (null != typeName && typeName.equalsIgnoreCase("sql_variant")) {
                            log.fatal(String.format("The sql_variant is not support by java.[%s, %s, %s, %s, %s]",
                                    host.getName(), allInOneName, tableName, host.getSqlType(), javaClass));
                            return null;
                        } else if (null != typeName && typeName.equalsIgnoreCase("datetimeoffset")) {
                            javaClass = DateTimeOffset.class;
                        } else {
                            log.fatal(String.format("The java type cant be mapped.[%s, %s, %s, %s, %s]", host.getName(),
                                    allInOneName, tableName, host.getSqlType(), javaClass));
                            return null;
                        }
                    }
                    host.setJavaClass(javaClass);
                    host.setIndex(rs.getInt(ORDINAL_POSITION));
                    host.setIdentity(rs.getString(IS_AUTOINCREMENT).equalsIgnoreCase("YES"));
                    String remarks = rs.getString(REMARKS);
                    if (remarks == null) {
                        String description = columnComment.get(columnName.toLowerCase());
                        remarks = description == null ? "" : description;
                    }

                    host.setComment(remarks.replace("\n", " "));
                    host.setDefaultValue(rs.getString(COLUMN_DEF));
                    host.setDbCategory(dbCategory);
                    int dataType = rs.getInt(DATA_TYPE);
                    host.setDataType(dataType);
                    allColumns.add(host);
                }
            }

        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            // throw e;
        }
        return allColumns;
    }

}
