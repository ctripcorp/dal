package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end

import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;

public class ${host.getPojoClassName()}DaoUnitTest {
	private static final String DATA_BASE = "${host.getDbName()}";

	private static ${host.getPojoClassName()}Dao dao = null;
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
    		dao = new ${host.getPojoClassName()}Dao();
		
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
	
	@Test
	public void testGetAll(){
		try{
			List<${host.getPojoClassName()}> pojos = dao.getAll(new DalHints());
			int count = dao.Count(new DalHints());
			//TODO: Verify the result.
			assertTrue(null != pojos && pojos.size() == count);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCount(){
		int count = -1;
		try{
			count = dao.Count(new DalHints());
			List<${host.getPojoClassName()}> pojos = dao.getAll(new DalHints());
			//TODO: Verify the result.
			assertTrue(count >= pojos.size());
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testQueryByPage(){
		int pageNo = 1;
		int pageSize = 10;
		try{
			List<${host.getPojoClassName()}> pojos = dao.getListByPage(pageSize, pageNo, new DalHints());
			assertTrue(pojos.size() >= 10);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
}