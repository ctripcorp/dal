package com.ctrip.platform.dao;

import java.sql.ResultSet;

public class ConcreteDAO extends AbstractDAO {
	
	public ConcreteDAO(){
		physicDbName = "SysDalTest";
		servicePort = 9000;
		credentialID = "30303";
		super.Init();
	}
	
	public ResultSet fetchAll(){
		return this.fetch("select * from person", null, null);
	}

}
