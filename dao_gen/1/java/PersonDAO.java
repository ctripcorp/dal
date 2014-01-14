package com.ctrip.platform.tools;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class PersonDAO extends AbstractDAO {

	public PersonDAO() {
		logicDbName = "SysDalTest";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}

	public ResultSet getAllByID(int ID) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(ID).build());
		return this.fetch("SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person WHERE  ID = @ID ", parameters, null);
	}

	
	public int insertBySPA(int ID, String Address, String Name, String Telephone, int Age, int Gender, Timestamp Birth) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(ID).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(Address).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(Name).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(Telephone).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(Age).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(Gender).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.DateTime).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(Birth).build());

		return this.executeSP("spa_Person_i", parameters, null);
	}

	
}
