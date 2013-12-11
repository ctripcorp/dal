package com.ctrip.sysdev.das.daogen.domain;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class SPDAO extends AbstractDAO {

	public SPDAO() {
		logicDbName = "SysDalTest";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}
	
	public ResultSet getIndexedColumns(String dbName, String tableName) {
		
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(tableName)
				.build());
		return this.fetchBySp("sp_helpindex", parameters, null);
	}
	
public ResultSet getSPCode(String dbName, String spName) {
		
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(spName)
				.build());
		return this.fetchBySp("sp_helptext", parameters, null);
	}
	
}
