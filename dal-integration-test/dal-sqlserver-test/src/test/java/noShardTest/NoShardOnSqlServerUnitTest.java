package noShardTest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalDirectClient;
import org.junit.*;
import static org.junit.Assert.*;



import com.ctrip.platform.dal.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.Soundbank;


/**
 * JUnit test of PeopleGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class NoShardOnSqlServerUnitTest {

	private static final String DATA_BASE = "noShardTestOnSqlServer";

	private static DalClient client = null;
	private static NoShardOnSqlServerDao dao = null;
	private static Logger log= LoggerFactory.getLogger(NoShardOnSqlServerDao.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		* Initialize DalClientFactory.
		* The Dal.config can be specified from class-path or local file path.
		* One of follow three need to be enabled.
		**/
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
//		DalClientFactory.warmUpConnections();
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new NoShardOnSqlServerDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
//		for(int i = 0; i < 10; i++) {
//			PeopleGen daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		
		dao.test_def_update(new DalHints());

		
		List<NoShardOnSqlServerGen> daoPojos1 = new ArrayList<NoShardOnSqlServerGen>(3);
		for(int i=0;i<6;i++)
		{
			NoShardOnSqlServerGen daoPojo = new NoShardOnSqlServerGen();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Initial_"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints(), daoPojos1);
	}
	
	private NoShardOnSqlServerGen createPojo(int index) {
		NoShardOnSqlServerGen daoPojo = new NoShardOnSqlServerGen();

		//daoPojo.setId(index);
		//daoPojo set not null field
		
		return daoPojo;
	}

	private void changePojo(NoShardOnSqlServerGen daoPojo) {
		// Change a field to make pojo different with original one
	}
	
	private void changePojos(List<NoShardOnSqlServerGen> daoPojos) {
		for(NoShardOnSqlServerGen daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(NoShardOnSqlServerGen daoPojo) {
		//assert changed value
	}
	
	private void verifyPojos(List<NoShardOnSqlServerGen> daoPojos) {
		for(NoShardOnSqlServerGen daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
//		Thread.sleep(5000);
	}



	@Test
	public void testCount() throws Exception {
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
	}
	
//	@Test
//	public void testDelete1() throws Exception {
//	    DalHints hints = new DalHints();
//		NoShardOnSqlServerGen daoPojo = createPojo(1);
//		int affected = dao.delete(hints, daoPojo); 
//		assertEquals(1, affected);
//	}
//	
//	@Test
//	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<NoShardOnSqlServerGen> daoPojos = dao.queryAll(null);
//		int[] affected = dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testBatchDelete() throws Exception {
//		DalHints hints = new DalHints();
//		List<NoShardOnSqlServerGen> daoPojos = dao.queryAll(null);
//		int[] affected = dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testQueryAll() throws Exception {
//		List<NoShardOnSqlServerGen> list = dao.queryAll(new DalHints());
//		assertEquals(10, list.size());
//	}
//	
//	@Test
//	public void testInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		NoShardOnSqlServerGen daoPojo = new NoShardOnSqlServerGen();
//		daoPojo.setPeopleID(10l);  
//		daoPojo.setName("insert");
//		daoPojo.setCityID(24);
//		daoPojo.setProvinceID(34);
//		daoPojo.setCountryID(44);
//		int affected = dao.insert(hints.enableIdentityInsert(), daoPojo);
////		assertEquals(1, affected);
//		NoShardOnSqlServerGen ret=dao.queryByPk(10l, null);
//		assertNotNull(ret);
//	}
//	
//	@Test
//	public void testInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		List<NoShardOnSqlServerGen> daoPojos = dao.queryAll(new DalHints());
//		int[] affected = dao.insert(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		List<NoShardOnSqlServerGen> daoPojos = new ArrayList<>(
				6);
		for (int i = 0; i < 6; i++) {
			NoShardOnSqlServerGen daoPojo = new NoShardOnSqlServerGen();
//			daoPojo.setPeopleID(20l+i*2);
			daoPojo.setCityID(i + 20);
			if(i%2==0)
//			daoPojo.setName("Initial_Shard_0" + i);
//			else
//				daoPojo.setName("Initial_Shard_1" + i);
			daoPojos.add(daoPojo);
		}
		int[] affected = dao.insert(hints, daoPojos);
	}

//	@Test
//	public void testTimeout() throws Exception {
//			int i=1;
//			while(1==1) {
//				try {
//					log.info(String.format("Test %d started", i));
//					List<NoShardOnSqlServerGen> ret;
//
//					try (Connection conn= DalClientFactory.getDalConfigure().getLocator().getConnection("DalServiceDB")) {
//						System.out.println("Connection: " + conn);
//					}
//
//					if (i == 1 ) {
//						log.info("10 seconds query...");
//						log.info("query ",dao.test_timeout(20,new DalHints().timeout(60)));
//
//					} else {
//						log.info("1 seconds query...");
//						log.info("query ",dao.test_timeout(1,new DalHints().timeout(60)));
//
////						assertEquals(1, ret.size());
//					}
//					log.info(String.format("Test %d passed", i));
//				}
//				catch(Exception e){
//						log.error(String.format("Test %d failed", i), e);
//					}
//					i++;
//				log.info("sleep 1 second...");
//				Thread.sleep(1000);
//				}
//	}


//	@Test
//	public void testInsert4() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		List<NoShardOnSqlServerGen> daoPojos = dao.queryAll(new DalHints());
//		int[] affected = dao.insert(hints, keyHolder, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		assertEquals(10, keyHolder.size());
//	}
//	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<NoShardOnSqlServerGen> daoPojos = new ArrayList<NoShardOnSqlServerGen>(
				6);
		for (int i = 0; i < 6; i++) {
			NoShardOnSqlServerGen daoPojo = new NoShardOnSqlServerGen();
			daoPojo.setPeopleID(20l+i*2);
			daoPojo.setCityID(i + 20);
			if(i%2==0)
			daoPojo.setName("Initial_Shard_0" + i);
//			else
//				daoPojo.setName("Initial_Shard_1" + i);
			daoPojos.add(daoPojo);
		}
		int[] affected = dao.batchInsert(hints, daoPojos);
