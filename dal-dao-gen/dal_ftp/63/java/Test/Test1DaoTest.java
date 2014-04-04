package com.ctrip.dal.test.test4;


import com.ctrip.platform.dal.dao.DalClientFactory;
import java.util.List;

public class Test1DaoTest {
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
			
			Test1Dao dao = new Test1Dao();
			
			System.out.println(dao.Count());
			List<Test1> ls = dao.getAll();
			if(null != ls)
				System.out.println(ls.size());
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}