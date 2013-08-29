package com.ctrip.sysdev.apptools.daogen.dao.tableview;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.apptools.daogen.dao.common.AbstractDAO;
import com.ctrip.sysdev.apptools.daogen.dao.exception.ParametersInvalidException;
import com.ctrip.sysdev.apptools.daogen.dao.msg.AvailableType;

public class PersonDAO extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonDAO.class);

	private Map<String, String> dbField2POJOField;

	public PersonDAO() {
		dbField2POJOField = new HashMap<String, String>();
		// dbField2POJOField.put("Name", "name");
	}

	
	//None									
	public ResultSet getByAll(AvailableType... params)
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
