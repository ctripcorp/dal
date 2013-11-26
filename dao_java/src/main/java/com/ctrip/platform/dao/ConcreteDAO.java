package com.ctrip.platform.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConcreteDAO extends AbstractDAO {
	
	public ConcreteDAO(){
		logicDbName = "SysDalTest";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}
	
	public ResultSet fetchAll(){
		return this.fetch("select * from Person", null, null);
	}
	
	public static void main(String[] args) throws SQLException {
		int count = 0;
		ResultSet rs = new ConcreteDAO().fetchAll();
		if(null != rs){
			while(rs.next()){
				count++;
			}
			rs.close();
		}
		System.out.println(count);
	}

}
