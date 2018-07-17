package shardtest.newVersionCodeTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.google.common.collect.Lists;
import dao.shard.newVersionCode.IgnoreMissingFieldsAndAllowPartialTestOnMysqlDao;
import entity.MysqlPersonTableWithDiffColumns;
import org.junit.*;
import testUtil.DalHintsChecker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit test of MysqlPersonTableWithDiffColumnsDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class IgnoreMissingFieldsAndAllowPartialTestOnMysqlDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnMysql";

	private static DalClient client = null;
	private static IgnoreMissingFieldsAndAllowPartialTestOnMysqlDao dao = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new IgnoreMissingFieldsAndAllowPartialTestOnMysqlDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		dao.test_def_truncate(new DalHints().inShard(0));
		dao.test_def_truncate(new DalHints().inShard(1));
		
		List<MysqlPersonTableWithDiffColumns> daoPojos = new ArrayList<MysqlPersonTableWithDiffColumns>(
				6);
		for (int i = 0; i < 6; i++) {
			MysqlPersonTableWithDiffColumns daoPojo = new MysqlPersonTableWithDiffColumns();
			daoPojo.setAge(i + 20);
//			if(i%2==0)
//			daoPojo.setName("Initial_Shard_0" + i);
////				daoPojo.setName(null);
//			else
//				daoPojo.setName("Initial_Shard_1" + i);
////				daoPojo.setName(null);
			daoPojos.add(daoPojo);
		}
		dao.insert(new DalHints(), daoPojos);

	}
	
	@After
	public void tearDown() throws Exception {
//		dao.test_def_truncate(new DalHints().inShard(0));
//		dao.test_def_truncate(new DalHints().inShard(1));
	}


	@Test
	public void test_build_queryIgnoreMissingFields_first() throws Exception {
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
		
		MysqlPersonTableWithDiffColumns ret;
		
		try{
			ret= dao.test_build_query_first(Age, new DalHints().inAllShards());
			fail();
		}
		catch(Exception e)
		{}

		DalHints hints1=new DalHints();
		DalHints original=hints1.clone();

		ret = dao.test_build_query_first(Age, hints1.inAllShards().ignoreMissingFields());
	    assertEquals(1,ret.getID().intValue());
//	    assertEquals("Initial_Shard_13", ret.getName());
//	    List<DalHintEnum> notNullItems=new ArrayList<>();
//	    notNullItems.add(DalHintEnum.allShards);
//		notNullItems.add(DalHintEnum.ignoreMissingFields);
//		notNullItems.add(DalHintEnum.resultSorter);
//		DalHintsChecker.checkNull(hints1,notNullItems);
		List<DalHintEnum> notNullItems1=Lists.newArrayList(DalHintEnum.allShards,DalHintEnum.ignoreMissingFields,DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints1,notNullItems1);

		ret = dao.test_build_query_first(Age, new DalHints().setShardColValue("Age", 21).ignoreMissingFields());
		assertEquals(2,ret.getID().intValue());
//	    assertEquals("Initial_Shard_00", ret.getName());
	}
	
	
	
	@Test
	public void test_build_queryIgnoreMissingFields_list() throws Exception {
	    //List<MysqlPersonTableWithDiffColumns> ret = dao.test_build_query_list(Age, new DalHints());

		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
	
		List<MysqlPersonTableWithDiffColumns> ret ;
		 
		try{
			ret= dao.test_build_query_list(Age, new DalHints().inAllShards());
			fail();
		}
		catch(Exception e){}
		
		ret= dao.test_build_query_list(Age, new DalHints().inAllShards().ignoreMissingFields());
	    assertEquals(3, ret.size());
	    
	    ret = dao.test_build_query_list(Age, new DalHints().setShardColValue("Age", 20).ignoreMissingFields());
	    assertEquals(2, ret.size());
	    
	    ret = dao.test_build_query_list(Age, new DalHints().shardBy("Age").ignoreMissingFields());
	    assertEquals(3, ret.size());
	    
	    Age.clear();
	    Age.add(200);
	    ret=dao.test_build_query_list(Age, new DalHints().inAllShards().ignoreMissingFields());
	    assertEquals(0, ret.size());
	    
//	    for(int i=0;i<ret.size();i++)
//	    	System.out.println(ret.get(i).getAge());
//	    assertNull(ret);
	}
	
	
	
	@Test
	public void testtest_build_queryIgnoreMissingFields_listByPage() throws Exception {
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
	    List<MysqlPersonTableWithDiffColumns> ret = dao.test_build_query_listByPage(Age, 2, 1, new DalHints().inShard(0).ignoreMissingFields());
//	    assertEquals("Initial_Shard_02", ret.get(0).getName());
	    assertEquals(2, ret.get(0).getID().intValue());
	    
	    ret = dao.test_build_query_listByPage(Age, 1, 1, new DalHints().setShardColValue("Age", 21).ignoreMissingFields());
//	    assertEquals("Initial_Shard_13", ret.get(0).getName());
	    assertEquals(2, ret.get(0).getID().intValue());
	}
	
	
	
	@Test
	public void testtest_build_queryIgnoreMissingFields_single() throws Exception {
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
	    MysqlPersonTableWithDiffColumns ret = dao.test_build_query_single(Age, new DalHints().setShardColValue("Age", 21).ignoreMissingFields());
	    assertEquals(2, ret.getID().intValue());
	}
	
	
	
	@Test
	public void testtest_def_queryIgnoreMissingFields_first() throws Exception {
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
	    
		MysqlPersonTableWithDiffColumns ret;
		try {
			ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().setShardColValue("Age", 20));
			fail();
		} catch (Exception e) {
		}
		
		ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().setShardColValue("Age", 20).ignoreMissingFields());
	    assertEquals(1, ret.getID().intValue());
	    
	    ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().inAllShards().ignoreMissingFields());
	    assertEquals(1, ret.getID().intValue());
	    
	    Age.clear();
	    Age.add(30);
	    Age.add(31);
	    
	    ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().inShard(1).ignoreMissingFields());
	    assertNull(ret);	    
	}
	
	@Test
	public void testtest_def_queryAllowPartial_first() throws Exception {
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
	    
		MysqlPersonTableWithDiffColumns ret;
		try {
			ret = dao.test_def_queryAllowPartial_first(Age, new DalHints().setShardColValue("Age", 20));
			fail();
		} catch (Exception e) {
		}
		
		ret = dao.test_def_queryAllowPartial_first(Age, new DalHints().setShardColValue("Age", 20).allowPartial());
	    assertEquals(1, ret.getID().intValue());
	    
	    ret = dao.test_def_queryAllowPartial_first(Age, new DalHints().inAllShards().allowPartial());
	    assertEquals(1, ret.getID().intValue());
	    
	    Age.clear();
	    Age.add(30);
	    Age.add(31);
	    
	    ret = dao.test_def_queryAllowPartial_first(Age, new DalHints().inShard(1).allowPartial());
	    assertNull(ret);	    
	}
	
	
	
	@Test
	public void test_def_queryPartialStrings_first() throws Exception {
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(21);
		Age.add(22);
	    
//		MysqlPersonTableWithDiffColumns ret;
//		try {
//			ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().setShardColValue("Age", 20));
//			fail();
//		} catch (Exception e) {
//		}
//		
//		ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().setShardColValue("Age", 20).ignoreMissingFields());
//	    assertEquals(1, ret.getID().intValue());
//	    
//	    ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().inAllShards().ignoreMissingFields());
//	    assertEquals(1, ret.getID().intValue());
//	    
//	    Age.clear();
//	    Age.add(30);
//	    Age.add(31);
	    DalHints hints=new DalHints();
	    DalHints original=hints.clone();
		MysqlPersonTableWithDiffColumns ret = dao.test_def_queryPartialStrings_first(Age, hints.ignoreMissingFields().inShard(1));
	    assertNotNull(ret);


		List<DalHintEnum> notNullItems=new ArrayList<>();
		notNullItems.add(DalHintEnum.partialQuery);
		notNullItems.add(DalHintEnum.ignoreMissingFields);
		notNullItems.add(DalHintEnum.shard);
//		DalHintsChecker.checkNull(hints,notNullItems);
		DalHintsChecker.checkEquals(original,hints,notNullItems);
	}
	
	@Test
	public void testtest_def_queryPartialStrings_first1() throws Exception {
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(21);
		Age.add(22);
	    
//		MysqlPersonTableWithDiffColumns ret;
//		try {
//			ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().setShardColValue("Age", 20));
//			fail();
//		} catch (Exception e) {
//		}
//		
//		ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().setShardColValue("Age", 20).ignoreMissingFields());
//	    assertEquals(1, ret.getID().intValue());
//	    
//	    ret = dao.test_def_queryIgnoreMissingFields_first(Age, new DalHints().inAllShards().ignoreMissingFields());
//	    assertEquals(1, ret.getID().intValue());
//	    
//	    Age.clear();
//	    Age.add(30);
//	    Age.add(31);
	    
		MysqlPersonTableWithDiffColumns ret = dao.test_def_queryPartialStrings_first(Age, new DalHints().allowPartial().inShard(1));
	    assertNotNull(ret);	    
	}
	
	@Test
	public void testtest_def_queryIgnoreMissingFields_list() throws Exception {
		
		List<Integer> Age = new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
		
		List<MysqlPersonTableWithDiffColumns> ret;
		try
		{
			ret=dao.test_def_queryIgnoreMissingFields_list(Age, new DalHints().inShard(1));
			fail();
		}
		catch(Exception e){}
		
		ret = dao.test_def_queryIgnoreMissingFields_list(Age, new DalHints().inShard(1).ignoreMissingFields());
		assertEquals(1, ret.size());
		assertEquals(23, ret.get(0).getAge().intValue());
		assertEquals(2,ret.get(0).getID().intValue());

		ret = dao.test_def_queryIgnoreMissingFields_list(Age,
				new DalHints().setShardColValue("Age", 20).ignoreMissingFields());
		assertEquals(2, ret.size());
		assertEquals(20, ret.get(0).getAge().intValue());
		assertEquals(1,ret.get(0).getID().intValue());
		
		assertEquals(22, ret.get(1).getAge().intValue());
		assertEquals(2,ret.get(1).getID().intValue());

		ret = dao.test_def_queryIgnoreMissingFields_list(Age,
				new DalHints().shardBy("Age").ignoreMissingFields());

		assertEquals(3, ret.size());

		
		assertEquals(20, ret.get(0).getAge().intValue());
		assertEquals(1,ret.get(0).getID().intValue());
		
		assertEquals(22, ret.get(1).getAge().intValue());
		assertEquals(2,ret.get(1).getID().intValue());

		assertEquals(23, ret.get(2).getAge().intValue());
		assertEquals(2,ret.get(2).getID().intValue());

		Age.clear();
		Age.add(300);
		ret = dao.test_def_queryIgnoreMissingFields_list(Age, new DalHints().inShard(0).ignoreMissingFields());
		assertEquals(0, ret.size());
	}
	
	@Test
	public void testtest_def_queryAllowPartial_list() throws Exception {
		
		List<Integer> Age = new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
		
		List<MysqlPersonTableWithDiffColumns> ret;
		try
		{
			ret=dao.test_def_queryAllowPartial_list(Age, new DalHints().inShard(1));
			fail();
		}
		catch(Exception e){}
		
		ret = dao.test_def_queryAllowPartial_list(Age, new DalHints().inShard(1).allowPartial());
		assertEquals(1, ret.size());
//		assertEquals(23, ret.get(0).getAge().intValue());
		assertEquals(2,ret.get(0).getID().intValue());

		ret = dao.test_def_queryAllowPartial_list(Age,
				new DalHints().setShardColValue("Age", 20).allowPartial());
		assertEquals(2, ret.size());
//		assertEquals(20, ret.get(0).getAge().intValue());
		assertEquals(1,ret.get(0).getID().intValue());
		
//		assertEquals(22, ret.get(1).getAge().intValue());
		assertEquals(2,ret.get(1).getID().intValue());

		ret = dao.test_def_queryAllowPartial_list(Age,
				new DalHints().shardBy("Age").allowPartial());

		assertEquals(3, ret.size());
//		assertEquals(23, ret.get(0).getAge().intValue());
		assertEquals(1,ret.get(0).getID().intValue());
		
//		assertEquals(20, ret.get(1).getAge().intValue());
		assertEquals(2,ret.get(1).getID().intValue());
		
//		assertEquals(22, ret.get(2).getAge().intValue());
		assertEquals(2,ret.get(2).getID().intValue());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		Age.clear();
		Age.add(300);
		ret = dao.test_def_queryAllowPartial_list(Age, hints.inShard(0).allowPartial());
		assertEquals(0, ret.size());


		List<DalHintEnum> notNullItems=new ArrayList<>();
		notNullItems.add(DalHintEnum.partialQuery);
		notNullItems.add(DalHintEnum.allowPartial);
		notNullItems.add(DalHintEnum.shard);
		notNullItems.add(DalHintEnum.resultSorter);

//		DalHintsChecker.checkNull(hints,notNullItems);
		DalHintsChecker.checkEquals(original,hints,notNullItems);
	}
	
	@Test
	public void testtest_def_queryIgnoreMissingFields_listByPage() throws Exception {
		//Integer Age = 1;// Test value here
		//List<Test_def_pojoPojo> ret = dao.test_def_query_listByPage(Age, 1, 10, new DalHints());
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
		
		List<MysqlPersonTableWithDiffColumns> ret;
		
		try{
			 ret = dao.test_def_queryIgnoreMissingFields_listByPage(Age, 2, 1, new DalHints().inShard(0));
			 fail();
		}
		catch(Exception e){}
		
	    ret = dao.test_def_queryIgnoreMissingFields_listByPage(Age, 2, 1, new DalHints().inShard(0).ignoreMissingFields());
	    assertEquals(22, ret.get(0).getAge().intValue());
	    assertEquals(2,ret.get(0).getID().intValue());

	    
	    ret = dao.test_def_queryIgnoreMissingFields_listByPage(Age, 1, 1, new DalHints().setShardColValue("Age", 21).ignoreMissingFields());
	    assertEquals(23, ret.get(0).getAge().intValue());
	    assertEquals(2,ret.get(0).getID().intValue());
	}
	
	@Test
	public void test_def_queryAllowPartial_listByPage() throws Exception {
		//Integer Age = 1;// Test value here
		//List<Test_def_pojoPojo> ret = dao.test_def_query_listByPage(Age, 1, 10, new DalHints());
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
		
		List<MysqlPersonTableWithDiffColumns> ret;
		
		try{
			 ret = dao.test_def_queryAllowPartial_listByPage(Age, 2, 1, new DalHints().inShard(0));
			 fail();
		}
		catch(Exception e){}
		
	    ret = dao.test_def_queryAllowPartial_listByPage(Age, 2, 1, new DalHints().inShard(0).allowPartial());
	    assertEquals(22, ret.get(0).getAge().intValue());
	    assertEquals(2,ret.get(0).getID().intValue());

	    DalHints hints=new DalHints();
	    DalHints original=hints.clone();
	    ret = dao.test_def_queryAllowPartial_listByPage(Age, 1, 1, hints.setShardColValue("Age", 21).allowPartial());
	    assertEquals(23, ret.get(0).getAge().intValue());
	    assertEquals(2,ret.get(0).getID().intValue());

		List<DalHintEnum> notNullItems=new ArrayList<>();
		notNullItems.add(DalHintEnum.shardColValues);
		notNullItems.add(DalHintEnum.allowPartial);
		notNullItems.add(DalHintEnum.resultSorter);
//		DalHintsChecker.checkNull(hints,notNullItems);
		DalHintsChecker.checkEquals(original,hints,notNullItems);
	}
	
	@Test
	public void testtest_def_queryIgnoreMissingFields_single() throws Exception {
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
	   
		MysqlPersonTableWithDiffColumns ret;
		
		try {
			ret = dao.test_def_queryIgnoreMissingFields_single(Age,
							new DalHints().setShardColValue("Age", 21));
			fail();
		}
		catch(Exception e){	
		}
		
		ret = dao.test_def_queryIgnoreMissingFields_single(Age, new DalHints().setShardColValue("Age", 21).ignoreMissingFields());

	    assertEquals(23, ret.getAge().intValue());
	    assertEquals(2, ret.getID().intValue());
	    
	    Age.clear();
	    Age.add(11);
	    Age.add(12);
	    Age.add(23);
	    ret = dao.test_def_queryIgnoreMissingFields_single(Age, new DalHints().inAllShards().ignoreMissingFields());

	    assertEquals(23, ret.getAge().intValue());
	    assertEquals(2, ret.getID().intValue());
	    
	    Age.clear();
	    Age.add(30);
	    Age.add(31);
	    ret = dao.test_def_queryIgnoreMissingFields_single(Age, new DalHints().ignoreMissingFields().setShardColValue("Age", 21));
	    assertNull(ret);
	    
	}
	
	@Test
	public void testtest_def_queryAllowPartial_single() throws Exception {
		List<Integer> Age=new ArrayList<Integer>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
	   
		MysqlPersonTableWithDiffColumns ret;
		
		try {
			ret = dao.test_def_queryAllowPartial_single(Age,
							new DalHints().setShardColValue("Age", 21));
			fail();
		}
		catch(Exception e){	
		}
		
		ret = dao.test_def_queryAllowPartial_single(Age, new DalHints().setShardColValue("Age", 21).allowPartial());

	    assertEquals(23, ret.getAge().intValue());
	    assertEquals(2, ret.getID().intValue());
	    
	    Age.clear();
	    Age.add(11);
	    Age.add(12);
	    Age.add(23);
	    ret = dao.test_def_queryAllowPartial_single(Age, new DalHints().inAllShards().allowPartial());

	    assertEquals(23, ret.getAge().intValue());
	    assertEquals(2, ret.getID().intValue());
	    
	    Age.clear();
	    Age.add(30);
	    Age.add(31);
	    ret = dao.test_def_queryAllowPartial_single(Age, new DalHints().allowPartial().setShardColValue("Age", 21));
	    assertNull(ret);
	    
	}
	
}
