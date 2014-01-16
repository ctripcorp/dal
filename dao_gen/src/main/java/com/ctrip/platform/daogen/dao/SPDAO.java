package com.ctrip.platform.daogen.dao;

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
		
		return this.fetch("use "
				+dbName
				+ " create table  #rs (indexname nvarchar(255), index_description nvarchar(1000), index_keys nvarchar(1000)) "
				+ " insert #rs exec sp_helpindex '"
				+tableName
				+ "' select * from #rs drop table #rs ", null, null);
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
