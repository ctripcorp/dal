package com.ctrip.platform.dao.logstic;

import java.util.List;

import com.ctrip.platform.dal.dao.*;

public class MySqlFreeDaoTest {
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
			
			MySqlFreeDao dao = new MySqlFreeDao();
			

			// Test fquery1: MySql自定义SQL常规测试: List + Entity+Paging
		    List<PeoplePojo> results1 = dao.fquery1(1, 10, new DalHints());
			System.out.println(results1.size());

			// Test fquery2: MySql自定义SQL常规测试: List+Entity+No Paging
		    List<PeoplePojo> results2 = dao.fquery2(new DalHints());
			System.out.println(results2.size());

			// Test fquery3: MySql自定义SQL常规测试: Single+Simple+Paging
			Integer id3 = 1;// Test value here
		    String results3 = dao.fquery3(id3, 1, 10, new DalHints());
			System.out.println(results3);

			// Test fquery4: MySql自定义SQL常规测试: Single+Simple+no Paging
		    Integer results4 = dao.fquery4(new DalHints());
			System.out.println(results4);

			// Test fquery5: MySql自定义SQL常规测试: First+Simple+Paging
			String name5 = "test";// Test value here
		    Integer results5 = dao.fquery5(name5, 1, 10, new DalHints());
			System.out.println(results5);

			// Test fquery6: MySql自定义SQL常规测试: First+Simple+no Paging
			Integer age6 = 1;// Test value here
		    Integer results6 = dao.fquery6(age6, new DalHints());
			System.out.println(results6);

			// Test fquery7: MySql自定义SQL常规测试: List+Simple+Paging
		    List<String> results7 = dao.fquery7(1, 10, new DalHints());
			System.out.println(results7.size());

			// Test fquery8: MySql自定义SQL常规测试: List+Simple+no Paging
			Integer age8 = 1;// Test value here
		    List<Integer> results8 = dao.fquery8(age8, new DalHints());
			System.out.println(results8.size());

			// Test fquery9: MySql自定义SQL常规测试: Singel+Entity+Paging
		    PeoplePojo results9 = dao.fquery9(1, 10, new DalHints());
			System.out.println(results9);

			// Test fquery10: MySql自定义SQL常规测试: Singel+Entity+No Paging
		    PeoplePojo results10 = dao.fquery10(new DalHints());
			System.out.println(results10);

			// Test fquery11: MySql自定义SQL常规测试: First+Entity+Paging
		    PeoplePojo results11 = dao.fquery11(1, 10, new DalHints());
			System.out.println(results11);

			// Test fquery12: MySql自定义SQL常规测试: First+Entity+no Paging
		    PeoplePojo results12 = dao.fquery12(new DalHints());
			System.out.println(results12);
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
