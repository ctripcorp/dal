package noShardTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of AllTypesCIDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class AllTypesCIDaoUnitTest {

	private static final String DATA_BASE = "noShardTestOnSqlServer";

	private static DalClient client = null;
	private static AllTypesCIDao dao = null;
	
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
		dao = new AllTypesCIDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		dao.cleanTable();

		AllTypesCI daoPojo1 = new AllTypesCI();
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
		daoPojo1.setDatachangeLasttime(Timestamp.valueOf("2017-4-21 15:50:30"));
		dao.insert(daoPojo1);


		AllTypesCI daoPojo2 = new AllTypesCI();
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
		daoPojo2.setDatachangeLasttime(Timestamp.valueOf("2017-4-21 15:50:40"));
		dao.insert(daoPojo2);

		AllTypesCI daoPojo3 = new AllTypesCI();
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
		daoPojo3.setDatachangeLasttime(Timestamp.valueOf("2017-4-21 15:50:50"));
		dao.insert(daoPojo3);

		AllTypesCI daoPojo4 = new AllTypesCI();
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
		daoPojo4.setDatachangeLasttime(Timestamp.valueOf("2017-4-21 15:50:55"));
		dao.insert(daoPojo4);
	}
	
	private AllTypesCI createPojo(int index) {
		AllTypesCI daoPojo = new AllTypesCI();

		//daoPojo.setId(index);
		//daoPojo set not null field
		
		return daoPojo;
	}

	private void changePojo(AllTypesCI daoPojo) {
		// Change a field to make pojo different with original one
	}
	
	private void changePojos(List<AllTypesCI> daoPojos) {
		for(AllTypesCI daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(AllTypesCI daoPojo) {
		//assert changed value
	}
	
	private void verifyPojos(List<AllTypesCI> daoPojos) {
		for(AllTypesCI daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
//		To clean up all test data
//		dao.delete(null, dao.queryAll(null));
	} 
	
	
	@Test
	public void testCount() throws Exception {
		int affected = dao.count(new DalHints());
		assertEquals(4, affected);
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		AllTypesCI daoPojo = new AllTypesCI();
		daoPojo.setID(2);

		dao.delete(hints, daoPojo);

		assertEquals(3,dao.count());
	}
	
	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesCI> daoPojos = dao.queryAll();

		dao.delete(daoPojos);

		int count=dao.count();
		assertEquals(0, count);

		dao.delete(null,daoPojos);

		count=dao.count();
		assertEquals(0, count);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesCI> daoPojos = dao.queryAll();

		dao.batchDelete(daoPojos);

		int count=dao.count();
		assertEquals(0, count);

        dao.batchDelete(null,daoPojos);

		count=dao.count();
		assertEquals(0, count);
	}
	
	@Test
	public void testQueryAll() throws Exception {
		List<AllTypesCI> list = dao.queryAll(new DalHints());
		assertEquals(4, list.size());
	}
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		AllTypesCI daoPojo = createPojo(1);
		dao.insert(hints, daoPojo);
		assertEquals(5, dao.count());
	}
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesCI> daoPojos = dao.queryAll(new DalHints());
		dao.insert(hints, daoPojos);
		assertEquals(8,  dao.count());
	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		AllTypesCI daoPojo = createPojo(1);
		dao.insert(hints, keyHolder, daoPojo);
		assertEquals(5, dao.count());
		assertEquals(1, keyHolder.size());
		assertEquals(5,keyHolder.getKey());
	}
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<AllTypesCI> daoPojos = dao.queryAll(new DalHints());
		dao.insert(hints, keyHolder, daoPojos);
		assertEquals(8, dao.count());
		assertEquals(4, keyHolder.size());
		assertEquals(8,keyHolder.getKey(3));
	}
	
	@Test
	public void testInsert5() throws Exception {
//		DalHints hints = new DalHints();
//		List<AllTypesCI> daoPojos = dao.queryAll(new DalHints());
//		int[] affected = dao.batchInsert(hints, daoPojos);
//
		List<AllTypesCI> daoPojos = new ArrayList<>();

		for(int i=0;i<4;i++){
			AllTypesCI pojo=new AllTypesCI();
			pojo.setID(i);
			pojo.setIntCol(i);
			daoPojos.add(pojo);
		}

		int[] affected = dao.batchInsert(daoPojos);

		int count=dao.count();
		assertEquals(8, count);

        affected = dao.batchInsert(null,daoPojos);

		count=dao.count();
		assertEquals(12, count);
	}

	/*combinedInsert is not supported by ctrip sqlserver*/
//	@Test
//	public void testCombinedInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		List<AllTypesCI> daoPojos = dao.queryAll(new DalHints());
//		dao.combinedInsert(hints, daoPojos);
//		assertEquals(8, dao.count());
//	}
//
//	@Test
//	public void testCombinedInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		List<AllTypesCI> daoPojos = dao.queryAll(new DalHints());
//		dao.combinedInsert(hints, keyHolder, daoPojos);
//		assertEquals(4, keyHolder.size());
//		assertEquals(8,keyHolder.getKey(3));
//	}
	
	@Test
	public void testQueryAllByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<AllTypesCI> list = dao.queryAllByPage(pageNo, pageSize, hints);
		assertEquals(4, list.size());
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1;
		DalHints hints = new DalHints();
		AllTypesCI affected = dao.queryByPk(id, hints);
		assertNotNull(affected);
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		AllTypesCI pk = createPojo(1);
		pk.setID(4);
		DalHints hints = new DalHints();
		AllTypesCI affected = dao.queryByPk(pk, hints);
		assertNotNull(affected);
	}
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		AllTypesCI daoPojo = dao.queryByPk(2, hints);
		daoPojo.setIntCol(900);
		dao.update(hints, daoPojo);

		daoPojo = dao.queryByPk(2, null);
		assertEquals(900,daoPojo.getIntCol().intValue());
	}
	
	@Test
	public void testUpdate2() throws Exception {
		List<AllTypesCI> daoPojos = dao.queryAll();
		daoPojos.get(0).setVarcharCol("update21");
		daoPojos.get(1).setVarcharCol("update22");
		daoPojos.get(2).setVarcharCol("update23");
		daoPojos.get(3).setVarcharCol("update24");
		int[] affected = dao.update(daoPojos);

		AllTypesCI daoPojo = dao.queryByPk(3);
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
	public void testBatchUpdate() throws Exception {
		List<AllTypesCI> daoPojos = dao.queryAll();
		daoPojos.get(0).setVarcharCol("update21");
		daoPojos.get(1).setVarcharCol("update22");
		daoPojos.get(2).setVarcharCol("update23");
		daoPojos.get(3).setVarcharCol("update24");
		int[] affected = dao.batchUpdate(daoPojos);

		AllTypesCI daoPojo = dao.queryByPk(3);
		assertEquals("update23", daoPojo.getVarcharCol());

		daoPojos.get(0).setVarcharCol("update211");
		daoPojos.get(1).setVarcharCol("update222");
		daoPojos.get(2).setVarcharCol("update233");
		daoPojos.get(3).setVarcharCol("update244");
		affected = dao.batchUpdate(null,daoPojos);//update(hints,daoPojos)

		daoPojo = dao.queryByPk(3);
		assertEquals("update233", daoPojo.getVarcharCol());
	}

}
