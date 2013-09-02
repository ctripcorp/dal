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
	public  ResultSet  getByName(AvailableType... params)
			throws Exception {
		
		final int paramCount = 1;

		final String sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person WHERE ID = ?";
		
		if(params.length != paramCount){
			throw new ParametersInvalidException(String.format(
					"Required %d parameter(s), but got %d!", 
					paramCount, params.length));
		}

		return super.fetch(null, sql, 0, params);
	}
	

	

}
