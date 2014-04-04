package com.ctrip.dal.test.test4;


import java.util.Map;
import com.ctrip.platform.dal.dao.DalClientFactory;

public class AccCorpDB_INSERT_1SpDaoTest {
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
			
			AccCorpDB_INSERT_1SpDao dao = new AccCorpDB_INSERT_1SpDao();
			
			//Test callGetret method
			Getret param = new Getret();	
			// Set test value here
			//param.setXXX(value);
			
			Map<String, ?> result = dao.callGetret(param);
			for(String key: result.keySet()) {
				System.out.print("Key: " + key);
				System.out.println(" Value: " + result.get(key));
			}
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}