//		assertEquals(6, affected);
		
//		NoShardOnMySqlGen ret=new NoShardOnMySqlGen();
//		ret=dao.queryByPk(30, new DalHints());
//		assertNotNull(ret);
	}
//	
//	@Test
//	public void testQueryAllByPage() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
//		List<NoShardOnSqlServerGen> list = dao.queryAllByPage(pageNo, pageSize, hints);
//		assertEquals(10, list.size());
//	}
//	
//	@Test
//	public void testQueryByPk1() throws Exception {
//		Number id = 1;
//		DalHints hints = new DalHints();
//		NoShardOnSqlServerGen affected = dao.queryByPk(id, hints);
//		assertNotNull(affected);
//	}
//	
//	@Test
//	public void testQueryByPk2() throws Exception {
//		NoShardOnSqlServerGen pk = createPojo(1);
//		DalHints hints = new DalHints();
//		NoShardOnSqlServerGen affected = dao.queryByPk(pk, hints);
//		assertNotNull(affected);
//	}
//
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		NoShardOnSqlServerGen daoPojo = new NoShardOnSqlServerGen();
		daoPojo.setPeopleID(1L);
		daoPojo.setName("update");
		dao.update(hints, daoPojo);

		daoPojo = dao.queryByPk(1L, null);
		assertNotNull(daoPojo.getCityID());
		assertNotNull(daoPojo.getCountryID());
		assertNotNull(daoPojo.getProvinceID());
	}
//	
//	@Test
//	public void testUpdate2() throws Exception {
//		DalHints hints = new DalHints();
//		List<NoShardOnSqlServerGen> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.update(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
//	}
//	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<NoShardOnSqlServerGen> daoPojos = new ArrayList<>();
		NoShardOnSqlServerGen pojo1=new NoShardOnSqlServerGen();
		pojo1.setPeopleID(1L);
		pojo1.setName("batchUpdate1");
		daoPojos.add(pojo1);

		NoShardOnSqlServerGen pojo2=new NoShardOnSqlServerGen();
		pojo2.setPeopleID(2L);
		pojo2.setCityID(300);
		daoPojos.add(pojo2);
		dao.batchUpdate(hints, daoPojos);

		pojo1=dao.queryByPk(1L,null);
		assertEquals("batchUpdate1",pojo1.getName());
		assertNotNull(pojo1.getCityID());
		assertNotNull(pojo1.getCountryID());
		assertNotNull(pojo1.getProvinceID());

		pojo2=dao.queryByPk(2L,null);
		assertEquals(300,pojo2.getCityID().intValue());
		assertNotNull(pojo1.getName());
		assertNotNull(pojo1.getCountryID());
		assertNotNull(pojo1.getProvinceID());
	}

