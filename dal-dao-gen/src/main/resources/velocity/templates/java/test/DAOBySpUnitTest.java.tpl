package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end

import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;

public class ${host.getDbName()}SpDaoUnitTest {
	private static final String DATA_BASE = "${host.getDbName()}";

	private static ${host.getDbName()}SpDao dao = null;
	private static DalClient client = null;
		
	//The optional setup SQL, which will be executed on test begin.
	private static String[] setupsqls = null;
	//The optional cleanup SQL, which will be executed on test end.
	private static String[] cleanupsqls = null;
	
	static{
		try{
    		/**
    		* Initialize DalClientFactory.
    		* The Dal.config can be specified from class-path or local file path.
    		* One of follow three need to be enabled.
    		**/
    		//DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
    		DalClientFactory.initClientFactory(); // load from class-path Dal.config
    		//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
    		
    		client = DalClientFactory.getClient(DATA_BASE);
    		dao = new ${host.getDbName()}SpDao();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(null != setupsqls && setupsqls.length >= 0){
			client.batchUpdate(setupsqls, new DalHints());
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(null != cleanupsqls && cleanupsqls.length >= 0){
			client.batchUpdate(cleanupsqls, new DalHints());
		}
	}
#foreach($h in $host.getSpHosts())
	//Test batch call ${h.getSpName()} method
	@Test
	public void testCall${h.getPojoClassName()}(){
		${h.getPojoClassName()} params = null;// TODO: Test data
		try{
			int[] ret = dao.batchCall${h.getPojoClassName()}(params, new DalHints()));
			assertTrue(ret != null);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Test call ${h.getSpName()} method
	@Test
	public void testCall${h.getPojoClassName()}(){
		${h.getPojoClassName()} param = new ${h.getPojoClassName()}();	
		// Set test value here
		//param.setXXX(value);
		try{
			Map<String, ?> result = dao.call${h.getPojoClassName()}(param, new DalHints());
			assertTrue(result != null && result.size() >= 0);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
}
