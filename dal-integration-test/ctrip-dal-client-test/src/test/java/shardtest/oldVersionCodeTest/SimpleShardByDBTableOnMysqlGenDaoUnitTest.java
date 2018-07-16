package shardtest.oldVersionCodeTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import dao.shard.oldVersionCode.SimpleShardByDBOnMysqlGenDao;
import dao.shard.oldVersionCode.SimpleShardByDBTableOnMysqlGenDao;
import entity.MysqlPeopleTable;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test of PeopleGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class SimpleShardByDBTableOnMysqlGenDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByDBTableOnMysql";

	private static DalClient client = null;
	private static SimpleShardByDBTableOnMysqlGenDao dao = null;
	private static SimpleShardByDBOnMysqlGenDao dao2=null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new SimpleShardByDBTableOnMysqlGenDao();
		dao2=new SimpleShardByDBOnMysqlGenDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		dao2.test_def_update(new DalHints().inShard(0),"0");
		dao2.test_def_update(new DalHints().inShard(0),"1");
		dao2.test_def_update(new DalHints().inShard(1),"0");
		dao2.test_def_update(new DalHints().inShard(1),"1");
		
		List<MysqlPeopleTable> daoPojos1 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    int[] affected = dao.insert(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(3,affected.length);
		
		List<MysqlPeopleTable> daoPojos101 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
	    affected = dao.insert(new DalHints().inShard(0).inTableShard("1"), daoPojos101);
		assertEquals(3,affected.length);
		
		List<MysqlPeopleTable> daoPojos2 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_0"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1).inTableShard("0"), daoPojos2);
		assertEquals(3,affected.length);
		
		List<MysqlPeopleTable> daoPojos211 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_1"+i);
			daoPojos211.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1).inTableShard("1"), daoPojos211);
		assertEquals(3,affected.length);
	}

	@After
	public void tearDown() throws Exception {
		dao2.test_def_update(new DalHints().inShard(0),"0");
		dao2.test_def_update(new DalHints().inShard(0),"1");
		dao2.test_def_update(new DalHints().inShard(1),"0");
		dao2.test_def_update(new DalHints().inShard(1),"1");
	} 
	
	
	@Test
	public void testCount() throws Exception {
//		int ret1 = dao.count(new DalHints().inShard(0).inTableShard(0));
//		assertEquals(3,ret1);
//		int ret2 = dao.count(new DalHints().inShard(0).inTableShard(1));
//		assertEquals(3,ret2);
//		int ret3 = dao.count(new DalHints().inShard(1).inTableShard(0));
//		assertEquals(3,ret3);
//		int ret4 = dao.count(new DalHints().inShard(1).inTableShard(1));
//		assertEquals(3,ret4);
	}
	
	@Test
	public void testDelete1() throws Exception {
		MysqlPeopleTable daoPojo = new MysqlPeopleTable();
		daoPojo.setID(1);
		int affected = dao.delete(new DalHints().inShard(0).inTableShard(0), daoPojo);
		assertEquals(1,affected);
		
	    affected = dao.delete(new DalHints().inShard(0).inTableShard(1), daoPojo);
		assertEquals(1,affected);
	
	    affected = dao.delete(new DalHints().inShard(1).inTableShard(0), daoPojo);
		assertEquals(1,affected);
	
		affected = dao.delete(new DalHints().inShard(1).inTableShard(1), daoPojo);
		assertEquals(1,affected);
	}
	
	@Test
	public void testDelete2() throws Exception {
		List<MysqlPeopleTable> daoPojos1 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setCityID(i+200);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    int[] affected = dao.delete(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(3,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setCityID(i+200);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    affected = dao.delete(new DalHints().inShard(0).inTableShard("1"), daoPojos1);
		assertEquals(3,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setCityID(i+200);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    affected = dao.delete(new DalHints().inShard(1).inTableShard("0"), daoPojos1);
		assertEquals(3,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setCityID(i+200);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    affected = dao.delete(new DalHints().inShard(1).inTableShard("1"), daoPojos1);
		assertEquals(3,affected.length);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		List<MysqlPeopleTable> daoPojos1 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setCityID(i+200);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    int[] affected = dao.batchDelete(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(3,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setCityID(i+200);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    affected = dao.batchDelete(new DalHints().inShard(0).inTableShard("1"), daoPojos1);
		assertEquals(3,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setCityID(i+200);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    affected = dao.batchDelete(new DalHints().inShard(1).inTableShard("0"), daoPojos1);
		assertEquals(3,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setCityID(i+200);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    affected = dao.batchDelete(new DalHints().inShard(1).inTableShard("1"), daoPojos1);
		assertEquals(3,affected.length);
	}
	
	@Test
	public void testGetAll() throws Exception {
//		List<PeopleGen> list = dao.getAll(new DalHints().inShard(0).inTableShard(0));
//		assertEquals(3,list.size());
	}
	
	@Test
	public void testInsert1() throws Exception {
		MysqlPeopleTable daoPojo = new MysqlPeopleTable();
		daoPojo.setAge(100);
		daoPojo.setCityID(200);
		daoPojo.setName("InsertByDbShard_0TableShard_0");
		int affected = dao.insert(new DalHints().inShard(0).inTableShard(0), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByDbShard_0TableShard_1");
	    affected = dao.insert(new DalHints().inShard(0).inTableShard(1), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByDbShard_1TableShard_0");
	    affected = dao.insert(new DalHints().inShard(1).inTableShard(0), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByDbShard_1TableShard_1");
		affected = dao.insert(new DalHints().inShard(1).inTableShard(1), daoPojo);
		assertEquals(1,affected);
	}
	
	@Test
	public void testInsert2() throws Exception {
		List<MysqlPeopleTable> daoPojos1 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    int[] affected = dao.insert(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(3,affected.length);
		
		List<MysqlPeopleTable> daoPojos101 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
	    affected = dao.insert(new DalHints().inShard(0).inTableShard("1"), daoPojos101);
		assertEquals(3,affected.length);
		
		List<MysqlPeopleTable> daoPojos2 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_0"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1).inTableShard("0"), daoPojos2);
		assertEquals(3,affected.length);
		
		List<MysqlPeopleTable> daoPojos211 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_1"+i);
			daoPojos211.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1).inTableShard("1"), daoPojos211);
		assertEquals(3,affected.length);
	}
	
	@Test
	public void testInsert3() throws Exception {
		MysqlPeopleTable daoPojo = new MysqlPeopleTable();
		daoPojo.setAge(100);
		daoPojo.setCityID(200);
		KeyHolder keyHolder00 = new KeyHolder();
		daoPojo.setName("InsertByShard_0TableShard_0");
		int affected = dao.insert(new DalHints().inShard(0).inTableShard(0), keyHolder00, daoPojo);
		assertEquals(1,affected);
		assertEquals(4L,keyHolder00.getKey());
		
		KeyHolder keyHolder01 = new KeyHolder();
		daoPojo.setName("InsertByShard_0TableShard_1");
		affected = dao.insert(new DalHints().inShard(0).inTableShard(1), keyHolder01, daoPojo);
		assertEquals(1,affected);
		assertEquals(4L,keyHolder01.getKey());
		
		KeyHolder keyHolder10 = new KeyHolder();
		daoPojo.setName("InsertByShard_1TableShard_0");
		affected = dao.insert(new DalHints().inShard(1).inTableShard(0), keyHolder10, daoPojo);
		assertEquals(1,affected);
		assertEquals(4L,keyHolder10.getKey());
		
		KeyHolder keyHolder11 = new KeyHolder();
		daoPojo.setName("InsertByShard_1TableShard_1");
		affected = dao.insert(new DalHints().inShard(1).inTableShard(1), keyHolder11, daoPojo);
		assertEquals(1,affected);
		assertEquals(4L,keyHolder11.getKey());
	}
	
	@Test
	public void testInsert4() throws Exception {
		KeyHolder keyHolder100 = new KeyHolder();
		List<MysqlPeopleTable> daoPojos100 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDbShard_0TableShard_0"+i);
			daoPojos100.add(daoPojo);
		}
		int[] affected = dao.insert(new DalHints().inShard(0).inTableShard("0"), keyHolder100, daoPojos100);
		assertEquals(2,affected.length);
		assertEquals(2,keyHolder100.size());
		
		KeyHolder keyHolder101 = new KeyHolder();
		List<MysqlPeopleTable> daoPojos101 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDbShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(0).inTableShard("1"), keyHolder101, daoPojos101);
		assertEquals(2,affected.length);
		assertEquals(2,keyHolder101.size());
		
		KeyHolder keyHolder110 = new KeyHolder();
		List<MysqlPeopleTable> daoPojos110 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(201);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDbShard_0TableShard_0"+i);
			daoPojos110.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1).inTableShard("0"), keyHolder110, daoPojos110);
		assertEquals(2,affected.length);
		assertEquals(2,keyHolder110.size());
		
		KeyHolder keyHolder111 = new KeyHolder();
		List<MysqlPeopleTable> daoPojos111 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(201);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDbShard_0TableShard_1"+i);
			daoPojos111.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1).inTableShard("1"), keyHolder111, daoPojos111);
		assertEquals(2,affected.length);
		assertEquals(2,keyHolder111.size());
	}
	
	@Test
	public void testInsert5() throws Exception {
		List<MysqlPeopleTable> daoPojos1 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    int[] affected = dao.batchInsert(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(3,affected.length);
		
		List<MysqlPeopleTable> daoPojos101 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
	    affected = dao.batchInsert(new DalHints().inShard(0).inTableShard("1"), daoPojos101);
		assertEquals(3,affected.length);
		
		List<MysqlPeopleTable> daoPojos2 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_0"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchInsert(new DalHints().inShard(1).inTableShard("0"), daoPojos2);
		assertEquals(3,affected.length);
		
		List<MysqlPeopleTable> daoPojos211 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_1"+i);
			daoPojos211.add(daoPojo);
		}
		affected = dao.batchInsert(new DalHints().inShard(1).inTableShard("1"), daoPojos211);
		assertEquals(3,affected.length);
	}
	
	@Test
	public void testCombinedInsert1() throws Exception {
		List<MysqlPeopleTable> daoPojos1 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    int affected = dao.combinedInsert(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(3,affected);
		
		List<MysqlPeopleTable> daoPojos101 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
	    affected = dao.combinedInsert(new DalHints().inShard(0).inTableShard("1"), daoPojos101);
		assertEquals(3,affected);
		
		List<MysqlPeopleTable> daoPojos2 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_0"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inShard(1).inTableShard("0"), daoPojos2);
		assertEquals(3,affected);
		
		List<MysqlPeopleTable> daoPojos211 = new ArrayList<>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_1"+i);
			daoPojos211.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inShard(1).inTableShard("1"), daoPojos211);
		assertEquals(3,affected);
	}
	
	@Test
	public void testCombinedInsert2() throws Exception {
		KeyHolder keyHolder100 = new KeyHolder();
		List<MysqlPeopleTable> daoPojos100 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDBShard_0TableShard_0"+i);
			daoPojos100.add(daoPojo);
		}
		int affected = dao.combinedInsert(new DalHints().inShard(0).inTableShard("0"), keyHolder100, daoPojos100);
		assertEquals(2,affected);
		assertEquals(2,keyHolder100.size());
		
		KeyHolder keyHolder101 = new KeyHolder();
		List<MysqlPeopleTable> daoPojos101 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDBShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inShard(0).inTableShard("1"), keyHolder101, daoPojos101);
		assertEquals(2,affected);
		assertEquals(2,keyHolder101.size());
		
		KeyHolder keyHolder110 = new KeyHolder();
		List<MysqlPeopleTable> daoPojos110 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(201);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDBShard_1TableShard_0"+i);
			daoPojos110.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inShard(1).inTableShard("0"), keyHolder110, daoPojos110);
		assertEquals(2,affected);
		assertEquals(2,keyHolder110.size());
		
		KeyHolder keyHolder111 = new KeyHolder();
		List<MysqlPeopleTable> daoPojos111 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(201);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDBShard_1TableShard_1"+i);
			daoPojos111.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inShard(1).inTableShard("1"), keyHolder111, daoPojos111);
		assertEquals(2,affected);
		assertEquals(2,keyHolder111.size());
	}
	
	@Test
	public void testQueryByPage() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
