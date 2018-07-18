package com.ctrip.platform.dal.daogen.host.csharp;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.daogen.enums.DbType;
import com.ctrip.platform.dal.daogen.enums.ParameterDirection;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CsharpSpParamResultSetExtractor implements DalResultSetExtractor<List<AbstractParameterHost>> {
    @Override
    public List<AbstractParameterHost> extract(ResultSet rs) throws SQLException {
        List<AbstractParameterHost> parameters = new ArrayList<>();
        while (rs.next()) {
            int paramMode = rs.getShort("COLUMN_TYPE");

            if (!DbUtils.validMode.contains(paramMode)) {
                continue;
            }

            CSharpParameterHost host = new CSharpParameterHost();
            DbType dbType = DbUtils.getDotNetDbType(rs.getString("TYPE_NAME"), rs.getInt("DATA_TYPE"),
                    rs.getInt("LENGTH"), false, null);
            host.setDbType(dbType);
            host.setNullable(rs.getShort("NULLABLE") == DatabaseMetaData.columnNullable);

            if (paramMode == DatabaseMetaData.procedureColumnIn) {
                host.setDirection(ParameterDirection.Input);
            } else if (paramMode == DatabaseMetaData.procedureColumnInOut) {
                host.setDirection(ParameterDirection.InputOutput);
            } else {
                host.setDirection(ParameterDirection.Output);
            }

            host.setName(rs.getString("COLUMN_NAME"));
            host.setType(DbType.getCSharpType(host.getDbType()));
            host.setNullable(rs.getShort("NULLABLE") == DatabaseMetaData.columnNullable);

            if (host.getType() == null) {
                host.setType("string");
                host.setDbType(DbType.AnsiString);
            }
            parameters.add(host);
        }
        return parameters;
    }

}
