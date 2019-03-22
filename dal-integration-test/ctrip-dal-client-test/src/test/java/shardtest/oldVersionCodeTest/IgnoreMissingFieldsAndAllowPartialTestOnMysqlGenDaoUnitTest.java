package shardtest.oldVersionCodeTest;


import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import dao.shard.oldVersionCode.IgnoreMissingFieldsAndAllowPartialTestOnMysqlGenDao;
import entity.MysqlPersonTableWithDiffColumns;
import org.junit.*;
import testUtil.DalHintsChecker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit test of PersonSimpleShardByDBOnMysqlGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class IgnoreMissingFieldsAndAllowPartialTestOnMysqlGenDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnMysql";

	private static DalClient client = null;
	private static IgnoreMissingFieldsAndAllowPartialTestOnMysqlGenDao dao = null;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new IgnoreMissingFieldsAndAllowPartialTestOnMysqlGenDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));
		
//		String Name = "InsertInAllShards";// Test value here
//		Integer Age = 100;// Test value here 
//	    int ret = dao.test_build_insert(Name, Age, new DalHints().inAllShards());
//	    assertEquals(2, ret);
//	    
//	    Set<String> shards = new HashSet<>();
//	    shards.add("0");
//	    shards.add("1");
//	    Name = "InsertByShards";
//	    ret = dao.test_build_insert(Name, Age, new DalHints().inShards(shards));
//	    assertEquals(2, ret);
	    
	    List<MysqlPersonTableWithDiffColumns> daoPojos1 = new ArrayList<MysqlPersonTableWithDiffColumns>(
				3);

		for (int i = 0; i < 6; i++) {
			MysqlPersonTableWithDiffColumns daoPojo = new MysqlPersonTableWithDiffColumns();
			daoPojo.setAge(i + 20);
//			if(i%2==0)
//			daoPojo.setName("Initial_Shard_0" + i);
//			else
//				daoPojo.setName("Initial_Shard_1" + i);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints(), daoPojos1);
	}

	@After
	public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints().inShard(0));
//		dao.test_def_update(new DalHints().inShard(1));
	} 
	
	@Test
	public void testtest_build_queryIgnoreMissingFields_first() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(23);
        Age.add(22);

        DalHints hints=new DalHints();
        DalHints original=hints.clone();
		MysqlPersonTableWithDiffColumns ret = dao.test_build_queryIgnoreMissingFields_first(Age, hints.inAllShards().ignoreMissingFields());
		assertEquals(1, ret.getID().intValue());

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.ignoreMissingFields);
		exclude.add(DalHintEnum.implicitAllTableShards);
		DalHintsChecker.checkEquals(original,hints,exclude);

       	ret = dao.test_build_queryIgnoreMissingFields_first(Age, new DalHints().setShardColValue("Age", 20).ignoreMissingFields());
		assertEquals(1, ret.getID().intValue());
//        assertNull(ret);
		
		Age.clear();
		Age.add(200);
		ret = dao.test_build_queryIgnoreMissingFields_first(Age, new DalHints().inAllShards().ignoreMissingFields());
		assertNull(ret);
	}
	
	@Test
	public void testtest_build_queryAllowPartial_first() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(23);
        Age.add(22);
		
		MysqlPersonTableWithDiffColumns ret = dao.test_build_queryAllowPartial_first(Age, new DalHints().inAllShards().allowPartial());
		assertEquals(1, ret.getID().intValue());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
       	ret = dao.test_build_queryAllowPartial_first(Age, hints.setShardColValue("Age", 20).allowPartial());
		assertEquals(1, ret.getID().intValue());
