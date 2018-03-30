package noShardTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.sqlbuilder.MatchPattern;
import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of AllTypesDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class AllTypesOnSqlServerDaoUnitTest {

	private static final String DATA_BASE = "noShardTestOnSqlServer";

	private static DalClient client = null;
	private static AllTypesOnSqlServerDao dao = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		* Initialize DalClientFactory.
		* The Dal.config can be specified from class-path or local file path.
		* One of follow three need to be enabled.
		**/
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
		DalClientFactory.warmUpConnections();
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new AllTypesOnSqlServerDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
//		To prepare test data, you can simply uncomment the following.
//		In case of DB and table shard, please revise the code to reflect shard
//		for(int i = 0; i < 10; i++) {
//			AllTypes daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		DalHints hints = new DalHints();
		dao.testDefTruncate(hints);
		
		AllTypesOnSqlServer daoPojo1 = new AllTypesOnSqlServer();
		daoPojo1.setIntCol(8);
		daoPojo1.setSmallIntCol((short) 2);
		daoPojo1.setTinyIntCol((short) 80);
		daoPojo1.setBigIntCol(80l);
		daoPojo1.setBitCol(false);
		daoPojo1.setNumericCol(new BigDecimal("2013"));
		daoPojo1.setRealCol(8.99f);
		daoPojo1.setCharCol("a");
		daoPojo1.setNcharCol("c");
		daoPojo1.setVarcharCol("colorful");
		daoPojo1.setDatetimeCol(Timestamp.valueOf("2017-4-21 15:50:30"));
		dao.insert(hints, daoPojo1);
		
		
		AllTypesOnSqlServer daoPojo2 = new AllTypesOnSqlServer();
		daoPojo2.setIntCol(1);
		daoPojo2.setSmallIntCol((short) 30);
		daoPojo2.setTinyIntCol((short) 49);
		daoPojo2.setBigIntCol(100l);
		daoPojo2.setBitCol(true);
		daoPojo2.setNumericCol(new BigDecimal("2015"));
		daoPojo2.setFloatCol(2.3);
		daoPojo2.setCharCol("b");
		daoPojo2.setNcharCol("a");
		daoPojo2.setVarcharCol("new");
		daoPojo2.setDatetimeCol(Timestamp.valueOf("2017-4-21 15:50:40"));
		dao.insert(hints, daoPojo2);
		
        AllTypesOnSqlServer daoPojo3 = new AllTypesOnSqlServer();
        daoPojo3.setIntCol(6);
		daoPojo3.setSmallIntCol((short) 7);
		daoPojo3.setTinyIntCol((short) 50);
		daoPojo3.setBigIntCol(70l);
		daoPojo3.setBitCol(true);
		daoPojo3.setNumericCol(new BigDecimal("2016"));
		daoPojo3.setRealCol(2.33f);
		daoPojo3.setCharCol("a");
		daoPojo3.setNcharCol("b");
		daoPojo3.setVarcharCol("hello");
		daoPojo3.setDatetimeCol(Timestamp.valueOf("2017-4-21 15:50:50"));
 		dao.insert(hints, daoPojo3);
 		
         AllTypesOnSqlServer daoPojo4 = new AllTypesOnSqlServer();
         daoPojo4.setIntCol(12);
 		daoPojo4.setSmallIntCol((short) 9);
 		daoPojo4.setTinyIntCol((short) 70);
 		daoPojo4.setBigIntCol(90l);
 		daoPojo4.setBitCol(false);
 		daoPojo4.setNumericCol(new BigDecimal("2014"));
 		daoPojo4.setRealCol(5.66f);
 		daoPojo4.setCharCol("a");
 		daoPojo4.setNcharCol("a");
 		daoPojo4.setVarcharCol("world");
 		daoPojo4.setDatetimeCol(Timestamp.valueOf("2017-4-21 15:50:55"));
  		dao.insert(hints, daoPojo4);
	}
	
	private AllTypesOnSqlServer createPojo(int index) {
		AllTypesOnSqlServer daoPojo = new AllTypesOnSqlServer();

		//daoPojo.setId(index);
		//daoPojo set not null field
		
		return daoPojo;
	}

	private void changePojo(AllTypesOnSqlServer daoPojo) {
		// Change a field to make pojo different with original one
	}
	
	private void changePojos(List<AllTypesOnSqlServer> daoPojos) {
		for(AllTypesOnSqlServer daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(AllTypesOnSqlServer daoPojo) {
		//assert changed value
	}
	
	private void verifyPojos(List<AllTypesOnSqlServer> daoPojos) {
		for(AllTypesOnSqlServer daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
//		To clean up all test data
//		dao.delete(null, dao.queryAll(null));
	} 
	
	
	@Test
	public void testCount() throws Exception {
		int affected = dao.count();
		assertEquals(4, affected);
		
		affected = dao.count(null);
		assertEquals(4, affected);
	}
	
	@Test
	public void testDelete1() throws Exception {
		AllTypesOnSqlServer daoPojo = createPojo(1);
		daoPojo.setID(1);
		/**
		 * WARNING !!!
		 * To test delete, please make sure you can easily restore all the data. otherwise data will not be revovered.
		 */
		int affected = dao.delete(daoPojo); 
		
		
		affected = dao.count();
		assertEquals(3, affected);
		
		daoPojo.setID(2);
		affected = dao.delete(null,daoPojo); 
		
		
		affected = dao.count();
		assertEquals(2, affected);
	}
	
	@Test
	public void testDelete2() throws Exception {
		List<AllTypesOnSqlServer> daoPojos = dao.queryAll();
		/**
		 * WARNING !!!
		 * To test delete, please make sure you can easily restore all the data. otherwise data will not be revovered.
		 */

		int[] affected = dao.delete(daoPojos);		
		
		int count=dao.count();
		assertEquals(0, count);
		
		affected = dao.delete(null,daoPojos);		
		
		count=dao.count();
		assertEquals(0, count);
	}
	
//	@Test
//	public void testBatchDelete() throws Exception {
//		List<AllTypesOnSqlServer> daoPojos = dao.queryAll();

		//Data type LONGVARBINARY not supported in Table-Valued Parameter
//		for(AllTypesOnSqlServer pojo :daoPojos) {
//			pojo.setImageCol(null);
//			pojo.setVarBinaryCol(null);
//			pojo.setVarBinaryMaxCol(null);
//			pojo.setTimestampCol(null);
//		}

//		int[] affected = dao.batchDelete(daoPojos);
//
//
//		int count=dao.count();
//		assertEquals(0, count);
//
//        affected = dao.batchDelete(null,daoPojos);
//
//
//		count=dao.count();
//		assertEquals(0, count);
//	}
	
	@Test
	public void testQueryAll() throws Exception {
		List<AllTypesOnSqlServer> list = dao.queryAll();
		assertEquals(4, list.size());
		
		list.clear();
		list = dao.queryAll(new DalHints().selectByNames());
		assertEquals(4, list.size());
	}

	/*@Test
	public void  test() throws Exception{
		AllTypesOnSqlServer pojo=new AllTypesOnSqlServer();
		pojo.setNvarcharCol("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		dao.insert(pojo);
	}*/

	@Test
	public void testInsert1() throws Exception {
		
		AllTypesOnSqlServer daoPojo = createPojo(1);
		daoPojo.setVarcharCol("insert1");
		int affected = dao.insert(daoPojo);
		
		AllTypesOnSqlServer insertPojo=dao.queryByPk(5);
		assertEquals("insert1", insertPojo.getVarcharCol());
		
		affected = dao.insert(null,daoPojo);//insert(hints,daoPojo);		
		
		insertPojo=dao.queryByPk(6);
		assertEquals("insert1", insertPojo.getVarcharCol());
		
	}
	
	@Test
	public void testInsert2() throws Exception {
		
		List<AllTypesOnSqlServer> daoPojos = dao.queryAll();
		int[] affected = dao.insert(daoPojos);
		
		int count=dao.count();
		assertEquals(8, count);
		
        affected = dao.insert(null,daoPojos);//insert(hints,daoPojos)
		
		count=dao.count();
		assertEquals(12, count);
	}
	
	@Test
	public void testInsert3() throws Exception {
		KeyHolder keyHolder = new KeyHolder();
		AllTypesOnSqlServer daoPojo = createPojo(1);
		daoPojo.setVarcharCol("insert3");
		int affected = dao.insertWithKeyHolder(keyHolder, daoPojo);
//		assertEquals(1, affected);
		assertEquals(1, keyHolder.size());
		
		AllTypesOnSqlServer insertPojo=dao.queryByPk(5);
		assertEquals("insert3", insertPojo.getVarcharCol());
		
		affected = dao.insert(null,keyHolder, daoPojo);//insert(hints,keyHolder, daoPojo)
//		assertEquals(1, affected);
		assertEquals(1, keyHolder.size());
		
		insertPojo=dao.queryByPk(6);
		assertEquals("insert3", insertPojo.getVarcharCol());
	}
	
	@Test
	public void testInsert4() throws Exception {
		
		KeyHolder keyHolder = new KeyHolder();
		List<AllTypesOnSqlServer> daoPojos = dao.queryAll();
		int[] affected = dao.insertWithKeyHolder(keyHolder, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1},  affected);
		assertEquals(4, keyHolder.size());
		
		int count=dao.count();
		assertEquals(8, count);
		
		affected = dao.insert(null,keyHolder, daoPojos);//insert(hints,keyHolder, daoPojos)
//		assertArrayEquals(new int[]{1,1,1,1},  affected);
		assertEquals(4, keyHolder.size());
		
		count=dao.count();
		assertEquals(12, count);
	}
	
//	@Test
//	public void testInsert5() throws Exception {
//
//		List<AllTypesOnSqlServer> daoPojos = new ArrayList<>();
//
//		for(int i=0;i<4;i++){
//			AllTypesOnSqlServer pojo=new AllTypesOnSqlServer();
//			pojo.setID(i);
//			pojo.setIntCol(i);
//			daoPojos.add(pojo);
//		}
//
//		int[] affected = dao.batchInsert(daoPojos);
//
//		int count=dao.count();
//		assertEquals(8, count);
//
//        affected = dao.batchInsert(null,daoPojos);
//
//		count=dao.count();
//		assertEquals(12, count);
//	}
	
//	@Test
//	public void testCombinedInsert1() throws Exception {
//		
//		List<AllTypes> daoPojos = dao.queryAll();
//		int affected = dao.combinedInsert(daoPojos);
//		assertEquals(4, affected);
//		
//		int count=dao.count();
//		assertEquals(8, count);
//	}
	
//	@Test
//	public void testCombinedInsert11() throws Exception {
//		
//		List<AllTypes> daoPojos = dao.queryAll();
//		int affected = dao.combinedInsert(null,daoPojos);
//		assertEquals(4, affected);
//		
//		int count=dao.count();
//		assertEquals(8, count);
//	}
//	
//	@Test
//	public void testCombinedInsert2() throws Exception {
//		
//		KeyHolder keyHolder = new KeyHolder();
//		List<AllTypes> daoPojos = dao.queryAll();
//		int affected = dao.combinedInsert(keyHolder, daoPojos);
//		assertEquals(4, affected);
//		assertEquals(4, keyHolder.size());
//		
//		int count=dao.count();
//		assertEquals(8, count);
//	}
	
	@Test
	public void testQueryAllByPage() throws Exception {

		int pageSize = 2;
		int pageNo = 2;
		List<AllTypesOnSqlServer> list = dao.queryAllByPage(pageNo, pageSize);
		assertEquals(2, list.size());
		
		list = dao.queryAllByPage(pageNo, pageSize,new DalHints().selectByNames());
		assertEquals(2, list.size());
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1;
		
		AllTypesOnSqlServer affected = dao.queryByPk(id);
		assertNotNull(affected);
		
		affected = dao.queryByPk(id,new DalHints().selectByNames());
		assertNotNull(affected);
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		AllTypesOnSqlServer pk = createPojo(1);
		pk.setID(2);
		AllTypesOnSqlServer affected = dao.queryByPk(pk);
		assertNotNull(affected);
		
		affected = dao.queryByPk(pk,new DalHints().selectByNames());
		assertNotNull(affected);
	}
	
	@Test
	public void testQueryLike() throws Exception {
		AllTypesOnSqlServer sample = createPojo(1);
		sample.setVarcharCol("new");
		List<AllTypesOnSqlServer> affected = dao.queryLike(sample);
		assertEquals(2, affected.get(0).getID().intValue());
		
		affected = dao.queryLike(sample,new DalHints().selectByNames());
		assertEquals(2, affected.get(0).getID().intValue());
	}
	
	@Test
	public void testUpdate1() throws Exception {
		
		AllTypesOnSqlServer daoPojo = dao.queryByPk(1);
		daoPojo.setVarcharCol("update1");
		int affected = dao.update(daoPojo);
		
		daoPojo = dao.queryByPk(1);
		assertEquals("update1", daoPojo.getVarcharCol());
		
		daoPojo.setVarcharCol("update11");
        affected = dao.update(null,daoPojo);//update(hints,daoPojo)
		
		daoPojo = dao.queryByPk(1);
		assertEquals("update11", daoPojo.getVarcharCol());
	}
	
	@Test
	public void testUpdate2() throws Exception {
		
		List<AllTypesOnSqlServer> daoPojos = dao.queryAll();
		daoPojos.get(0).setVarcharCol("update21");
		daoPojos.get(1).setVarcharCol("update22");
		daoPojos.get(2).setVarcharCol("update23");
		daoPojos.get(3).setVarcharCol("update24");
		int[] affected = dao.update(daoPojos);
		
		AllTypesOnSqlServer daoPojo = dao.queryByPk(3);
		assertEquals("update23", daoPojo.getVarcharCol());
		
		daoPojos.get(0).setVarcharCol("update211");
		daoPojos.get(1).setVarcharCol("update222");
		daoPojos.get(2).setVarcharCol("update233");
		daoPojos.get(3).setVarcharCol("update244");
        affected = dao.update(null,daoPojos);//update(hints,daoPojos)
		
		daoPojo = dao.queryByPk(3);
		assertEquals("update233", daoPojo.getVarcharCol());
	}

	@Test
	public void testtest_build_nullable_inNull() throws Exception {
		String CharCol = null;
		String NcharCol = null;
		Boolean BitCol = null;
		Integer IntCol = null;
		Short SmallIntCol = null;
		Short TinyIntCol = null;
		BigDecimal NumericCol = null;
		Long BigIntCol_start = null;
		Long BigIntCol_end = 90l;
		List<String> varCharCol = new ArrayList<String>();
		varCharCol.add(null);
		varCharCol.add(null);
		varCharCol.add(null);
		List<AllTypesOnSqlServer> ret = dao.test_build_query_nullable(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
		assertEquals(3, ret.size());
	}

	@Test
	public void testtest_build_nullable_null() throws Exception {
		String CharCol = null;
		String NcharCol = null;
		Boolean BitCol = null;
		Integer IntCol = null;
		Short SmallIntCol = null;
		Short TinyIntCol = null;
		BigDecimal NumericCol = null;
		Long BigIntCol_start = null;
		Long BigIntCol_end = 90l;
        List<String> varCharCol = new ArrayList<String>();
        varCharCol.add("hello");
        varCharCol.add(null);
        varCharCol.add("world");
	    List<AllTypesOnSqlServer> ret = dao.test_build_query_nullable(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
	    assertEquals(2, ret.size());
	}

	@Test
	public void testFreeSqlQueryWithAppendNullable() throws Exception {
		String CharCol = null;
		String NcharCol = null;
		Boolean BitCol = null;
		Integer IntCol = null;
		Short SmallIntCol = null;
		Short TinyIntCol = null;
		BigDecimal NumericCol = null;
		Long BigIntCol_start = null;
		Long BigIntCol_end = 90l;
		List<String> varCharCol = new ArrayList<String>();
		varCharCol.add("hello");
		varCharCol.add(null);
		varCharCol.add("world");
		List<AllTypesOnSqlServer> ret = dao.testFreeSqlQueryWithAppendNullable(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
		assertEquals(2, ret.size());
	}

	@Test
	public void testFreeSqlQueryNullable() throws Exception {
		String CharCol = null;
		String NcharCol = null;
		Boolean BitCol = null;
		Integer IntCol = null;
		Short SmallIntCol = null;
		Short TinyIntCol = null;
		BigDecimal NumericCol = null;
		Long BigIntCol_start = null;
		Long BigIntCol_end = 90l;
		List<String> varCharCol = new ArrayList<String>();
		varCharCol.add("hello");
		varCharCol.add(null);
		varCharCol.add("world");
		List<AllTypesOnSqlServer> ret = dao.testFreeSqlQueryNullable(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
		assertEquals(2, ret.size());
	}

	@Test
	public void testFreeSqlQueryNotNullWithSet() throws Exception {
		String CharCol = "a%";
		String NcharCol = "a";
		Boolean BitCol = false;
		Integer IntCol = 3;
		Short SmallIntCol = 20;
		Short TinyIntCol = 50;
		BigDecimal NumericCol = new BigDecimal(2015);
		Long BigIntCol_start = 70l;
		Long BigIntCol_end = 90l;
		List<String> varCharCol = new ArrayList<String>();
		varCharCol.add("hello");
		varCharCol.add("colorful");
		varCharCol.add("world");
		List<AllTypesOnSqlServer> ret = dao.testFreeSqlQueryNotNullWithSet(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
		assertEquals(2, ret.size());
	}

	@Test
	public void testFreeSqlQueryNotNull() throws Exception {
		String CharCol = "a%";
		String NcharCol = "a";
		Boolean BitCol = false;
		Integer IntCol = 3;
		Short SmallIntCol = 20;
		Short TinyIntCol = 50;
		BigDecimal NumericCol = new BigDecimal(2015);
		Long BigIntCol_start = 70l;
		Long BigIntCol_end = 90l;
		List<String> varCharCol = new ArrayList<String>();
		varCharCol.add("hello");
		varCharCol.add("colorful");
		varCharCol.add("world");
		List<AllTypesOnSqlServer> ret = dao.testFreeSqlQueryNotNull(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
		assertEquals(2, ret.size());
	}

	@Test
	public void testFreeSqlSetNullable() throws Exception {
		List<AllTypesOnSqlServer> ret1 = dao.testFreeSqlSetNullableAndIncludeAll(null, new DalHints());
		assertEquals(4, ret1.size());

		List<AllTypesOnSqlServer> ret2 = dao.testFreeSqlSetNullableAndExcludeAll(null, new DalHints());
		assertEquals(0, ret2.size());
	}

	@Test
	public void testtest_build_nullable_notnull() throws Exception {
		String CharCol = "a%";
		String NcharCol = "a";
		Boolean BitCol = false;
		Integer IntCol = 3;
		Short SmallIntCol = 20;
		Short TinyIntCol = 50;
		BigDecimal NumericCol = new BigDecimal(2015);
		Long BigIntCol_start = 70l;
		Long BigIntCol_end = 90l;
        List<String> varCharCol = new ArrayList<String>();
        varCharCol.add("hello");
        varCharCol.add("colorful");
        varCharCol.add("world");
	    List<AllTypesOnSqlServer> ret = dao.test_build_query_nullable(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
	    assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_build_notnull_notnull() throws Exception {
		String CharCol = "a%";
		String NcharCol = "a";
		Boolean BitCol = false;
		Integer IntCol = 3;
		Short SmallIntCol = 20;
		Short TinyIntCol = 50;
		BigDecimal NumericCol = new BigDecimal(2015);
		Long BigIntCol_start = 70l;
		Long BigIntCol_end = 90l;
        List<String> varCharCol = new ArrayList<String>();
        varCharCol.add("hello");
        varCharCol.add("colorful");
        varCharCol.add("world");
	    List<AllTypesOnSqlServer> ret = dao.test_build_query_notnull(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
	    assertEquals(2, ret.size());
	}
	
//	@Test
//	public void testBatchUpdate() throws Exception {
//
//		List<AllTypesOnSqlServer> daoPojos = dao.queryAll();
//		daoPojos.get(0).setVarcharCol("batchUpdate1");
//		daoPojos.get(1).setVarcharCol("batchUpdate2");
//		daoPojos.get(2).setVarcharCol("batchUpdate3");
//		daoPojos.get(3).setVarcharCol("batchUpdate4");
//
////		for(AllTypesOnSqlServer pojo : daoPojos){
////			pojo.setTimestampCol(null);
////		}
//		int[] affected = dao.batchUpdate(daoPojos);
//
//		AllTypesOnSqlServer daoPojo = dao.queryByPk(3);
//		assertEquals("batchUpdate3", daoPojo.getVarcharCol());
//
//		daoPojos.get(0).setVarcharCol("batchUpdate11");
//		daoPojos.get(1).setVarcharCol("batchUpdate22");
//		daoPojos.get(2).setVarcharCol("batchUpdate33");
//		daoPojos.get(3).setVarcharCol("batchUpdate44");
//		affected = dao.batchUpdate(null,daoPojos);//batchUpdate(hints,daoPojos)
//
//		daoPojo = dao.queryByPk(3);
//		assertEquals("batchUpdate33", daoPojo.getVarcharCol());
//	}
	
	@Test
	public void testtestBuildDelete() throws Exception {
		Integer id = 1;// Test value here
	    int ret = dao.testBuildDelete(id);
	    AllTypesOnSqlServer daoPojo = dao.queryByPk(1);
		assertNull(daoPojo);
		
		ret = dao.testBuildDelete(2,null);
	    daoPojo = dao.queryByPk(2);
		assertNull(daoPojo);
	}

	@Test
	public void testFreeSqlDelete() throws Exception {
		Integer id = 1;// Test value here
		int ret = dao.testFreeSqlDelete(id, null);

		AllTypesOnSqlServer pojo = dao.queryByPk(1);
		assertNull(pojo);

		ret = dao.testFreeSqlDelete(2, null);

		pojo = dao.queryByPk(2);
		assertNull(pojo);
	}
	
	@Test
	public void testtestBuildInsert() throws Exception {
		String VarCharCol = "buildinsert";// Test value here
		Integer TinyIntCol = 3;// Test value here
	    int ret = dao.testBuildInsert(VarCharCol, TinyIntCol);
	    AllTypesOnSqlServer daoPojo = dao.queryByPk(5);
		assertNotNull(daoPojo);
		
		ret = dao.testBuildInsert(VarCharCol, TinyIntCol,null);
	    daoPojo = dao.queryByPk(5);
		assertNotNull(daoPojo);
	}

	@Test
	public void testFreeSqlInsert() throws Exception {
		String varcharcol = "defInsert";// Test value here
		Integer intcol = 1;// Test value here
		dao.testFreeSqlInsert(varcharcol, intcol, null);
		AllTypesOnSqlServer pojo = dao.queryByPk(5);
		assertEquals("defInsert", pojo.getVarcharCol());
	}
	
	@Test
	public void testtestBuildUpdate() throws Exception {
		String VarCharCol = "buildupdate";// Test value here
		Integer id = 3;// Test value here
	    int ret = dao.testBuildUpdate(VarCharCol, id);
	    
	    AllTypesOnSqlServer daoPojo = dao.queryByPk(3);
		assertEquals("buildupdate", daoPojo.getVarcharCol());
		
        ret = dao.testBuildUpdate(VarCharCol, id,null);
	    
	    daoPojo = dao.queryByPk(3);
		assertEquals("buildupdate", daoPojo.getVarcharCol());
	}

	@Test
	public void testFreeSqlUpdate() throws Exception {
		String varcharcol = "defupdate";// Test value here
		String charcol = "b";// Test value here
		int id = 2;
		dao.testFreeSqlUpdate(varcharcol, charcol, id, new DalHints());

		AllTypesOnSqlServer pojo = dao.queryByPk(2);
		assertEquals("defupdate", pojo.getVarcharCol());
		assertEquals("b", pojo.getCharCol().trim());
	}

	@Test
	public void testtestBuildQueryPojoFirst() throws Exception {
		Integer id = 1;// Test value here
	    AllTypesOnSqlServer ret = dao.testBuildQueryPojoFirst(id);
	    assertEquals("world", ret.getVarcharCol());
	    
	    ret = dao.testBuildQueryPojoFirst(id,null);
	    assertEquals("world", ret.getVarcharCol());
	}

	@Test
	public void testtestBuildQueryPojoList() throws Exception {
		Integer id = 1;// Test value here
	    List<AllTypesOnSqlServer> ret = dao.testBuildQueryPojoList(id);    
	    assertEquals(3, ret.size());
	    
	    ret = dao.testBuildQueryPojoList(id,null);    
	    assertEquals(3, ret.size());
	}

	@Test
	public void testtestBuildQueryPojoListByPage() throws Exception {
		Integer id = 1;// Test value here
	    List<AllTypesOnSqlServer> ret = dao.testBuildQueryPojoListByPage(id, 2, 2);
	    assertEquals(1, ret.size());
	    
	    ret = dao.testBuildQueryPojoListByPage(id, 2, 2,null);
	    assertEquals(1, ret.size());
	}
	
	@Test
	public void testtestBuildQueryPojoSingle() throws Exception {
		Integer id = 2;// Test value here
	    AllTypesOnSqlServer ret = dao.testBuildQueryPojoSingle(id);
	    assertEquals("new", ret.getVarcharCol());
	    
	    ret = dao.testBuildQueryPojoSingle(id,null);
	    assertEquals("new", ret.getVarcharCol());
	}
	
	@Test
	public void testtestBuildQueryFieldFirst() throws Exception {
		Integer id = 1;// Test value here
	    int ret = dao.testBuildQueryFieldFirst(id);
	    assertEquals(12, ret);
	    
	    ret = dao.testBuildQueryFieldFirst(id,null);
	    assertEquals(12, ret);
	}

	@Test
	public void testtestBuildQueryFieldList() throws Exception {
		Integer id = 1;// Test value here
	    List<Integer> ret = dao.testBuildQueryFieldList(id);
	    assertEquals(3, ret.size());
	    
	    ret = dao.testBuildQueryFieldList(id,null);
	    assertEquals(3, ret.size());
	}

	@Test
	public void testtestBuildQueryFieldListByPage() throws Exception {
		Integer id =1;// Test value here
	    List<Integer> ret = dao.testBuildQueryFieldListByPage(id, 2, 2);
	    assertEquals(1, ret.size());
	    
	    ret = dao.testBuildQueryFieldListByPage(id, 2, 2,null);
	    assertEquals(1, ret.size());
	}

	
	@Test
	public void testtestBuildQueryFieldSingle() throws Exception {
		Integer id = 2;// Test value here
	    Integer ret = dao.testBuildQueryFieldSingle(id);
	    assertEquals(1, ret.intValue());
	    
	    ret = dao.testBuildQueryFieldSingle(id,null);
	    assertEquals(1, ret.intValue());
	}

	@Test
	public void testFreeSQLBuilderQueryMax() throws Exception {
		Integer id = 2;
		int maxIntCol = dao.testFreeSQLBuilderQueryMax(id, new DalHints());
		assertEquals(12, maxIntCol);
	}

	@Test
	public void testFreeSqlLikePattern() throws Exception {
		List<AllTypesOnSqlServer> ret1 = dao.testFreeSqlLikePattern("n", MatchPattern.BEGIN_WITH);
		assertEquals(1, ret1.size());
		assertEquals(100, ret1.get(0).getBigIntCol().intValue());

		List<AllTypesOnSqlServer> ret2 = dao.testFreeSqlLikePattern("e", MatchPattern.CONTAINS);
		assertEquals(2, ret2.size());

		List<AllTypesOnSqlServer> ret3 = dao.testFreeSqlLikePattern("ful", MatchPattern.END_WITH);
		assertEquals(1, ret3.size());

		List<AllTypesOnSqlServer> ret4 = dao.testFreeSqlLikePattern("%ful", MatchPattern.USER_DEFINED);
		assertEquals(1, ret4.size());
	}

	@Test
	public void testFreeSqlNotLikePattern() throws Exception {
		List<AllTypesOnSqlServer> ret1 = dao.testFreeSqlNotLikePattern("n", MatchPattern.BEGIN_WITH);
		assertEquals(3, ret1.size());

		List<AllTypesOnSqlServer> ret2 = dao.testFreeSqlNotLikePattern("e", MatchPattern.CONTAINS);
		assertEquals(2, ret2.size());

		List<AllTypesOnSqlServer> ret3 = dao.testFreeSqlNotLikePattern("ful", MatchPattern.END_WITH);
		assertEquals(3, ret3.size());

		List<AllTypesOnSqlServer> ret4 = dao.testFreeSqlNotLikePattern("%ful", MatchPattern.USER_DEFINED);
		assertEquals(3, ret4.size());
	}

	@Test
	public void testFreeSqlGroupByHaving() throws Exception {
		List<String> VarCharColList = new ArrayList<>();
		VarCharColList.add("hello");
		VarCharColList.add("world");
		List<Map<String, Object>> retList = dao.testFreeSqlGroupByHaving2(VarCharColList);
		assertEquals("hello", retList.get(0).get("VarCharCol").toString());
		assertEquals(1, Integer.parseInt(retList.get(0).get("Count").toString()));

		assertEquals("world", retList.get(1).get("VarCharCol").toString());
		assertEquals(1, Integer.parseInt(retList.get(1).get("Count").toString()));

		List<Map<String, Object>> retList2 = dao.testFreeSqlGroupByHaving(VarCharColList);
		assertEquals("hello", retList2.get(0).get("VarCharCol").toString());
		assertEquals(1, Integer.parseInt(retList2.get(0).get("Count").toString()));

		assertEquals("world", retList2.get(1).get("VarCharCol").toString());
		assertEquals(1, Integer.parseInt(retList2.get(1).get("Count").toString()));
	}

	@Test
	public void testFreeSqlNotNotNull() throws Exception {
		int low = 2;
		int upper = 4;
		List<Long> bigIntCol = new ArrayList<>();
		bigIntCol.add(1l);
		bigIntCol.add(70l);
		String varCharCol = "%ful";
		List<AllTypesOnSqlServer> ret = dao.testNotNotNull(low, upper, bigIntCol, varCharCol);
		assertEquals(2, ret.size());
		assertEquals(2, ret.get(0).getID().intValue());
		assertEquals(4, ret.get(1).getID().intValue());
	}

	@Test
	public void testFreeSqlNotNullable() throws Exception {
		List<Long> bigIntCol = new ArrayList<>();
		bigIntCol.add(70l);
		bigIntCol.add(null);
		String varCharCol = null;
		List<AllTypesOnSqlServer> ret = dao.testNotNullable(null, 4, bigIntCol, varCharCol);
		assertEquals(3, ret.size());
	}

	@Test
	public void testtestDefUpdate() throws Exception {
		String varcharcol = "defupdate";// Test value here
		String charcol = "b";// Test value here
	    int ret = dao.testDefUpdate(varcharcol, charcol);
	    
	    AllTypesOnSqlServer pojo=dao.queryByPk(2);
	    assertEquals("defupdate", pojo.getVarcharCol());
	    
	      varcharcol = "defupdate1";
         ret = dao.testDefUpdate(varcharcol, charcol,null);
	    
	   pojo=dao.queryByPk(2);
	    assertEquals("defupdate1", pojo.getVarcharCol());
	}
	
	@Test
	public void testtestDefDelete() throws Exception {
		Integer id = 1;// Test value here
	    int ret = dao.testDefDelete(id);
	    
	    AllTypesOnSqlServer pojo=dao.queryByPk(1);
	    assertNull(pojo);
	    
        ret = dao.testDefDelete(2);
	    
	     pojo=dao.queryByPk(2);
	    assertNull(pojo);
	}
	
	@Test
	public void testtestDefInsert() throws Exception {
		String varcharcol = "defInsert";// Test value here
		Integer intcol = 1;// Test value here
	    int ret = dao.testDefInsert(varcharcol, intcol);
	    AllTypesOnSqlServer pojo=dao.queryByPk(5);
	    assertEquals("defInsert", pojo.getVarcharCol());
	    
	    ret = dao.testDefInsert(varcharcol, intcol,null);
	    pojo=dao.queryByPk(6);
	    assertEquals("defInsert", pojo.getVarcharCol());
	}
	
	@Test
	public void testtestDefQueryPojoFirst() throws Exception {
		Integer id = 1;// Test value here
		AllTypesOnSqlServer ret = dao.testDefQueryPojoFirst(id);
		assertEquals("new", ret.getVarcharCol());
		
		ret = dao.testDefQueryPojoFirst(id,null);
		assertEquals("new", ret.getVarcharCol());

		ret = dao.testFreeSqlQueryFirst(id,null);
		assertEquals("new", ret.getVarcharCol());
	}
	
	@Test
	public void testtestDefQueryFieldFirst() throws Exception {
		Integer id = 1;// Test value here
		int ret = dao.testDefQueryFieldFirst(id);
		assertEquals(1, ret);
		
		ret = dao.testDefQueryFieldFirst(id,null);
		assertEquals(1, ret);

		ret = dao.testFreeSQLBuilderQueryFieldFirst(id,null);
		assertEquals(1, ret);
	}
	
	@Test
	public void testtestDefQueryPojoList() throws Exception {
		List<Integer> intcol = new ArrayList<>();
		intcol.add(1);
		intcol.add(12);
		intcol.add(99);
		List<AllTypesOnSqlServer> ret = dao.testDefQueryPojoList(intcol);
		assertEquals(2, ret.size());
		
		ret = dao.testDefQueryPojoList(intcol,null);
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtestDefQueryPojoListByPage() throws Exception {
		Integer id = 1;// Test value here
		List<AllTypesOnSqlServer> ret = dao.testDefQueryPojoListByPage(id, 2, 2);
		assertEquals(1, ret.size());
		
		ret = dao.testDefQueryPojoListByPage(id, 2, 2,null);
		assertEquals(1, ret.size());

		ret = dao.testFreeSqlQueryListByPage(id, 2, 2,null);
		assertEquals(1, ret.size());
	}
	
	@Test
	public void testtestDefQueryFieldList() throws Exception {
		Integer id = 1;// Test value here
		List<Integer> ret = dao.testDefQueryFieldList(id);
		assertEquals(3, ret.size());
		
		ret = dao.testDefQueryFieldList(id,null);
		assertEquals(3, ret.size());

		ret = dao.testFreeSQLBuilderQueryFieldList(id,null);
		assertEquals(3, ret.size());
	}
	
	@Test
	public void testtestDefQueryFieldListByPage() throws Exception {
		Integer id = 1;// Test value here
		List<Integer> ret = dao.testDefQueryFieldListByPage(id, 2, 2);
		assertEquals(1, ret.size());
		
		ret = dao.testDefQueryFieldListByPage(id, 2, 2,null);
		assertEquals(1, ret.size());

		ret = dao.testFreeSQLBuilderQueryFieldListByPage(id, 2, 2,null);
		assertEquals(1, ret.size());
	}
	
	@Test
	public void testtestDefQueryPojoSingle() throws Exception {
		Integer id = 1;// Test value here
		AllTypesOnSqlServer ret = dao.testDefQueryPojoSingle(id);
		assertEquals("colorful", ret.getVarcharCol());
		
		ret = dao.testDefQueryPojoSingle(id,null);
		assertEquals("colorful", ret.getVarcharCol());

		ret = dao.testFreeSqlQuerySingle(id,null);
		assertEquals("colorful", ret.getVarcharCol());
	}
	
	@Test
	public void testtestDefQueryFieldSingle() throws Exception {
		Integer id = 1;// Test value here
		int ret = dao.testDefQueryFieldSingle(id);
		assertEquals(8, ret);
		
		ret = dao.testDefQueryFieldSingle(id,null);
		assertEquals(8, ret);

		ret = dao.testFreeSQLBuilderQueryFieldSingle(id,null);
		assertEquals(8, ret);
	}
}
