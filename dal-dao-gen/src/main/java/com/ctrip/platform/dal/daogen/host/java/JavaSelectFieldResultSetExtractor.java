package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JavaSelectFieldResultSetExtractor implements DalResultSetExtractor<List<AbstractParameterHost>> {

    @Override
    public List<AbstractParameterHost> extract(ResultSet rs) throws SQLException {
        ResultSetMetaData rsMeta = rs.getMetaData();
        List<AbstractParameterHost> hosts = new ArrayList<>();
        for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
            JavaParameterHost paramHost = new JavaParameterHost();
            paramHost.setName(rsMeta.getColumnLabel(i));
            paramHost.setSqlType(rsMeta.getColumnType(i));
            Class<?> javaClass = null;
            try {
                javaClass = Class.forName(rsMeta.getColumnClassName(i));
            } catch (Exception e) {
                javaClass = Consts.jdbcSqlTypeToJavaClass.get(paramHost.getSqlType());
            }
            paramHost.setJavaClass(javaClass);
            paramHost.setIdentity(false);
            paramHost.setNullable(rsMeta.isNullable(i) == 1 ? true : false);
            paramHost.setPrimary(false);
            paramHost.setLength(rsMeta.getColumnDisplaySize(i));
            hosts.add(paramHost);
        }
        return hosts;
    }

}