//        assertNull(ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.shardColValues);
		exclude.add(DalHintEnum.allowPartial);
		DalHintsChecker.checkEquals(original,hints,exclude);

		Age.clear();
		Age.add(200);
		ret = dao.test_build_queryAllowPartial_first(Age, new DalHints().inAllShards().allowPartial());
		assertNull(ret);
	}
	
	@Test
	public void testtest_build_queryIgnoreMissingFields() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(21);
		
		List<MysqlPersonTableWithDiffColumns> ret = dao.test_build_queryIgnoreMissingFields(Age, new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(2, ret.size());
		
		ret = dao.test_build_queryIgnoreMissingFields(Age, new DalHints().inShard(1).ignoreMissingFields());
		assertEquals(1, ret.size());
	}
	
	@Test
	public void testtest_build_queryAllowPartial() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(21);
		
		List<MysqlPersonTableWithDiffColumns> ret = dao.test_build_queryAllowPartial(Age, new DalHints().inAllShards().allowPartial());
		assertEquals(2, ret.size());
		
		ret = dao.test_build_queryAllowPartial(Age, new DalHints().inShard(1).allowPartial());
		assertEquals(1, ret.size());
	}
	
	@Test
	public void testtest_build_queryIgnoreMissingFields_byPage() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);
		
        List<MysqlPersonTableWithDiffColumns> ret = dao.test_build_queryIgnoreMissingFields_byPage(Age,2,2, new DalHints().setShardValue(3).ignoreMissingFields());
		assertEquals(0, ret.size());
		
		ret = dao.test_build_queryIgnoreMissingFields_byPage(Age,1,2, new DalHints().setShardColValue("Age", 20).ignoreMissingFields());
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_build_queryAllowPartial_byPage() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);
		
        List<MysqlPersonTableWithDiffColumns> ret = dao.test_build_queryAllowPartial_byPage(Age,2,2, new DalHints().setShardValue(3).allowPartial());
		assertEquals(0, ret.size());
		
		ret = dao.test_build_queryAllowPartial_byPage(Age,1,2, new DalHints().setShardColValue("Age", 20).allowPartial());
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_build_queryIgnoreMissingFields_single() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(31);
        Age.add(30);
        
        MysqlPersonTableWithDiffColumns ret = dao.test_build_queryIgnoreMissingFields_single(Age, new DalHints().setShardColValue("Age", 20).ignoreMissingFields());
        assertEquals(1, ret.getID().intValue());
        
//        ret = dao.test_build_queryIgnoreMissingFields_single(Age, new DalHints().setShardColValue("Age", 21).ignoreMissingFields());
//        assertEquals(1, ret.getID().intValue());
        
        ret = dao.test_build_queryIgnoreMissingFields_single(Age, new DalHints().inAllShards().ignoreMissingFields());
        assertEquals(1, ret.getID().intValue());
        
        
	}
	
	@Test
	public void testtest_build_queryAllowPartial_single() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(31);
        Age.add(30);
        
        MysqlPersonTableWithDiffColumns ret = dao.test_build_queryAllowPartial_single(Age, new DalHints().setShardColValue("Age", 20).allowPartial());
        assertEquals(1, ret.getID().intValue());
        
