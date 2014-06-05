package intest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.dialect.DalMySqlHelper;

public class PersonDaoTest {
	public static void main(String[] args) {
		
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of follow three need to be enabled.
			**/
			DalClientFactory.initClientFactory(); //Load from class-path connections.properties
			//alClientFactory.initClientFactory(); // load from class-path Dal.config
			//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
			
			PersonDao dao = new PersonDao();
		
			Person[] persons = new Person[1000780-1000773];
			for(int i = 1000773; i < 1000780; i++){
			    Person pojo1 = new Person();
			    pojo1.setID(i);
			    pojo1.setName("forest" + i);
			    pojo1.setBirth(new Timestamp(System.currentTimeMillis()));
			    persons[i - 1000773] = pojo1;
			}
		    
			
		    DalHints hints = new DalHints();
		    KeyHolder holder = new KeyHolder();
		    DalMySqlHelper<Person> helper = new DalMySqlHelper<Person>(dao.getParser());
			//dao.delete(persons);
		    int x = helper.replace(holder, hints, persons);
		    
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
