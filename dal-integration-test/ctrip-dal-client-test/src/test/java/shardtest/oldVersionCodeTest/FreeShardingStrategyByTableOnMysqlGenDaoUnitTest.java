package shardtest.oldVersionCodeTest;


import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import dao.noshard.NoShardOnMysqlDao;
import dao.shard.oldVersionCode.FreeShardingStrategyByTableOnMysqlGenDao;
import entity.MysqlPersonTable;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * JUnit test of PersonGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class FreeShardingStrategyByTableOnMysqlGenDaoUnitTest {

	private static final String DATA_BASE = "FreeShardingStrategyByTableOnMysql";

	private static DalClient client = null;
	private static FreeShardingStrategyByTableOnMysqlGenDao dao = null;
	private static NoShardOnMysqlDao dao2=null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new FreeShardingStrategyByTableOnMysqlGenDao();
		dao2=new NoShardOnMysqlDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		dao2.test_def_update(new DalHints(), "0");
		dao2.test_def_update(new DalHints(), "1");

		// By tabelShard
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByTableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
			int[] affected = dao.insert(new DalHints().inTableShard("0"), daoPojos1);
			assertEquals(3,affected.length);

			List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByTableShard_1"+i);
				daoPojos2.add(daoPojo);
			}
			affected = dao.insert(new DalHints().inTableShard("1"), daoPojos2);
			assertEquals(3,affected.length);
			// By tableShardValue
			List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByTableShardValue_0"+i);
				daoPojos3.add(daoPojo);
			}
			affected = dao.insert(new DalHints().setTableShardValue(4), daoPojos3);
			assertEquals(3,affected.length);

			List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByTableShardValue_1"+i);
				daoPojos4.add(daoPojo);
			}
			affected = dao.insert(new DalHints().setTableShardValue(5), daoPojos4);
			assertEquals(3,affected.length);

			// By shardColValue
			List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardColvalue_0"+i);
				daoPojos5.add(daoPojo);
			}
			affected = dao.insert(new DalHints().setShardColValue("Age", 20), daoPojos5);
			assertEquals(3,affected.length);

			List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardColvalue_1"+i);
				daoPojos6.add(daoPojo);
			}
			affected = dao.insert(new DalHints().setShardColValue("Age", 21), daoPojos6);
			assertEquals(3,affected.length);

			// By fields
			List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByfields_0"+i);
				daoPojos7.add(daoPojo);
			}
			affected = dao.insert(new DalHints(), daoPojos7);
			assertEquals(3,affected.length);


	}

	@After
	public void tearDown() throws Exception {
//		dao2.test_def_update(new DalHints(), "0");
//		dao2.test_def_update(new DalHints(), "1");
	}


	@Test
	public void testCount() throws Exception {
		int ret = dao.count(new DalHints());
	}

	@Test
	public void testDelete1() throws Exception {
		// By tabelShard
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setID(3);
				int ret = dao.delete(new DalHints().inTableShard("0"), daoPojo);
				assertEquals(1,ret);

				ret = dao.delete(new DalHints().inTableShard("1"), daoPojo);
				assertEquals(1,ret);

				// By tableShardValue
				daoPojo.setID(6);
				ret = dao.delete(new DalHints().setTableShardValue(4), daoPojo);
				assertEquals(1,ret);

				ret = dao.delete(new DalHints().setTableShardValue(5), daoPojo);
				assertEquals(1,ret);

				// By shardColValue
				daoPojo.setID(9);
				ret = dao.delete(new DalHints().setShardColValue("Age", 20), daoPojo);
				assertEquals(1,ret);

				ret = dao.delete(new DalHints().setShardColValue("Age", 21), daoPojo);
				assertEquals(1,ret);

				// By fields
				daoPojo.setID(10);
				daoPojo.setAge(20);
				ret = dao.delete(new DalHints(), daoPojo);
				assertEquals(1,ret);

				daoPojo.setAge(21);
				ret = dao.delete(new DalHints(), daoPojo);
				assertEquals(1,ret);
	}

	@Test
	public void testDelete2() throws Exception {
		// By tabelShard
				List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(2);
				for(int i=0;i<2;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
//					daoPojo.setAge(i+20);
//					daoPojo.setName("Initial_TableShard_0"+i);
					daoPojos1.add(daoPojo);
				}
				int[] affected = dao.delete(new DalHints().inTableShard("0"), daoPojos1);
				assertEquals(2,affected.length);

				List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(2);
				for(int i=0;i<2;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
//					daoPojo.setAge(i+20);
//					daoPojo.setName("Initial_TableShard_1"+i);
					daoPojos2.add(daoPojo);
				}
				affected = dao.delete(new DalHints().inTableShard("1"), daoPojos2);
				assertEquals(2,affected.length);

				// By tableShardValue
						List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(2);
						for(int i=3;i<5;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
//							daoPojo.setName("Initial_TableShard_0"+i);
							daoPojos3.add(daoPojo);
						}
						affected = dao.delete(new DalHints().setTableShardValue(4), daoPojos3);
						assertEquals(2,affected.length);

						List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(2);
						for(int i=3;i<5;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
//							daoPojo.setName("Initial_TableShard_1"+i);
							daoPojos4.add(daoPojo);
						}
						affected = dao.delete(new DalHints().setTableShardValue(5), daoPojos4);
						assertEquals(2,affected.length);

						// By shardColValue
						List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(2);
						for(int i=6;i<8;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
//							daoPojo.setName("Initial_TableShard_0"+i);
							daoPojos5.add(daoPojo);
						}
						affected = dao.delete(new DalHints().setTableShardValue(4), daoPojos5);
						assertEquals(2,affected.length);

						List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(2);
						for(int i=6;i<8;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
//							daoPojo.setName("Initial_TableShard_1"+i);
							daoPojos6.add(daoPojo);
						}
						affected = dao.delete(new DalHints().setTableShardValue(5), daoPojos6);
						assertEquals(2,affected.length);

						// By fields
						List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(2);
						for(int i=9;i<11;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(10);
							daoPojo.setAge(i+11);
//							daoPojo.setName("Initial_TableShard_0"+i);
							daoPojos7.add(daoPojo);
						}
						affected = dao.delete(new DalHints(), daoPojos7);
						assertEquals(2,affected.length);
	}

	@Test
	public void testBatchDelete() throws Exception {
		// By tabelShard
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("Initial_TableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchDelete(new DalHints().inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);

		List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i+1);
//			daoPojo.setAge(i+20);
//			daoPojo.setName("Initial_TableShard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchDelete(new DalHints().inTableShard("1"), daoPojos2);
		assertEquals(2,affected.length);

		// By tableShardValue
				List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(2);
				for(int i=3;i<5;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
//					daoPojo.setAge(i+20);
//					daoPojo.setName("Initial_TableShard_0"+i);
					daoPojos3.add(daoPojo);
				}
				affected = dao.batchDelete(new DalHints().setTableShardValue(4), daoPojos3);
				assertEquals(2,affected.length);

				List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(2);
				for(int i=3;i<5;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
//					daoPojo.setAge(i+20);
//					daoPojo.setName("Initial_TableShard_1"+i);
					daoPojos4.add(daoPojo);
				}
				affected = dao.batchDelete(new DalHints().setTableShardValue(5), daoPojos4);
				assertEquals(2,affected.length);

				// By shardColValue
				List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(2);
				for(int i=6;i<8;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
//					daoPojo.setAge(i+20);
//					daoPojo.setName("Initial_TableShard_0"+i);
					daoPojos5.add(daoPojo);
				}
				affected = dao.batchDelete(new DalHints().setTableShardValue(4), daoPojos5);
				assertEquals(2,affected.length);

				List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(2);
				for(int i=6;i<8;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
//					daoPojo.setAge(i+20);
//					daoPojo.setName("Initial_TableShard_1"+i);
					daoPojos6.add(daoPojo);
				}
				affected = dao.batchDelete(new DalHints().setTableShardValue(5), daoPojos6);
				assertEquals(2,affected.length);

				// By fields
				List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(2);
				for(int i=9;i<11;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(10);
					daoPojo.setAge(i+11);
//					daoPojo.setName("Initial_TableShard_0"+i);
					daoPojos7.add(daoPojo);
				}
				affected = dao.batchDelete(new DalHints(), daoPojos7);
				assertEquals(2,affected.length);
	}

	@Test
	public void testGetAll() throws Exception {
		List<MysqlPersonTable> list = dao.getAll(new DalHints());
	}

	@Test
	public void testInsert1() throws Exception {
		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setAge(100);

		// By tabelShard
		//直接给出shard值
		daoPojo.setName("InsertByTableShard_0");
		int affected = dao.insert(new DalHints().inTableShard(0), daoPojo);
		assertEquals(1,affected);

		daoPojo.setName("InsertByTableShard_1");
		affected = dao.insert(new DalHints().inTableShard(1), daoPojo);
		assertEquals(1,affected);

		// By tableShardValue
		//用给出的值进行mod运算，从而得出shard值
		daoPojo.setName("InsertByTableShardValue_0");
		affected = dao.insert(new DalHints().setTableShardValue(4), daoPojo);
		assertEquals(1,affected);

		daoPojo.setName("InsertByTableShardValue_1");
		affected = dao.insert(new DalHints().setTableShardValue(5), daoPojo);
		assertEquals(1,affected);

		// By shardColValue
		//用对应的shardcol值进行mod运算，从而得出shard值，但插入的pojo值不受该shardcol值影响
		daoPojo.setName("InsertByShardColvalue_0");
		affected = dao.insert(new DalHints().setShardColValue("Age", 20), daoPojo);
		assertEquals(1,affected);

		daoPojo.setName("InsertByShardColvalue_1");
		affected = dao.insert(new DalHints().setShardColValue("Age", 31), daoPojo);
		assertEquals(1,affected);

		// By fields
		//系统自动根据pojo中提供的shardcol值进行mod运算，从而得出shard值
		daoPojo.setAge(100);
		daoPojo.setName("InsertByfields_0");
		affected = dao.insert(new DalHints(), daoPojo);
		assertEquals(1,affected);

		daoPojo.setAge(101);
		daoPojo.setName("InsertByfields_1");
		affected = dao.insert(new DalHints(), daoPojo);
		assertEquals(1,affected);
	}

	@Test
	public void testInsert2() throws Exception {
		// By tabelShard
				List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(3);
					for(int i=0;i<3;i++)
					{
						MysqlPersonTable daoPojo = new MysqlPersonTable();
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByTableShard_0"+i);
						daoPojos1.add(daoPojo);
					}
					int[] affected = dao.insert(new DalHints().inTableShard("0"), daoPojos1);
					assertEquals(3,affected.length);

					List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(3);
					for(int i=0;i<3;i++)
					{
						MysqlPersonTable daoPojo = new MysqlPersonTable();
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByTableShard_1"+i);
						daoPojos2.add(daoPojo);
					}
					affected = dao.insert(new DalHints().inTableShard("1"), daoPojos2);
					assertEquals(3,affected.length);

					// By tableShardValue
					List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(3);
					for(int i=0;i<3;i++)
					{
						MysqlPersonTable daoPojo = new MysqlPersonTable();
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByTableShardValue_0"+i);
						daoPojos3.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setTableShardValue(4), daoPojos3);
					assertEquals(3,affected.length);

					List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(3);
					for(int i=0;i<3;i++)
					{
						MysqlPersonTable daoPojo = new MysqlPersonTable();
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByTableShardValue_1"+i);
						daoPojos4.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setTableShardValue(5), daoPojos4);
					assertEquals(3,affected.length);

					// By shardColValue
					List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(3);
					for(int i=0;i<3;i++)
					{
						MysqlPersonTable daoPojo = new MysqlPersonTable();
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardColvalue_0"+i);
						daoPojos5.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardColValue("Age", 20), daoPojos5);
					assertEquals(3,affected.length);

					List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(3);
					for(int i=0;i<3;i++)
					{
						MysqlPersonTable daoPojo = new MysqlPersonTable();
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByShardColvalue_1"+i);
						daoPojos6.add(daoPojo);
					}
					affected = dao.insert(new DalHints().setShardColValue("Age", 21), daoPojos6);
					assertEquals(3,affected.length);

					// By fields
					List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(3);
					for(int i=0;i<3;i++)
					{
						MysqlPersonTable daoPojo = new MysqlPersonTable();
						daoPojo.setAge(i+20);
						daoPojo.setName("InsertByfields_0"+i);
						daoPojos7.add(daoPojo);
					}
					affected = dao.insert(new DalHints(), daoPojos7);
					assertEquals(3,affected.length);
	}

	@Test
	public void testInsert3() throws Exception {
		KeyHolder keyHolder1 = new KeyHolder();
		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setAge(100);

		// By tabelShard
		//直接给出shard值
		daoPojo.setName("InsertByTableShard_0");
		int affected = dao.insert(new DalHints().inTableShard(0), keyHolder1, daoPojo);
		assertEquals(1,affected);
		assertEquals(12l,keyHolder1.getKey());

		KeyHolder keyHolder2 = new KeyHolder();
		daoPojo.setName("InsertByTableShard_1");
		affected = dao.insert(new DalHints().inTableShard(1), keyHolder2, daoPojo);
		assertEquals(1,affected);
		assertEquals(11l,keyHolder2.getKey());

		// By tableShardValue
		//用给出的值进行mod运算，从而得出shard值
		KeyHolder keyHolder3 = new KeyHolder();
		daoPojo.setName("InsertByTableShardValue_0");
		affected = dao.insert(new DalHints().setTableShardValue(4), keyHolder3, daoPojo);
		assertEquals(1,affected);
		assertEquals(13l,keyHolder3.getKey());

		KeyHolder keyHolder4 = new KeyHolder();
		daoPojo.setName("InsertByTableShardValue_1");
		affected = dao.insert(new DalHints().setTableShardValue(5), keyHolder4, daoPojo);
		assertEquals(1,affected);
		assertEquals(12l,keyHolder4.getKey());

		// By shardColValue
		//用对应的shardcol值进行mod运算，从而得出shard值，但插入的pojo值不受该shardcol值影响
		KeyHolder keyHolder5 = new KeyHolder();
		daoPojo.setName("InsertByShardColvalue_0");
		affected = dao.insert(new DalHints().setShardColValue("Age", 20), keyHolder5, daoPojo);
		assertEquals(1,affected);
		assertEquals(14l,keyHolder5.getKey());

		KeyHolder keyHolder6 = new KeyHolder();
		daoPojo.setName("InsertByShardColvalue_1");
		affected = dao.insert(new DalHints().setShardColValue("Age", 31), keyHolder6, daoPojo);
		assertEquals(1,affected);
		assertEquals(13l,keyHolder6.getKey());

		// By fields
		//系统自动根据pojo中提供的shardcol值进行mod运算，从而得出shard值
		KeyHolder keyHolder7 = new KeyHolder();
		daoPojo.setAge(100);
		daoPojo.setName("InsertByfields_0");
		affected = dao.insert(new DalHints(), keyHolder7, daoPojo);
		assertEquals(1,affected);
		assertEquals(15l,keyHolder7.getKey());

		KeyHolder keyHolder8 = new KeyHolder();
		daoPojo.setAge(101);
		daoPojo.setName("InsertByfields_1");
		affected = dao.insert(new DalHints(), keyHolder8, daoPojo);
		assertEquals(1,affected);
		assertEquals(14l,keyHolder8.getKey());
	}

	@Test
	public void testInsert4() throws Exception {
		// By tabelShard
				KeyHolder keyHolder1 = new KeyHolder();
				List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(3);
				for(int i=0;i<3;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByTableShard_0"+i);
					daoPojos1.add(daoPojo);
				}
				int[] affected = dao.insert(new DalHints().inTableShard("0"), keyHolder1, daoPojos1);
				assertEquals(3,affected.length);
				assertEquals(3,keyHolder1.size());

				List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(3);
				for(int i=0;i<3;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByTableShard_1"+i);
					daoPojos2.add(daoPojo);
				}
				affected = dao.insert(new DalHints().inTableShard("1"), keyHolder1, daoPojos2);
				assertEquals(3,affected.length);
				assertEquals(3,keyHolder1.size());

				// By tableShardValue
						KeyHolder keyHolder2 = new KeyHolder();
						List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByTableShardValue_0"+i);
							daoPojos3.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setTableShardValue(4), keyHolder2, daoPojos3);
						assertEquals(3,affected.length);
						assertEquals(3,keyHolder2.size());

						List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByTableShardValue_0"+i);
							daoPojos4.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setTableShardValue(5), keyHolder2, daoPojos4);
						assertEquals(3,affected.length);
						assertEquals(3,keyHolder2.size());

						// By shardColValue
						KeyHolder keyHolder3 = new KeyHolder();
						List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByShardColvalue_0"+i);
							daoPojos5.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setShardColValue("Age", 20), keyHolder3, daoPojos5);
						assertEquals(3,affected.length);
						assertEquals(3,keyHolder3.size());

						List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByShardColvalue_1"+i);
							daoPojos6.add(daoPojo);
						}
						affected = dao.insert(new DalHints().setShardColValue("Age", 21), keyHolder3, daoPojos6);
						assertEquals(3,affected.length);
						assertEquals(3,keyHolder3.size());

						// By fields
						KeyHolder keyHolder4 = new KeyHolder();
						List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields"+i);
							daoPojos7.add(daoPojo);
						}
						affected = dao.insert(new DalHints(), keyHolder4, daoPojos7);
						assertEquals(3,affected.length);
						assertEquals(3,keyHolder4.size());
	}

	@Test
	public void testInsert5() throws Exception {
		// By tabelShard
					List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByTableShard_0"+i);
							daoPojos1.add(daoPojo);
						}
						int[] affected = dao.batchInsert(new DalHints().inTableShard("0"), daoPojos1);
						assertEquals(3,affected.length);

						List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByTableShard_1"+i);
							daoPojos2.add(daoPojo);
						}
						affected = dao.batchInsert(new DalHints().inTableShard("1"), daoPojos2);
						assertEquals(3,affected.length);

						// By tableShardValue
						List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByTableShardValue_0"+i);
							daoPojos3.add(daoPojo);
						}
						affected = dao.batchInsert(new DalHints().setTableShardValue(4), daoPojos3);
						assertEquals(3,affected.length);

						List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByTableShardValue_1"+i);
							daoPojos4.add(daoPojo);
						}
						affected = dao.batchInsert(new DalHints().setTableShardValue(5), daoPojos4);
						assertEquals(3,affected.length);

						// By shardColValue
						List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByShardColvalue_0"+i);
							daoPojos5.add(daoPojo);
						}
						affected = dao.batchInsert(new DalHints().setShardColValue("Age", 20), daoPojos5);
						assertEquals(3,affected.length);

						List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByShardColvalue_1"+i);
							daoPojos6.add(daoPojo);
						}
						affected = dao.batchInsert(new DalHints().setShardColValue("Age", 21), daoPojos6);
						assertEquals(3,affected.length);

						// By fields
						List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(3);
						for(int i=0;i<3;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setAge(i+20);
							daoPojo.setName("InsertByfields_0"+i);
							daoPojos7.add(daoPojo);
						}
						affected = dao.batchInsert(new DalHints(), daoPojos7);
						assertEquals(3,affected.length);
	}

	@Test
	public void testCombinedInsert1() throws Exception {
		// By tabelShard
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByTableShard_0"+i);
				daoPojos1.add(daoPojo);
			}
			int affected = dao.combinedInsert(new DalHints().inTableShard("0"), daoPojos1);
			assertEquals(3,affected);

			List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByTableShard_1"+i);
				daoPojos2.add(daoPojo);
			}
			affected = dao.combinedInsert(new DalHints().inTableShard("1"), daoPojos2);
			assertEquals(3,affected);

			// By tableShardValue
			List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByTableShardValue_0"+i);
				daoPojos3.add(daoPojo);
			}
			affected = dao.combinedInsert(new DalHints().setTableShardValue(4), daoPojos3);
			assertEquals(3,affected);

			List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByTableShardValue_1"+i);
				daoPojos4.add(daoPojo);
			}
			affected = dao.combinedInsert(new DalHints().setTableShardValue(5), daoPojos4);
			assertEquals(3,affected);

			// By shardColValue
			List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardColvalue_0"+i);
				daoPojos5.add(daoPojo);
			}
			affected = dao.combinedInsert(new DalHints().setShardColValue("Age", 20), daoPojos5);
			assertEquals(3,affected);

			List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByShardColvalue_1"+i);
				daoPojos6.add(daoPojo);
			}
			affected = dao.combinedInsert(new DalHints().setShardColValue("Age", 21), daoPojos6);
			assertEquals(3,affected);

			// By fields
			List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(3);
			for(int i=0;i<3;i++)
			{
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setAge(i+20);
				daoPojo.setName("InsertByfields_0"+i);
				daoPojos7.add(daoPojo);
			}
			affected = dao.combinedInsert(new DalHints(), daoPojos7);
			assertEquals(3,affected);
	}

	@Test
	public void testCombinedInsert2() throws Exception {
		// By tabelShard
		KeyHolder keyHolder1 = new KeyHolder();
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByTableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int affected = dao.combinedInsert(new DalHints().inTableShard("0"), keyHolder1, daoPojos1);
		assertEquals(3,affected);
		assertEquals(3,keyHolder1.size());

		List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(3);
		for(int i=0;i<3;i++)
		{
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i+20);
			daoPojo.setName("InsertByTableShard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.combinedInsert(new DalHints().inTableShard("1"), keyHolder1, daoPojos2);
		assertEquals(3,affected);
		assertEquals(3,keyHolder1.size());

		// By tableShardValue
				KeyHolder keyHolder2 = new KeyHolder();
				List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(3);
				for(int i=0;i<3;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByTableShardValue_0"+i);
					daoPojos3.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setTableShardValue(4), keyHolder2, daoPojos3);
				assertEquals(3,affected);
				assertEquals(3,keyHolder2.size());

				List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(3);
				for(int i=0;i<3;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByTableShardValue_0"+i);
					daoPojos4.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setTableShardValue(5), keyHolder2, daoPojos4);
				assertEquals(3,affected);
				assertEquals(3,keyHolder2.size());

				// By shardColValue
				KeyHolder keyHolder3 = new KeyHolder();
				List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(3);
				for(int i=0;i<3;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByShardColvalue_0"+i);
					daoPojos5.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setShardColValue("Age", 20), keyHolder3, daoPojos5);
				assertEquals(3,affected);
				assertEquals(3,keyHolder3.size());

				List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(3);
				for(int i=0;i<3;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByShardColvalue_1"+i);
					daoPojos6.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints().setShardColValue("Age", 21), keyHolder3, daoPojos6);
				assertEquals(3,affected);
				assertEquals(3,keyHolder3.size());

				// By fields
				KeyHolder keyHolder4 = new KeyHolder();
				List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(3);
				for(int i=0;i<3;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setAge(i+20);
					daoPojo.setName("InsertByfields"+i);
					daoPojos7.add(daoPojo);
				}
				affected = dao.combinedInsert(new DalHints(), keyHolder4, daoPojos7);
				assertEquals(3,affected);
				assertEquals(3,keyHolder4.size());
	}

	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<MysqlPersonTable> list = dao.queryByPage(pageSize, pageNo, hints);
	}

	@Test
	public void testQueryByPk1() throws Exception {
		//by tableShard
				Number id = 3;
				MysqlPersonTable ret = dao.queryByPk(id, new DalHints().inTableShard(0));
				assertEquals("InsertByTableShard_02",ret.getName());

				ret = dao.queryByPk(id, new DalHints().inTableShard(1));
				assertEquals("InsertByTableShard_12",ret.getName());

				// By tableShardValue
				ret = dao.queryByPk(id, new DalHints().setTableShardValue(4));
				assertEquals("InsertByTableShard_02",ret.getName());

				ret = dao.queryByPk(id, new DalHints().setTableShardValue(5));
				assertEquals("InsertByTableShard_12",ret.getName());

				// By shardColValue
				ret = dao.queryByPk(id, new DalHints().setShardColValue("Age", 20));
				assertEquals("InsertByTableShard_02",ret.getName());

				ret = dao.queryByPk(id, new DalHints().setShardColValue("Age", 21));
				assertEquals("InsertByTableShard_12",ret.getName());
	}

	@Test
	public void testQueryByPk2() throws Exception {
		//by tableShard
				MysqlPersonTable pk = new MysqlPersonTable();
				pk.setID(3);
				MysqlPersonTable ret = dao.queryByPk(pk, new DalHints().inTableShard(0));
				assertEquals("InsertByTableShard_02",ret.getName());

				ret = dao.queryByPk(pk, new DalHints().inTableShard(1));
				assertEquals("InsertByTableShard_12",ret.getName());

				// By tableShardValue
				ret = dao.queryByPk(pk, new DalHints().setTableShardValue(4));
				assertEquals("InsertByTableShard_02",ret.getName());

				ret = dao.queryByPk(pk, new DalHints().setTableShardValue(5));
				assertEquals("InsertByTableShard_12",ret.getName());

				// By shardColValue
				ret = dao.queryByPk(pk, new DalHints().setShardColValue("Age", 20));
				assertEquals("InsertByTableShard_02",ret.getName());

				ret = dao.queryByPk(pk, new DalHints().setShardColValue("Age", 21));
				assertEquals("InsertByTableShard_12",ret.getName());

				// By fields
				pk.setID(10);
				pk.setAge(20);
				ret = dao.queryByPk(pk, new DalHints());
				assertEquals("InsertByfields_00",ret.getName());

				pk.setAge(21);
				ret = dao.queryByPk(pk, new DalHints());
				assertEquals("InsertByfields_01",ret.getName());
	}

	@Test
	public void testUpdate1() throws Exception {
		//by tableShard
				MysqlPersonTable daoPojo = new MysqlPersonTable();
				daoPojo.setID(1);
				daoPojo.setName("UpdateByTableShard_00");
				int ret = dao.update(new DalHints().inTableShard(0), daoPojo);
				assertEquals(1,ret);

				daoPojo.setName("UpdateByTableShard_10");
				ret = dao.update(new DalHints().inTableShard(1), daoPojo);
				assertEquals(1,ret);

				// By tableShardValue
				daoPojo.setID(2);
				daoPojo.setName("UpdateByTableShardValue_01");
				ret = dao.update(new DalHints().setTableShardValue(4), daoPojo);
				assertEquals(1,ret);

				daoPojo.setName("UpdateByTableShardValue_11");
				ret = dao.update(new DalHints().setTableShardValue(5), daoPojo);
				assertEquals(1,ret);

				// By shardColValue
				daoPojo.setID(3);
				daoPojo.setName("UpdateByShardColvalue_02");
				ret = dao.update(new DalHints().setShardColValue("Age", 20), daoPojo);
				assertEquals(1,ret);

				daoPojo.setName("UpdateByShardColvalue_12");
				ret = dao.update(new DalHints().setShardColValue("Age", 21), daoPojo);
				assertEquals(1,ret);

				// By fields
				daoPojo.setID(10);
				daoPojo.setAge(20);
				daoPojo.setName("UpdateByfields_0");
				ret = dao.update(new DalHints(), daoPojo);
				assertEquals(1,ret);

				daoPojo.setAge(21);
				daoPojo.setName("UpdatetByfields_1");
				ret = dao.update(new DalHints(), daoPojo);
				assertEquals(1,ret);
	}

	@Test
	public void testUpdate2() throws Exception {
		// By tabelShard
				List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(2);
				for(int i=0;i<2;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
//					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByTableShard_0"+i);
					daoPojos1.add(daoPojo);
				}
				int[] affected = dao.update(new DalHints().inTableShard("0"), daoPojos1);
				assertEquals(2,affected.length);

				List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(2);
				for(int i=0;i<2;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
//					daoPojo.setAge(i+20);
					daoPojo.setName("UpdateByTableShard_1"+i);
					daoPojos2.add(daoPojo);
				}
				affected = dao.update(new DalHints().inTableShard("1"), daoPojos2);
				assertEquals(2,affected.length);

				// By tableShardValue
						List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(2);
						for(int i=3;i<5;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByTableShardValue_0"+i);
							daoPojos3.add(daoPojo);
						}
						affected = dao.update(new DalHints().setTableShardValue(4), daoPojos3);
						assertEquals(2,affected.length);

						List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(2);
						for(int i=3;i<5;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByTableShardValue_1"+i);
							daoPojos4.add(daoPojo);
						}
						affected = dao.update(new DalHints().setTableShardValue(5), daoPojos4);
						assertEquals(2,affected.length);

						// By shardColValue
						List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(2);
						for(int i=6;i<8;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardColvalue_0"+i);
							daoPojos5.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardColValue("Age", 20), daoPojos5);
						assertEquals(2,affected.length);

						List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(2);
						for(int i=6;i<8;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(i+1);
//							daoPojo.setAge(i+20);
							daoPojo.setName("UpdateByShardColvalue_1"+i);
							daoPojos6.add(daoPojo);
						}
						affected = dao.update(new DalHints().setShardColValue("Age", 21), daoPojos6);
						assertEquals(2,affected.length);

						// By fields
						List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(2);
						for(int i=9;i<11;i++)
						{
							MysqlPersonTable daoPojo = new MysqlPersonTable();
							daoPojo.setID(10);
							daoPojo.setAge(i+11);
							daoPojo.setName("UpdateByfields"+i);
							daoPojos7.add(daoPojo);
						}
						affected = dao.update(new DalHints(), daoPojos7);
						assertEquals(2,affected.length);
	}

	@Test
	public void testBatchUpdate() throws Exception {
		// By tabelShard
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+20);
			daoPojo.setName("batchUpdateByTableShard_0"+i);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchUpdate(new DalHints().inTableShard("0"), daoPojos1);
		assertEquals(2,affected.length);

		List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(2);
		for(int i=0;i<2;i++)
		{
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+20);
			daoPojo.setName("batchUpdateByTableShard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchUpdate(new DalHints().inTableShard("1"), daoPojos2);
		assertEquals(2,affected.length);

		// By tableShardValue
				List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(2);
				for(int i=3;i<5;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
					daoPojo.setAge(i+20);
					daoPojo.setName("batchUpdateByTableShardValue_0"+i);
					daoPojos3.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setTableShardValue(4), daoPojos3);
				assertEquals(2,affected.length);

				List<MysqlPersonTable> daoPojos4 = new ArrayList<MysqlPersonTable>(2);
				for(int i=3;i<5;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
					daoPojo.setAge(i+20);
					daoPojo.setName("batchUpdateByTableShardValue_1"+i);
					daoPojos4.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setTableShardValue(5), daoPojos4);
				assertEquals(2,affected.length);

				// By shardColValue
				List<MysqlPersonTable> daoPojos5 = new ArrayList<MysqlPersonTable>(2);
				for(int i=6;i<8;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
					daoPojo.setAge(i+20);
					daoPojo.setName("batchUpdateByShardColvalue_0"+i);
					daoPojos5.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardColValue("Age", 20), daoPojos5);
				assertEquals(2,affected.length);

				List<MysqlPersonTable> daoPojos6 = new ArrayList<MysqlPersonTable>(2);
				for(int i=6;i<8;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(i+1);
					daoPojo.setAge(i+20);
					daoPojo.setName("batchUpdateByShardColvalue_1"+i);
					daoPojos6.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints().setShardColValue("Age", 21), daoPojos6);
				assertEquals(2,affected.length);

				// By fields
				List<MysqlPersonTable> daoPojos7 = new ArrayList<MysqlPersonTable>(2);
				for(int i=9;i<11;i++)
				{
					MysqlPersonTable daoPojo = new MysqlPersonTable();
					daoPojo.setID(10);
					daoPojo.setAge(i+11);
					daoPojo.setName("batchUpdateByfields"+i);
					daoPojos7.add(daoPojo);
				}
				affected = dao.batchUpdate(new DalHints(), daoPojos7);
				assertEquals(2,affected.length);
	}

}
