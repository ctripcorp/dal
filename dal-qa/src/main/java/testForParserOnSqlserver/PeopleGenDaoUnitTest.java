package testForParserOnSqlserver;

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

	private static final String DATA_BASE = "test_parser_Sqlserver";

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
	public void test() throws Exception {
		assertEquals("test_parser_1", dao.parser.getDatabaseName());
		assertEquals("People_0", dao.parser.getTableName());
	}
	
	@Test
	public void testCount() throws Exception {
		int ret = dao.count(new DalHints());
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		PeopleGen daoPojo = new PeopleGen();
		daoPojo.setPeopleID(5l);
		int ret = dao.delete(hints, daoPojo);
		assertEquals(0, ret);
	}
	
	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
		List<PeopleGen> daoPojos = null;
		int[] affected = dao.delete(hints, daoPojos);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<PeopleGen> daoPojos = null;
		int[] affected = dao.batchDelete(hints, daoPojos);
	}
	
	@Test
	public void testGetAll() throws Exception {
		List<PeopleGen> list = dao.getAll(new DalHints());
	}
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		PeopleGen daoPojo = new PeopleGen();
		daoPojo.setCityID(1);
		daoPojo.setCountryID(1);
		daoPojo.setName("db_1_tb_0");
		daoPojo.setProvinceID(3);		
		int affected = dao.insert(hints, daoPojo);
		assertEquals(0, affected);
	}
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		List<PeopleGen> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		PeopleGen daoPojo = null;
		int affected = dao.insert(hints, keyHolder, daoPojo);
	}
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<PeopleGen> daoPojos = null;
		int[] affected = dao.insert(hints, keyHolder, daoPojos);
	}
	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<PeopleGen> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
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
		Number id = null;
		DalHints hints = new DalHints();
		PeopleGen ret = dao.queryByPk(id, hints);
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		PeopleGen pk = null;
		DalHints hints = new DalHints();
		PeopleGen ret = dao.queryByPk(pk, hints);
	}
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		PeopleGen daoPojo = new PeopleGen();
		daoPojo.setPeopleID(5l);
		daoPojo.setName("update");
		int ret = dao.update(hints, daoPojo);
		assertEquals(0, ret);
	}
	
	@Test
	public void testUpdate2() throws Exception {
		DalHints hints = new DalHints();
		List<PeopleGen> daoPojos = null;
		int[] ret = dao.update(hints, daoPojos);
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<PeopleGen> daoPojos = null;
		int[] ret = dao.batchUpdate(hints, daoPojos);
	}

}
