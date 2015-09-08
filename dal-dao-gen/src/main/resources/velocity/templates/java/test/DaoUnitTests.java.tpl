package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end
import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of ${host.getPojoClassName()}Dao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class ${host.getPojoClassName()}DaoUnitTest {

	private static final String DATA_BASE = "${host.getDbSetName()}";

	private static DalClient client = null;
	private static ${host.getPojoClassName()}Dao dao = null;
	
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
	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		
	} 
	
#if($host.generateAPI(4,16))
	
	@Test
	public void testCount() throws Exception {
		int ret = dao.count(new DalHints());
	}
#end
#if($host.generateAPI(10,31))
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		${host.getPojoClassName()} daoPojo = null;
		int ret = dao.delete(hints, daoPojo); 
	}
#end
#if($host.generateAPI(86,87))
	
	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = null;
		int[] affected = dao.delete(hints, daoPojos);
	}
#end
#if($host.generateAPI(88,89))
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = null;
		int[] affected = dao.batchDelete(hints, daoPojos);
	}
#end
#if($host.generateAPI(6,18))
	
	@Test
	public void testGetAll() throws Exception {
		List<${host.getPojoClassName()}> list = dao.getAll(new DalHints());
	}
#end
#if($host.generateAPI(7,19))
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		${host.getPojoClassName()} daoPojo = null;
		int affected = dao.insert(hints, daoPojo);
	}
#end
#if($host.generateAPI(75,77))
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}
#end
#if($host.generateAPI(9,73))
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		${host.getPojoClassName()} daoPojo = null;
		int affected = dao.insert(hints, keyHolder, daoPojo);
	}
#end
#if($host.generateAPI(78,79))
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<${host.getPojoClassName()}> daoPojos = null;
		int[] affected = dao.insert(hints, keyHolder, daoPojos);
	}
#end
#if($host.generateAPI(80,81))
	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}
#end
#if($host.generateAPI(82,83) and !$host.getSpInsert().isExist())
	
	@Test
	public void testCombinedInsert1() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = null;
		int affected = dao.combinedInsert(hints, daoPojos);
	}
#end
#if($host.generateAPI(84,85) and !$host.getSpInsert().isExist())
	
	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<${host.getPojoClassName()}> daoPojos = null;
		int affected = dao.combinedInsert(hints, keyHolder, daoPojos);
	}
#end
#if($host.generateAPI(5,17))
	
	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<${host.getPojoClassName()}> list = dao.queryByPage(pageSize, pageNo, hints);
	}
#end
#if($host.hasPk())
#if($host.isIntegerPk() && $host.generateAPI(1,13))
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = null;
		DalHints hints = new DalHints();
		${host.getPojoClassName()} ret = dao.queryByPk(id, hints);
	}
#end
#if($host.generateAPI(3,15))
	
	@Test
	public void testQueryByPk2() throws Exception {
		${host.getPojoClassName()} pk = null;
		DalHints hints = new DalHints();
		${host.getPojoClassName()} ret = dao.queryByPk(pk, hints);
	}
#end
#end
#if($host.generateAPI(12,33))
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		${host.getPojoClassName()} daoPojo = null;
		int ret = dao.update(hints, daoPojo);
	}
#end
#if($host.generateAPI(90,91))
	
	@Test
	public void testUpdate2() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = null;
		int[] ret = dao.update(hints, daoPojos);
	}
#end
#if($host.generateAPI(96,97))
	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = null;
		int[] ret = dao.batchUpdate(hints, daoPojos);
	}
#end
#parse("templates/java/test/BuildSQLDaoUnitTests.java.tpl")
}
