package com.ctrip.platform.international.daogen.dao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.international.daogen.dao.common.AbstractDAO;
import com.ctrip.platform.international.daogen.dao.exception.ParametersInvalidException;
import com.ctrip.platform.international.daogen.dao.param.Parameter;

public class GeneratorDAOSPDAO extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(GeneratorDAOSPDAO.class);

	public GeneratorDAOSPDAO() {
	}

	
	//None									
	public  int  demoInsertSp(Parameter... params)
			throws Exception {
		
		final String spName = "dbo.demoInsertSp";
	
		
			return super.executeSp(null, spName, 0, params);
		
	}
	
	//None									
	public  ResultSet  demoSelectSp(Parameter... params)
			throws Exception {
		
		final String spName = "dbo.demoSelectSp";
	
		
			return super.fetchBySp(null, spName, 0, params);
		
	}
	

}
