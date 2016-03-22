package SimpleShardByDBTableOnMysql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import org.junit.*;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;



/**
 * JUnit test of PeopleGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class PeopleGenDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByDBTableOnMysql";

	private static DalClient client = null;
	private static PeopleGenDao dao = null;
	
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
		dao = new PeopleGenDao();
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
        PeopleGen daoPojo = new PeopleGen();
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
		List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
		List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
		List<PeopleGen> list = dao.getAll(new DalHints());
	}
	
	@Test
	public void testInsert1() throws Exception {
		PeopleGen daoPojo = new PeopleGen();
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
		List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    int[] affected = dao.insert(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(3,affected.length);
		
		List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
	    affected = dao.insert(new DalHints().inShard(0).inTableShard("1"), daoPojos101);
		assertEquals(3,affected.length);
		
		List<PeopleGen> daoPojos2 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_0"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1).inTableShard("0"), daoPojos2);
		assertEquals(3,affected.length);
		
		List<PeopleGen> daoPojos211 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
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
		PeopleGen daoPojo = new PeopleGen();
		daoPojo.setAge(100);
		daoPojo.setCityID(200);
		KeyHolder keyHolder00 = new KeyHolder();
		daoPojo.setName("InsertByShard_0TableShard_0");
		int affected = dao.insert(new DalHints().inShard(0).inTableShard(0), keyHolder00, daoPojo);
		assertEquals(1,affected);
		assertEquals(1l,keyHolder00.getKey());
		
		KeyHolder keyHolder01 = new KeyHolder();
		daoPojo.setName("InsertByShard_0TableShard_1");
		affected = dao.insert(new DalHints().inShard(0).inTableShard(1), keyHolder01, daoPojo);
		assertEquals(1,affected);
		assertEquals(1l,keyHolder01.getKey());
		
		KeyHolder keyHolder10 = new KeyHolder();
		daoPojo.setName("InsertByShard_1TableShard_0");
		affected = dao.insert(new DalHints().inShard(1).inTableShard(0), keyHolder10, daoPojo);
		assertEquals(1,affected);
		assertEquals(1l,keyHolder10.getKey());
		
		KeyHolder keyHolder11 = new KeyHolder();
		daoPojo.setName("InsertByShard_1TableShard_1");
		affected = dao.insert(new DalHints().inShard(1).inTableShard(1), keyHolder11, daoPojo);
		assertEquals(1,affected);
		assertEquals(1l,keyHolder11.getKey());
		
	}
	
	@Test
	public void testInsert4() throws Exception {
		KeyHolder keyHolder100 = new KeyHolder();
		List<PeopleGen> daoPojos100 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDbShard_0TableShard_0"+i);
			daoPojos100.add(daoPojo);
		}
		int[] affected = dao.insert(new DalHints().inShard(0).inTableShard("0"), keyHolder100, daoPojos100);
		assertEquals(2,affected.length);
		assertEquals(2,keyHolder100.size());
		
		KeyHolder keyHolder101 = new KeyHolder();
		List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDbShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(0).inTableShard("1"), keyHolder101, daoPojos101);
		assertEquals(2,affected.length);
		assertEquals(2,keyHolder101.size());
		
		KeyHolder keyHolder110 = new KeyHolder();
		List<PeopleGen> daoPojos110 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(201);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDbShard_0TableShard_0"+i);
			daoPojos110.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1).inTableShard("0"), keyHolder110, daoPojos110);
		assertEquals(2,affected.length);
		assertEquals(2,keyHolder110.size());
		
		KeyHolder keyHolder111 = new KeyHolder();
		List<PeopleGen> daoPojos111 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
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
		List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    int[] affected = dao.batchInsert(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(3,affected.length);
		
		List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
	    affected = dao.batchInsert(new DalHints().inShard(0).inTableShard("1"), daoPojos101);
		assertEquals(3,affected.length);
		
		List<PeopleGen> daoPojos2 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_0"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchInsert(new DalHints().inShard(1).inTableShard("0"), daoPojos2);
		assertEquals(3,affected.length);
		
		List<PeopleGen> daoPojos211 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
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
		List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
	    int affected = dao.combinedInsert(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
		assertEquals(3,affected);
		
		List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
	    affected = dao.combinedInsert(new DalHints().inShard(0).inTableShard("1"), daoPojos101);
		assertEquals(3,affected);
		
		List<PeopleGen> daoPojos2 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(i+200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByShard_1TableShard_0"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inShard(1).inTableShard("0"), daoPojos2);
		assertEquals(3,affected);
		
		List<PeopleGen> daoPojos211 = new ArrayList<PeopleGen>(3);
		for(int i=0;i<3;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
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
		List<PeopleGen> daoPojos100 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDBShard_0TableShard_0"+i);
			daoPojos100.add(daoPojo);
		}
		int affected = dao.combinedInsert(new DalHints().inShard(0).inTableShard("0"), keyHolder100, daoPojos100);
		assertEquals(2,affected);
		assertEquals(2,keyHolder100.size());
		
		KeyHolder keyHolder101 = new KeyHolder();
		List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDBShard_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inShard(0).inTableShard("1"), keyHolder101, daoPojos101);
		assertEquals(2,affected);
		assertEquals(2,keyHolder101.size());
		
		KeyHolder keyHolder110 = new KeyHolder();
		List<PeopleGen> daoPojos110 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(201);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByDBShard_1TableShard_0"+i);
			daoPojos110.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inShard(1).inTableShard("0"), keyHolder110, daoPojos110);
		assertEquals(2,affected);
		assertEquals(2,keyHolder110.size());
		
		KeyHolder keyHolder111 = new KeyHolder();
		List<PeopleGen> daoPojos111 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
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
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<PeopleGen> list = dao.queryByPage(pageSize, pageNo, hints);
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 3l;
		PeopleGen ret = dao.queryByPk(id, new DalHints().inShard(0).inTableShard(0));
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
		PeopleGen pk = new PeopleGen();
		pk.setID(3);
		PeopleGen ret = dao.queryByPk(pk, new DalHints().inShard(0).inTableShard(0));
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
		PeopleGen daoPojo = new PeopleGen();
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
		List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
		List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
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
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setID(i+1);
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("UpdateByShard_1TableShard_1"+i);
			daoPojos1.add(daoPojo);
		}
		affected = dao.batchUpdate(new DalHints().inShard(1).inTableShard("1"), daoPojos1);
		assertEquals(2,affected.length);
	}

}
