package com.ctrip.sysdev.apptools.dao.common;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.apptools.dao.msg.AvailableType;

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
	
	public static void main(String[] args) throws Exception {
		
		//AvailableType inputParam = new <Integer> AvailableType(1, 1);
		
		SPDAO spDAO = new SPDAO();
		
		spDAO.setUseDBClient(true);
		
		AvailableType nameParam = new <String> AvailableType(1, "gawu");
		AvailableType addrParam = new <String> AvailableType(2, "shanghai");
		
		int row = spDAO.executeSp(null, "demoInsertSp", 0, nameParam, addrParam);
		
		logger.debug(String.valueOf(row));
		
//		ResultSet rs = spDAO.fetchBySp(null, "demoSelectSp",
//				0, inputParam);
//		
//		while(rs.next()){
//			logger.debug(rs.getString(1));
//		}
		
	}

}
