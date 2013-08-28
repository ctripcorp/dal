package com.ctrip.sysdev.apptools.daogen.dao.common;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.apptools.daogen.dao.msg.AvailableType;

public class SPDAO extends AbstractDAO {
	
	/**
	 * The logger 
	 */
	private static final Logger logger = LoggerFactory.getLogger(SPDAO.class);
	
	public ResultSet fetchBySp(String ctnCtxt, String sp, int flag, 
			AvailableType... params) throws Exception{
		
		return super.fetchBySp(null, sp, 0, params);
		
	}
	
	public int executeSp(String tnxCtxt, String sp, int flag,
			AvailableType... params) throws Exception{
		
		return super.executeSp(tnxCtxt, sp, flag, params);
		
	}

}
