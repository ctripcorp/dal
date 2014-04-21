package intest;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;

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
		    pojo1.setID(10004);
		    pojo1.setName(null);
		    pojo1.setBirth(new Timestamp(System.currentTimeMillis()));
		    
		    DalHints hints = new DalHints();
			hints.set(DalHintEnum.updateNullField);
			
		    dao.update(hints,pojo1);
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
