package testForNullableByMysql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.sql.SQLException;

import org.junit.*;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of AllTypesGenDao class. Before run the unit test, you should
 * initiate the test data and change all the asserts correspond to you case.
 **/
public class AllTypesGenDaoUnitTest {

	private static final String DATA_BASE = "testForNullableByMysql";

	private static DalClient client = null;
	private static AllTypesGenDao dao = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		 * Initialize DalClientFactory. The Dal.config can be specified from
		 * class-path or local file path. One of follow three need to be
		 * enabled.
		 **/
		// DalClientFactory.initPrivateFactory(); //Load from class-path
		// connections.properties
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
		// DalClientFactory.initClientFactory("E:/DalMult.config"); // load from
		// the specified Dal.config file path
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new AllTypesGenDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		dao.def_truncate(hints);
		
		AllTypesGen daoPojo1 = new AllTypesGen();
		daoPojo1.setIntCol(8);
		daoPojo1.setBigIntCol(80l);
		daoPojo1.setMediumIntCol(80);
		daoPojo1.setSmallIntCol(2);
		daoPojo1.setTinyIntCol(2);
		daoPojo1.setDoubleCol(8.99);
		daoPojo1.setCharCol("a");
		daoPojo1.setVarCharCol("colorful");
		daoPojo1.setDecimalCol(new BigDecimal(2013));
		daoPojo1.setBitCol(false);
		daoPojo1.setSetCol("c");
		dao.insert(hints, daoPojo1);
		
		AllTypesGen daoPojo2 = new AllTypesGen();
		daoPojo2.setIntCol(1);
		daoPojo2.setBigIntCol(100l);
		daoPojo2.setMediumIntCol(49);
		daoPojo2.setSmallIntCol(30);
		daoPojo2.setTinyIntCol(4);
		daoPojo2.setFloatCol(2.3f);
		daoPojo2.setCharCol("b");
		daoPojo2.setVarCharCol("new");
		daoPojo2.setDecimalCol(new BigDecimal(2016));
		daoPojo2.setBitCol(true);
		daoPojo2.setSetCol("a");
		dao.insert(hints, daoPojo2);
		
         AllTypesGen daoPojo3 = new AllTypesGen();
 		daoPojo3.setIntCol(6);
 		daoPojo3.setBigIntCol(70l);
 		daoPojo3.setMediumIntCol(50);
 		daoPojo3.setSmallIntCol(7);
 		daoPojo3.setTinyIntCol(6);
 		daoPojo3.setDoubleCol(2.33);
 		daoPojo3.setCharCol("a");
 		daoPojo3.setVarCharCol("hello");
 		daoPojo3.setDecimalCol(new BigDecimal(2015));
 		daoPojo3.setBitCol(true);
 		daoPojo3.setSetCol("b");
 		dao.insert(hints, daoPojo3);
 		
