package shardtest.oldVersionCodeTest;


import com.ctrip.platform.dal.dao.*;
import dao.shard.oldVersionCode.ShardColModShardByDBOnMysqlGenDao;
import entity.MysqlPersonTable;
import org.junit.*;
import testUtil.DalHintsChecker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * JUnit test of PersonSimpleShardByDBOnMysqlGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class ShardColModShardByDBOnMysqlGenDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnMysql";

	private static DalClient client = null;
	private static ShardColModShardByDBOnMysqlGenDao dao = null;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new ShardColModShardByDBOnMysqlGenDao();
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
	    
	    List<MysqlPersonTable> daoPojos1 = new ArrayList<>(
				3);

		for (int i = 0; i < 6; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i + 20);
			if(i%2==0)
			daoPojo.setName("Initial_Shard_0" + i);
			else
				daoPojo.setName("Initial_Shard_1" + i);
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
	public void testCount() throws Exception {
		int ret = dao.count(new DalHints().inShard(0));
		assertEquals(3, ret);
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		ret = dao.count(hints.inShard(1));
		assertEquals(3, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		exclude.add(DalHintEnum.parameters);//baseClient没有cleanup parameters,如果hints复用可能导致shardID错乱
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
	    DalHints original=hints.clone();
		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setID(1);
		daoPojo.setAge(22);
		int ret = dao.delete(hints, daoPojo); 
		assertEquals(1, ret);
		DalHintsChecker.checkEquals(original,hints);
		ret = dao.count(new DalHints().inShard(0));
		assertEquals(2, ret);
		ret = dao.count(new DalHints().inShard(1));
		assertEquals(3, ret);
		
		daoPojo.setAge(24);
		ret = dao.delete(hints, daoPojo); 
		assertEquals(0, ret);
		
		ret = dao.count(new DalHints().inShard(0));
		assertEquals(2, ret);
		ret = dao.count(new DalHints().inShard(1));
		assertEquals(3, ret);
	}
	
	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> daoPojos = new ArrayList<>(2);
		MysqlPersonTable daoPojo1=new MysqlPersonTable();
		daoPojo1.setID(1);
		daoPojo1.setAge(20);
		daoPojos.add(daoPojo1);
		
		MysqlPersonTable daoPojo2=new MysqlPersonTable();
		daoPojo2.setID(1);
		daoPojo2.setAge(21);
		daoPojos.add(daoPojo2);
		
		int[] affected = dao.delete(hints, daoPojos);
		assertEquals(2, affected.length);

		DalHintsChecker.checkEquals(original,hints);

		int ret = dao.count(new DalHints().inShard(0));
		assertEquals(2, ret);
		ret = dao.count(new DalHints().inShard(1));
		assertEquals(2, ret);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> daoPojos = null;
		int[] affected = dao.batchDelete(hints, daoPojos);
		DalHintsChecker.checkEquals(original,hints);
	}
	
	@Test
	public void testGetAll() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> list = dao.getAll(hints.inShard(1));
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testInsert1() throws Exception {
        DalHints hints = new DalHints();
		DalHints original=hints.clone();
		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setAge(20);
		dao.insert(hints, daoPojo);
		DalHintsChecker.checkEquals(original,hints);
	}
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> daoPojos = dao.getAll(new DalHints().inAllShards());
		dao.insert(hints, daoPojos);
		DalHintsChecker.checkEquals(original,hints);
	}

	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		KeyHolder keyHolder = new KeyHolder();
		MysqlPersonTable daoPojo = dao.queryByPk(1,new DalHints().inShard(0));
		dao.insert(hints, keyHolder, daoPojo);
		DalHintsChecker.checkEquals(original,hints);
	}

	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		KeyHolder keyHolder = new KeyHolder();
		List<MysqlPersonTable> daoPojos = dao.getAll(new DalHints().inShard(1));
		dao.insert(hints, keyHolder, daoPojos);
		DalHintsChecker.checkEquals(original,hints);
	}

	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> daoPojos = dao.getAll(new DalHints().inShard(1));
		dao.batchInsert(hints, daoPojos);
		DalHintsChecker.checkEquals(original,hints);
	}

	@Test
	public void testCombinedInsert1() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> daoPojos = dao.getAll(new DalHints().inShard(1));
		dao.combinedInsert(hints, daoPojos);
		DalHintsChecker.checkEquals(original,hints);
	}

	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		KeyHolder keyHolder = new KeyHolder();
		List<MysqlPersonTable> daoPojos = dao.getAll(new DalHints().inShard(1));
		dao.combinedInsert(hints, keyHolder, daoPojos);
		DalHintsChecker.checkEquals(original,hints);
	}


	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		int pageSize = 100;
		int pageNo = 1;
		List<MysqlPersonTable> list = dao.queryByPage(pageSize, pageNo, hints.inShard(0));
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1;
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		MysqlPersonTable ret = dao.queryByPk(id, hints.inShard(0));
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

	@Test
	public void testQueryByPk2() throws Exception {
		MysqlPersonTable pk = new MysqlPersonTable();
		pk.setID(2);
		pk.setAge(21);
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		MysqlPersonTable ret = dao.queryByPk(pk, hints);
		assertEquals("Initial_Shard_13",ret.getName());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.fields);//没有清除DalHintEnum.fields
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setID(1);
		daoPojo.setAge(20);
		daoPojo.setName("update20");
		int ret = dao.update(hints, daoPojo);
		assertEquals(1, ret);
		DalHintsChecker.checkEquals(original,hints);

		DalHints hints1=new DalHints();
		DalHints original1=hints1.clone();
		MysqlPersonTable updatePojo=dao.queryByPk(1L, hints1.inShard(0));
		assertEquals("update20", updatePojo.getName());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		DalHintsChecker.checkEquals(original1,hints1,exclude);
	}
	
	@Test
	public void testUpdate_null() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setID(1);
		daoPojo.setAge(20);
		daoPojo.setName(null);
		int ret = dao.update(hints.set(DalHintEnum.updateNullField), daoPojo);
		assertEquals(1, ret);	
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.updateNullField);
		DalHintsChecker.checkEquals(original,hints,exclude);

		MysqlPersonTable updatePojo=dao.queryByPk(1L, new DalHints().inShard(0));
		assertNull(updatePojo.getName());
	}

	@Test
	public void testUpdate2() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> daoPojos = dao.getAll(new DalHints().inShard(0));
		dao.update(hints.inShard(1), daoPojos);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> daoPojos = dao.getAll(new DalHints().inShard(0));
		dao.batchUpdate(hints.inShard(1), daoPojos);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_build_delete() throws Exception {
		//Integer Age = null;// Test value here
	    //int ret = dao.test_build_delete(Age, new DalHints());
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);

		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		int ret = dao.test_build_delete(Age, hints.inAllShards());
		assertEquals(2, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		DalHintsChecker.checkEquals(original,hints,exclude);

		MysqlPersonTable pojo=dao.queryByPk(1L, new DalHints().inShard(0));
		assertNull(pojo);
		
		pojo=dao.queryByPk(1L, new DalHints().inShard(1));
		assertNull(pojo);
	}
	
	
	@Test
	public void testtest_build_insert() throws Exception {
		String Name = "build_insert_shard_0";// Test value here
		Integer Age = 26;// Test value here
	    int ret= dao.test_build_insert(Name, Age, new DalHints().inAllShards());
	    assertEquals(2, ret);
	    
	    Name = "build_insert_shard_1";// Test value here
		Age = 27;// Test value here

		DalHints hints = new DalHints();
		DalHints original=hints.clone();
	    ret = dao.test_build_insert(Name, Age, hints.inShard(1));
	    assertEquals(1, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		DalHintsChecker.checkEquals(original,hints,exclude);

	    MysqlPersonTable pojo=dao.queryByPk(4L, new DalHints().inShard(0));
		assertNotNull(pojo);
		
		pojo=dao.queryByPk(4L, new DalHints().inShard(1));
		assertNotNull(pojo);
		
		pojo=dao.queryByPk(5L, new DalHints().inShard(1));
		assertNotNull(pojo);
	}
	
	@Test
	public void testtest_build_update() throws Exception {
		String Name = "build_update_shard";// Test value here
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		List<Integer> Age=new ArrayList<>(2);
		Age.add(21);
		Age.add(20);
	    int ret = dao.test_build_update(Name, Age, hints.inAllShards());
	    assertEquals(2, ret);
	    List<DalHintEnum> exclude=new ArrayList<>();
	    exclude.add(DalHintEnum.allShards);
	    DalHintsChecker.checkEquals(original,hints,exclude);

	    MysqlPersonTable pojo=dao.queryByPk(1L, new DalHints().inShard(0));
		assertEquals("build_update_shard", pojo.getName());
		
		pojo=dao.queryByPk(1L, new DalHints().inShard(1));
		assertEquals("build_update_shard", pojo.getName());    
	}
	
	@Test
	public void testtest_build_update_new() throws Exception {
		String Name = "build_update_shard";// Test value here
		List<Integer> Age=new ArrayList<>(2);
		Age.add(21);
		Age.add(20);
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
	    int ret = dao.test_build_update_new(Name, Age, hints.inAllShards());
	    assertEquals(2, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		DalHintsChecker.checkEquals(original,hints,exclude);
//	    ignoreMissingFieldsAndAllowPartialTestOnMysqlGen	pojo=dao.queryByPk(1l, new DalHints().inShard(0));
//		assertEquals("build_update_shard", pojo.getName());
//		
//		pojo=dao.queryByPk(1l, new DalHints().inShard(1));
//		assertEquals("build_update_shard", pojo.getName());    
	}
	
	@Test
	public void testtest_build_queryFrom() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();

		List<MysqlPersonTable> ret = dao.test_build_queryFrom(Age, hints.inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getAge().intValue());

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.resultSetType);//queryDao的queryFrom设置TYPE_SCROLL_INSENSITIVE
		DalHintsChecker.checkEquals(original,hints,exclude);

		ret = dao.test_build_queryFrom(Age, new DalHints().inShard(1),0,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getAge().intValue());
	}
	
	@Test
	public void testtest_build_queryFromPartialFieldsSet() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);
		
		List<MysqlPersonTable> ret = dao.test_build_queryFromPartialFieldsSet(Age, new DalHints().inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getAge().intValue());
		assertNull(ret.get(0).getID());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		ret = dao.test_build_queryFromPartialFieldsSet(Age, hints.inShard(1),0,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getAge().intValue());
		assertNull(ret.get(0).getID());

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.resultSetType);//queryDao的queryFrom设置TYPE_SCROLL_INSENSITIVE
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_build_queryFromPartialFieldsStrings() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);
		
		List<MysqlPersonTable> ret = dao.test_build_queryFromPartialFieldsStrings(Age, new DalHints().inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getAge().intValue());
		assertNull(ret.get(0).getID());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		ret = dao.test_build_queryFromPartialFieldsStrings(Age, hints.inShard(1),0,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getAge().intValue());
		assertNull(ret.get(0).getID());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.resultSetType);//queryDao的queryFrom设置TYPE_SCROLL_INSENSITIVE
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_ClientQueryFrom_list() throws Exception {
	    //List<ignoreMissingFieldsAndAllowPartialTestOnMysql> ret = dao.test_build_query_list(Age, new DalHints());

		List<Integer> Age=new ArrayList<>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
		List<MysqlPersonTable> ret = dao.test_ClientQueryFrom_list(Age, new DalHints().inAllShards(), 0, 1);
	    assertEquals(1, ret.size());
	    assertEquals(20, ret.get(0).getAge().intValue());
	    assertEquals(1, ret.get(0).getID().intValue());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
	    ret = dao.test_ClientQueryFrom_list(Age, hints.setShardColValue("Age", 20),0,1);
	    assertEquals(1, ret.size());
	    assertEquals(20, ret.get(0).getAge().intValue());
	    assertEquals(1, ret.get(0).getID().intValue());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.shardColValues);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_ClientQueryFromPartialFieldsSet_list() throws Exception {
	    //List<ignoreMissingFieldsAndAllowPartialTestOnMysql> ret = dao.test_build_query_list(Age, new DalHints());

		List<Integer> Age=new ArrayList<>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
		List<MysqlPersonTable> ret = dao.test_ClientQueryFromPartialFieldsSet_list(Age, new DalHints().inAllShards(), 0, 1);
	    assertEquals(1, ret.size());
	    assertEquals(20, ret.get(0).getAge().intValue());
	    assertNull(ret.get(0).getID());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
	    ret = dao.test_ClientQueryFromPartialFieldsSet_list(Age, hints.setShardColValue("Age", 20),0,1);
	    assertEquals(1, ret.size());
	    assertEquals(20, ret.get(0).getAge().intValue());
	    assertNull(ret.get(0).getID());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_ClientQueryFromPartialFieldsStrings_list() throws Exception {
	    //List<ignoreMissingFieldsAndAllowPartialTestOnMysql> ret = dao.test_build_query_list(Age, new DalHints());

		List<Integer> Age=new ArrayList<>();
		Age.add(20);
		Age.add(23);
		Age.add(22);
		List<MysqlPersonTable> ret = dao.test_ClientQueryFromPartialFieldsStrings_list(Age, new DalHints().inAllShards(), 0, 1);
	    assertEquals(1, ret.size());
	    assertEquals(20, ret.get(0).getAge().intValue());
	    assertNull(ret.get(0).getID());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
	    ret = dao.test_ClientQueryFromPartialFieldsStrings_list(Age, hints.setShardColValue("Age", 20),0,1);
	    assertEquals(1, ret.size());
	    assertEquals(20, ret.get(0).getAge().intValue());
	    assertNull(ret.get(0).getID());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_build_query_first() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);
        Age.add(22);
		
		MysqlPersonTable ret = dao.test_build_query_first(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
		
       	ret = dao.test_build_query_first(Age, new DalHints().setShardColValue("Age", 20));
		assertEquals("Initial_Shard_00", ret.getName());
//        assertNull(ret);
		
		Age.clear();
		Age.add(200);
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		ret = dao.test_build_query_first(Age, hints.inAllShards());
		assertNull(ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_build_queryPartialFields_first() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);
        Age.add(22);
		
		MysqlPersonTable ret = dao.test_build_queryPartialFields_first(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20, ret.getAge().intValue());
		assertNull(ret.getBirth());
		assertNull(ret.getID());
		
       	ret = dao.test_build_queryPartialFields_first(Age, new DalHints().setShardColValue("Age", 20));
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20, ret.getAge().intValue());
		assertNull(ret.getBirth());
		assertNull(ret.getID());
		
		Age.clear();
		Age.add(200);
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		ret = dao.test_build_queryPartialFields_first(Age, hints.inAllShards());
		assertNull(ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

	@Test
	public void testtest_build_query() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> ret = dao.test_build_query(Age, hints.inAllShards());
		assertEquals(2, ret.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);

		ret = dao.test_build_query(Age, new DalHints().inShard(1));
		assertEquals(1, ret.size());
	}
	
	@Test
	public void testtest_build_queryPartialFields() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> ret = dao.test_build_queryPartialFields(Age, hints.inAllShards());
		assertEquals(2, ret.size());
		assertEquals("Initial_Shard_11", ret.get(1).getName());
		assertEquals(21, ret.get(1).getAge().intValue());
		assertNull(ret.get(1).getBirth());
		assertNull(ret.get(1).getID());
		
		assertEquals("Initial_Shard_00", ret.get(0).getName());
		assertEquals(20, ret.get(0).getAge().intValue());
		assertNull(ret.get(0).getBirth());
		assertNull(ret.get(0).getID());

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);

		ret = dao.test_build_queryPartialFields(Age, new DalHints().inShard(1));
		assertEquals(1, ret.size());
		assertEquals("Initial_Shard_11", ret.get(0).getName());
		assertEquals(21, ret.get(0).getAge().intValue());
		assertNull(ret.get(0).getBirth());
		assertNull(ret.get(0).getID());
	}

	@Test
	public void testtest_build_query_byPage() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        List<MysqlPersonTable> ret = dao.test_build_query_byPage(Age,2,2, hints.setShardValue(3));
		assertEquals(0, ret.size());

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardValue);
		DalHintsChecker.checkEquals(original,hints,exclude);

		ret = dao.test_build_query_byPage(Age,1,2, new DalHints().setShardColValue("Age", 20));
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_build_queryPartialFields_byPage() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        List<MysqlPersonTable> ret = dao.test_build_queryPartialFields_byPage(Age,2,2, hints.setShardValue(3));
		assertEquals(0, ret.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardValue);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);
		
		ret = dao.test_build_queryPartialFields_byPage(Age,1,2, new DalHints().setShardColValue("Age", 20));
		assertEquals(2, ret.size());
		assertEquals("Initial_Shard_00", ret.get(0).getName());
		assertEquals(20, ret.get(0).getAge().intValue());
		assertNull(ret.get(0).getBirth());
		assertNull(ret.get(0).getID());
		
		assertEquals("Initial_Shard_02", ret.get(1).getName());
		assertEquals(22, ret.get(1).getAge().intValue());
		assertNull(ret.get(1).getBirth());
		assertNull(ret.get(1).getID());
	}
	
	@Test
	public void testtest_build_query_single() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        MysqlPersonTable ret = dao.test_build_query_single(Age, hints.setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret.getName());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		DalHintsChecker.checkEquals(original,hints,exclude);

        ret = dao.test_build_query_single(Age, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_11", ret.getName());
	}
	
	@Test
	public void testtest_build_queryPartialFields_single() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(29);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        MysqlPersonTable ret = dao.test_build_queryPartialFields_single(Age, hints.setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20, ret.getAge().intValue());
		assertNull(ret.getBirth());
		assertNull(ret.getID());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);


        ret = dao.test_build_queryPartialFields_single(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20, ret.getAge().intValue());
		assertNull(ret.getBirth());
		assertNull(ret.getID());
	}
	
	@Test
	public void testtest_build_query_field_first() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(22);
        Age.add(23);
        Age.add(24);
        
        String ret = dao.test_build_query_field_first(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_00", ret);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        ret = dao.test_build_query_field_first(Age, hints.setShardValue(0));
        assertEquals("Initial_Shard_00", ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardValue);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

	@Test
	public void testtest_build_query_field() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(22);
        Age.add(23);
        Age.add(24);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        List<String> ret = dao.test_build_query_field(Age, hints.inAllShards());
        assertEquals(4, ret.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);

        ret = dao.test_build_query_field(Age, new DalHints().inShard(0));
        assertEquals(3, ret.size());
	}

	@Test
	public void testtest_build_query_field_byPage() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(22);
        Age.add(23);
        Age.add(24);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        List<String> ret = dao.test_build_query_field_byPage(Age, 1, 10, new DalHints().inShard(0));
        assertEquals(3, ret.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		DalHintsChecker.checkEquals(original,hints,exclude);

        ret = dao.test_build_query_field_byPage(Age, 2, 10, new DalHints().inShard(1));
        assertEquals(0, ret.size());
	}

	
	@Test
	public void testtest_build_query_field_single() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(22);
        Age.add(23);
        Age.add(24);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        String ret = dao.test_build_query_field_single(Age, hints.setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_update() throws Exception {
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		int ret = dao.test_def_update(hints.setShardColValue("Age", 20));
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		exclude.add(DalHintEnum.parameters);//baseClient增删改没有cleanup parameters,如果hints复用可能导致shardID错乱
		DalHintsChecker.checkEquals(original,hints,exclude);

		List<MysqlPersonTable> pojos=dao.getAll(new DalHints().inShard(0));
		assertEquals(0, pojos.size());
		
		pojos=dao.getAll(new DalHints().inShard(1));
		assertEquals(3, pojos.size());
	}
	
	@Test
	public void testtest_def_delete() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		int ret = dao.test_def_delete(Age, hints.inShard(0));
	    assertEquals(1, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		exclude.add(DalHintEnum.parameters);//baseClient增删改没有cleanup parameters,如果hints复用可能导致shardID错乱
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_delete_equal() throws Exception {
		int age=20;
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		int ret = dao.test_def_delete_equal(age, hints.inShard(0));
	    assertEquals(1, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		exclude.add(DalHintEnum.parameters);//baseClient增删改没有cleanup parameters,如果hints复用可能导致shardID错乱
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_update_in() throws Exception {
		String Name = "def_update_shard";// Test value here
		List<Integer> Age=new ArrayList<>(2);
		Age.add(21);
		Age.add(20);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		int ret = dao.test_def_update_in(Name, Age, hints.inShard(0));
	    assertEquals(1, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		exclude.add(DalHintEnum.parameters);//baseClient增删改没有cleanup parameters,如果hints复用可能导致shardID错乱
		DalHintsChecker.checkEquals(original,hints,exclude);

	    MysqlPersonTable pojo=dao.queryByPk(1l, new DalHints().inShard(0));
		assertEquals("def_update_shard", pojo.getName());
		
		pojo=dao.queryByPk(1l, new DalHints().inShard(1));
		assertEquals("Initial_Shard_11", pojo.getName());    
	}
	
	@Test
	public void testtest_def_query_list_first() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);
        Age.add(22);
		
		MysqlPersonTable ret = dao.test_def_query_list_first(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
		
        ret = dao.test_def_query_list_first(Age, new DalHints().setShardColValue("Age", 20));               
		assertEquals("Initial_Shard_00", ret.getName());
		
		Age.clear();
		Age.add(100);
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		ret = dao.test_def_query_list_first(Age, hints.inAllShards());
		assertNull(ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_queryPartialSet_list_first() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);
        Age.add(22);
		
		MysqlPersonTable ret = dao.test_def_queryPartialSet_list_first(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20,ret.getAge().intValue());
		assertNull(ret.getID());
		assertNull(ret.getBirth());
		
        ret = dao.test_def_queryPartialSet_list_first(Age, new DalHints().setShardColValue("Age", 20));               
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20,ret.getAge().intValue());
		assertNull(ret.getID());
		assertNull(ret.getBirth());
		
		Age.clear();
		Age.add(100);
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		ret = dao.test_def_queryPartialSet_list_first(Age, hints.inAllShards());
		assertNull(ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_queryPartialStrings_list_first() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);
        Age.add(22);
		
		MysqlPersonTable ret = dao.test_def_queryPartialStrings_list_first(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20,ret.getAge().intValue());
		assertNull(ret.getID());
		assertNull(ret.getBirth());
		
        ret = dao.test_def_queryPartialStrings_list_first(Age, new DalHints().setShardColValue("Age", 20));               
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20,ret.getAge().intValue());
		assertNull(ret.getID());
		assertNull(ret.getBirth());
		
		Age.clear();
		Age.add(100);
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		ret = dao.test_def_queryPartialStrings_list_first(Age, hints.inAllShards());
		assertNull(ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_query_list() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> pojos=dao.test_def_query_list(Age, hints.inShard(0));
		assertEquals(1, pojos.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);

		pojos=dao.test_def_query_list(Age, new DalHints().inAllShards());
		assertEquals(2, pojos.size());
	}
	
	@Test
	public void testtest_def_queryFrom_list() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		List<MysqlPersonTable> pojos=dao.test_def_queryFrom_list(Age, new DalHints().inShard(0),0,1);
		assertEquals(1, pojos.size());
		assertEquals(20, pojos.get(0).getAge().intValue());
		assertEquals(1, pojos.get(0).getID().intValue());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		pojos=dao.test_def_queryFrom_list(Age, hints.inAllShards(),1,1);
		assertEquals(1, pojos.size());
		assertEquals(21, pojos.get(0).getAge().intValue());
		assertEquals(1, pojos.get(0).getID().intValue());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.resultSetType);//queryDao的queryFrom设置TYPE_SCROLL_INSENSITIVE
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_queryFromPartialFieldsStrings_list() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		List<MysqlPersonTable> pojos=dao.test_def_queryFromPartialFieldsStrings_list(Age, new DalHints().inShard(0),0,1);
		assertEquals(1, pojos.size());
		assertEquals(20, pojos.get(0).getAge().intValue());
		assertNull(pojos.get(0).getID());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		pojos=dao.test_def_queryFromPartialFieldsStrings_list(Age, hints.inAllShards(),1,1);
		assertEquals(1, pojos.size());
		assertEquals(21, pojos.get(0).getAge().intValue());
		assertNull(pojos.get(0).getID());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.resultSetType);//queryDao的queryFrom设置TYPE_SCROLL_INSENSITIVE
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_queryFromPartialFieldsSet_list() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		List<MysqlPersonTable> pojos=dao.test_def_queryFromPartialFieldsSet_list(Age, new DalHints().inShard(0),0,1);
		assertEquals(1, pojos.size());
		assertEquals(20, pojos.get(0).getAge().intValue());
		assertNull(pojos.get(0).getID());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		pojos=dao.test_def_queryFromPartialFieldsSet_list(Age, hints.inAllShards(),1,1);
		assertEquals(1, pojos.size());
		assertEquals(21, pojos.get(0).getAge().intValue());
		assertNull(pojos.get(0).getID());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.resultSorter);
		exclude.add(DalHintEnum.resultSetType);//queryDao的queryFrom设置TYPE_SCROLL_INSENSITIVE
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_queryPartialSet_list() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		List<MysqlPersonTable> pojos=dao.test_def_queryPartialSet_list(Age, new DalHints().inShard(0));
		assertEquals(1, pojos.size());
		assertEquals("Initial_Shard_00", pojos.get(0).getName());
		assertEquals(20,pojos.get(0).getAge().intValue());
		assertNull(pojos.get(0).getID());
		assertNull(pojos.get(0).getBirth());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		pojos=dao.test_def_queryPartialSet_list(Age, hints.inAllShards());
		assertEquals(2, pojos.size());
		
		assertEquals("Initial_Shard_11", pojos.get(1).getName());
		assertEquals(21,pojos.get(1).getAge().intValue());
		assertNull(pojos.get(1).getID());
		assertNull(pojos.get(1).getBirth());
		
		assertEquals("Initial_Shard_00", pojos.get(0).getName());
		assertEquals(20,pojos.get(0).getAge().intValue());
		assertNull(pojos.get(0).getID());
		assertNull(pojos.get(0).getBirth());

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);

	}
	
	@Test
	public void testtest_def_queryPartialStrings_list() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		List<MysqlPersonTable> pojos=dao.test_def_queryPartialStrings_list(Age, new DalHints().inShard(0));
		assertEquals(1, pojos.size());
		assertEquals("Initial_Shard_00", pojos.get(0).getName());
		assertEquals(20,pojos.get(0).getAge().intValue());
		assertNull(pojos.get(0).getID());
		assertNull(pojos.get(0).getBirth());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		pojos=dao.test_def_queryPartialStrings_list(Age, hints.inAllShards());
		assertEquals(2, pojos.size());
		
		assertEquals("Initial_Shard_11", pojos.get(1).getName());
		assertEquals(21,pojos.get(1).getAge().intValue());
		assertNull(pojos.get(1).getID());
		assertNull(pojos.get(1).getBirth());
		
		assertEquals("Initial_Shard_00", pojos.get(0).getName());
		assertEquals(20,pojos.get(0).getAge().intValue());
		assertNull(pojos.get(0).getID());
		assertNull(pojos.get(0).getBirth());

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.partialQuery);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_query_listByPage() throws Exception {
		List<Integer> Age=new ArrayList<>(5);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        List<MysqlPersonTable> ret = dao.test_def_query_listByPage(Age,2,2, hints.setShardValue(3));
		assertEquals(0, ret.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardValue);
		DalHintsChecker.checkEquals(original,hints,exclude);

		ret = dao.test_def_query_listByPage(Age,1,2, new DalHints().setShardColValue("Age", 20));
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_def_queryPartialSet_listByPage() throws Exception {
		List<Integer> Age=new ArrayList<>(5);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        List<MysqlPersonTable> ret = dao.test_def_queryPartialSet_listByPage(Age,2,2, hints.setShardValue(3));
		assertEquals(0, ret.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardValue);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);
		
		ret = dao.test_def_queryPartialSet_listByPage(Age,1,2, new DalHints().setShardColValue("Age", 20));
		assertEquals(2, ret.size());
		
		assertEquals("Initial_Shard_00",ret.get(0).getName());
		assertEquals(20,ret.get(0).getAge().intValue());
		assertNull(ret.get(0).getID());
		assertNull(ret.get(0).getBirth());
		
		assertEquals("Initial_Shard_02",ret.get(1).getName());
		assertEquals(22,ret.get(1).getAge().intValue());
		assertNull(ret.get(1).getID());
		assertNull(ret.get(1).getBirth());
		
	}
	
	@Test
	public void testtest_def_queryPartialStrings_listByPage() throws Exception {
		List<Integer> Age=new ArrayList<>(5);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        List<MysqlPersonTable> ret = dao.test_def_queryPartialStrings_listByPage(Age,2,2, hints.setShardValue(3));
		assertEquals(0, ret.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardValue);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);
		
		ret = dao.test_def_queryPartialStrings_listByPage(Age,1,2, new DalHints().setShardColValue("Age", 20));
		assertEquals(2, ret.size());
		
		assertEquals("Initial_Shard_00",ret.get(0).getName());
		assertEquals(20,ret.get(0).getAge().intValue());
		assertNull(ret.get(0).getID());
		assertNull(ret.get(0).getBirth());
		
		assertEquals("Initial_Shard_02",ret.get(1).getName());
		assertEquals(22,ret.get(1).getAge().intValue());
		assertNull(ret.get(1).getID());
		assertNull(ret.get(1).getBirth());
		
	}
	
	@Test
	public void testtest_def_query_list_single() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        MysqlPersonTable ret = dao.test_def_query_list_single(Age, hints.setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret.getName());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		DalHintsChecker.checkEquals(original,hints,exclude);

        ret = dao.test_def_query_list_single(Age, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_11", ret.getName());
	}
	
	@Test
	public void testtest_def_queryPartialSet_list_single() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(30);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        MysqlPersonTable ret = dao.test_def_queryPartialSet_list_single(Age,hints.setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret.getName());
        assertEquals(20,ret.getAge().intValue());
		assertNull(ret.getID());
		assertNull(ret.getBirth());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);


        ret = dao.test_def_queryPartialSet_list_single(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_00", ret.getName());
        assertEquals(20,ret.getAge().intValue());
		assertNull(ret.getID());
		assertNull(ret.getBirth());
	}
	
	@Test
	public void testtest_def_queryPartialStrings_list_single() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(30);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        MysqlPersonTable ret = dao.test_def_queryPartialStrings_list_single(Age, hints.setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret.getName());
        assertEquals(20,ret.getAge().intValue());
		assertNull(ret.getID());
		assertNull(ret.getBirth());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		exclude.add(DalHintEnum.partialQuery);
		DalHintsChecker.checkEquals(original,hints,exclude);
        
		ret = dao.test_def_queryPartialStrings_list_single(Age,new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20, ret.getAge().intValue());
		assertNull(ret.getID());
		assertNull(ret.getBirth());
	}
	
	@Test
	public void testtest_def_query_field_first() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(22);
        Age.add(23);
        Age.add(24);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        String ret = dao.test_def_query_field_first(Age, hints.inAllShards());
        assertEquals("Initial_Shard_00", ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);

        ret = dao.test_def_query_field_first(Age, new DalHints().setShardValue(0));
        assertEquals("Initial_Shard_00", ret);
	}
	
	@Test
	public void testtest_def_query_field() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		List<String> pojos=dao.test_def_query_field(Age, hints.inShard(0));
		assertEquals(1, pojos.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shard);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);

		pojos=dao.test_def_query_field(Age, new DalHints().inAllShards());
		assertEquals(2, pojos.size());
		
		Age.clear();
		Age.add(30);
		Age.add(31);
		
		pojos=dao.test_def_query_field(Age, new DalHints().inShard(0));
		assertEquals(0, pojos.size());
		
	}
	
	@Test
	public void testtest_def_query_field_byPage() throws Exception {
		List<Integer> Age=new ArrayList<>(5);
		Age.add(20);
        Age.add(21);
        Age.add(22);
        Age.add(23);
        Age.add(24);


        List<String> ret = dao.test_def_query_field_byPage(Age,1,2, new DalHints().setShardValue(3));
		assertEquals(2, ret.size());

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		ret = dao.test_def_query_field_byPage(Age,1,10,hints.setShardColValue("Age", 20));
		assertEquals(3, ret.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}
	
	@Test
	public void testtest_def_query_field_single() throws Exception {
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
        Age.add(21);

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
        String ret = dao.test_def_query_field_single(Age, hints.setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shardColValues);
		DalHintsChecker.checkEquals(original,hints,exclude);

        ret = dao.test_def_query_field_single(Age, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_11", ret);
        
        Age.clear();
        Age.add(30);
        Age.add(31);
        ret = dao.test_def_query_field_single(Age, new DalHints().setShardColValue("Age", 20));
        assertNull(ret);
	}
	
	@Test
	public void testtest_build_delete_equal() throws Exception {
		Integer param1 = 20;// Test value here
	    int ret = dao.test_build_delete_equal(param1, new DalHints().inAllShards());
	    assertEquals(1, ret);
	    
	    int count=dao.count(new DalHints().inShard(0));
	    assertEquals(2, count);
	    
	    count=dao.count(new DalHints().inShard(1));
	    assertEquals(3, count);
	   
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    param1 = 21;// Test value here

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
	    ret = dao.test_build_delete_equal(param1, hints.inShards(shards));
	    assertEquals(1, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shards);
		DalHintsChecker.checkEquals(original,hints,exclude);
	    count=dao.count(new DalHints().inShard(0));
	    assertEquals(2, count);
	    
	    count=dao.count(new DalHints().inShard(1));
	    assertEquals(2, count);
	}
	
	@Test
	public void testtest_build_insert_equal() throws Exception {
		String Name = "InsertInAllShards";// Test value here
		Integer Age = 100;// Test value here

		DalHints hints=new DalHints();
		DalHints original=hints.clone();
	    int ret = dao.test_build_insert_equal(Name, Age,hints.inAllShards());
	    assertEquals(2, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		DalHintsChecker.checkEquals(original,hints,exclude);

	    int count=dao.count(new DalHints().inShard(0));
	    assertEquals(4, count);
	    
	    count=dao.count(new DalHints().inShard(1));
	    assertEquals(4, count);
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    Name = "InsertByShards";
	    ret = dao.test_build_insert_equal(Name, Age, new DalHints().inShards(shards));
	    assertEquals(2, ret);
	    
	    count=dao.count(new DalHints().inShard(0));
	    assertEquals(5, count);
	    
	    count=dao.count(new DalHints().inShard(1));
	    assertEquals(5, count);
	   
	}
	
	@Test
	public void testtest_build_update_equal() throws Exception {
		String Name = "updateInAllSahrds";// Test value here
		Integer Age = 20;// Test value here
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
	    int ret = dao.test_build_update_equal(Name, Age, hints.inAllShards());
	    assertEquals(1, ret);
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		DalHintsChecker.checkEquals(original,hints,exclude);

	    MysqlPersonTable pojo=dao.queryByPk(1, new DalHints().inShard(0));
	    assertEquals("updateInAllSahrds", pojo.getName());
	       
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    Age = 21;
	    Name = "updateBySahrds";
	    ret = dao.test_build_update_equal(Name, Age, new DalHints().inShards(shards));
	    assertEquals(1, ret);
	    
	    pojo=dao.queryByPk(1, new DalHints().inShard(1));
	    assertEquals("updateBySahrds", pojo.getName());
	}
	
	@Test
	public void testtest_build_query_equal() throws Exception {
		Integer Age = 20;
		DalHints hints=new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> ret = dao.test_build_query_equal(Age, new DalHints());
		assertEquals(1, ret.size());
		assertEquals("Initial_Shard_00", ret.get(0).getName());
		DalHintsChecker.checkEquals(original,hints);
	}
}
