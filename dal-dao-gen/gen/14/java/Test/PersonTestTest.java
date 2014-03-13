package DAL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class PersonTestTestDao {
	public static void main(String[] args) {
		try {
			// Initialize DalClientFactory
			DalClientFactory.initClientFactory("PerformanceTest");
			PersonTestDao dao = new PersonTestDao();
		
			// Test justATest
			Integer id = 1;// Test value here
			List<JustATestPojo> JustATestPojos = dao.justATest(id);

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
