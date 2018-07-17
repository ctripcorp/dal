package shardtest.newVersionCodeTest;


import com.ctrip.platform.dal.dao.*;
import dao.shard.newVersionCode.PeopleShardColModShardByDBTableOnMysqlDao;
import entity.MysqlPeopleTable;
import org.junit.*;
import testUtil.DalHintsChecker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit test of PeopleShardColModShardByDBTableOnMysqlDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class FreeShardingStrategyByDBTableOnMysqlDaoUnitTest {

	private static final String DATA_BASE = "FreeShardingStrategyByDBTableOnMysql";

	private static DalClient client = null;
	private static PeopleShardColModShardByDBTableOnMysqlDao dao = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new PeopleShardColModShardByDBTableOnMysqlDao(DATA_BASE);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		// By fieldsfields
		dao.test_def_truncate(new DalHints().inShard(0),"_0");
		dao.test_def_truncate(new DalHints().inShard(0),"_1");
		dao.test_def_truncate(new DalHints().inShard(1),"_0");
		dao.test_def_truncate(new DalHints().inShard(1),"_1");
	
		List<MysqlPeopleTable> daoPojos1 = new ArrayList<>(2);
		for (int i = 0; i < 6; i++) {
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(200);
			daoPojo.setAge(i + 20);
			daoPojo.setName("InsertByfields_0fields_" + i);
			daoPojos1.add(daoPojo);
		}
	   dao.insert(new DalHints(), daoPojos1);
		
		List<MysqlPeopleTable> daoPojos2 = new ArrayList<>(2);
		for (int i = 0; i <6; i++) {
			MysqlPeopleTable daoPojo = new MysqlPeopleTable();
			daoPojo.setCityID(201);
			daoPojo.setAge(i + 20);
			daoPojo.setName("InsertByfields_1fields_" + i);
			daoPojos2.add(daoPojo);
		}
	   dao.insert(new DalHints(), daoPojos2);

	}


	private MysqlPeopleTable createPojo(int index) {
		MysqlPeopleTable daoPojo = new MysqlPeopleTable();

		//daoPojo.setId(index);
		//daoPojo set not null field
		
		return daoPojo;
	}

	private void changePojo(MysqlPeopleTable daoPojo) {
		// Change a field to make pojo different with original one
	}
	
	private void changePojos(List<MysqlPeopleTable> daoPojos) {
		for(MysqlPeopleTable daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(MysqlPeopleTable daoPojo) {
		//assert changed value
	}
	
	private void verifyPojos(List<MysqlPeopleTable> daoPojos) {
		for(MysqlPeopleTable daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
//		dao.test_def_truncate(new DalHints().inShard(0),"_0");
//		dao.test_def_truncate(new DalHints().inShard(0),"_1");
//		dao.test_def_truncate(new DalHints().inShard(1),"_0");
//		dao.test_def_truncate(new DalHints().inShard(1),"_1");
	} 
	
	
	@Test
	public void testCount() throws Exception {
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testDelete1() throws Exception {
		MysqlPeopleTable daoPojo = createPojo(1);
		daoPojo.setID(1);
		daoPojo.setCityID(201);
		daoPojo.setAge(23);
		int ret = dao.delete(new DalHints(), daoPojo); 
		assertEquals(1, ret);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(2, affected);
	}
	
	@Test
	public void testDelete2() throws Exception {
		List<MysqlPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(0));
		int[] affected1 = dao.delete(new DalHints(), daoPojos);
		assertArrayEquals(new int[]{1,1,1},  affected1);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(0, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
		
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<MysqlPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		int[] affected1 = dao.batchDelete(new DalHints(), daoPojos);
		assertArrayEquals(new int[]{1,1,1},  affected1);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(0, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testQueryAll() throws Exception {
		List<MysqlPeopleTable> list = dao.queryAll(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, list.size());
		
		list = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, list.size());
		
		list = dao.queryAll(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, list.size());
		
		list = dao.queryAll(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, list.size());
	}
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		MysqlPeopleTable daoPojo = createPojo(1);
		daoPojo.setCityID(307);
		daoPojo.setAge(28);
		daoPojo.setName("insert");
		int affected1 = dao.insert(new DalHints(), daoPojo);
		assertEquals(1, affected1);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(4, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(0));
		int[] affected1 = dao.insert(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1},  affected1);

		DalHintsChecker.checkEquals(original,hints);

		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		KeyHolder keyHolder = new KeyHolder();
		MysqlPeopleTable daoPojo = createPojo(1);
		daoPojo.setCityID(307);
		daoPojo.setAge(28);
		daoPojo.setName("insert");
		int affected1 = dao.insert(hints, keyHolder, daoPojo);

		DalHintsChecker.checkEquals(original,hints);
		assertEquals(1, affected1);
		assertEquals(1, keyHolder.size());
		assertEquals(4L, keyHolder.getKey());
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(4, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<MysqlPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(1));
		int[] affected1 = dao.insert(new DalHints(), keyHolder, daoPojos);
		assertArrayEquals(new int[]{1,1,1},  affected1);
		assertEquals(3, keyHolder.size());
		assertEquals(4L, keyHolder.getKey(0));
		assertEquals(5L, keyHolder.getKey(1));
		assertEquals(6L, keyHolder.getKey(2));
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(6, affected);
	}
	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<MysqlPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(0));
		dao.batchInsert(new DalHints(), daoPojos);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	
	@Test
	public void testCombinedInsert1() throws Exception {
		DalHints hints = new DalHints();
		List<MysqlPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		int affected1 = dao.combinedInsert(new DalHints(), daoPojos);
		assertEquals(3, affected1);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<MysqlPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(0));
		dao.combinedInsert(hints, keyHolder, daoPojos);
		assertEquals(3, keyHolder.size());
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
		List<MysqlPeopleTable> list = dao.queryAllByPage(2, 2, new DalHints().inShard(1).inTableShard(0));
		assertEquals(1, list.size());
		assertEquals("InsertByfields_1fields_4", list.get(0).getName());
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1;
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		MysqlPeopleTable affected = dao.queryByPk(id,  hints.inShard(1).setTableShardValue(0));
		assertNotNull(affected);
		assertEquals("InsertByfields_1fields_0", affected.getName());
		List<DalHintEnum> notNullItems=new ArrayList<>();
		notNullItems.add(DalHintEnum.shard);
		notNullItems.add(DalHintEnum.tableShardValue);
		DalHintsChecker.checkEquals(original,hints,notNullItems);
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		MysqlPeopleTable pk = createPojo(1);
		pk.setID(2);
		DalHints hints = new DalHints();
		MysqlPeopleTable affected = dao.queryByPk(pk, new DalHints().inShard(0).inTableShard(1));
		assertNotNull(affected);
		assertEquals("InsertByfields_0fields_3", affected.getName());
	}
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		MysqlPeopleTable daoPojo = new MysqlPeopleTable();
		daoPojo.setID(2);
		daoPojo.setCityID(200);
		daoPojo.setAge(20);
		daoPojo.setName("UpdateByfields_0fields_0");
		
		int affected = dao.update(new DalHints(), daoPojo);
		assertEquals(1, affected);
		
		MysqlPeopleTable daoPojo1=dao.queryByPk(2, new DalHints().inShard(0).inTableShard(0));
		assertEquals("UpdateByfields_0fields_0", daoPojo1.getName());
		
		daoPojo = new MysqlPeopleTable();
		daoPojo.setID(3);
		daoPojo.setCityID(200);
		daoPojo.setAge(100);
		daoPojo.setName("UpdateByfields_0fields_0");
		
		affected = dao.update(new DalHints().exclude("Name"), daoPojo);
		assertEquals(1, affected);
		daoPojo1=dao.queryByPk(3, new DalHints().inShard(0).inTableShard(0));
		assertEquals("InsertByfields_0fields_4", daoPojo1.getName());
		assertEquals(200, daoPojo1.getCityID().intValue());
		assertEquals(100, daoPojo1.getAge().intValue());
		
		daoPojo = new MysqlPeopleTable();
		daoPojo.setID(1);
		daoPojo.setCityID(201);
		daoPojo.setAge(100);
		daoPojo.setName("UpdateByfields_0fields_0");
 
		affected = dao.update(new DalHints().include("CityID","Age"), daoPojo);
		assertEquals(1, affected);
		daoPojo1=dao.queryByPk(1, new DalHints().inShard(1).inTableShard(0));
		assertEquals("InsertByfields_1fields_0", daoPojo1.getName());
		assertEquals(201, daoPojo1.getCityID().intValue());
		assertEquals(100, daoPojo1.getAge().intValue());
	}
	
	@Test
	public void testUpdate2() throws Exception {
		DalHints hints = new DalHints();
		List<MysqlPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		daoPojos.get(0).setName("Update0");
		daoPojos.get(1).setName("Update1");
		daoPojos.get(2).setName("Update2");
		int[] affected = dao.update(new DalHints(), daoPojos);
		assertArrayEquals(new int[]{1,1,1},  affected);
		
		daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		for(int i=0;i<daoPojos.size();i++)
			assertEquals("Update"+i, daoPojos.get(i).getName());
		
		MysqlPeopleTable daoPojo1=dao.queryByPk(1, new DalHints().inShard(0).inTableShard(0));
		daoPojo1.setID(2);
		daoPojo1.setCityID(300);
		daoPojo1.setAge(100);
		MysqlPeopleTable daoPojo2=dao.queryByPk(1, new DalHints().inShard(1).inTableShard(1));
		daoPojo2.setID(2);
		daoPojo2.setCityID(301);
		daoPojo2.setAge(101);
		
		daoPojos.clear();
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);
		
		affected = dao.update(new DalHints().exclude("Name"), daoPojos);
		assertArrayEquals(new int[]{1,1},  affected);
		
		daoPojo1=dao.queryByPk(2, new DalHints().inShard(0).inTableShard(0));
		assertEquals("InsertByfields_0fields_2", daoPojo1.getName());
		assertEquals(300, daoPojo1.getCityID().intValue());
		assertEquals(100, daoPojo1.getAge().intValue());
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<MysqlPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		daoPojos.get(0).setName("Update0");
		daoPojos.get(1).setName("Update1");
		daoPojos.get(2).setName("Update2");
		int[] affected = dao.batchUpdate(new DalHints(), daoPojos);
		assertArrayEquals(new int[]{1,1,1},  affected);
		daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		for(int i=0;i<daoPojos.size();i++)
			assertEquals("Update"+i, daoPojos.get(i).getName());
		
		MysqlPeopleTable daoPojo1=dao.queryByPk(1, new DalHints().inShard(0).inTableShard(0));
		daoPojo1.setID(2);
		daoPojo1.setCityID(300);
		daoPojo1.setAge(100);
		MysqlPeopleTable daoPojo2=dao.queryByPk(1, new DalHints().inShard(1).inTableShard(1));
		daoPojo2.setID(2);
		daoPojo2.setCityID(301);
		daoPojo2.setAge(101);
		
		daoPojos.clear();
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);
		
		affected = dao.batchUpdate(new DalHints().include("CityID","Age"), daoPojos);
		assertArrayEquals(new int[]{1,1},  affected);
		
		daoPojo1=dao.queryByPk(2, new DalHints().inShard(0).inTableShard(0));
		assertEquals("InsertByfields_0fields_2", daoPojo1.getName());
		assertEquals(300, daoPojo1.getCityID().intValue());
		assertEquals(100, daoPojo1.getAge().intValue());
	}
	
	@Test
	public void testtest_build_delete() throws Exception {
		//Integer CityID = null;// Test value here
		//Integer Age = null;// Test value here
	    //int ret = dao.test_build_delete(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		int ret=dao.test_build_delete(CityID, Age, new DalHints().inAllShards().inTableShard(0));
		assertEquals(2, ret);

		DalHints hints=new DalHints();
		DalHints original= hints.clone();
		ret=dao.test_build_delete(CityID, Age, hints.setShardColValue("CityID", 201).inTableShard(1));
		assertEquals(1, ret);
        List<DalHintEnum> notNullItems=new ArrayList<DalHintEnum>(){{add(DalHintEnum.shardColValues);add(DalHintEnum.tableShard);}};
        DalHintsChecker.checkEquals(original,hints,notNullItems);

		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(2, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(2, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(2, affected);
	}
	
	@Test
	public void testtest_build_insert() throws Exception {
		String Name = "test_build_insert";// Test value here
		Integer CityID = 206;// Test value here
		Integer Age = 26;// Test value here
	    int ret = dao.test_build_insert(Name, CityID, Age, new DalHints());
	    assertEquals(1, ret);
	    
	    int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(4, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	    
		ret = dao.test_build_insert(Name, CityID, Age, new DalHints().inAllShards().inTableShard(1));
	    assertEquals(2, ret);
	    
	    affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(4, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(4, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(4, affected);
	}
	
	@Test
	public void testtest_build_update() throws Exception {
		String Name = "test_build_update";// Test value here
		
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
	    int ret = dao.test_build_update(Name, CityID, Age, new DalHints().inAllShards().inTableShard(1));
	    assertEquals(2, ret);
	    
	    MysqlPeopleTable pojo=dao.queryByPk(1, new DalHints().inShard(0).inTableShard(0));
	    assertEquals("InsertByfields_0fields_0", pojo.getName());
	    
	    pojo=dao.queryByPk(1, new DalHints().inShard(0).inTableShard(1));
	    assertEquals("test_build_update", pojo.getName());
	    
	    pojo=dao.queryByPk(1, new DalHints().inShard(1).inTableShard(0));
	    assertEquals("InsertByfields_1fields_0", pojo.getName());
	    
	    pojo=dao.queryByPk(1, new DalHints().inShard(1).inTableShard(1));
	    assertEquals("test_build_update", pojo.getName());
	    
	    ret = dao.test_build_update(Name, CityID, Age, new DalHints().setShardValue(3).inTableShard(0));
	    assertEquals(1, ret);
	    
	    pojo=dao.queryByPk(1, new DalHints().inShard(1).inTableShard(0));
	    assertEquals("test_build_update", pojo.getName());
	    
	}
	
	
	@Test
	public void testtest_build_query_listFirst() throws Exception {
		//Integer CityID = null;// Test value here
		//Integer Age = null;// Test value here
	    //MysqlPeopleTable ret = dao.test_build_query_listFirst(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		MysqlPeopleTable ret = dao.test_build_query_listFirst(CityID, Age, new DalHints().inShard(0).inTableShard(0));
		assertEquals("InsertByfields_0fields_0", ret.getName());
		
		ret = dao.test_build_query_listFirst(CityID, Age, new DalHints().inAllShards().inTableShard(0));
		assertEquals("InsertByfields_0fields_0", ret.getName());
		
		ret = dao.test_build_query_listFirst(CityID, Age, new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 21));
		assertEquals("InsertByfields_1fields_1", ret.getName());
	}

	@Test
	public void testtest_build_query_list() throws Exception {
		//Integer CityID = null;// Test value here
		//Integer Age = null;// Test value here
	    //List<MysqlPeopleTable> ret = dao.test_build_query_list(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		List<MysqlPeopleTable> ret = dao.test_build_query_list(CityID, Age, new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20));
		assertEquals(2, ret.size());
		
//		for(int i=0;i<ret.size();i++)
//			System.out.println("1: "+ret.get(i).getName());
		
		ret = dao.test_build_query_list(CityID, Age, new DalHints().inAllShards().setShardColValue("Age", 20));
		assertEquals(4, ret.size());
		
//		for(int i=0;i<ret.size();i++)
//			System.out.println("2: "+ret.get(i).getName());
		
		ret = dao.test_build_query_list(CityID, Age, new DalHints().setShardValue(200).inTableShard(0));
		assertEquals(2, ret.size());
		
//		for(int i=0;i<ret.size();i++)
//			System.out.println("3: "+ret.get(i).getName());
		
		ret = dao.test_build_query_list(CityID, Age, new DalHints().setShardValue(200).setTableShardValue(20));
		assertEquals(2, ret.size());
		
//		for(int i=0;i<ret.size();i++)
//			System.out.println("4: "+ret.get(i).getName());
	}

	@Test
	public void testtest_build_query_listByPage() throws Exception {
		//Integer CityID = null;// Test value here
		//Integer Age = null;// Test value here
	    //List<MysqlPeopleTable> ret = dao.test_build_query_listByPage(CityID, Age, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		List<MysqlPeopleTable> ret = dao.test_build_query_listByPage(CityID, Age, 2, 1, new DalHints().inShard(0).inTableShard(1));
		assertEquals("InsertByfields_0fields_3", ret.get(0).getName());
		
	}
	
	@Test
	public void testtest_build_query_listSingle() throws Exception {
		//Integer CityID = null;// Test value here
		//Integer Age = null;// Test value here
	    //MysqlPeopleTable ret = dao.test_build_query_listSingle(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		MysqlPeopleTable ret = dao.test_build_query_listSingle(CityID, Age, new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21));
		assertEquals("InsertByfields_0fields_1", ret.getName());
		
		ret = dao.test_build_query_listSingle(CityID, Age, new DalHints().inShard(1).setTableShardValue(1));
		assertNull(ret);
	}
	
	@Test
	public void testtest_build_query_fieldFirst() throws Exception {
		//Integer CityID = null;// Test value here
		//Integer Age = null;// Test value here
	    //MysqlPeopleTable ret = dao.test_build_query_fieldFirst(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		String ret = dao.test_build_query_fieldFirst(CityID, Age, new DalHints().inShard(0).inTableShard(0));
		assertEquals("InsertByfields_0fields_0", ret);
		
		ret = dao.test_build_query_fieldFirst(CityID, Age, new DalHints().inAllShards().inTableShard(0));
		assertEquals("InsertByfields_0fields_0", ret);
		
		ret = dao.test_build_query_fieldFirst(CityID, Age, new DalHints().setShardColValue("CityID", 201).setShardColValue("Age", 21));
		assertEquals("InsertByfields_1fields_1", ret);
	}

	@Test
	public void test_build_query_fieldList() throws Exception {
		//Integer CityID = null;// Test value here
		//Integer Age = null;// Test value here
	    //List<String> ret = dao.test_build_query_fieldList(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		List<String> ret = dao.test_build_query_fieldList(CityID, Age, new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 20));
		assertEquals(2, ret.size());
		
		ret = dao.test_build_query_fieldList(CityID, Age, new DalHints().inAllShards().setShardColValue("Age", 20));
		assertEquals(4, ret.size());
		
		ret = dao.test_build_query_fieldList(CityID, Age, new DalHints().setShardValue(200).inTableShard(0));
		assertEquals(2, ret.size());
		
		ret = dao.test_build_query_fieldList(CityID, Age, new DalHints().setShardValue(200).setTableShardValue(20));
		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_build_query_fieldListByPage() throws Exception {
		//Integer CityID = null;// Test value here
		//Integer Age = null;// Test value here
	    //List<String> ret = dao.test_build_query_fieldListByPage(CityID, Age, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		List<String> ret = dao.test_build_query_fieldListByPage(CityID, Age, 2, 1, new DalHints().inShard(0).inTableShard(1));
		assertEquals("InsertByfields_0fields_3", ret.get(0));
	}

	
	@Test
	public void testtest_build_query_fieldSingle() throws Exception {
		//Integer CityID = null;// Test value here
		//Integer Age = null;// Test value here
	    //String ret = dao.test_build_query_fieldSingle(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		String ret = dao.test_build_query_fieldSingle(CityID, Age, new DalHints().setShardColValue("CityID", 200).setShardColValue("Age", 21));
		assertEquals("InsertByfields_0fields_1", ret);
		
		ret = dao.test_build_query_fieldSingle(CityID, Age, new DalHints().inShard(1).setTableShardValue(1));
		assertNull(ret);
	}
	
	@Test
	public void testtest_def_update() throws Exception {
		//String Name = "";// Test value here
		//Integer CityID = 1;// Test value here
		//Integer Age = 1;// Test value here
	    //int ret = dao.test_def_update(Name, CityID, Age, new DalHints());
        String Name = "test_def_update";// Test value here
		
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
	    int ret = dao.test_def_update(Name, CityID, Age, new DalHints().inShard(0),"_1");
	    assertEquals(1, ret);
	    
	    MysqlPeopleTable pojo=dao.queryByPk(1, new DalHints().inShard(0).inTableShard(0));
	    assertEquals("InsertByfields_0fields_0", pojo.getName());
	    
	    pojo=dao.queryByPk(1, new DalHints().inShard(0).inTableShard(1));
	    assertEquals("test_def_update", pojo.getName());
	    
	    pojo=dao.queryByPk(1, new DalHints().inShard(1).inTableShard(0));
	    assertEquals("InsertByfields_1fields_0", pojo.getName());
	    
	    pojo=dao.queryByPk(1, new DalHints().inShard(1).inTableShard(1));
	    assertEquals("InsertByfields_1fields_1", pojo.getName());
	    
	    ret = dao.test_def_update(Name, CityID, Age, new DalHints().setShardValue(3),"_0");
	    assertEquals(1, ret);
	    
	    pojo=dao.queryByPk(1, new DalHints().inShard(1).inTableShard(0));
	    assertEquals("test_def_update", pojo.getName());
	}
	
	@Test
	public void testtest_def_truncate() throws Exception {
	    //int ret = dao.test_def_truncate(new DalHints());
		dao.test_def_truncate(new DalHints().inShard(0),"_0");
		dao.test_def_truncate(new DalHints().inShard(0),"_1");
		dao.test_def_truncate(new DalHints().inShard(1),"_0");
		dao.test_def_truncate(new DalHints().inShard(1),"_1");
		
	}
	
	@Test
	public void testtest_def_query_listFirst() throws Exception {
		//Integer CityID = 1;// Test value here
		//Integer Age = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listFirst(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(4);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		MysqlPeopleTable ret = dao.test_def_query_listFirst(CityID, Age, new DalHints().inShard(0),"_0");
		assertEquals("InsertByfields_0fields_0", ret.getName());
		
		ret = dao.test_def_query_listFirst(CityID, Age, new DalHints().inAllShards(),"_0");
		assertEquals("InsertByfields_0fields_0", ret.getName());
		
		ret = dao.test_def_query_listFirst(CityID, Age, new DalHints().setShardColValue("CityID", 201),"_1");
		assertEquals("InsertByfields_1fields_1", ret.getName());
	}
	
	@Test
	public void testtest_def_query_list() throws Exception {
		//Integer CityID = 1;// Test value here
		//Integer Age = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_list(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		List<MysqlPeopleTable> ret = dao.test_def_query_list(CityID, Age, new DalHints().setShardColValue("CityID", 200),"_0");
		assertEquals(2, ret.size());
		
//		for(int i=0;i<ret.size();i++)
//			System.out.println("1: "+ret.get(i).getName());
		
		ret = dao.test_def_query_list(CityID, Age, new DalHints().inAllShards(),"_0");
		assertEquals(4, ret.size());
		
//		for(int i=0;i<ret.size();i++)
//			System.out.println("2: "+ret.get(i).getName());
		
		ret = dao.test_def_query_list(CityID, Age,new DalHints(),"_0");
		assertEquals(2, ret.size());
		
//		for(int i=0;i<ret.size();i++)
//			System.out.println("3: "+ret.get(i).getName());
		
		ret = dao.test_def_query_list(CityID, Age, new DalHints().setShardValue(200),"_0");
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_def_query_listByPage() throws Exception {
		//Integer CityID = 1;// Test value here
		//Integer Age = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_listByPage(CityID, Age, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		List<MysqlPeopleTable> ret = dao.test_def_query_listByPage(CityID, Age, 2, 1, new DalHints().inShard(0),"_1");
		assertEquals("InsertByfields_0fields_3", ret.get(0).getName());
	}
	
	@Test
	public void testtest_def_query_listSingle() throws Exception {
		//Integer CityID = 1;// Test value here
		//Integer Age = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listSingle(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		MysqlPeopleTable ret = dao.test_def_query_listSingle(CityID, Age, new DalHints().setShardColValue("CityID", 200),"_1");
		assertEquals("InsertByfields_0fields_1", ret.getName());
		
		ret = dao.test_def_query_listSingle(CityID, Age, new DalHints().inShard(1),"_1");
		assertNull(ret);
	}
	
	@Test
	public void testtest_def_query_fieldFirst() throws Exception {
		//Integer CityID = 1;// Test value here
		//Integer Age = 1;// Test value here
		//String ret = dao.test_def_query_fieldFirst(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		String ret = dao.test_def_query_fieldFirst(CityID, Age, new DalHints().inShard(0),"_0");
		assertEquals("InsertByfields_0fields_0", ret);
		
		ret = dao.test_def_query_fieldFirst(CityID, Age, new DalHints().inAllShards(),"_0");
		assertEquals("InsertByfields_0fields_0", ret);
		
		ret = dao.test_def_query_fieldFirst(CityID, Age, new DalHints().setShardColValue("CityID", 201),"_1");
		assertEquals("InsertByfields_1fields_1", ret);
	}
	
	@Test
	public void testtest_def_query_fieldList() throws Exception {
		//Integer CityID = 1;// Test value here
		//Integer Age = 1;// Test value here
		//List<String> ret = dao.test_def_query_fieldList(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		List<String> ret = dao.test_def_query_fieldList(CityID, Age, new DalHints().setShardColValue("CityID", 200),"_0");
		assertEquals(2, ret.size());
		
		ret = dao.test_def_query_fieldList(CityID, Age, new DalHints().inAllShards(),"_0");
		assertEquals(4, ret.size());
		
		ret = dao.test_def_query_fieldList(CityID, Age, new DalHints().setShardValue(200),"_0");
		assertEquals(2, ret.size());
		
		ret = dao.test_def_query_fieldList(CityID, Age, new DalHints().setShardValue(200),"_0");
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_def_query_fieldListByPage() throws Exception {
		//Integer CityID = 1;// Test value here
		//Integer Age = 1;// Test value here
		//List<String> ret = dao.test_def_query_fieldListByPage(CityID, Age, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		CityID.add(201);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		Age.add(22);
		Age.add(23);
		
		List<String> ret = dao.test_def_query_fieldListByPage(CityID, Age, 2, 1, new DalHints().inShard(0),"_1");
		assertEquals("InsertByfields_0fields_3", ret.get(0));
	}
	
	@Test
	public void testtest_def_query_fieldSingle() throws Exception {
		//Integer CityID = 1;// Test value here
		//Integer Age = 1;// Test value here
		//String ret = dao.test_def_query_fieldSingle(CityID, Age, new DalHints());
		List<Integer> CityID=new ArrayList<>(2);
		CityID.add(200);
		
		List<Integer> Age=new ArrayList<>(2);
		Age.add(20);
		Age.add(21);
		
		String ret = dao.test_def_query_fieldSingle(CityID, Age, new DalHints().setShardColValue("CityID", 200),"_1");
		assertEquals("InsertByfields_0fields_1", ret);
		
		ret = dao.test_def_query_fieldSingle(CityID, Age, new DalHints().inShard(1),"_1");
		assertNull(ret);
	}
}
