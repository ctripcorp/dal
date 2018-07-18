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
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
		DalClientFactory.warmUpConnections();
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new ${host.getPojoClassName()}Dao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
//		To prepare test data, you can simply uncomment the following.
//		In case of DB and table shard, please revise the code to reflect shard
//		for(int i = 0; i < 10; i++) {
//			${host.getPojoClassName()} daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	private ${host.getPojoClassName()} createPojo(int index) {
		${host.getPojoClassName()} daoPojo = new ${host.getPojoClassName()}();

		//daoPojo.setId(index);
		//daoPojo set not null field
		
		return daoPojo;
	}

	private void changePojo(${host.getPojoClassName()} daoPojo) {
		// Change a field to make pojo different with original one
	}
	
	private void changePojos(List<${host.getPojoClassName()}> daoPojos) {
		for(${host.getPojoClassName()} daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(${host.getPojoClassName()} daoPojo) {
		//assert changed value
	}
	
	private void verifyPojos(List<${host.getPojoClassName()}> daoPojos) {
		for(${host.getPojoClassName()} daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
//		To clean up all test data
//		dao.delete(null, dao.queryAll(null));
	} 
	
#if($host.generateAPI(4,16))
	
	@Test
	public void testCount() throws Exception {
		int affected = dao.count(new DalHints());
		assertEquals(10, affected);
	}
#end
#if($host.generateAPI(10,31))
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		${host.getPojoClassName()} daoPojo = createPojo(1);
		/**
		 * WARNING !!!
		 * To test delete, please make sure you can easily restore all the data. otherwise data will not be revovered.
		 */
//		int affected = dao.delete(hints, daoPojo); 
//		assertEquals(1, affected);
	}
#end
#if($host.generateAPI(86,87))
	
	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
//		List<${host.getPojoClassName()}> daoPojos = dao.queryAll(null);
		/**
		 * WARNING !!!
		 * To test delete, please make sure you can easily restore all the data. otherwise data will not be revovered.
		 */

//		int[] affected = dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
	}
#end
#if($host.generateAPI(88,89))
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
//		List<${host.getPojoClassName()}> daoPojos = dao.queryAll(null);
		/**
		 * WARNING !!!
		 * To test batchDelete, please make sure you can easily restore all the data. otherwise data will not be revovered.
		 */
//		int[] affected = dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
	}
#end
#if($host.generateAPI(6,18))
	
	@Test
	public void testQueryAll() throws Exception {
//		List<${host.getPojoClassName()}> list = dao.queryAll(new DalHints());
//		assertEquals(10, list.size());
	}
#end
#if($host.generateAPI(7,19))
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		${host.getPojoClassName()} daoPojo = createPojo(1);
		int affected = dao.insert(hints, daoPojo);
		assertEquals(1, affected);
	}
#end
#if($host.generateAPI(75,77))
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = dao.queryAll(new DalHints());
		int[] affected = dao.insert(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
	}
#end
#if($host.generateAPI(9,73))
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		${host.getPojoClassName()} daoPojo = createPojo(1);
		int affected = dao.insert(hints, keyHolder, daoPojo);
		assertEquals(1, affected);
		assertEquals(1, keyHolder.size());
	}
#end
#if($host.generateAPI(78,79))
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<${host.getPojoClassName()}> daoPojos = dao.queryAll(new DalHints());
		int[] affected = dao.insert(hints, keyHolder, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		assertEquals(10, keyHolder.size());
	}
#end
#if($host.generateAPI(80,81))
	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = dao.queryAll(new DalHints());
		int[] affected = dao.batchInsert(hints, daoPojos);
	}
#end
#if($host.generateAPI(82,83) and !$host.getSpInsert().isExist())
	
	@Test
	public void testCombinedInsert1() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = dao.queryAll(new DalHints());
		int affected = dao.combinedInsert(hints, daoPojos);
		assertEquals(10, affected);
	}
#end
#if($host.generateAPI(84,85) and !$host.getSpInsert().isExist())
	
	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<${host.getPojoClassName()}> daoPojos = dao.queryAll(new DalHints());
		int affected = dao.combinedInsert(hints, keyHolder, daoPojos);
		assertEquals(10, keyHolder.size());
	}
#end
#if($host.generateAPI(5,17))
	
	@Test
	public void testQueryAllByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<${host.getPojoClassName()}> list = dao.queryAllByPage(pageNo, pageSize, hints);
		assertEquals(10, list.size());
	}
#end
#if($host.hasPk())
#if($host.isIntegerPk() && $host.generateAPI(1,13))
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1;
		DalHints hints = new DalHints();
		${host.getPojoClassName()} affected = dao.queryByPk(id, hints);
		assertNotNull(affected);
	}
#end
#if($host.generateAPI(3,15))
	
	@Test
	public void testQueryByPk2() throws Exception {
		${host.getPojoClassName()} pk = createPojo(1);
		DalHints hints = new DalHints();
		${host.getPojoClassName()} affected = dao.queryByPk(pk, hints);
		assertNotNull(affected);
	}
#end
#end
#if($host.generateAPI(12,33))
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		${host.getPojoClassName()} daoPojo = dao.queryByPk(createPojo(1), hints);
		changePojo(daoPojo);
		int affected = dao.update(hints, daoPojo);
		assertEquals(1, affected);
		daoPojo = dao.queryByPk(createPojo(1), null);
		verifyPojo(daoPojo);
	}
#end
#if($host.generateAPI(90,91))
	
	@Test
	public void testUpdate2() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = dao.queryAll(new DalHints());
		changePojos(daoPojos);
		int[] affected = dao.update(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		verifyPojos(dao.queryAll(new DalHints()));
	}
#end
#if($host.generateAPI(96,97))
	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> daoPojos = dao.queryAll(new DalHints());
		changePojos(daoPojos);
		int[] affected = dao.batchUpdate(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		verifyPojos(dao.queryAll(new DalHints()));
	}
#end
#parse("templates/java/test/BuildSQLDaoUnitTests.java.tpl")
}
