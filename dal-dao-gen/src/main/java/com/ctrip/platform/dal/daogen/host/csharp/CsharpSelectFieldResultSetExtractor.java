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

public class CsharpSelectFieldResultSetExtractor implements ResultSetExtractor<List<AbstractParameterHost>> {

	@Override
	public List<AbstractParameterHost> extractData(ResultSet rs) throws SQLException {
		ResultSetMetaData rsMeta = rs.getMetaData();
		List<AbstractParameterHost> hosts = new ArrayList<AbstractParameterHost>();
		for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
			CSharpParameterHost pHost = new CSharpParameterHost();
			pHost.setName(rsMeta.getColumnLabel(i));
			pHost.setDbType(DbType.getDbTypeFromJdbcType(rsMeta.getColumnType(i)));
			pHost.setType(DbType.getCSharpType(pHost.getDbType()));
			pHost.setIdentity(false);
			pHost.setNullable(rsMeta.isNullable(i)==1 ? true : false);
			pHost.setValueType(Consts.CSharpValueTypes.contains(pHost.getType()));
			pHost.setPrimary(false);
			pHost.setLength(rsMeta.getColumnDisplaySize(i));
			hosts.add(pHost);
		}
		return hosts;
	}

}
