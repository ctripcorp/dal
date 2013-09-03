package com.ctrip.platform.international.daogen.dao.tabledao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.international.daogen.dao.common.AbstractDAO;
import com.ctrip.platform.international.daogen.dao.exception.ParametersInvalidException;
import com.ctrip.platform.international.daogen.dao.msg.AvailableType;

public class PersonDAO extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonDAO.class);

	
	//None									
	public  ResultSet  getByID(AvailableType... params)
			throws Exception {

		final String sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person  WHERE  ID = ? ";

		return super.fetch(null, sql, 0, params);
	}
	
	//None									
	public  int  setByID(AvailableType... params)
			throws Exception {

		final String sql = "UPDATE Person SET ID = ?, Address = ?, Name = ?, Telephone = ?, Age = ?, Gender = ?, Birth = ?  WHERE  ID = ? ";

		return super.execute(null, sql, 0, params);
	}
	

	
	//None									
	public  int  insert(AvailableType... params)
			throws Exception {
		

		final String spName = "spa_Person_i";
	

		
			return super.executeSp(null, spName, 0, params);
		
	}
	
	//None									
	public  int  delete(AvailableType... params)
			throws Exception {
		

		final String spName = "spa_Person_d";
	

		
			return super.executeSp(null, spName, 0, params);
		
	}
	

}
