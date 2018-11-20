package test.${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end
import ${host.getPackageName()}.dao.${host.getPojoClassName()}Dao;
import ${host.getPackageName()}.entity.${host.getPojoClassName()};

import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;

public class ${host.getDbSetName()}SpDaoUnitTest {
	private static final String DATA_BASE = "${host.getDbSetName()}";

	private static ${host.getDbSetName()}SpDao dao = null;
	private static DalClient client = null;
		
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		* Initialize DalClientFactory.
		* The Dal.config can be specified from class-path or local file path.
		* One of follow three need to be enabled.
		**/
		//DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
		//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
		
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new ${host.getDbSetName()}SpDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
#foreach($h in $host.getSpHosts())
#if($h.isSp3())
	//Test batch call ${h.getSpName()} method
	@Test
	public void testCall${h.getPojoClassName()}() throws Exception {
//		${h.getPojoClassName()} params = null;
//		int[] ret = dao.batchCall${h.getPojoClassName()}(params, new DalHints()));
//		assertTrue(ret != null);
	}
#end	
	//Test call ${h.getSpName()} method
	@Test
	public void testCall${h.getPojoClassName()}() throws Exception {
//		${h.getPojoClassName()} param = new ${h.getPojoClassName()}();
		// Set test value here
		//param.setXXX(value);
//		Map<String, ?> result = dao.call${h.getPojoClassName()}(param, new DalHints());
//		assertTrue(result != null && result.size() >= 0);
	}
#end
}
