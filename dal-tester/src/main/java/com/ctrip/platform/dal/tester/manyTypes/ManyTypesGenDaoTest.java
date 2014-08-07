package com.ctrip.platform.dal.tester.manyTypes;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import com.ctrip.platform.dal.dao.*;

public class ManyTypesGenDaoTest {
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
			
			ManyTypesGenDao dao = new ManyTypesGenDao();
		
			//Query by perimary key
		    Integer id = 63;
		    Integer intCol = 1;
			ManyTypesGen pk = dao.queryByPk(id,intCol,new DalHints());
			
			//Query by the Pojo which contains perimary key
			pk = dao.queryByPk(pk, new DalHints()); 
			
			//Invoke the paging function
			//Note that both the pageSize and the pageNumber must be greater than 1
			List<ManyTypesGen> pojos = dao.queryByPage(100, 1, new DalHints());
			
			//Get all records
			List<ManyTypesGen> all = dao.getAll(new DalHints());

		    ManyTypesGen pojo1 = new ManyTypesGen();
			ManyTypesGen pojo2 = new ManyTypesGen();
			ManyTypesGen pojo3 = new ManyTypesGen();			
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
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
