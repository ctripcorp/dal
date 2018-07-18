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

public class ${host.getPojoClassName()}DaoUnitTest {

	private static final String DATA_BASE = "${host.getDbSetName()}";

	private static ${host.getPojoClassName()}Dao dao = null;
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
		dao = new ${host.getPojoClassName()}Dao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}
	
	@Test
	public void testGetAll() throws Exception {
		List<${host.getPojoClassName()}> pojos = dao.getAll(new DalHints());
	}
	
	@Test
	public void testCount() throws Exception {
		int count = dao.count(new DalHints());
	}
	
	@Test
	public void testGetListByPage() throws Exception {
		int pageNo = 1;
		int pageSize = 10;
		List<${host.getPojoClassName()}> pojos = dao.getListByPage(pageSize, pageNo, new DalHints());
	}
}