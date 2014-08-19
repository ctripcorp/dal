package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end
import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

/**
 * Auto-generated: JUnit test of ${host.getClassName()}Dao class.
**/
public class ${host.getClassName()}DaoUnitTest {

	private static final String DATA_BASE = "${host.getDbName()}";
	private static ${host.getClassName()}Dao dao = null;
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
    		dao = new ${host.getClassName()}Dao();
		
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
	
#foreach($method in $host.getMethods())
	/**
	 * Test ${method.getName()}: ${method.getComments()}
	**/
	@Test
	public void test${method.getName()}(){
#foreach($p in $method.getParameters())
		${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
#if($method.isQuery())
#if($method.isSampleType())
#if($method.isReturnList())
		try{
    		List<${method.getPojoClassName()}> results = dao.${method.getName()}(${method.getParameterNames("")});
    		//TODO: Verify the results here
    		assertTrue(results != null);
		}catch (SQLException e) {
			e.printStackTrace();
		}
#else
		try{
    		${method.getPojoClassName()} results = dao.${method.getName()}(${method.getParameterNames("")});
    		//TODO: Verify the results here
    		assertTrue(results != null);
		}catch (SQLException e) {
			e.printStackTrace();
		}
#end
#else
#if($method.isReturnList())
		try{
    		List<${method.getPojoClassName()}> results = dao.${method.getName()}(${method.getParameterNames("")});
    		//TODO: Verify the results here
    		assertTrue(results != null);
		}catch (SQLException e) {
			e.printStackTrace();
		}
#else
		try
    		${method.getPojoClassName()} results = dao.${method.getName()}(${method.getParameterNames("")});
    		//TODO: Verify the results here
    		assertTrue(results != null);
		}catch (SQLException e) {
			e.printStackTrace();
		}
#end
#end
#else
		try{
    		int results = dao.${method.getName()}(${method.getParameterNames("")});
    		//TODO: Verify the results here
    		assertTrue(results >= 0);
		}catch (SQLException e) {
			e.printStackTrace();
		}
#end
	}
#end

}