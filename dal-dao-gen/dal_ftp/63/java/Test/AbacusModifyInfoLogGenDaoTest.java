package com.ctrip.dal.test.test4;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.*;

public class AbacusModifyInfoLogGenDaoTest {
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
			
			AbacusModifyInfoLogGenDao dao = new AbacusModifyInfoLogGenDao();
		
			AbacusModifyInfoLogGen pk = dao.queryByPk(null);// you value here
			
			pk = dao.queryByPk(pk);
			List<AbacusModifyInfoLogGen> pojos = dao.queryByPage(pk, 100, 0);

			// Test SPA related CUD
			// test insert
			AbacusModifyInfoLogGen pojo = new AbacusModifyInfoLogGen();
			// Set pojo attribute value here
			dao.insert(pojo);
	
			// make some change to the pojo. set primary key
			dao.update(pojo);

			// remove the pojo
			dao.delete(pojo);
	

			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}