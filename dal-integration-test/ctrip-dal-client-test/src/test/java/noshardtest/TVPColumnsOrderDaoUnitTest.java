package noshardtest;

import dao.noshard.TVPColumnsOrderDao;
import entity.TVPColumnsOrder;
import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JUnit test of TVPColumnsOrderDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class TVPColumnsOrderDaoUnitTest {

//	private static final String DATA_BASE = "AllTypeTest";
//
//	private static DalClient client = null;
	private static TVPColumnsOrderDao dao = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		* Initialize DalClientFactory.
		* The Dal.config can be specified from class-path or local file path.
		* One of follow three need to be enabled.
		**/
//		DalClientFactory.initClientFactory(); // load from class-path Dal.config
//		DalClientFactory.warmUpConnections();
//		client = DalClientFactory.getClient(DATA_BASE);
		DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "DalConfigForSpt/Dal.config");
		dao = new TVPColumnsOrderDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalClientFactory.shutdownFactory();
	}
	
	@Before
	public void setUp() throws Exception {
		List<TVPColumnsOrder> data=dao.queryAll();
		dao.delete(data);

		List<TVPColumnsOrder> list=new ArrayList<>();
		TVPColumnsOrder pojo1=new TVPColumnsOrder();
		pojo1.setColumn1("Column11");
		pojo1.setColumn3("Column13");
		pojo1.setColumn2("Column12");
		pojo1.setColumnTest("Test11");
		TVPColumnsOrder pojo2=new TVPColumnsOrder();
		pojo2.setColumn1("Column21");
		pojo2.setColumn3("Column23");
		pojo2.setColumn2("Column22");
		pojo2.setColumnTest("Test21");
		TVPColumnsOrder pojo3=new TVPColumnsOrder();
		pojo3.setColumn1("Column31");
		pojo3.setColumn3("Column33");
		pojo3.setColumn2("Column32");
		pojo3.setColumnTest("Test31");
		list.add(pojo1);
		list.add(pojo2);
		list.add(pojo3);
		dao.insert(list);
	}

	
	@After
	public void tearDown() throws Exception {
//		To clean up all test data
//		dao.delete(null, dao.queryAll(null));
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<TVPColumnsOrder> daoPojos = dao.queryAll(null);

		int[] affected = dao.batchDelete(hints, daoPojos);
		assertEquals(0,dao.count());
	}

	@Test
	public void testBatchInsert() throws Exception {
		DalHints hints = new DalHints();
		List<TVPColumnsOrder> daoPojos = dao.queryAll(new DalHints());
		dao.batchInsert(hints, daoPojos);
		List<TVPColumnsOrder> newPojos = dao.queryAll(new DalHints());
		assertEquals(6,newPojos.size());
		assertEquals("Column11",newPojos.get(3).getColumn1());
		assertEquals("Column12",newPojos.get(3).getColumn2());
		assertEquals("Column13",newPojos.get(3).getColumn3());
	}

	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<TVPColumnsOrder> daoPojos = dao.queryAll(new DalHints());
		daoPojos.get(0).setColumn1("Update0");
		daoPojos.get(1).setColumn1("Update1");
		daoPojos.get(2).setColumn1("Update2");
		dao.batchUpdate(hints, daoPojos);
		List<TVPColumnsOrder> newPojos = dao.queryAll(new DalHints());
		assertEquals("Update0", newPojos.get(0).getColumn1());
		assertEquals("Update1", newPojos.get(1).getColumn1());
		assertEquals("Update2", newPojos.get(2).getColumn1());
	}
}
