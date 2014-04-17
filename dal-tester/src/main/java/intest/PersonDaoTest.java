package intest;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class PersonDaoTest {
	public static void main(String[] args) {
		
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of follow three need to be enabled.
			**/
			DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
			//alClientFactory.initClientFactory(); // load from class-path Dal.config
			//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
			
			PersonDao dao = new PersonDao();
		
		    Person pojo1 = new Person();
		    pojo1.setID(303);
			Person pojo2 = new Person();
			pojo2.setID(304);
			Person pojo3 = new Person();	
			pojo3.setID(305);

			int[] vals = dao.batchInsert(pojo1, pojo2, pojo3);
			System.out.println(dao.count());
			
			int[] upds = dao.batchDelete(pojo1, pojo2, pojo3);
			System.out.println(dao.count());
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
