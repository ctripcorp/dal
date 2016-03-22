package ShardColModShardByDBOnMysql;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.sql.SQLException;

import org.junit.*;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of PersonGenShardColModByDbOnMySqlLljDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class PersonGenShardColModByDbOnMySqlLljDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnMysql";

	private static DalClient client = null;
	private static PersonGenShardColModByDbOnMySqlLljDao dao = null;
	
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
		dao = new PersonGenShardColModByDbOnMySqlLljDao();
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
	
	
	@Test
	public void testCount() throws Exception {
		int ret = dao.count(new DalHints());
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		PersonGenShardColModByDbOnMySqlLlj daoPojo = null;
		int ret = dao.delete(hints, daoPojo); 
	}
	
	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
		List<PersonGenShardColModByDbOnMySqlLlj> daoPojos = null;
		int[] affected = dao.delete(hints, daoPojos);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<PersonGenShardColModByDbOnMySqlLlj> daoPojos = null;
		int[] affected = dao.batchDelete(hints, daoPojos);
	}
	
	@Test
	public void testGetAll() throws Exception {
		List<PersonGenShardColModByDbOnMySqlLlj> list = dao.getAll(new DalHints());
	}
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		PersonGenShardColModByDbOnMySqlLlj daoPojo = null;
		int affected = dao.insert(hints, daoPojo);
	}
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		List<PersonGenShardColModByDbOnMySqlLlj> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		PersonGenShardColModByDbOnMySqlLlj daoPojo = null;
		int affected = dao.insert(hints, keyHolder, daoPojo);
	}
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<PersonGenShardColModByDbOnMySqlLlj> daoPojos = null;
		int[] affected = dao.insert(hints, keyHolder, daoPojos);
	}
	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<PersonGenShardColModByDbOnMySqlLlj> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}
	
	@Test
	public void testCombinedInsert1() throws Exception {
		DalHints hints = new DalHints();
		List<PersonGenShardColModByDbOnMySqlLlj> daoPojos = null;
		int affected = dao.combinedInsert(hints, daoPojos);
	}
	
	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<PersonGenShardColModByDbOnMySqlLlj> daoPojos = null;
		int affected = dao.combinedInsert(hints, keyHolder, daoPojos);
	}
	
	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<PersonGenShardColModByDbOnMySqlLlj> list = dao.queryByPage(pageSize, pageNo, hints);
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = null;
		DalHints hints = new DalHints();
		PersonGenShardColModByDbOnMySqlLlj ret = dao.queryByPk(id, hints);
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		PersonGenShardColModByDbOnMySqlLlj pk = null;
		DalHints hints = new DalHints();
		PersonGenShardColModByDbOnMySqlLlj ret = dao.queryByPk(pk, hints);
	}
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		PersonGenShardColModByDbOnMySqlLlj daoPojo = null;
		int ret = dao.update(hints, daoPojo);
	}
	
	@Test
	public void testUpdate2() throws Exception {
		DalHints hints = new DalHints();
		List<PersonGenShardColModByDbOnMySqlLlj> daoPojos = null;
		int[] ret = dao.update(hints, daoPojos);
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<PersonGenShardColModByDbOnMySqlLlj> daoPojos = null;
		int[] ret = dao.batchUpdate(hints, daoPojos);
	}
	
	
	
	//构建SQL，新增
	@Test
	public void testtest_build_insert() throws Exception {
		String Name = "InsertInAllShards";// Test value here
		Integer Age = 100;// Test value here
	    int ret = dao.test_build_insert(Name, Age, new DalHints().inAllShards());
	    assertEquals(2, ret);
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    Name = "InsertByShards";
	    ret = dao.test_build_insert(Name, Age, new DalHints().inShards(shards));
	    assertEquals(2, ret);

	}
	
	
	//构建SQL，更新
	@Test
	public void testtest_build_update() throws Exception {
		String Name = "updateInAllSahrds";// Test value here
		Integer Age = 100;// Test value here
	    int ret = dao.test_build_update(Name, Age, new DalHints().inAllShards());
	    assertEquals(4, ret);
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    Name = "updateBySahrds";
	    ret = dao.test_build_update(Name, Age, new DalHints().inShards(shards));
	    assertEquals(4, ret);
	}

	//构建SQL，查询
	@Test
	public void testtest_build_query() throws Exception {
		Integer Age = 100;// Test value here
	    List<PersonGenShardColModByDbOnMySqlLlj> ret = dao.test_build_query(Age, new DalHints().inAllShards());
	    assertEquals(4, ret.size());
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.test_build_query(Age, new DalHints().inShards(shards));
	    assertEquals(4, ret.size());
	}
	
	//构建SQL，删除
	@Test
	public void testtest_build_delete() throws Exception {
		Integer param1 = 100;// Test value here
	    int ret = dao.test_build_delete(param1, new DalHints().inAllShards());
	    assertEquals(4, ret);
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    param1 = 26;// Test value here
	    ret = dao.test_build_delete(param1, new DalHints().inShards(shards));
	    assertEquals(4, ret);
	}

}
