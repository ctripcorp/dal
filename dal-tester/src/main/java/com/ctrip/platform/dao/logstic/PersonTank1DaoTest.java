package com.ctrip.platform.dao.logstic;

import java.sql.Timestamp;
import java.util.List;

import com.ctrip.platform.dal.dao.*;

public class PersonTank1DaoTest {
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
			
			PersonTank1Dao dao = new PersonTank1Dao();
		
			//Query by perimary key
		    Integer iD = null;
			PersonTank1 pk = dao.queryByPk(iD,new DalHints());
			
			//Query by the Pojo which contains perimary key
			pk = dao.queryByPk(pk, new DalHints()); 
			
			//Invoke the paging function
			//Note that both the pageSize and the pageNumber must be greater than 1
			List<PersonTank1> pojos = dao.queryByPage(100, 1, new DalHints());
			
			//Get all records
			List<PersonTank1> all = dao.getAll(new DalHints());

		    PersonTank1 pojo1 = new PersonTank1();
			PersonTank1 pojo2 = new PersonTank1();
			PersonTank1 pojo3 = new PersonTank1();			
			//Set fields for pojos here
			KeyHolder kh = new KeyHolder();
			//keyHolder will pull back the auto-increament keys
			dao.insert(new DalHints(), kh, pojo1, pojo2, pojo3);
			
			Number generatedKey = kh.getKey(0);
			System.out.println(generatedKey);
		    //Make some change to the pojo1. set primary key
			dao.update(new DalHints(), pojo1);
	        //Remove the pojos according to its primary keys
		    dao.delete(new DalHints(), pojo1, pojo2, pojo3);

	        //Get the count
			int count = dao.count(new DalHints());
			System.out.println(count);
			
			// Test additional customized method
			// Test query1: MySql构建SQL常规测试: Single + Simple type+paging
			Integer age1 = null; //set you value here
			String results1 = dao.query1(age1, 1, 10, new DalHints());
			System.out.println(results1);

			// Test query2: MySql构建SQL常规测试: Single + Simple type+no paging
			Integer age2 = null; //set you value here
			String results2 = dao.query2(age2, new DalHints());
			System.out.println(results2);

			// Test query3: MySql构建SQL常规测试: List+ Simple type+paging
			Integer age3 = null; //set you value here
			List<String> results3 = dao.query3(age3, 1, 10, new DalHints());
			System.out.println(results3.size());

			// Test query4: MySql构建SQL常规测试: List+ Simple type+ no paging
			Integer age4 = null; //set you value here
			List<String> results4 = dao.query4(age4, 1, 10, new DalHints());
			System.out.println(results4.size());

			// Test query5: MySql构建SQL常规测试:First + Simple type + paging
			Integer age5 = null; //set you value here
			String results5 = dao.query5(age5, 1, 10, new DalHints());
			System.out.println(results5);

			// Test query6: MySql构建SQL常规测试: First+ Simple type+ no paging
			Integer age6 = null; //set you value here
			String results6 = dao.query6(age6, new DalHints());
			System.out.println(results6);

			// Test query7: MySql构建SQL常规测试: Single+ Entity+ paging
			Integer age7 = null; //set you value here
			List<PersonTank1> results7 = dao.query7(age7, 1, 10, new DalHints());
			System.out.println(results7.size());

			// Test query8: MySql构建SQL常规测试: Single+ Entity+ no paging
			Integer minAge8 = null; //set you value here
			Integer maxAge8 = null; //set you value here
			List<PersonTank1> results8 = dao.query8(minAge8, maxAge8, new DalHints());
			System.out.println(results8.size());

			// Test query9: MySql构建SQL常规测试: List+ Entity+ paging
			String namelike9 = null; //set you value here
			List<PersonTank1> results9 = dao.query9(namelike9, 1, 10, new DalHints());
			System.out.println(results9.size());

			// Test query10: MySql构建SQL常规测试: List+ Entity+ no paging
			Timestamp minBirth10 = null; //set you value here
			Timestamp maxBirth10 = null; //set you value here
			List<PersonTank1> results10 = dao.query10(minBirth10, maxBirth10, new DalHints());
			System.out.println(results10.size());

			// Test query11: MySql构建SQL常规测试: First+ Entity+ paging
			Integer age11 = null; //set you value here
			String address11 = null; //set you value here
			PersonTank1 results11 = dao.query11(age11, address11, 1, 10, new DalHints());
			System.out.println(results11);

			// Test query12: MySql构建SQL常规测试: First+ Entity+ no paging
			Integer partment12 = null; //set you value here
			PersonTank1 results12 = dao.query12(partment12, new DalHints());
			System.out.println(results12);

			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}