         AllTypesGen daoPojo4 = new AllTypesGen();
  		daoPojo4.setIntCol(12);
  		daoPojo4.setBigIntCol(90l);
  		daoPojo4.setMediumIntCol(70);
  		daoPojo4.setSmallIntCol(9);
  		daoPojo4.setTinyIntCol(8);
  		daoPojo4.setDoubleCol(5.66);
  		daoPojo4.setCharCol("a");
  		daoPojo4.setVarCharCol("world");
  		daoPojo4.setDecimalCol(new BigDecimal(2014));
  		daoPojo4.setBitCol(false);
  		daoPojo4.setSetCol("a");
  		dao.insert(hints, daoPojo4);
	}

	@After
	public void tearDown() throws Exception {
		dao.def_truncate(new DalHints());
	}

	@Test
	public void testCount() throws Exception {
		int ret = dao.count(new DalHints());
	}

	@Test
	public void testDelete1() throws Exception {
		DalHints hints = new DalHints();
		AllTypesGen daoPojo = null;
		int ret = dao.delete(hints, daoPojo);
	}

	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesGen> daoPojos = null;
		int[] affected = dao.delete(hints, daoPojos);
	}

	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesGen> daoPojos = null;
		int[] affected = dao.batchDelete(hints, daoPojos);
	}

	@Test
	public void testGetAll() throws Exception {
		List<AllTypesGen> list = dao.getAll(new DalHints());
	}

	@Test
	public void testInsert1() throws Exception {
		
	}

	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesGen> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}

	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		AllTypesGen daoPojo = null;
		int affected = dao.insert(hints, keyHolder, daoPojo);
	}

	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<AllTypesGen> daoPojos = null;
		int[] affected = dao.insert(hints, keyHolder, daoPojos);
	}

	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesGen> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}

	@Test
	public void testCombinedInsert1() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesGen> daoPojos = null;
		int affected = dao.combinedInsert(hints, daoPojos);
	}

	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<AllTypesGen> daoPojos = null;
		int affected = dao.combinedInsert(hints, keyHolder, daoPojos);
	}

	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<AllTypesGen> list = dao.queryByPage(pageSize, pageNo, hints);
	}

	@Test
	public void testQueryByPk1() throws Exception {
		Number id = null;
		DalHints hints = new DalHints();
		AllTypesGen ret = dao.queryByPk(id, hints);
	}

	@Test
	public void testQueryByPk2() throws Exception {
//		AllTypesGen pk = null;
//		DalHints hints = new DalHints();
//		AllTypesGen ret = dao.queryByPk(pk, hints);
	}

	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		AllTypesGen daoPojo = null;
		int ret = dao.update(hints, daoPojo);
	}

	@Test
	public void testUpdate2() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesGen> daoPojos = null;
		int[] ret = dao.update(hints, daoPojos);
	}

	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<AllTypesGen> daoPojos = null;
		int[] ret = dao.batchUpdate(hints, daoPojos);
	}

	@Test
	public void testtest_Java_nullable_notNull() throws Exception {

		String CharCol = "a%";
		String SetCol = "a";
		Boolean BitCol = false;
		Integer IntCol = 3;
		Integer SmallIntCol = 20;
		Integer MediumIntCol = 50;
		BigDecimal DecimalCol = new BigDecimal(2015);
		Long BigIntCol_start = 70l;
		Long BigIntCol_end = 90l;
		List<String> varCharCol = new ArrayList<String>();
		varCharCol.add("hello");
		varCharCol.add("colorful");
		varCharCol.add("world");
		List<AllTypesGen> ret = dao.test_Java_nullable(CharCol, SetCol, BitCol,
				IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
				BigIntCol_end, varCharCol, new DalHints());
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_Java_nullable_Null() throws Exception {

		String CharCol = null;
		String SetCol = null;
		Boolean BitCol = null;
		Integer IntCol = null;
		Integer SmallIntCol = null;
		Integer MediumIntCol = null;
		BigDecimal DecimalCol = null;
		Long BigIntCol_start = null;
		Long BigIntCol_end = null;
		List<String> varCharCol = new ArrayList<String>();
		varCharCol.add("hello");
		varCharCol.add(null);
		varCharCol.add("world");
		List<AllTypesGen> ret = dao.test_Java_nullable(CharCol, SetCol, BitCol,
				IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
				BigIntCol_end, varCharCol, new DalHints());
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_Java() throws Exception {

		String CharCol = "a%";
		String SetCol = "a";
		Boolean BitCol = false;
		Integer IntCol = 3;
		Integer SmallIntCol = 20;
		Integer MediumIntCol = 50;
		BigDecimal DecimalCol = new BigDecimal(2015);
		Long BigIntCol_start = 70l;
		Long BigIntCol_end = 90l;
		List<String> varCharCol = new ArrayList<String>();
		varCharCol.add("hello");
		varCharCol.add("colorful");
		varCharCol.add("world");
		List<AllTypesGen> ret = dao.test_Java_nullable(CharCol, SetCol, BitCol,
				IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
				BigIntCol_end, varCharCol, new DalHints());
		assertEquals(2, ret.size());
	}

	@Test
	public void testdef_truncate() throws Exception {
//		int ret = dao.def_truncate(new DalHints());
//		assertEquals(4, ret);
	}

}
