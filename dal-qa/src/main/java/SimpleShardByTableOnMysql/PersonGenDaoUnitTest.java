package SimpleShardByTableOnMysql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import org.junit.*;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;


/**
 * JUnit test of PersonGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class PersonGenDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByTableOnMySql";

	private static DalClient client = null;
	private static PersonGenDao dao = null;
	
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
		dao = new PersonGenDao();
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
	
	
	//参数中没有POJO,不支持分表
	@Test
	public void testCount() throws Exception {
		int ret = dao.count(new DalHints().inTableShard("0"));
		assertEquals(3,ret);
		
		ret = dao.count(new DalHints().inTableShard("1"));
		assertEquals(3,ret);
	}
	
	@Test
	public void testDelete1() throws Exception {
		PersonGen daoPojo = new PersonGen();
		daoPojo.setID(3);
		int ret = dao.delete(new DalHints().inTableShard("0"), daoPojo); 
		assertEquals(1,ret);
		
		ret = dao.delete(new DalHints().inTableShard("1"), daoPojo); 
		assertEquals(1,ret);
	}
	
	@Test
	public void testDelete2() throws Exception {
		List<PersonGen> daoPojos1 = new ArrayList<PersonGen>(2);
		for(int i=0;i<2;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+20);
			daoPojo.setName("Initial_TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.delete(new DalHints().inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);
		
		List<PersonGen> daoPojos2 = new ArrayList<PersonGen>(2);
		for(int i=0;i<2;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+20);
			daoPojo.setName("Initial_TableShard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.delete(new DalHints().inTableShard("1"), daoPojos2);
		assertEquals(2,affected.length);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		
		List<PersonGen> daoPojos1 = new ArrayList<PersonGen>(2);
		for(int i=0;i<2;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("Initial_TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchDelete(new DalHints().inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);
		
		List<PersonGen> daoPojos2 = new ArrayList<PersonGen>(2);
		for(int i=0;i<2;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("Initial_TableShard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchDelete(new DalHints().inTableShard("1"), daoPojos2);
		assertEquals(2,affected.length);
	}
	
	@Test
	public void testGetAll() throws Exception {
		List<PersonGen> list = dao.getAll(new DalHints());
	}
	
	@Test
	public void testInsert1() throws Exception {
	//	DalHints hints = new DalHints();
		PersonGen daoPojo = new PersonGen();
		daoPojo.setAge(100);
		daoPojo.setName("InsertByTableShard_0");
		
		//bytableshard
		int affected = dao.insert(new DalHints().inTableShard(0), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByTableShard_1");
		affected = dao.insert(new DalHints().inTableShard(1), daoPojo);
		assertEquals(1,affected);
	}
	
	@Test
	public void testInsert2() throws Exception {
		 List<PersonGen> daoPojos1 = new ArrayList<PersonGen>(3);
			for(int i=0;i<3;i++)
			{
				PersonGen daoPojo = new PersonGen();
				daoPojo.setAge(i+20);
				daoPojo.setName("Initial_TableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
			int[] affected = dao.insert(new DalHints().inTableShard("0"), daoPojos1);
			assertEquals(3,affected.length);
			
			List<PersonGen> daoPojos2 = new ArrayList<PersonGen>(3);
			for(int i=0;i<3;i++)
			{
				PersonGen daoPojo = new PersonGen();
				daoPojo.setAge(i+20);
				daoPojo.setName("Initial_TableShard_1"+i);
				daoPojos2.add(daoPojo);
			}
			affected = dao.insert(new DalHints().inTableShard("1"), daoPojos2);
			assertEquals(3,affected.length);
	}
	
	@Test
	public void testInsert3() throws Exception {
		
		KeyHolder keyHolder = new KeyHolder();
		
		DalHints hints = new DalHints();
		PersonGen daoPojo = new PersonGen();
		daoPojo.setAge(100);
		daoPojo.setName("Lily");
		int affected = dao.insert(hints.inTableShard(0), keyHolder, daoPojo);
		assertEquals(1,affected);
		assertEquals(5l,keyHolder.getKey());
		
		affected = dao.insert(hints.inTableShard(1), keyHolder, daoPojo);
		assertEquals(1,affected);
		assertEquals(5l,keyHolder.getKey());
	}
	
	@Test
	public void testInsert4() throws Exception {
		
		KeyHolder keyHolder = new KeyHolder();
		List<PersonGen> daoPojos1 = new ArrayList<PersonGen>(3);
		for(int i=0;i<3;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setAge(i+20);
			daoPojo.setName("Initial_TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.insert(new DalHints().inTableShard("0"), keyHolder, daoPojos1);
		assertEquals(3,affected.length);
		assertEquals(3,keyHolder.size());
		
		List<PersonGen> daoPojos2 = new ArrayList<PersonGen>(3);
		for(int i=0;i<3;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setAge(i+20);
			daoPojo.setName("Initial_TableShard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inTableShard("1"), keyHolder, daoPojos2);
		assertEquals(3,affected.length);
		assertEquals(3,keyHolder.size());
		

	}
	
	@Test
	public void testInsert5() throws Exception {
		 List<PersonGen> daoPojos1 = new ArrayList<PersonGen>(3);
			for(int i=0;i<3;i++)
			{
				PersonGen daoPojo = new PersonGen();
				daoPojo.setAge(i+20);
				daoPojo.setName("Initial_TableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
			int[] affected = dao.batchInsert(new DalHints().inTableShard("0"), daoPojos1);
			assertEquals(3,affected.length);
			
			List<PersonGen> daoPojos2 = new ArrayList<PersonGen>(3);
			for(int i=0;i<3;i++)
			{
				PersonGen daoPojo = new PersonGen();
				daoPojo.setAge(i+20);
				daoPojo.setName("Initial_TableShard_1"+i);
				daoPojos2.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().inTableShard("1"), daoPojos2);
			assertEquals(3,affected.length);
	}
	
	@Test
	public void testCombinedInsert1() throws Exception {
        List<PersonGen> daoPojos1 = new ArrayList<PersonGen>(3);
		for(int i=0;i<3;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setAge(i+20);
			daoPojo.setName("Initial_TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int affected = dao.combinedInsert(new DalHints().inTableShard("0"), daoPojos1);
		assertEquals(3,affected);
		
		List<PersonGen> daoPojos2 = new ArrayList<PersonGen>(3);
		for(int i=0;i<3;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setAge(i+20);
			daoPojo.setName("Initial_TableShard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inTableShard("1"), daoPojos2);
		assertEquals(3,affected);
	}
	
	@Test
	public void testCombinedInsert2() throws Exception {
		
		 KeyHolder keyHolder = new KeyHolder();
		 List<PersonGen> daoPojos1 = new ArrayList<PersonGen>(3);
			for(int i=0;i<3;i++)
			{
				PersonGen daoPojo = new PersonGen();
				daoPojo.setAge(i+20);
				daoPojo.setName("Initial_TableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
			int affected = dao.combinedInsert(new DalHints().inTableShard("0"), keyHolder, daoPojos1);
			assertEquals(3,affected);
			assertEquals(3,keyHolder.size());
			
			List<PersonGen> daoPojos2 = new ArrayList<PersonGen>(3);
			for(int i=0;i<3;i++)
			{
				PersonGen daoPojo = new PersonGen();
				daoPojo.setAge(i+20);
				daoPojo.setName("Initial_TableShard_1"+i);
				daoPojos2.add(daoPojo);
			}
			affected = dao.combinedInsert(new DalHints().inTableShard("1"), keyHolder, daoPojos2);
			assertEquals(3,affected);
			assertEquals(3,keyHolder.size());
	}
	
	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<PersonGen> list = dao.queryByPage(pageSize, pageNo, hints);
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 3;
		PersonGen ret = dao.queryByPk(id, new DalHints().inTableShard(0));
		assertEquals("Initial_TableShard_02",ret.getName());
		
		ret = dao.queryByPk(id, new DalHints().inTableShard(1));
		assertEquals("Initial_TableShard_12",ret.getName());
		
		
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		PersonGen pk = new PersonGen();
		pk.setID(3);
		PersonGen ret = dao.queryByPk(pk, new DalHints().inTableShard(0));
		assertEquals("Initial_TableShard_02",ret.getName());
		
		ret = dao.queryByPk(pk, new DalHints().inTableShard(1));
		assertEquals("Initial_TableShard_12",ret.getName());
	}
	
	@Test
	public void testUpdate1() throws Exception {
		PersonGen daoPojo = new PersonGen();
		daoPojo.setID(3);
		daoPojo.setName("UpdateTableShard02");
		int ret = dao.update(new DalHints().inTableShard(0), daoPojo);
		assertEquals(1,ret);
		
		daoPojo.setName("UpdateTableShard12");
		ret = dao.update(new DalHints().inTableShard(1), daoPojo);
		assertEquals(1,ret);
	}
	
	@Test
	public void testUpdate2() throws Exception {
		List<PersonGen> daoPojos1 = new ArrayList<PersonGen>(2);
		for(int i=0;i<2;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateTableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.update(new DalHints().inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);
		
		List<PersonGen> daoPojos2 = new ArrayList<PersonGen>(2);
		for(int i=0;i<2;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateTableShard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.update(new DalHints().inTableShard("1"), daoPojos2);
		assertEquals(2,affected.length);
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		
		List<PersonGen> daoPojos1 = new ArrayList<PersonGen>(2);
		for(int i=0;i<2;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+20);
			daoPojo.setName("batchUpdateTableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchUpdate(new DalHints().inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);
		
		List<PersonGen> daoPojos2 = new ArrayList<PersonGen>(2);
		for(int i=0;i<2;i++)
		{
			PersonGen daoPojo = new PersonGen();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+20);
			daoPojo.setName("batchUpdateTableShard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchUpdate(new DalHints().inTableShard("1"), daoPojos2);
		assertEquals(2,affected.length);
	}
	
}
