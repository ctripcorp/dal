package DAL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.*;

public class PersonGenDaoTest {
	public static void main(String[] args) {
		
		try {
			// Initialize DalClientFactory
			DalClientFactory.initClientFactory("PerformanceTest");
			PersonGenDao dao = new PersonGenDao();
		
			PersonGen pk = dao.queryByPk(null);// you value here
			
			pk = dao.queryByPk(pk);
			List<PersonGen> pojos = dao.queryByPage(pk, 100, 0);
			
			// Test normal CUD (non SPA) 
			KeyHolder keyHolder = new KeyHolder();
			PersonGen pojo1 = new PersonGen();
			PersonGen pojo2 = new PersonGen();
			PersonGen pojo3 = new PersonGen();
			dao.insert(keyHolder, pojo1, pojo2, pojo3);
			dao.delete(pojo1, pojo2, pojo3);
			dao.update(pojo1, pojo2, pojo3);
			// Test additional customized method
			int affectedRows = 0;
			List<PersonGen> results = null;
			// Test GetNameByID
			Integer ID = ${p.getValidationValue()};
		    results = dao.GetNameByID(ID});

			// Test deleteByName
			String Name = "null";
    		affectedRows = dao.deleteByName(Name});

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}