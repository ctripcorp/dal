package com.ctrip.platform.dao;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dao.common.AbstractDAO;
import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.param.ParameterFactory;

public class SysDalTestSPDAO extends AbstractDAO {
	
	SysDalTestSPDAO() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(SysDalTestSPDAO.class);

	public int demoInsertSp(int flag, Parameter... params) throws Exception{
		return super.executeSp(null, flag, "demoInsertSp", params);
	}
	
	public ResultSet demoSelectSp(int flag, Parameter... params) throws Exception{
		return super.fetchBySp(null, flag, "demoSelectSp", params);
	}


}
