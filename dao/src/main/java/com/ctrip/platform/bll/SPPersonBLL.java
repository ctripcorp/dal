package com.ctrip.platform.bll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dao.GeneratorDAOSPDAO;
import com.ctrip.platform.dao.msg.AvailableType;

public class SPPersonBLL {
	
	private static final Logger logger = LoggerFactory.getLogger(SPPersonBLL.class);
	
	public static void main(String[] args) throws Exception {
		
		GeneratorDAOSPDAO spDAO = new GeneratorDAOSPDAO();
		
		spDAO.setUseDBClient(false);
		
		AvailableType nameParam = new AvailableType(1, "gawu");
		AvailableType addrParam = new AvailableType(2, "shanghai");
		
		int row = spDAO.executeSp(null, "demoInsertSp", 0, nameParam, addrParam);
		
		logger.debug(String.valueOf(row));
		
//		AvailableType inputParam = new AvailableType(1, 1);
//		
//		ResultSet rs = spDAO.fetchBySp(null, "demoSelectSp", 0, inputParam);
//
//		while (rs.next()) {
//			logger.info(rs.getString(1));
//		}

		
	}

}
