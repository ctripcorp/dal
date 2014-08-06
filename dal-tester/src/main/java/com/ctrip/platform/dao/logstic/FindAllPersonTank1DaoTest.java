package com.ctrip.platform.dao.logstic;

import java.sql.Timestamp;
import java.util.List;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class FindAllPersonTank1DaoTest {
	public static void main(String[] args) {
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of follow three need to be enabled.
			**/
			//DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
			DalClientFactory.initClientFactory(); // load from class-path Dal.config
			//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
			
			FindAllPersonTank1Dao dao = new FindAllPersonTank1Dao();
			
			//Get the count
			System.out.println(dao.Count(null));
			
			//Get all records
			List<FindAllPersonTank1> ls = dao.getAll(null);
			if(null != ls)
				System.out.println(ls.size());
				
			List<FindAllPersonTank1> lsp = dao.getListByPage(100, 1, null);
			if(null != lsp)
				System.out.println(ls.size());
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}