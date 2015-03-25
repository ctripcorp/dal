package com.ctrip.platform.dal.daogen.host.csharp;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.utils.ResultSetExtractor;

public class CsharpSelectFieldResultSetExtractor implements ResultSetExtractor<List<AbstractParameterHost>> {

	@Override
	public List<AbstractParameterHost> extract(ResultSet rs) throws SQLException {
		ResultSetMetaData rsMeta = rs.getMetaData();
		List<AbstractParameterHost> hosts = new ArrayList<AbstractParameterHost>();
		for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
			CSharpParameterHost pHost = new CSharpParameterHost();
			pHost.setName(rsMeta.getColumnLabel(i));
			pHost.setDbType(DbType.getDbTypeFromJdbcType(rsMeta.getColumnType(i)));
			pHost.setType(DbType.getCSharpType(pHost.getDbType()));
			pHost.setIdentity(false);
			pHost.setNullable(true);
			pHost.setValueType(Consts.CSharpValueTypes.contains(pHost.getType()));
			pHost.setPrimary(false);
			pHost.setLength(rsMeta.getColumnDisplaySize(i));
			hosts.add(pHost);
		}
		return hosts;
	}

}