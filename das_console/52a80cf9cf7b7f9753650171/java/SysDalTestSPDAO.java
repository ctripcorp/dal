package com.ctrip.flight.intl.engine;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class SysDalTestSPDAO extends AbstractDAO {

	public SysDalTestSPDAO() {
		logicDbName = "SysDalTest";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}

	public int demoInsertSp(String name, String address){
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(name).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(address).build());

		return this.executeSP("dbo.demoInsertSp", parameters, null);
	}
	
}
