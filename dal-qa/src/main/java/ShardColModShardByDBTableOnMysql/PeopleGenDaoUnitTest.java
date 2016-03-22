package ShardColModShardByDBTableOnMysql;

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

	private static final String DATA_BASE = "ShardColModShardByDBTableOnMysql";

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
	
		
		// By DbShardtabelShard
		//直接给出shard值
		daoPojo.setID(1);
		int affected = dao.delete(new DalHints().inShard(0).inTableShard(0), daoPojo);
		assertEquals(1,affected);
		
	    affected = dao.delete(new DalHints().inShard(0).inTableShard(1), daoPojo);
		assertEquals(1,affected);
	
	    affected = dao.delete(new DalHints().inShard(1).inTableShard(0), daoPojo);
		assertEquals(1,affected);
	
		affected = dao.delete(new DalHints().inShard(1).inTableShard(1), daoPojo);
		assertEquals(1,affected);
		
		// By DbShardValuetableShardValue
		//用给出的值进行mod运算，从而得出shard值
		daoPojo.setID(4);
		affected = dao.delete(new DalHints().setShardValue(4).setTableShardValue(4), daoPojo);
		assertEquals(1,affected);
		
		affected = dao.delete(new DalHints().setShardValue(4).setTableShardValue(5), daoPojo);
		assertEquals(1,affected);
	
		affected = dao.delete(new DalHints().setShardValue(5).setTableShardValue(4), daoPojo);
		assertEquals(1,affected);
	
		affected = dao.delete(new DalHints().setShardValue(5).setTableShardValue(5), daoPojo);
		assertEquals(1,affected);
		
		// By ShardColValueshardColValue
		//用对应的shardcol值进行mod运算，从而得出shard值，但插入的pojo值不受该shardcol值影响
		daoPojo.setID(7);
		affected = dao.delete(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20), daoPojo);
		assertEquals(1,affected);
	
		affected = dao.delete(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21), daoPojo);
		assertEquals(1,affected);
	
		affected = dao.delete(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 30), daoPojo);
		assertEquals(1,affected);
	
		affected = dao.delete(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 31), daoPojo);
		assertEquals(1,affected);
		
		// By fieldsfields
		//系统自动根据pojo中提供的shardcol值进行mod运算，从而得出shard值
		daoPojo.setID(10);
		
		daoPojo.setCityID(200);
		daoPojo.setAge(20);
		affected = dao.delete(new DalHints(), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setAge(21);
		affected = dao.delete(new DalHints(), daoPojo);
		assertEquals(1,affected);

		daoPojo.setCityID(201);
		daoPojo.setAge(20);
		affected = dao.delete(new DalHints(), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setAge(21);
		affected = dao.delete(new DalHints(), daoPojo);
		assertEquals(1,affected);
	}
	
	@Test
	public void testDelete2() throws Exception {
		// By shardtabelShard
		List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
		    int[] affected = dao.delete(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
			assertEquals(3,affected.length);
			
			daoPojos1.clear();
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
		    affected = dao.delete(new DalHints().inShard(0).inTableShard("1"), daoPojos1);
			assertEquals(3,affected.length);
			
			daoPojos1.clear();
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
		    affected = dao.delete(new DalHints().inShard(1).inTableShard("0"), daoPojos1);
			assertEquals(3,affected.length);
			
			daoPojos1.clear();
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
		    affected = dao.delete(new DalHints().inShard(1).inTableShard("1"), daoPojos1);
			assertEquals(3,affected.length);
		
			// By shardValuetableShardValue
			List<PeopleGen> daoPojos3 = new ArrayList<PeopleGen>(3);
			for(int i=3;i<6;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos3.add(daoPojo);
			}
			affected = dao.delete(new DalHints().setShardValue(4).setTableShardValue(4), daoPojos3);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos301 = new ArrayList<PeopleGen>(3);
			for(int i=3;i<6;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos301.add(daoPojo);
			}
			affected = dao.delete(new DalHints().setShardValue(4).setTableShardValue(5), daoPojos301);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos4 = new ArrayList<PeopleGen>(3);
			for(int i=3;i<6;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos4.add(daoPojo);
			}
			affected = dao.delete(new DalHints().setShardValue(5).setTableShardValue(4), daoPojos4);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos411 = new ArrayList<PeopleGen>(3);
			for(int i=3;i<6;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos411.add(daoPojo);
			}
			affected = dao.delete(new DalHints().setShardValue(5).setTableShardValue(5), daoPojos411);
			assertEquals(3,affected.length);
			
			// By shardColValueshardColValue
			List<PeopleGen> daoPojos5 = new ArrayList<PeopleGen>(3);
			for(int i=6;i<9;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos5.add(daoPojo);
			}
			affected = dao.delete(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20), daoPojos5);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos501 = new ArrayList<PeopleGen>(3);
			for(int i=6;i<9;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos501.add(daoPojo);
			}
			affected = dao.delete(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21), daoPojos501);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos6 = new ArrayList<PeopleGen>(3);
			for(int i=6;i<9;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos6.add(daoPojo);
			}
			affected = dao.delete(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 20), daoPojos6);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos611 = new ArrayList<PeopleGen>(3);
			for(int i=6;i<9;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(i+1);
//				daoPojo.setCityID(i+200);
//				daoPojo.setAge(i+20);
//				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos611.add(daoPojo);
			}
			affected = dao.delete(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 21), daoPojos611);
			assertEquals(3,affected.length);
			
			// By fieldsfields
			List<PeopleGen> daoPojos7 = new ArrayList<PeopleGen>(2);
			for(int i=9;i<11;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(10);
				daoPojo.setCityID(200);
				daoPojo.setAge(i+11);
//				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos7.add(daoPojo);
			}
			affected = dao.delete(new DalHints(), daoPojos7);
			assertEquals(2,affected.length);
			
			List<PeopleGen> daoPojos711 = new ArrayList<PeopleGen>(2);
			for(int i=9;i<11;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setID(10);
				daoPojo.setCityID(201);
				daoPojo.setAge(i+11);
//				daoPojo.setName("InsertByShardValue_1fields_"+i);
				daoPojos711.add(daoPojo);
			}
			affected = dao.delete(new DalHints(), daoPojos711);
			assertEquals(2,affected.length);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		// By shardtabelShard
				List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
						daoPojos1.add(daoPojo);
					}
				    int[] affected = dao.batchDelete(new DalHints().inShard(0).inTableShard("0"), daoPojos1);
					assertEquals(3,affected.length);
					
					daoPojos1.clear();
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
						daoPojos1.add(daoPojo);
					}
				    affected = dao.batchDelete(new DalHints().inShard(0).inTableShard("1"), daoPojos1);
					assertEquals(3,affected.length);
					
					daoPojos1.clear();
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
						daoPojos1.add(daoPojo);
					}
				    affected = dao.batchDelete(new DalHints().inShard(1).inTableShard("0"), daoPojos1);
					assertEquals(3,affected.length);
					
					daoPojos1.clear();
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
						daoPojos1.add(daoPojo);
					}
				    affected = dao.batchDelete(new DalHints().inShard(1).inTableShard("1"), daoPojos1);
					assertEquals(3,affected.length);
				
					// By shardValuetableShardValue
					List<PeopleGen> daoPojos3 = new ArrayList<PeopleGen>(3);
					for(int i=3;i<6;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos3.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints().setShardValue(4).setTableShardValue(4), daoPojos3);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos301 = new ArrayList<PeopleGen>(3);
					for(int i=3;i<6;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos301.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints().setShardValue(4).setTableShardValue(5), daoPojos301);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos4 = new ArrayList<PeopleGen>(3);
					for(int i=3;i<6;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos4.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints().setShardValue(5).setTableShardValue(4), daoPojos4);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos411 = new ArrayList<PeopleGen>(3);
					for(int i=3;i<6;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos411.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints().setShardValue(5).setTableShardValue(5), daoPojos411);
					assertEquals(3,affected.length);
					
					// By shardColValueshardColValue
					List<PeopleGen> daoPojos5 = new ArrayList<PeopleGen>(3);
					for(int i=6;i<9;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos5.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20), daoPojos5);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos501 = new ArrayList<PeopleGen>(3);
					for(int i=6;i<9;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos501.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21), daoPojos501);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos6 = new ArrayList<PeopleGen>(3);
					for(int i=6;i<9;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos6.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 20), daoPojos6);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos611 = new ArrayList<PeopleGen>(3);
					for(int i=6;i<9;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(i+1);
//						daoPojo.setCityID(i+200);
//						daoPojo.setAge(i+20);
//						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos611.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 21), daoPojos611);
					assertEquals(3,affected.length);
					
					// By fieldsfields
					List<PeopleGen> daoPojos7 = new ArrayList<PeopleGen>(2);
					for(int i=9;i<11;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(10);
						daoPojo.setCityID(200);
						daoPojo.setAge(i+11);
//						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos7.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints(), daoPojos7);
					assertEquals(2,affected.length);
					
					List<PeopleGen> daoPojos711 = new ArrayList<PeopleGen>(2);
					for(int i=9;i<11;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setID(10);
						daoPojo.setCityID(201);
						daoPojo.setAge(i+11);
//						daoPojo.setName("InsertByShardValue_1fields_"+i);
						daoPojos711.add(daoPojo);
					}
					affected = dao.batchDelete(new DalHints(), daoPojos711);
					assertEquals(2,affected.length);
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
		
		// By DbShardtabelShard
		//直接给出shard值
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
		
		// By DbShardtableShardValue
		//用给出的值进行mod运算，从而得出shard值
		daoPojo.setName("InsertByDbShard_0TableShardValue_0");
		affected = dao.insert(new DalHints().inShard(0).setTableShardValue(4), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByDbShard_0TableShardValue_1");
		affected = dao.insert(new DalHints().inShard(0).setTableShardValue(5), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByDbShard_1TableShardValue_0");
		affected = dao.insert(new DalHints().inShard(1).setTableShardValue(4), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByDbShard_1TableShardValue_1");
		affected = dao.insert(new DalHints().inShard(1).setTableShardValue(5), daoPojo);
		assertEquals(1,affected);
		
		// By DbShardshardColValue
		//用对应的shardcol值进行mod运算，从而得出shard值，但插入的pojo值不受该shardcol值影响
		daoPojo.setName("InsertByDbShard_0ShardColvalue_0");
		affected = dao.insert(new DalHints().inShard(0).setShardColValue("Age", 20), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByDbShard_0ShardColvalue_1");
		affected = dao.insert(new DalHints().inShard(0).setShardColValue("Age", 21), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByDbShard_1ShardColvalue_0");
		affected = dao.insert(new DalHints().inShard(1).setShardColValue("Age", 30), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setName("InsertByDbShard_1ShardColvalue_1");
		affected = dao.insert(new DalHints().inShard(1).setShardColValue("Age", 31), daoPojo);
		assertEquals(1,affected);
		
		// By DbShardsfields
		//系统自动根据pojo中提供的shardcol值进行mod运算，从而得出shard值
		daoPojo.setAge(100);
		daoPojo.setCityID(200);
		daoPojo.setName("InsertByDbShard_0fields_0");
		affected = dao.insert(new DalHints().inShard(0), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setAge(101);
		daoPojo.setName("InsertByDbShard_0fields_1");
		affected = dao.insert(new DalHints().inShard(0), daoPojo);
		assertEquals(1,affected);

		daoPojo.setCityID(201);
		daoPojo.setAge(100);
		daoPojo.setName("InsertByDbShard_1fields_0");
		affected = dao.insert(new DalHints().inShard(1), daoPojo);
		assertEquals(1,affected);
		
		daoPojo.setAge(101);
		daoPojo.setName("InsertByDbShard_1fields_1");
		affected = dao.insert(new DalHints().inShard(1), daoPojo);
		assertEquals(1,affected);
		
		// By fieldsfields
				//系统自动根据pojo中提供的shardcol值进行mod运算，从而得出shard值
				daoPojo.setAge(100);
				daoPojo.setCityID(200);
				daoPojo.setName("InsertByfields_0fields_0");
				affected = dao.insert(new DalHints(), daoPojo);
				assertEquals(1,affected);
				
				daoPojo.setAge(101);
				daoPojo.setName("InsertByfields_0fields_1");
				affected = dao.insert(new DalHints(), daoPojo);
				assertEquals(1,affected);

				daoPojo.setCityID(201);
				daoPojo.setAge(100);
				daoPojo.setName("InsertByfields_1fields_0");
				affected = dao.insert(new DalHints(), daoPojo);
				assertEquals(1,affected);
				
				daoPojo.setAge(101);
				daoPojo.setName("InsertByfields_1fields_1");
				affected = dao.insert(new DalHints(), daoPojo);
				assertEquals(1,affected);
	}
	
	@Test
	public void testInsert2() throws Exception {
		// By shardValuetabelShard
				List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
						daoPojos1.add(daoPojo);
					}
				    int[] affected = dao.insert(new DalHints().setShardValue(4).inTableShard("0"), daoPojos1);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0TableShard_1"+i);
						daoPojos101.add(daoPojo);
					}
				    affected = dao.insert(new DalHints().setShardValue(4).inTableShard("1"), daoPojos101);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos2 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1TableShard_0"+i);
						daoPojos2.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(5).inTableShard("0"), daoPojos2);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos211 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1TableShard_1"+i);
						daoPojos211.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(5).inTableShard("1"), daoPojos211);
					assertEquals(3,affected.length);
					
					// By shardValuetableShardValue
					List<PeopleGen> daoPojos3 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos3.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(4).setTableShardValue(4), daoPojos3);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos301 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0TableShardValue_1"+i);
						daoPojos301.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(4).setTableShardValue(5), daoPojos301);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos4 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1TableShardValue_0"+i);
						daoPojos4.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(5).setTableShardValue(4), daoPojos4);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos411 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1TableShardValue_1"+i);
						daoPojos411.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(5).setTableShardValue(5), daoPojos411);
					assertEquals(3,affected.length);
					
					// By shardValueshardColValue
					List<PeopleGen> daoPojos5 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0ShardColvalue_0"+i);
						daoPojos5.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(4).setShardColValue("Age", 20), daoPojos5);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos501 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0ShardColvalue_1"+i);
						daoPojos501.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(4).setShardColValue("Age", 21), daoPojos501);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos6 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1ShardColvalue_0"+i);
						daoPojos6.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(5).setShardColValue("Age", 20), daoPojos6);
					assertEquals(3,affected.length);
					
					List<PeopleGen> daoPojos611 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1ShardColvalue_1"+i);
						daoPojos611.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(5).setShardColValue("Age", 21), daoPojos611);
					assertEquals(3,affected.length);
					
					// By ShardValuefields
					List<PeopleGen> daoPojos7 = new ArrayList<PeopleGen>(2);
					for(int i=0;i<2;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0fields_"+i);
						daoPojos7.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(4), daoPojos7);
					assertEquals(2,affected.length);
					
					List<PeopleGen> daoPojos711 = new ArrayList<PeopleGen>(2);
					for(int i=0;i<2;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(201);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1fields_"+i);
						daoPojos711.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardValue(5), daoPojos711);
					assertEquals(2,affected.length);
					
					// By fieldsfields
					List<PeopleGen> daoPojos8 = new ArrayList<PeopleGen>(2);
					for(int i=0;i<2;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByfields_0fields_"+i);
						daoPojos8.add(daoPojo);
					}
					affected = dao.insert(new DalHints(), daoPojos8);
					assertEquals(2,affected.length);
					
					List<PeopleGen> daoPojos9 = new ArrayList<PeopleGen>(2);
					for(int i=0;i<2;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(201);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByfields_1fields_"+i);
						daoPojos9.add(daoPojo);
					}
					affected = dao.insert(new DalHints(), daoPojos9);
					assertEquals(2,affected.length);
	}
	
	@Test
	public void testInsert3() throws Exception {
		
		PeopleGen daoPojo = new PeopleGen();
		daoPojo.setAge(100);
		daoPojo.setCityID(200);
		
		// By shardColValuetabelShard
		//直接给出shard值
		KeyHolder keyHolder00 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_0TableShard_0");
		int affected = dao.insert(new DalHints().setShardColValue("CityID", 200).inTableShard(0), keyHolder00, daoPojo);
		assertEquals(1,affected);
		assertEquals(1l,keyHolder00.getKey());
		
		KeyHolder keyHolder01 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_0TableShard_1");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 200).inTableShard(1), keyHolder01, daoPojo);
		assertEquals(1,affected);
		assertEquals(1l,keyHolder01.getKey());
		
		KeyHolder keyHolder10 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_1TableShard_0");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 201).inTableShard(0), keyHolder10, daoPojo);
		assertEquals(1,affected);
		assertEquals(1l,keyHolder10.getKey());
		
		KeyHolder keyHolder11 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_1TableShard_1");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 201).inTableShard(1), keyHolder11, daoPojo);
		assertEquals(1,affected);
		assertEquals(1l,keyHolder11.getKey());
		
		// By shardColValuetableShardValue
		//用给出的值进行mod运算，从而得出shard值
		KeyHolder keyHolder200 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_0TableShardValue_0");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 200).setTableShardValue(4), keyHolder200, daoPojo);
		assertEquals(1,affected);
		assertEquals(2l,keyHolder200.getKey());
		
		KeyHolder keyHolder201 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_0TableShardValue_1");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 200).setTableShardValue(5), keyHolder201, daoPojo);
		assertEquals(1,affected);
		assertEquals(2l,keyHolder201.getKey());
		
		KeyHolder keyHolder210 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_1TableShardValue_0");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 201).setTableShardValue(4), keyHolder210, daoPojo);
		assertEquals(1,affected);
		assertEquals(2l,keyHolder210.getKey());
		
		KeyHolder keyHolder211 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_1TableShardValue_1");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 201).setTableShardValue(5), keyHolder211, daoPojo);
		assertEquals(1,affected);
		assertEquals(2l,keyHolder211.getKey());
		
		// By shardColValueshardColValue
		//用对应的shardcol值进行mod运算，从而得出shard值，但插入的pojo值不受该shardcol值影响
		KeyHolder keyHolder300 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_0ShardColvalue_0");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20), keyHolder300, daoPojo);
		assertEquals(1,affected);
		assertEquals(3l,keyHolder300.getKey());
		
		KeyHolder keyHolder301 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_0ShardColvalue_1");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21), keyHolder301, daoPojo);
		assertEquals(1,affected);
		assertEquals(3l,keyHolder301.getKey());
		
		KeyHolder keyHolder310 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_1ShardColvalue_0");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 20), keyHolder310, daoPojo);
		assertEquals(1,affected);
		assertEquals(3l,keyHolder310.getKey());
		
		KeyHolder keyHolder311 = new KeyHolder();
		daoPojo.setName("InsertByShardColValue_1ShardColvalue_1");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 31), keyHolder311, daoPojo);
		assertEquals(1,affected);
		assertEquals(3l,keyHolder311.getKey());
		
		// By shardColValuefields
		//系统自动根据pojo中提供的shardcol值进行mod运算，从而得出shard值
		KeyHolder keyHolder400 = new KeyHolder();
		daoPojo.setCityID(200);
		daoPojo.setAge(100);
		daoPojo.setName("InsertByShardColValue_0fields_0");
		affected = dao.insert(new DalHints().setShardColValue("CityID", 200), keyHolder400, daoPojo);
		assertEquals(1,affected);
		assertEquals(4l,keyHolder400.getKey());
		
		KeyHolder keyHolder401 = new KeyHolder();
		daoPojo.setCityID(200);
		daoPojo.setAge(101);
		daoPojo.setName("InsertByShardColValue_0fields_1");
		affected = dao.insert(new DalHints().setShardColValue("CityID",200), keyHolder401, daoPojo);
		assertEquals(1,affected);
		assertEquals(4l,keyHolder401.getKey());
		
		KeyHolder keyHolder410 = new KeyHolder();
		daoPojo.setCityID(201);
		daoPojo.setAge(100);
		daoPojo.setName("InsertByShardColValue_1fields_0");
		affected = dao.insert(new DalHints().setShardColValue("CityID",201), keyHolder410, daoPojo);
		assertEquals(1,affected);
		assertEquals(4l,keyHolder410.getKey());
		
		KeyHolder keyHolder411 = new KeyHolder();
		daoPojo.setCityID(201);
		daoPojo.setAge(101);
		daoPojo.setName("InsertByShardColValue_1fields_1");
		affected = dao.insert(new DalHints().setShardColValue("CityID",201), keyHolder411, daoPojo);
		assertEquals(1,affected);
		assertEquals(4l,keyHolder411.getKey());
		
		// By fieldsfields
				//系统自动根据pojo中提供的shardcol值进行mod运算，从而得出shard值
				KeyHolder keyHolder500 = new KeyHolder();
				daoPojo.setCityID(200);
				daoPojo.setAge(100);
				daoPojo.setName("InsertByfields_0fields_0");
				affected = dao.insert(new DalHints(), keyHolder500, daoPojo);
				assertEquals(1,affected);
				assertEquals(5l,keyHolder500.getKey());
				
				KeyHolder keyHolder501 = new KeyHolder();
				daoPojo.setCityID(200);
				daoPojo.setAge(101);
				daoPojo.setName("InsertByfields_0fields_1");
				affected = dao.insert(new DalHints(), keyHolder501, daoPojo);
				assertEquals(1,affected);
				assertEquals(5l,keyHolder501.getKey());
				
				KeyHolder keyHolder510 = new KeyHolder();
				daoPojo.setCityID(201);
				daoPojo.setAge(100);
				daoPojo.setName("InsertByfields_1fields_0");
				affected = dao.insert(new DalHints(), keyHolder510, daoPojo);
				assertEquals(1,affected);
				assertEquals(5l,keyHolder510.getKey());
				
				KeyHolder keyHolder511 = new KeyHolder();
				daoPojo.setCityID(201);
				daoPojo.setAge(101);
				daoPojo.setName("InsertByfields_1fields_1");
				affected = dao.insert(new DalHints(), keyHolder511, daoPojo);
				assertEquals(1,affected);
				assertEquals(5l,keyHolder511.getKey());
	}
	
	@Test
	public void testInsert4() throws Exception {
		// By fieldstabelShard
				KeyHolder keyHolder100 = new KeyHolder();
				List<PeopleGen> daoPojos100 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_0TableShard_0"+i);
					daoPojos100.add(daoPojo);
				}
				int[] affected = dao.insert(new DalHints().inTableShard("0"), keyHolder100, daoPojos100);
				assertEquals(2,affected.length);
				assertEquals(2,keyHolder100.size());
				
				KeyHolder keyHolder101 = new KeyHolder();
				List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_0TableShard_1"+i);
					daoPojos101.add(daoPojo);
				}
				affected = dao.insert(new DalHints().inTableShard("1"), keyHolder101, daoPojos101);
				assertEquals(2,affected.length);
				assertEquals(2,keyHolder101.size());
				
				KeyHolder keyHolder110 = new KeyHolder();
				List<PeopleGen> daoPojos110 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(201);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_1TableShard_0"+i);
					daoPojos110.add(daoPojo);
				}
				affected = dao.insert(new DalHints().inTableShard("0"), keyHolder110, daoPojos110);
				assertEquals(2,affected.length);
				assertEquals(2,keyHolder110.size());
				
				KeyHolder keyHolder111 = new KeyHolder();
				List<PeopleGen> daoPojos111 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(201);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_1TableShard_1"+i);
					daoPojos111.add(daoPojo);
				}
				affected = dao.insert(new DalHints().inTableShard("1"), keyHolder111, daoPojos111);
				assertEquals(2,affected.length);
				assertEquals(2,keyHolder111.size());
				
				// By fieldstableShardValue
						KeyHolder keyHolder200 = new KeyHolder();
						List<PeopleGen> daoPojos200 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(200);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_0TableShardValue_0"+i);
							daoPojos200.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setTableShardValue(4), keyHolder200, daoPojos200);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder200.size());
						
						KeyHolder keyHolder201 = new KeyHolder();
						List<PeopleGen> daoPojos201 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(200);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_0TableShardValue_1"+i);
							daoPojos201.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setTableShardValue(5), keyHolder201, daoPojos201);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder201.size());
						
						KeyHolder keyHolder210 = new KeyHolder();
						List<PeopleGen> daoPojos210 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(201);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_1TableShardValue_0"+i);
							daoPojos210.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setTableShardValue(4), keyHolder210, daoPojos210);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder210.size());
						
						KeyHolder keyHolder211 = new KeyHolder();
						List<PeopleGen> daoPojos211 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(201);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_1TableShardValue_1"+i);
							daoPojos211.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setTableShardValue(5), keyHolder211, daoPojos211);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder211.size());
						
						// By shardColValue
						KeyHolder keyHolder300 = new KeyHolder();
						List<PeopleGen> daoPojos300 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(200);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_0ShardColvalue_0"+i);
							daoPojos300.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setShardColValue("Age", 20), keyHolder300, daoPojos300);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder300.size());
						
						KeyHolder keyHolder301 = new KeyHolder();
						List<PeopleGen> daoPojos301 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(200);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_0ShardColvalue_1"+i);
							daoPojos301.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setShardColValue("Age", 21), keyHolder301, daoPojos301);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder301.size());
						
						KeyHolder keyHolder310 = new KeyHolder();
						List<PeopleGen> daoPojos310 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(201);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_1ShardColvalue_0"+i);
							daoPojos310.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setShardColValue("Age", 20), keyHolder310, daoPojos310);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder310.size());
						
						KeyHolder keyHolder311 = new KeyHolder();
						List<PeopleGen> daoPojos311 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(201);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_1ShardColvalue_1"+i);
							daoPojos311.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setShardColValue("Age", 21), keyHolder311, daoPojos311);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder311.size());
						
						// By fields
						KeyHolder keyHolder400 = new KeyHolder();
						List<PeopleGen> daoPojos7 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(200);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_0fields_"+i);
							daoPojos7.add(daoPojo);
						}
						affected = dao.insert(new DalHints(), keyHolder400, daoPojos7);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder400.size());
						
						KeyHolder keyHolder410 = new KeyHolder();
						List<PeopleGen> daoPojos8 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(201);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_1fields_"+i);
							daoPojos8.add(daoPojo);
						}
						affected = dao.insert(new DalHints(), keyHolder410, daoPojos8);
						assertEquals(2,affected.length);
						assertEquals(2,keyHolder410.size());
	}
	
	@Test
	public void testInsert5() throws Exception {
		
		// By shardValuetabelShard
		List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
		    int[] affected = dao.batchInsert(new DalHints().setShardValue(4).inTableShard("0"), daoPojos1);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_0TableShard_1"+i);
				daoPojos101.add(daoPojo);
			}
		    affected = dao.batchInsert(new DalHints().setShardValue(4).inTableShard("1"), daoPojos101);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos2 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_1TableShard_0"+i);
				daoPojos2.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(5).inTableShard("0"), daoPojos2);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos211 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_1TableShard_1"+i);
				daoPojos211.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(5).inTableShard("1"), daoPojos211);
			assertEquals(3,affected.length);
			
			// By shardValuetableShardValue
			List<PeopleGen> daoPojos3 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
				daoPojos3.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(4).setTableShardValue(4), daoPojos3);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos301 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_0TableShardValue_1"+i);
				daoPojos301.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(4).setTableShardValue(5), daoPojos301);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos4 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_1TableShardValue_0"+i);
				daoPojos4.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(5).setTableShardValue(4), daoPojos4);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos411 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_1TableShardValue_1"+i);
				daoPojos411.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(5).setTableShardValue(5), daoPojos411);
			assertEquals(3,affected.length);
			
			// By shardValueshardColValue
			List<PeopleGen> daoPojos5 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_0ShardColvalue_0"+i);
				daoPojos5.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(4).setShardColValue("Age", 20), daoPojos5);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos501 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_0ShardColvalue_1"+i);
				daoPojos501.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(4).setShardColValue("Age", 21), daoPojos501);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos6 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_1ShardColvalue_0"+i);
				daoPojos6.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(5).setShardColValue("Age", 20), daoPojos6);
			assertEquals(3,affected.length);
			
			List<PeopleGen> daoPojos611 = new ArrayList<PeopleGen>(3);
			for(int i=0;i<3;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(i+200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_1ShardColvalue_1"+i);
				daoPojos611.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(5).setShardColValue("Age", 21), daoPojos611);
			assertEquals(3,affected.length);
			
			// By setShardValuefields
			List<PeopleGen> daoPojos7 = new ArrayList<PeopleGen>(2);
			for(int i=0;i<2;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(200);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_0fields_"+i);
				daoPojos7.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(4), daoPojos7);
			assertEquals(2,affected.length);
			
			List<PeopleGen> daoPojos711 = new ArrayList<PeopleGen>(2);
			for(int i=0;i<2;i++)
			{
				PeopleGen daoPojo = new PeopleGen();
				daoPojo.setCityID(201);
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardValue_1fields_"+i);
				daoPojos711.add(daoPojo);
			}
			affected = dao.batchInsert(new DalHints().setShardValue(5), daoPojos711);
			assertEquals(2,affected.length);
			
			// By fieldsfields
						List<PeopleGen> daoPojos8 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(200);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_0fields_"+i);
							daoPojos8.add(daoPojo);
						}
						affected = dao.batchInsert(new DalHints(), daoPojos8);
						assertEquals(2,affected.length);
						
						List<PeopleGen> daoPojos811 = new ArrayList<PeopleGen>(2);
						for(int i=0;i<2;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setCityID(201);
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_1fields_"+i);
							daoPojos811.add(daoPojo);
						}
						affected = dao.batchInsert(new DalHints(), daoPojos811);
						assertEquals(2,affected.length);
	}
	
	@Test
	public void testcombinedInsert1() throws Exception {
		// By shardValuetabelShard
				List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0TableShard_0"+i);
						daoPojos1.add(daoPojo);
					}
				    int affected = dao.combinedInsert(new DalHints().setShardValue(4).inTableShard("0"), daoPojos1);
					assertEquals(3,affected);
					
					List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0TableShard_1"+i);
						daoPojos101.add(daoPojo);
					}
				    affected = dao.combinedInsert(new DalHints().setShardValue(4).inTableShard("1"), daoPojos101);
					assertEquals(3,affected);
					
					List<PeopleGen> daoPojos2 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1TableShard_0"+i);
						daoPojos2.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(5).inTableShard("0"), daoPojos2);
					assertEquals(3,affected);
					
					List<PeopleGen> daoPojos211 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1TableShard_1"+i);
						daoPojos211.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(5).inTableShard("1"), daoPojos211);
					assertEquals(3,affected);
					
					// By shardValuetableShardValue
					List<PeopleGen> daoPojos3 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0TableShardValue_0"+i);
						daoPojos3.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(4).setTableShardValue(4), daoPojos3);
					assertEquals(3,affected);
					
					List<PeopleGen> daoPojos301 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0TableShardValue_1"+i);
						daoPojos301.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(4).setTableShardValue(5), daoPojos301);
					assertEquals(3,affected);
					
					List<PeopleGen> daoPojos4 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1TableShardValue_0"+i);
						daoPojos4.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(5).setTableShardValue(4), daoPojos4);
					assertEquals(3,affected);
					
					List<PeopleGen> daoPojos411 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1TableShardValue_1"+i);
						daoPojos411.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(5).setTableShardValue(5), daoPojos411);
					assertEquals(3,affected);
					
					// By shardValueshardColValue
					List<PeopleGen> daoPojos5 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0ShardColvalue_0"+i);
						daoPojos5.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(4).setShardColValue("Age", 20), daoPojos5);
					assertEquals(3,affected);
					
					List<PeopleGen> daoPojos501 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0ShardColvalue_1"+i);
						daoPojos501.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(4).setShardColValue("Age", 21), daoPojos501);
					assertEquals(3,affected);
					
					List<PeopleGen> daoPojos6 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1ShardColvalue_0"+i);
						daoPojos6.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(5).setShardColValue("Age", 20), daoPojos6);
					assertEquals(3,affected);
					
					List<PeopleGen> daoPojos611 = new ArrayList<PeopleGen>(3);
					for(int i=0;i<3;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(i+200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1ShardColvalue_1"+i);
						daoPojos611.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(5).setShardColValue("Age", 21), daoPojos611);
					assertEquals(3,affected);
					
					// By shardValuefields
					List<PeopleGen> daoPojos7 = new ArrayList<PeopleGen>(2);
					for(int i=0;i<2;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_0fields_"+i);
						daoPojos7.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(4), daoPojos7);
					assertEquals(2,affected);
					
					List<PeopleGen> daoPojos711 = new ArrayList<PeopleGen>(2);
					for(int i=0;i<2;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(201);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardValue_1fields_"+i);
						daoPojos711.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints().setShardValue(5), daoPojos711);
					assertEquals(2,affected);
					
					// By fieldsfields
					List<PeopleGen> daoPojos8 = new ArrayList<PeopleGen>(2);
					for(int i=0;i<2;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(200);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByfields_0fields_"+i);
						daoPojos8.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints(), daoPojos8);
					assertEquals(2,affected);
					
					List<PeopleGen> daoPojos811 = new ArrayList<PeopleGen>(2);
					for(int i=0;i<2;i++)
					{
						PeopleGen daoPojo = new PeopleGen();
						daoPojo.setCityID(201);
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByfields_1fields_"+i);
						daoPojos811.add(daoPojo);
					}
					affected = dao.combinedInsert(new DalHints(), daoPojos811);
					assertEquals(2,affected);
	}
	
	@Test
	public void testcombinedInsert2() throws Exception {
		// By fieldstabelShard
		KeyHolder keyHolder100 = new KeyHolder();
		List<PeopleGen> daoPojos100 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByfields_0TableShard_0"+i);
			daoPojos100.add(daoPojo);
		}
		int affected = dao.combinedInsert(new DalHints().inTableShard("0"), keyHolder100, daoPojos100);
		assertEquals(2,affected);
		assertEquals(2,keyHolder100.size());
		
		KeyHolder keyHolder101 = new KeyHolder();
		List<PeopleGen> daoPojos101 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(200);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByfields_0TableShard_1"+i);
			daoPojos101.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inTableShard("1"), keyHolder101, daoPojos101);
		assertEquals(2,affected);
		assertEquals(2,keyHolder101.size());
		
		KeyHolder keyHolder110 = new KeyHolder();
		List<PeopleGen> daoPojos110 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(201);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByfields_1TableShard_0"+i);
			daoPojos110.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inTableShard("0"), keyHolder110, daoPojos110);
		assertEquals(2,affected);
		assertEquals(2,keyHolder110.size());
		
		KeyHolder keyHolder111 = new KeyHolder();
		List<PeopleGen> daoPojos111 = new ArrayList<PeopleGen>(2);
		for(int i=0;i<2;i++)
		{
			PeopleGen daoPojo = new PeopleGen();
			daoPojo.setCityID(201);
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByfields_1TableShard_1"+i);
			daoPojos111.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inTableShard("1"), keyHolder111, daoPojos111);
		assertEquals(2,affected);
		assertEquals(2,keyHolder111.size());
		
		// By fieldstableShardValue
				KeyHolder keyHolder200 = new KeyHolder();
				List<PeopleGen> daoPojos200 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_0TableShardValue_0"+i);
					daoPojos200.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setTableShardValue(4), keyHolder200, daoPojos200);
				assertEquals(2,affected);
				assertEquals(2,keyHolder200.size());
				
				KeyHolder keyHolder201 = new KeyHolder();
				List<PeopleGen> daoPojos201 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_0TableShardValue_1"+i);
					daoPojos201.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setTableShardValue(5), keyHolder201, daoPojos201);
				assertEquals(2,affected);
				assertEquals(2,keyHolder201.size());
				
				KeyHolder keyHolder210 = new KeyHolder();
				List<PeopleGen> daoPojos210 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(201);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_1TableShardValue_0"+i);
					daoPojos210.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setTableShardValue(4), keyHolder210, daoPojos210);
				assertEquals(2,affected);
				assertEquals(2,keyHolder210.size());
				
				KeyHolder keyHolder211 = new KeyHolder();
				List<PeopleGen> daoPojos211 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(201);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_1TableShardValue_1"+i);
					daoPojos211.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setTableShardValue(5), keyHolder211, daoPojos211);
				assertEquals(2,affected);
				assertEquals(2,keyHolder211.size());
				
				// By shardColValue
				KeyHolder keyHolder300 = new KeyHolder();
				List<PeopleGen> daoPojos300 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_0ShardColvalue_0"+i);
					daoPojos300.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setShardColValue("Age", 20), keyHolder300, daoPojos300);
				assertEquals(2,affected);
				assertEquals(2,keyHolder300.size());
				
				KeyHolder keyHolder301 = new KeyHolder();
				List<PeopleGen> daoPojos301 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_0ShardColvalue_1"+i);
					daoPojos301.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setShardColValue("Age", 21), keyHolder301, daoPojos301);
				assertEquals(2,affected);
				assertEquals(2,keyHolder301.size());
				
				KeyHolder keyHolder310 = new KeyHolder();
				List<PeopleGen> daoPojos310 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(201);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_1ShardColvalue_0"+i);
					daoPojos310.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setShardColValue("Age", 20), keyHolder310, daoPojos310);
				assertEquals(2,affected);
				assertEquals(2,keyHolder310.size());
				
				KeyHolder keyHolder311 = new KeyHolder();
				List<PeopleGen> daoPojos311 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(201);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_1ShardColvalue_1"+i);
					daoPojos311.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setShardColValue("Age", 21), keyHolder311, daoPojos311);
				assertEquals(2,affected);
				assertEquals(2,keyHolder311.size());
				
				// By fields
				KeyHolder keyHolder400 = new KeyHolder();
				List<PeopleGen> daoPojos7 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_0fields_"+i);
					daoPojos7.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints(), keyHolder400, daoPojos7);
				assertEquals(2,affected);
				assertEquals(2,keyHolder400.size());
				
				KeyHolder keyHolder410 = new KeyHolder();
				List<PeopleGen> daoPojos8 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setCityID(201);
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields_1fields_"+i);
					daoPojos8.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints(), keyHolder410, daoPojos8);
				assertEquals(2,affected);
				assertEquals(2,keyHolder410.size());
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
		        //by shardtableShard
				Number id = 3l;
				PeopleGen ret = dao.queryByPk(id, new DalHints().inShard(0).inTableShard(0));
				assertEquals("InsertByShardValue_0TableShard_02",ret.getName());
				
				ret = dao.queryByPk(id, new DalHints().inShard(0).inTableShard(1));
				assertEquals("InsertByShardValue_0TableShard_12",ret.getName());
				
				ret = dao.queryByPk(id, new DalHints().inShard(1).inTableShard(0));
				assertEquals("InsertByShardValue_1TableShard_02",ret.getName());
				
				ret = dao.queryByPk(id, new DalHints().inShard(1).inTableShard(1));
				assertEquals("InsertByShardValue_1TableShard_12",ret.getName());
				
				// By shardValuetableShardValue
				ret = dao.queryByPk(id, new DalHints().setShardValue(4).setTableShardValue(4));
				assertEquals("InsertByShardValue_0TableShard_02",ret.getName());
				
				ret = dao.queryByPk(id, new DalHints().setShardValue(4).setTableShardValue(5));
				assertEquals("InsertByShardValue_0TableShard_12",ret.getName());
				
				ret = dao.queryByPk(id, new DalHints().setShardValue(5).setTableShardValue(4));
				assertEquals("InsertByShardValue_1TableShard_02",ret.getName());
				
				ret = dao.queryByPk(id, new DalHints().setShardValue(5).setTableShardValue(5));
				assertEquals("InsertByShardValue_1TableShard_12",ret.getName());
				
				// By shardColValueshardColValue
				ret = dao.queryByPk(id, new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20));
				assertEquals("InsertByShardValue_0TableShard_02",ret.getName());
				
				ret = dao.queryByPk(id, new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21));
				assertEquals("InsertByShardValue_0TableShard_12",ret.getName());
				
				ret = dao.queryByPk(id, new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 20));
				assertEquals("InsertByShardValue_1TableShard_02",ret.getName());
				
				ret = dao.queryByPk(id, new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 21));
				assertEquals("InsertByShardValue_1TableShard_12",ret.getName());
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		//by shardtableShard
				PeopleGen pk = new PeopleGen();
				pk.setID(3);
				PeopleGen ret = dao.queryByPk(pk, new DalHints().inShard(0).inTableShard(0));
				assertEquals("InsertByShardValue_0TableShard_02",ret.getName());
				
				ret = dao.queryByPk(pk, new DalHints().inShard(0).inTableShard(1));
				assertEquals("InsertByShardValue_0TableShard_12",ret.getName());
				
				ret = dao.queryByPk(pk, new DalHints().inShard(1).inTableShard(0));
				assertEquals("InsertByShardValue_1TableShard_02",ret.getName());
				
				ret = dao.queryByPk(pk, new DalHints().inShard(1).inTableShard(1));
				assertEquals("InsertByShardValue_1TableShard_12",ret.getName());
				
				// By shardValuetableShardValue
				ret = dao.queryByPk(pk, new DalHints().setShardValue(4).setTableShardValue(4));
				assertEquals("InsertByShardValue_0TableShard_02",ret.getName());
				
				ret = dao.queryByPk(pk, new DalHints().setShardValue(4).setTableShardValue(5));
				assertEquals("InsertByShardValue_0TableShard_12",ret.getName());
				
				ret = dao.queryByPk(pk, new DalHints().setShardValue(5).setTableShardValue(4));
				assertEquals("InsertByShardValue_1TableShard_02",ret.getName());
				
				ret = dao.queryByPk(pk, new DalHints().setShardValue(5).setTableShardValue(5));
				assertEquals("InsertByShardValue_1TableShard_12",ret.getName());
				
				// By shardColValueshardColValue
				ret = dao.queryByPk(pk, new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20));
				assertEquals("InsertByShardValue_0TableShard_02",ret.getName());
				
				ret = dao.queryByPk(pk, new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21));
				assertEquals("InsertByShardValue_0TableShard_12",ret.getName());
				
				ret = dao.queryByPk(pk, new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 20));
				assertEquals("InsertByShardValue_1TableShard_02",ret.getName());
				
				ret = dao.queryByPk(pk, new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 21));
				assertEquals("InsertByShardValue_1TableShard_12",ret.getName());
				
				// By fields
				pk.setID(10);
				pk.setCityID(200);
				pk.setAge(20);
				ret = dao.queryByPk(pk, new DalHints());
				assertEquals("InsertByShardValue_0fields_0",ret.getName());
				
				pk.setAge(21);
				ret = dao.queryByPk(pk, new DalHints());
				assertEquals("InsertByShardValue_0fields_1",ret.getName());

				pk.setCityID(201);
				pk.setAge(20);
				ret = dao.queryByPk(pk, new DalHints());
				assertEquals("InsertByShardValue_1fields_0",ret.getName());
				
				pk.setAge(21);
				ret = dao.queryByPk(pk, new DalHints());
				assertEquals("InsertByShardValue_1fields_1",ret.getName());
	}
	
	@Test
	public void testUpdate1() throws Exception {
		//by dbshardtableShard
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
				
				// By shardValuetableShardValue
				daoPojo.setID(2);
				daoPojo.setName("UpdateByShardValue_0TableShardValue_01");
				ret = dao.update(new DalHints().setShardValue(4).setTableShardValue(4), daoPojo);
				assertEquals(1,ret);
				
				daoPojo.setName("UpdateByShardValue_0TableShardValue_11");
				ret = dao.update(new DalHints().setShardValue(4).setTableShardValue(5), daoPojo);
				assertEquals(1,ret);
				
				daoPojo.setName("UpdateByShardValue_1TableShardValue_01");
				ret = dao.update(new DalHints().setShardValue(5).setTableShardValue(4), daoPojo);
				assertEquals(1,ret);
				
				daoPojo.setName("UpdateByShardValue_1TableShardValue_11");
				ret = dao.update(new DalHints().setShardValue(5).setTableShardValue(5), daoPojo);
				assertEquals(1,ret);
				
				// By shardColValueshardColValue
				daoPojo.setID(3);
				daoPojo.setName("UpdateByShardColvalue_0ShardColvalue_02");
				ret = dao.update(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20), daoPojo);
				assertEquals(1,ret);
				
				daoPojo.setName("UpdateByShardColvalue_0ShardColvalue_12");
				ret = dao.update(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21), daoPojo);
				assertEquals(1,ret);
				
				daoPojo.setName("UpdateByShardColvalue_1ShardColvalue_02");
				ret = dao.update(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 20), daoPojo);
				assertEquals(1,ret);
				
				daoPojo.setName("UpdateByShardColvalue_1ShardColvalue_12");
				ret = dao.update(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 21), daoPojo);
				assertEquals(1,ret);
				
				// By fields
				daoPojo.setID(10);
				daoPojo.setCityID(200);
				daoPojo.setAge(20);
				daoPojo.setName("UpdateByfields_0fields_0");
				ret = dao.update(new DalHints(), daoPojo);
				assertEquals(1,ret);
				
				daoPojo.setAge(21);
				daoPojo.setName("UpdateByfields_0fields_1");
				ret = dao.update(new DalHints(), daoPojo);
				assertEquals(1,ret);
				
				daoPojo.setCityID(201);
				daoPojo.setAge(20);
				daoPojo.setName("UpdatetByfields_1fields_0");
				ret = dao.update(new DalHints(), daoPojo);
				assertEquals(1,ret);
				
				daoPojo.setAge(21);
				daoPojo.setName("UpdatetByfields_1fields_1");
				ret = dao.update(new DalHints(), daoPojo);
				assertEquals(1,ret);
	}
	
	@Test
	public void testUpdate2() throws Exception {
		// By dbshardtabelShard
				List<PeopleGen> daoPojos1 = new ArrayList<PeopleGen>(2);
				for(int i=0;i<2;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(i+1);
//					daoPojo.setAge(i+20);
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
//					daoPojo.setAge(i+20);
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
//					daoPojo.setAge(i+20);
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
//					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByShard_1TableShard_1"+i);
					daoPojos1.add(daoPojo);
				}
				affected = dao.update(new DalHints().inShard(1).inTableShard("1"), daoPojos1);
				assertEquals(2,affected.length);
				
				// By ShardValuetableShardValue
						List<PeopleGen> daoPojos3 = new ArrayList<PeopleGen>(2);
						for(int i=3;i<5;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardValue_0TableShardValue_0"+i);
							daoPojos3.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardValue(4).setTableShardValue(4), daoPojos3);
						assertEquals(2,affected.length);
						
						daoPojos3.clear();
						for(int i=3;i<5;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardValue_0TableShardValue_1"+i);
							daoPojos3.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardValue(4).setTableShardValue(5), daoPojos3);
						assertEquals(2,affected.length);
						
						daoPojos3.clear();
						for(int i=3;i<5;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardValue_1TableShardValue_0"+i);
							daoPojos3.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardValue(5).setTableShardValue(4), daoPojos3);
						assertEquals(2,affected.length);
						
						daoPojos3.clear();
						for(int i=3;i<5;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardValue_1TableShardValue_1"+i);
							daoPojos3.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardValue(5).setTableShardValue(5), daoPojos3);
						assertEquals(2,affected.length);
						
						// By shardColValueshardColValue
						List<PeopleGen> daoPojos5 = new ArrayList<PeopleGen>(2);
						for(int i=6;i<8;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardColValue_0ShardColvalue_0"+i);
							daoPojos5.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20), daoPojos5);
						assertEquals(2,affected.length);
						
						daoPojos5.clear();
						for(int i=6;i<8;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardColValue_0ShardColvalue_1"+i);
							daoPojos5.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21), daoPojos5);
						assertEquals(2,affected.length);
						
						daoPojos5.clear();
						for(int i=6;i<8;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardColValue_1ShardColvalue_0"+i);
							daoPojos5.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 20), daoPojos5);
						assertEquals(2,affected.length);
						
						daoPojos5.clear();
						for(int i=6;i<8;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardColValue_1ShardColvalue_1"+i);
							daoPojos5.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 21), daoPojos5);
						assertEquals(2,affected.length);
						
						// By fields
						List<PeopleGen> daoPojos7 = new ArrayList<PeopleGen>(2);
						for(int i=9;i<11;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(10);
							daoPojo.setCityID(200);
							daoPojo.setAge(i+11);
							daoPojo.setName("UpdateByfields_0fields_"+i);
							daoPojos7.add(daoPojo);
						}
						affected = dao.update(new DalHints(), daoPojos7);
						assertEquals(2,affected.length);
						
						daoPojos7.clear();
						for(int i=9;i<11;i++)
						{
							PeopleGen daoPojo = new PeopleGen();
							daoPojo.setID(10);
							daoPojo.setCityID(201);
							daoPojo.setAge(i+11);
							daoPojo.setName("UpdateByfields_1fields_"+i);
							daoPojos7.add(daoPojo);
						}
						affected = dao.update(new DalHints(), daoPojos7);
						assertEquals(2,affected.length);
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		// By dbshardtabelShard
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
		
		// By ShardValuetableShardValue
				List<PeopleGen> daoPojos3 = new ArrayList<PeopleGen>(2);
				for(int i=3;i<5;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(i+1);
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByShardValue_0TableShardValue_0"+i);
					daoPojos3.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardValue(4).setTableShardValue(4), daoPojos3);
				assertEquals(2,affected.length);
				
				daoPojos3.clear();
				for(int i=3;i<5;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(i+1);
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByShardValue_0TableShardValue_1"+i);
					daoPojos3.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardValue(4).setTableShardValue(5), daoPojos3);
				assertEquals(2,affected.length);
				
				daoPojos3.clear();
				for(int i=3;i<5;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(i+1);
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByShardValue_1TableShardValue_0"+i);
					daoPojos3.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardValue(5).setTableShardValue(4), daoPojos3);
				assertEquals(2,affected.length);
				
				daoPojos3.clear();
				for(int i=3;i<5;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(i+1);
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByShardValue_1TableShardValue_1"+i);
					daoPojos3.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardValue(5).setTableShardValue(5), daoPojos3);
				assertEquals(2,affected.length);
				
				// By shardColValueshardColValue
				List<PeopleGen> daoPojos5 = new ArrayList<PeopleGen>(2);
				for(int i=6;i<8;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(i+1);
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByShardColValue_0ShardColvalue_0"+i);
					daoPojos5.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20), daoPojos5);
				assertEquals(2,affected.length);
				
				daoPojos5.clear();
				for(int i=6;i<8;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(i+1);
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByShardColValue_0ShardColvalue_1"+i);
					daoPojos5.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21), daoPojos5);
				assertEquals(2,affected.length);
				
				daoPojos5.clear();
				for(int i=6;i<8;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(i+1);
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByShardColValue_1ShardColvalue_0"+i);
					daoPojos5.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 20), daoPojos5);
				assertEquals(2,affected.length);
				
				daoPojos5.clear();
				for(int i=6;i<8;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(i+1);
					daoPojo.setCityID(200);
					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByShardColValue_1ShardColvalue_1"+i);
					daoPojos5.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 21), daoPojos5);
				assertEquals(2,affected.length);
				
				// By fields
				List<PeopleGen> daoPojos7 = new ArrayList<PeopleGen>(2);
				for(int i=9;i<11;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(10);
					daoPojo.setCityID(200);
					daoPojo.setAge(i+11);
					daoPojo.setName("UpdateByfields_0fields_"+i);
					daoPojos7.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints(), daoPojos7);
				assertEquals(2,affected.length);
				
				daoPojos7.clear();
				for(int i=9;i<11;i++)
				{
					PeopleGen daoPojo = new PeopleGen();
					daoPojo.setID(10);
					daoPojo.setCityID(201);
					daoPojo.setAge(i+11);
					daoPojo.setName("UpdateByfields_1fields_"+i);
					daoPojos7.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints(), daoPojos7);
				assertEquals(2,affected.length);
	}

}
