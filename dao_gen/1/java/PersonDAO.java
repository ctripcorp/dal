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

	public int insertTest1(int ID, String Address, String Name, String Telephone, int Age, int Gender, Timestamp Birth) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(ID).build());
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(2).setName("").setSensitive(false).setValue(Address).build());
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(3).setName("").setSensitive(false).setValue(Name).build());
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(4).setName("").setSensitive(false).setValue(Telephone).build());
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.Input).setNullable(false).setIndex(5).setName("").setSensitive(false).setValue(Age).build());
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.Input).setNullable(false).setIndex(6).setName("").setSensitive(false).setValue(Gender).build());
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.DateTime).setDirection(ParameterDirection.Input).setNullable(false).setIndex(7).setName("").setSensitive(false).setValue(Birth).build());
		return this.execute("INSERT INTO Person (ID,Address,Name,Telephone,Age,Gender,Birth) VALUES ( @ID , @Address , @Name , @Telephone , @Age , @Gender , @Birth )", parameters, null);
	}

	public ResultSet SelectAll1(String Address) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(2).setName("").setSensitive(false).setValue(Address).build());
		return this.fetch("SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person WHERE  Address = @Address ", parameters, null);
	}

	
	public int insertTest333(int ID, String Address, String Name, String Telephone, int Age, int Gender, Timestamp Birth) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.InputOutput).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(ID).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(2).setName("").setSensitive(false).setValue(Address).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(3).setName("").setSensitive(false).setValue(Name).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String).setDirection(ParameterDirection.Input).setNullable(false).setIndex(4).setName("").setSensitive(false).setValue(Telephone).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.Input).setNullable(false).setIndex(5).setName("").setSensitive(false).setValue(Age).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32).setDirection(ParameterDirection.Input).setNullable(false).setIndex(6).setName("").setSensitive(false).setValue(Gender).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.DateTime).setDirection(ParameterDirection.Input).setNullable(false).setIndex(7).setName("").setSensitive(false).setValue(Birth).build());

		return this.executeSP("spa_Person_i", parameters, null);
	}

	
}
