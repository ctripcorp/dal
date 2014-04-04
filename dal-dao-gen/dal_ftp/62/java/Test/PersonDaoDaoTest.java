package com.ctrip.dal.test.test3;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class PersonDaoDaoTest {
	public static void main(String[] args) {
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of follow three need to be enabled.
			**/
			DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
			//DalClientFactory.initClientFactory(); // load from class-path Dal.config
			//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
			
			PersonDaoDao dao = new PersonDaoDao();
		
			// Test getPersonByAddrAndTel
			String param1 = "test";// Test value here
			String param2 = "test";// Test value here
			List<GetPersonByAddrAndTelPojo> GetPersonByAddrAndTelPojos = dao.getPersonByAddrAndTel(param1, param2);

			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
