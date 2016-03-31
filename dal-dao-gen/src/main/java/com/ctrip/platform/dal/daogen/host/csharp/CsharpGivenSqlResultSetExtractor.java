package com.ctrip.platform.dal.daogen.host.csharp;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CsharpGivenSqlResultSetExtractor implements ResultSetExtractor<List<AbstractParameterHost>> {
    @Override
    public List<AbstractParameterHost> extractData(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        List<AbstractParameterHost> pHosts = new ArrayList<>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            CSharpParameterHost pHost = new CSharpParameterHost();
            pHost.setName(rsmd.getColumnLabel(i));
            String typename = rsmd.getColumnTypeName(i);
            int dataType = rsmd.getColumnType(i);
            DbType dbType;
            if (null != typename && typename.equalsIgnoreCase("year")) {
                dbType = DbType.Int16;
            } else if (null != typename && typename.equalsIgnoreCase("uniqueidentifier")) {
                dbType = DbType.Guid;
            } else if (null != typename && typename.equalsIgnoreCase("sql_variant")) {
                dbType = DbType.Object;
            } else if (-155 == dataType) {
                dbType = DbType.DateTimeOffset;
            } else {
                dbType = DbType.getDbTypeFromJdbcType(dataType);
            }
            pHost.setDbType(dbType);
            pHost.setType(DbType.getCSharpType(pHost.getDbType()));
            pHost.setIdentity(false);
            pHost.setNullable(rsmd.isNullable(i) == 1 ? true : false);
            pHost.setPrimary(false);
            pHost.setLength(rsmd.getColumnDisplaySize(i));
            pHost.setValueType(Consts.CSharpValueTypes.contains(pHost.getType()));
            pHosts.add(pHost);
        }
        return pHosts;
    }

}