//        ret = dao.test_build_queryIgnoreMissingFields_single(Age, new DalHints().setShardColValue("Age", 21).ignoreMissingFields());
//        assertEquals(1, ret.getID().intValue());
        
        ret = dao.test_build_queryAllowPartial_single(Age, new DalHints().inAllShards().allowPartial());
        assertEquals(1, ret.getID().intValue());
        
        
	}
	
	@Test
	public void testtest_def_queryIgnoreMissingFields_list_first() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(23);
        Age.add(22);
		
		MysqlPersonTableWithDiffColumns ret = dao.test_def_queryIgnoreMissingFields_list_first(Age, new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(1, ret.getID().intValue());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        ret = dao.test_def_queryIgnoreMissingFields_list_first(Age, hints.setShardColValue("Age", 20).ignoreMissingFields());
		assertEquals(1, ret.getID().intValue());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.shardColValues);
		exclude.add(DalHintEnum.ignoreMissingFields);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);

		Age.clear();
		Age.add(100);
		ret = dao.test_def_queryIgnoreMissingFields_list_first(Age, new DalHints().inAllShards().ignoreMissingFields()); 
		assertNull(ret);
	}
	
	@Test
	public void testtest_def_queryAllowPartial_list_first() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(23);
        Age.add(22);
		
		MysqlPersonTableWithDiffColumns ret = dao.test_def_queryAllowPartial_list_first(Age, new DalHints().inAllShards().allowPartial());
		assertEquals(1, ret.getID().intValue());
		
        ret = dao.test_def_queryAllowPartial_list_first(Age, new DalHints().setShardColValue("Age", 20).allowPartial());               
		assertEquals(1, ret.getID().intValue());
		
		Age.clear();
		Age.add(100);
		ret = dao.test_def_queryAllowPartial_list_first(Age, new DalHints().inAllShards().allowPartial()); 
		assertNull(ret);
	}
	
	@Test
	public void testtest_def_queryIgnoreMissingFields_list() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
		Age.add(21);
		
		List<MysqlPersonTableWithDiffColumns> pojos=dao.test_def_queryIgnoreMissingFields_list(Age, new DalHints().inShard(0).ignoreMissingFields());
		assertEquals(1, pojos.size());
		
		pojos=dao.test_def_queryIgnoreMissingFields_list(Age, new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(2, pojos.size());
	}
	
	@Test
	public void testtest_def_queryAllowPartial_list() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
		Age.add(21);
		
		List<MysqlPersonTableWithDiffColumns> pojos=dao.test_def_queryAllowPartial_list(Age, new DalHints().inShard(0).allowPartial());
		assertEquals(1, pojos.size());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		pojos=dao.test_def_queryAllowPartial_list(Age, hints.inAllShards().allowPartial());
		assertEquals(2, pojos.size());
		List<DalHintEnum> exclued=new ArrayList<>();
		exclued.add(DalHintEnum.resultSorter);
		exclued.add(DalHintEnum.allShards);
		exclued.add(DalHintEnum.allowPartial);
		exclued.add(DalHintEnum.implicitAllTableShards);
		DalHintsChecker.checkEquals(original,hints,exclued);
	}
	
	@Test
	public void testtest_def_queryFromIgnoreMissingFields_list() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
		Age.add(23);
		
		List<MysqlPersonTableWithDiffColumns> pojos=dao.test_def_queryFromIgnoreMissingFields_list(Age, new DalHints().inShard(0).ignoreMissingFields(),0,1);
		assertEquals(1, pojos.size());
		assertEquals(20, pojos.get(0).getAge().intValue());
		assertEquals(1, pojos.get(0).getID().intValue());
		
		pojos=dao.test_def_queryFromIgnoreMissingFields_list(Age, new DalHints().inAllShards().ignoreMissingFields(),1,1);
		assertEquals(1, pojos.size());
		assertEquals(23, pojos.get(0).getAge().intValue());
		assertEquals(2, pojos.get(0).getID().intValue());
	}
	
	@Test
	public void testtest_def_queryFromAllowPartial_list() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
		Age.add(21);
		
		List<MysqlPersonTableWithDiffColumns> pojos=dao.test_def_queryFromAllowPartial_list(Age, new DalHints().inShard(0).allowPartial(),0,1);
		assertEquals(1, pojos.size());
		assertEquals(20, pojos.get(0).getAge().intValue());
		assertEquals(1, pojos.get(0).getID().intValue());
		
		pojos=dao.test_def_queryFromAllowPartial_list(Age, new DalHints().inAllShards().allowPartial(),0,1);
		assertEquals(1, pojos.size());
		assertEquals(20, pojos.get(0).getAge().intValue());
		assertEquals(1, pojos.get(0).getID().intValue());
	}
	
	@Test
	public void testtest_def_queryIgnoreMissingFields_listByPage() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(5);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);
		
        List<MysqlPersonTableWithDiffColumns> ret = dao.test_def_queryIgnoreMissingFields_listByPage(Age,2,2, new DalHints().setShardValue(3).ignoreMissingFields());
		assertEquals(0, ret.size());
		
		ret = dao.test_def_queryIgnoreMissingFields_listByPage(Age,1,2, new DalHints().setShardColValue("Age", 20).ignoreMissingFields());
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_def_queryAllowPartial_listByPage() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(5);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);
		
        List<MysqlPersonTableWithDiffColumns> ret = dao.test_def_queryAllowPartial_listByPage(Age,2,2, new DalHints().setShardValue(3).allowPartial());
		assertEquals(0, ret.size());
		
		ret = dao.test_def_queryAllowPartial_listByPage(Age,1,2, new DalHints().setShardColValue("Age", 20).allowPartial());
		assertEquals(2, ret.size());
	}
	
	
	@Test
	public void testtest_def_queryIgnoreMissingFields_list_single() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(21);
        Age.add(30);

        DalHints hints=new DalHints();
        DalHints original=hints.clone();
        MysqlPersonTableWithDiffColumns ret = dao.test_def_queryIgnoreMissingFields_list_single(Age, hints.setShardColValue("Age", 20).ignoreMissingFields());
        assertEquals(1, ret.getID().intValue());
        List<DalHintEnum> exclude=new ArrayList<>();
        exclude.add(DalHintEnum.ignoreMissingFields);
        exclude.add(DalHintEnum.shardColValues);
        exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);

        ret = dao.test_def_queryIgnoreMissingFields_list_single(Age, new DalHints().setShardColValue("Age", 21).ignoreMissingFields());
        assertEquals(1, ret.getID().intValue());
	}
	
	@Test
	public void testtest_def_queryAllowPartial_list_single() throws Exception {
		List<Integer> Age=new ArrayList<Integer>(2);
		Age.add(20);
        Age.add(21);
        Age.add(30);
        
        MysqlPersonTableWithDiffColumns ret = dao.test_def_queryAllowPartial_list_single(Age, new DalHints().setShardColValue("Age", 20).allowPartial());
        assertEquals(1, ret.getID().intValue());
        
        ret = dao.test_def_queryAllowPartial_list_single(Age, new DalHints().setShardColValue("Age", 21).allowPartial());
        assertEquals(1, ret.getID().intValue());
	}
	
}