@Test
public void testTransPass() throws Exception {
	DalCommand command = new DalCommand() {
		@Override
		public boolean execute(DalClient client) throws SQLException {
			NoShardOnSqlServerGen ret = dao.queryByPk(1,
					new DalHints());
			ret.setCityID(1000);
			dao.update(new DalHints(), ret);
			return true;
		}
	};
	try {
		client.execute(command, new DalHints());
	} catch (Exception e) {
		e.printStackTrace();
	}

	assertEquals(1000, dao.queryByPk(1l, new DalHints()).getCityID().intValue());
}

	@Test
	public void testTransFail() throws Exception{
		DalCommand command = new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				NoShardOnSqlServerGen pojo=new NoShardOnSqlServerGen();
				pojo.setPeopleID(2l);
				dao.delete(new DalHints(), pojo);
				NoShardOnSqlServerGen ret = dao.queryByPk(1,
						new DalHints());
				ret.setCityID(2000);
				dao.update(new DalHints(), ret);
				dao.insert(new DalHints(), pojo);
				throw new SQLException();
//				return true;
			}
		};
		try {
			client.execute(command, new DalHints());
			fail();
		} catch (Exception e) {}

		assertEquals(21, dao.queryByPk(2, new DalHints()).getCityID().intValue());
		assertEquals(6, dao.count(new DalHints()));
	}

	@Test
	public void testTransCommandsPass() throws Exception {
		List<DalCommand> cmds = new LinkedList<DalCommand>();
		cmds.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				NoShardOnSqlServerGen ret = dao.queryByPk(1,
						new DalHints());
				ret.setCityID(1000);
				dao.update(new DalHints(), ret);
				return true;
			}
		});
		cmds.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				NoShardOnSqlServerGen pojo=new NoShardOnSqlServerGen();
				pojo.setPeopleID(2l);
				dao.delete(new DalHints(), pojo);
				NoShardOnSqlServerGen ret = dao.queryByPk(3,
						new DalHints());
				ret.setCityID(2000);
				dao.update(new DalHints(), ret);
//				pojo.setPeopleID(4l);
				pojo.setCityID(100);
				pojo.setName("trans");
//				pojo.setProvinceID(200);
//				pojo.setCountryID(300);
				dao.insert(new DalHints(), pojo);
				return true;
			}
		});

		try {
			client.execute(cmds, new DalHints());
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(1000, dao.queryByPk(1, new DalHints()).getCityID().intValue());
		assertEquals(2000, dao.queryByPk(3, new DalHints()).getCityID().intValue());
		assertEquals(100, dao.queryByPk(7, new DalHints()).getCityID().intValue());
		assertEquals(6, dao.count(new DalHints()));
	}

	@Test
	public void testTransCommandsFail() throws Exception {
		List<DalCommand> cmds = new LinkedList<DalCommand>();
		cmds.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				NoShardOnSqlServerGen ret = dao.queryByPk(1,
						new DalHints());
				ret.setCityID(1000);
				dao.update(new DalHints(), ret);
				return true;
			}
		});
		cmds.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				NoShardOnSqlServerGen pojo=new NoShardOnSqlServerGen();
				pojo.setPeopleID(2l);
				dao.delete(new DalHints(), pojo);
				NoShardOnSqlServerGen ret = dao.queryByPk(3,
						new DalHints());
				ret.setCityID(2000);
				dao.update(new DalHints(), ret);
				dao.insert(new DalHints(), pojo);
				throw new SQLException();
//				return true;
			}
		});
		try {
			client.execute(cmds, new DalHints());
			fail();
		} catch (Exception e) {}
		assertEquals(20, dao.queryByPk(1, new DalHints()).getCityID().intValue());
		assertEquals(21, dao.queryByPk(2, new DalHints()).getCityID().intValue());
		assertEquals(22, dao.queryByPk(3, new DalHints()).getCityID().intValue());
		assertEquals(6, dao.count(new DalHints()));
	}
}