//		List<PeopleGen> list = dao.queryByPage(pageSize, pageNo, hints);
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 3L;
		MysqlPeopleTable ret = dao.queryByPk(id, new DalHints().inShard(0).inTableShard(0));
		assertEquals("InsertByShard_0TableShard_02",ret.getName());
		
		ret = dao.queryByPk(id, new DalHints().inShard(0).inTableShard(1));
		assertEquals("InsertByShard_0TableShard_12",ret.getName());
		
		ret = dao.queryByPk(id, new DalHints().inShard(1).inTableShard(0));
		assertEquals("InsertByShard_1TableShard_02",ret.getName());
		
		ret = dao.queryByPk(id, new DalHints().inShard(1).inTableShard(1));
		assertEquals("InsertByShard_1TableShard_12",ret.getName());
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		MysqlPeopleTable pk = new MysqlPeopleTable();
		pk.setID(3);
		MysqlPeopleTable ret = dao.queryByPk(pk, new DalHints().inShard(0).inTableShard(0));
		assertEquals("InsertByShard_0TableShard_02",ret.getName());
		
		ret = dao.queryByPk(pk, new DalHints().inShard(0).inTableShard(1));
		assertEquals("InsertByShard_0TableShard_12",ret.getName());
		
		ret = dao.queryByPk(pk, new DalHints().inShard(1).inTableShard(0));
		assertEquals("InsertByShard_1TableShard_02",ret.getName());
		
		ret = dao.queryByPk(pk, new DalHints().inShard(1).inTableShard(1));
		assertEquals("InsertByShard_1TableShard_12",ret.getName());
	}
	
	@Test
	public void testUpdate1() throws Exception {
		MysqlPeopleTable daoPojo = new MysqlPeopleTable();
		daoPojo.setID(1);
		daoPojo.setName("UpdateByDbShard_0TableShard_00");
		int ret = dao.update(new DalHints().inShard(0).inTableShard(0), daoPojo);
		assertEquals(1,ret);
		
		daoPojo.setName("UpdateByDbShard_0TableShard_10");
		ret = dao.update(new DalHints().inShard(0).inTableShard(1), daoPojo);
		assertEquals(1,ret);
		
		daoPojo.setName("UpdateByDbShard_1TableShard_00");
		ret = dao.update(new DalHints().inShard(1).inTableShard(0), daoPojo);
		assertEquals(1,ret);
		
		daoPojo.setName("UpdateByDbShard_1TableShard_10");
		ret = dao.update(new DalHints().inShard(1).inTableShard(1), daoPojo);
		assertEquals(1,ret);
	}
	
	@Test
	public void testUpdate2() throws Exception {
		List<MysqlPeopleTable> daoPojos1 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateByShard_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.update(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateByShard_0TableShard_1"+i);
			daoPojos1.add(daoPojo);
		}
		affected = dao.update(new DalHints().inShard(0).inTableShard("1"), daoPojos1);
		assertEquals(2,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateByShard_1TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		affected = dao.update(new DalHints().inShard(1).inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateByShard_1TableShard_1"+i);
			daoPojos1.add(daoPojo);
		}
		affected = dao.update(new DalHints().inShard(1).inTableShard("1"), daoPojos1);
		assertEquals(2,affected.length);
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		List<MysqlPeopleTable> daoPojos1 = new ArrayList<>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateByShard_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchUpdate(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateByShard_0TableShard_1"+i);
			daoPojos1.add(daoPojo);
		}
		affected = dao.batchUpdate(new DalHints().inShard(0).inTableShard("1"), daoPojos1);
		assertEquals(2,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateByShard_1TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		affected = dao.batchUpdate(new DalHints().inShard(1).inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);
		
		daoPojos1.clear();
		for(int i=0;i<2;i++)
		{
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setID(i+1);
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateByShard_1TableShard_1"+i);
			daoPojos1.add(daoPojo);
		}
		affected = dao.batchUpdate(new DalHints().inShard(1).inTableShard("1"), daoPojos1);
		assertEquals(2,affected.length);
	}
	
	 //dao2分片策略是只分库不分表，故将tableID作为参数传入自定义sql增删改api，实现分库分表效果
	@Test
	public void testtest_def_update_tableID() throws Exception {
	    int ret1 = dao2.test_def_update(new DalHints().inShard(0),"0");
	    int ret2 = dao2.test_def_update(new DalHints().inShard(0),"1");
	    assertEquals(0,ret1);
	    assertEquals(0,ret2);
	    
	    int ret3 = dao2.test_def_update(new DalHints().inShard(1),"0");
	    int ret4 = dao2.test_def_update(new DalHints().inShard(1),"1");
	    assertEquals(0,ret3);
	    assertEquals(0,ret4);
	}

}
