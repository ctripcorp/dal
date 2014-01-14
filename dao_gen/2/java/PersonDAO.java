package com.ctrip.platform.hello;

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

	public ResultSet getHello() {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		return this.fetch("SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person WHERE ", parameters, null);
	}

	
	
}
