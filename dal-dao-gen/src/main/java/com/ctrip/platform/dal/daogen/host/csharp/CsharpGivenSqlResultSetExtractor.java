package com.ctrip.platform.dal.daogen.host.csharp;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.enums.DbType;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CsharpGivenSqlResultSetExtractor implements DalResultSetExtractor<List<AbstractParameterHost>> {
    private DatabaseCategory dbCategory;

    public CsharpGivenSqlResultSetExtractor(DatabaseCategory dbCategory) {
        super();
        this.dbCategory = dbCategory;
    }

    @Override
    public List<AbstractParameterHost> extract(ResultSet rs) throws SQLException {
        List<AbstractParameterHost> hosts = new ArrayList<>();
        if (rs == null) {
            return hosts;
        }

        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            CSharpParameterHost host = new CSharpParameterHost();
            String columnName = metaData.getColumnLabel(i);
            host.setName(columnName);
            String typeName = metaData.getColumnTypeName(i);
            boolean isUnsigned = DbUtils.isColumnUnsigned(typeName);
            int dataType = metaData.getColumnType(i);
            int length = metaData.getColumnDisplaySize(i);
            // 特殊处理
            DbType dbType = DbUtils.getDotNetDbType(typeName, dataType, length, isUnsigned, dbCategory);
            host.setDbType(dbType);
            String type = DbType.getCSharpType(host.getDbType());
            host.setType(type);
            host.setIdentity(false);
            host.setNullable(metaData.isNullable(i) == 1 ? true : false);
            host.setPrimary(false);
            host.setLength(length);
            host.setValueType(Consts.CSharpValueTypes.contains(host.getType()));
            hosts.add(host);
        }

        return hosts;
    }
}
