package com.ctrip.platform.dal.daogen.host.csharp;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.enums.DbType;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsharpColumnNameResultSetExtractor implements ResultSetExtractor<List<AbstractParameterHost>> {
    private String allInOneName;
    private String tableName;
    private DatabaseCategory dbCategory;

    public CsharpColumnNameResultSetExtractor(String allInOneName, String tableName, DatabaseCategory dbCategory) {
        super();
        this.allInOneName = allInOneName;
        this.tableName = tableName;
        this.dbCategory = dbCategory;
    }

    @Override
    public List<AbstractParameterHost> extractData(ResultSet rs) throws SQLException {
        List<AbstractParameterHost> allColumns = new ArrayList<>();
        if (rs == null) {
            return allColumns;
        }

        Map<String, String> columnComment;
        try {
            columnComment = DbUtils.getSqlserverColumnComment(allInOneName, tableName);
        } catch (Exception e) {
            throw new SQLException(e.getMessage(), e);
        }

        while (rs.next()) {
            CSharpParameterHost host = new CSharpParameterHost();
            String columnName = rs.getString("COLUMN_NAME");
            host.setName(columnName);
            String typeName = rs.getString("TYPE_NAME");
            boolean isUnsigned = DbUtils.isColumnUnsigned(typeName);
            int dataType = rs.getInt("DATA_TYPE");
            int length = rs.getInt("COLUMN_SIZE");
            // 特殊处理
            DbType dbType = DbUtils.getDotNetDbType(typeName, dataType, length, isUnsigned, dbCategory);
            host.setDbType(dbType);
            String remark = rs.getString("REMARKS");
            if (remark == null) {
                String description = columnComment.get(columnName.toLowerCase());
                remark = description == null ? "" : description;
            }
            host.setComment(remark.replace("\n", " "));
            String type = DbType.getCSharpType(host.getDbType());
            host.setType(type);
            host.setIdentity(rs.getString("IS_AUTOINCREMENT").equalsIgnoreCase("YES"));
            host.setNullable(rs.getShort("NULLABLE") == DatabaseMetaData.columnNullable);
            host.setValueType(Consts.CSharpValueTypes.contains(host.getType()));
            // 仅获取String类型的长度
            if ("string".equalsIgnoreCase(host.getType()))
                host.setLength(length);

            allColumns.add(host);
        }
        return allColumns;
    }
}
