package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JavaGivenSqlResultSetExtractor implements ResultSetExtractor<List<AbstractParameterHost>> {
    @Override
    public List<AbstractParameterHost> extractData(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        List<AbstractParameterHost> paramHosts = new ArrayList<>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            JavaParameterHost paramHost = new JavaParameterHost();
            paramHost.setName(rsmd.getColumnLabel(i));
            paramHost.setSqlType(rsmd.getColumnType(i));
            //paramHost.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(paramHost.getSqlType()));
            Class<?> javaClass = null;
            try {
                javaClass = Class.forName(rsmd.getColumnClassName(i));
            } catch (Exception e) {
                e.printStackTrace();
                javaClass = Consts.jdbcSqlTypeToJavaClass.get(paramHost.getSqlType());
            }
            paramHost.setJavaClass(javaClass);
            paramHost.setIdentity(false);
            paramHost.setNullable(rsmd.isNullable(i) == 1 ? true : false);
            paramHost.setPrimary(false);
            paramHost.setLength(rsmd.getColumnDisplaySize(i));
            paramHosts.add(paramHost);
        }
        return paramHosts;
    }

}
