package testForNullableBySqlserver;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import microsoft.sql.DateTimeOffset;

import java.sql.SQLException;

import org.junit.*;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of AllTypesGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class AllTypesGenDaoUnitTest {

	private static final String DATA_BASE = "testForNullableBySqlserver";

	private static DalClient client = null;
	private static AllTypesGenDao dao = null;
	
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
		dao = new AllTypesGenDao();
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
		DalHints hints = new DalHints();
		AllTypesGen daoPojo = null;
		int affected = dao.insert(hints, daoPojo);
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
		AllTypesGen pk = null;
		DalHints hints = new DalHints();
		AllTypesGen ret = dao.queryByPk(pk, hints);
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
	public void testtest_alltypes_nullable_notNull() throws Exception {
		
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
	    List<AllTypesGen> ret = dao.test_alltypes_nullable(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
	    assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_alltypes_nullable_null() throws Exception {
		
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
	    List<AllTypesGen> ret = dao.test_alltypes_nullable(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
	    assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_alltypes() throws Exception {
		
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
	    List<AllTypesGen> ret = dao.test_alltypes(CharCol, NcharCol,  BitCol, IntCol,SmallIntCol, TinyIntCol, NumericCol, BigIntCol_start, BigIntCol_end, varCharCol, new DalHints());
	    assertEquals(2, ret.size());
	}

}
