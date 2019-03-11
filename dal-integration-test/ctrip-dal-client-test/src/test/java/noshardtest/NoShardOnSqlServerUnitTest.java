package noshardtest;


import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import dao.noshard.NoShardOnSqlServerDao;
import entity.SqlServerPeopleTable;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * JUnit test of PeopleGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
 **/
public class NoShardOnSqlServerUnitTest {

    private static final String DATA_BASE = "noShardTestOnSqlServer";

    private static DalClient client = null;
    private static NoShardOnSqlServerDao dao = null;
    private static Logger log = LoggerFactory.getLogger(NoShardOnSqlServerDao.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "DalConfigForSpt/Dal.config");
        client = DalClientFactory.getClient(DATA_BASE);
        dao = new NoShardOnSqlServerDao();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DalClientFactory.shutdownFactory();
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


        List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(3);
        for (int i = 0; i < 6; i++) {
            SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
            daoPojo.setPeopleID(Long.valueOf(i) + 1);
            daoPojo.setName("Initial_" + i);
            daoPojo.setCityID(i + 20);
            daoPojo.setProvinceID(i + 30);
            daoPojo.setCountryID(i + 40);
            daoPojos1.add(daoPojo);
        }
        dao.insert(new DalHints(), daoPojos1);
    }

    private SqlServerPeopleTable createPojo(int index) {
        SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();

        //daoPojo.setId(index);
        //daoPojo set not null field

        return daoPojo;
    }

    private void changePojo(SqlServerPeopleTable daoPojo) {
        // Change a field to make pojo different with original one
    }

    private void changePojos(List<SqlServerPeopleTable> daoPojos) {
        for (SqlServerPeopleTable daoPojo : daoPojos)
            changePojo(daoPojo);
    }

    private void verifyPojo(SqlServerPeopleTable daoPojo) {
        //assert changed value
    }

    private void verifyPojos(List<SqlServerPeopleTable> daoPojos) {
        for (SqlServerPeopleTable daoPojo : daoPojos)
            verifyPojo(daoPojo);
    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
//		Thread.sleep(5000);
    }

    @Test
    public void testExecInsertWithIndex() throws SQLException {
        Map<String, ?> result = null;
        String callSql = "exec spA_people_i @PeopleID=?, @Name=?, @CityID=?";

        StatementParameters parameter = new StatementParameters();
        int index = 1;
        parameter.registerInOut(index++, Types.BIGINT, null);
        parameter.set(index++, Types.VARCHAR, "testExecInsertWithIndex");
        parameter.set(index++, Types.INTEGER, 123);

        DalHints hints = new DalHints();
        result = client.call(callSql, parameter, hints);
        assertEquals(1, result.size());
        assertEquals("testExecInsertWithIndex", dao.queryByPk(7L,null).getName());
        assertEquals(7L,(long)parameter.get(0).getValue());
    }

    @Test
    public void testExecInsertWithName() throws SQLException {
        Map<String, ?> result = null;
        String callSql = "exec spA_people_i @PeopleID=?, @Name=?, @CityID=?";

        StatementParameters parameter = new StatementParameters();
        parameter.set("Name", Types.VARCHAR, "testExecInsertWithName");
        parameter.registerInOut("PeopleID", Types.BIGINT, null);
        parameter.set("CityID", Types.INTEGER, 123);

        DalHints hints = new DalHints();
        result = client.call(callSql, parameter, hints);
        assertEquals(1, result.size());
        assertEquals(7l, result.get("PeopleID"));
        assertEquals("testExecInsertWithName", dao.queryByPk(7l,null).getName());
        assertEquals(7L,(long)parameter.get(1).getValue());
    }


    @Test
    public void testDalTableDaoQueryTop() throws Exception{
        Integer cityid=20;
        Integer count=2;
        List<SqlServerPeopleTable> list=dao.queryTop(cityid,count,null);
        assertEquals(2,list.size());
    }

    @Test
    public void testDalTableDaoQueryFrom() throws Exception{
        Integer cityid=20;

        List<SqlServerPeopleTable> list=dao.queryFromWithOrderby(cityid,0,2,null);
        assertEquals(2,list.size());

        List<SqlServerPeopleTable> list2=dao.queryFromWithOrderby(cityid,1,2,null);
        assertEquals(2,list2.size());

        try {
            List<SqlServerPeopleTable> list3 = dao.queryFromWithoutOrderby(cityid, 0, 2, null);
            fail();
        }catch (Exception e){

        }

        try {
            List<SqlServerPeopleTable> list4 = dao.queryFromWithoutOrderby(cityid, 1, 2, null);
            fail();
        }catch (Exception e){

        }
    }

    @Test
    public void testFreeSqlBuilderParameterIndex() throws Exception {
        List<Integer> CityID = new ArrayList<>();
        CityID.add(30);
        CityID.add(31);
        CityID.add(22);
        List<SqlServerPeopleTable> daoPojos = dao.testFreeSqlBuilderParameterIndex("Initial_2", CityID, 3l, null);
        assertEquals(1, daoPojos.size());
    }

    @Test
    public void testFreeSqlBuilderWithDiscontinuedParameterIndex() throws Exception {
        List<Integer> CityID = new ArrayList<>();
        CityID.add(30);
        CityID.add(31);
        CityID.add(22);
        List<SqlServerPeopleTable> daoPojos = dao.testFreeSqlBuilderWithDiscontinuedParameterIndex("Initial_2", CityID, 3l, null);
        assertEquals(1, daoPojos.size());
    }

    @Test
    public void testFreeSqlBuilderParameterIndexNotIn() throws Exception {
        Integer Age = 22;
        List<SqlServerPeopleTable> daoPojos = dao.testFreeSqlBuilderParameterIndexNotIn("Initial_2", Age, 3l, null);
        assertEquals(1, daoPojos.size());
    }

    @Test
    public void testCount() throws Exception {
        int affected = dao.count(new DalHints());
        assertEquals(6, affected);
    }

//	@Test
//	public void testDelete1() throws Exception {
//	    DalHints hints = new DalHints();
//		SqlServerPeopleTable daoPojo = createPojo(1);
//		int affected = dao.delete(hints, daoPojo); 
//		assertEquals(1, affected);
//	}
//	
//	@Test
//	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<SqlServerPeopleTable> daoPojos = dao.queryAll(null);
//		int[] affected = dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testBatchDelete() throws Exception {
//		DalHints hints = new DalHints();
//		List<SqlServerPeopleTable> daoPojos = dao.queryAll(null);
//		int[] affected = dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testQueryAll() throws Exception {
//		List<SqlServerPeopleTable> list = dao.queryAll(new DalHints());
//		assertEquals(10, list.size());
//	}
//

    @Test
    public void testInsert1SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
        daoPojo.setPeopleID(10l);
        daoPojo.setName("insert");
        daoPojo.setCityID(24);
        daoPojo.setProvinceID(34);
        daoPojo.setCountryID(44);
        dao.insert(hints.setIdentityBack(), daoPojo);
//		assertEquals(1, affected);
        assertEquals(7, daoPojo.getPeopleID().intValue());
    }

    @Test
    public void testInsert2SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints());
        dao.insert(hints.setIdentityBack(), daoPojos);
        for (int i = 0; i < daoPojos.size(); i++) {
            assertEquals(i + 7, daoPojos.get(i).getPeopleID().intValue());
        }
    }


    @Test
    public void testInsertSetIdentityBack() throws Exception {
        KeyHolder keyHolder = new KeyHolder();
        SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
        daoPojo.setName("insert");
        dao.insert(new DalHints().setIdentityBack(), keyHolder, daoPojo);
        assertEquals(7L, keyHolder.getKey());
        assertEquals(7, daoPojo.getPeopleID().intValue());
    }

    @Test
    public void testInsertListSetIdentityBack() throws Exception {
        KeyHolder keyHolder = new KeyHolder();
        List<SqlServerPeopleTable> daoPojos = dao.queryAll(null);

        dao.insert(new DalHints().setIdentityBack(), keyHolder, daoPojos);
        int i = 0;
        for (SqlServerPeopleTable pojo : daoPojos) {
            assertEquals(pojo.getPeopleID().intValue(), keyHolder.getKey(i++).intValue());
        }
    }

    @Test
    public void testInsert3() throws Exception {
        DalHints hints = new DalHints();
        List<SqlServerPeopleTable> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
//			daoPojo.setPeopleID(20l+i*2);
            daoPojo.setCityID(i + 20);
            if (i % 2 == 0)
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
//					List<SqlServerPeopleTable> ret;
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
//		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints());
//		int[] affected = dao.insert(hints, keyHolder, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		assertEquals(10, keyHolder.size());
//	}
//	
    @Test
    public void testInsert5() throws Exception {
        DalHints hints = new DalHints();
        List<SqlServerPeopleTable> daoPojos = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
            daoPojo.setPeopleID(20l + i * 2);
            daoPojo.setCityID(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
            else
                daoPojo.setName("Initial_Shard_1" + i);
            daoPojos.add(daoPojo);
        }
        dao.batchInsert(hints, daoPojos);

        SqlServerPeopleTable ret = new SqlServerPeopleTable();
        ret = dao.queryByPk(12, new DalHints());
        assertNotNull(ret);
    }

    //
    @Test
    public void testQueryAll() throws Exception {
        List<SqlServerPeopleTable> list = dao.queryAll(new DalHints().selectByNames());
        assertEquals(6, list.size());
    }

    @Test
    public void testQueryAllByPage() throws Exception {
        DalHints hints = new DalHints();
        int pageSize = 100;
        int pageNo = 1;
        List<SqlServerPeopleTable> list = dao.queryAllByPage(pageNo, pageSize, hints.selectByNames());
        assertEquals(6, list.size());
    }

    @Test
    public void testQueryByPk1() throws Exception {
        Number id = 1;
        DalHints hints = new DalHints();
        SqlServerPeopleTable affected = dao.queryByPk(id, hints.selectByNames());
        assertNotNull(affected);
    }

    @Test
    public void testQueryByPk2() throws Exception {
        SqlServerPeopleTable pk = createPojo(1);
        pk.setPeopleID(1l);
        DalHints hints = new DalHints();
        SqlServerPeopleTable affected = dao.queryByPk(pk, hints.selectByNames());
        assertNotNull(affected);
    }

    @Test
    public void testQueryLike() throws Exception {
        SqlServerPeopleTable sample = new SqlServerPeopleTable();
        sample.setName("Initial_0");
        List<SqlServerPeopleTable> list = dao.queryLike(sample, new DalHints().selectByNames());
        assertEquals(1, list.size());

        try {
            SqlServerPeopleTable nullFieldsample = new SqlServerPeopleTable();
            list = dao.queryLike(nullFieldsample, new DalHints().selectByNames());
            assertEquals(6, list.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try{
            list.clear();
            list=dao.queryLike(null,null);
            fail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryBy() throws Exception {
        SqlServerPeopleTable sample = new SqlServerPeopleTable();
        sample.setName("Initial_0");
        List<SqlServerPeopleTable> list = dao.queryBy(sample, new DalHints().selectByNames());
        assertEquals(1, list.size());

        try {
            SqlServerPeopleTable nullFieldsample = new SqlServerPeopleTable();
            list = dao.queryBy(nullFieldsample, new DalHints().selectByNames());
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            list.clear();
            list=dao.queryBy(null,null);
            fail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdate1() throws Exception {
        DalHints hints = new DalHints();
        SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
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
//		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.update(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
//	}
//	
    @Test
    public void testBatchUpdate() throws Exception {
        DalHints hints = new DalHints();
        List<SqlServerPeopleTable> daoPojos = new ArrayList<>();
        SqlServerPeopleTable pojo1 = new SqlServerPeopleTable();
        pojo1.setPeopleID(1L);
        pojo1.setName("batchUpdate1");
        daoPojos.add(pojo1);

        SqlServerPeopleTable pojo2 = new SqlServerPeopleTable();
        pojo2.setPeopleID(2L);
        pojo2.setCityID(300);
        daoPojos.add(pojo2);
        dao.batchUpdate(hints, daoPojos);

        pojo1 = dao.queryByPk(1L, null);
        assertEquals("batchUpdate1", pojo1.getName());
        assertNotNull(pojo1.getCityID());
        assertNotNull(pojo1.getCountryID());
        assertNotNull(pojo1.getProvinceID());

        pojo2 = dao.queryByPk(2L, null);
        assertEquals(300, pojo2.getCityID().intValue());
        assertNotNull(pojo1.getName());
        assertNotNull(pojo1.getCountryID());
        assertNotNull(pojo1.getProvinceID());
    }

    @Test
    public void testDalColumnMapRowMapper() throws Exception {
        List<Map<String, Object>> ret = dao.testDalColumnMapRowMapper(new DalHints());
        assertEquals(20, ret.get(0).get("CityID"));
        assertEquals("Initial_0", ret.get(0).get("Name"));
    }

    @Test
    public void testDalColumnMapRowMapperWithAlias() throws Exception {
        List<Map<String, Object>> ret = dao.testDalColumnMapRowMapperWithAlias(new DalHints());
        assertEquals(20, ret.get(0).get("c"));
        assertEquals("Initial_0", ret.get(0).get("n"));
    }

    @Test
    public void testTransPass() throws Exception {
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                SqlServerPeopleTable ret = dao.queryByPk(1,
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

    /*@Test
    public void testTransSetRollback() throws Exception {
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                SqlServerPeopleTable pojo = new SqlServerPeopleTable();
                pojo.setPeopleID(2l);
                dao.delete(new DalHints(), pojo);
//                DalTransactionManager.setRollbackOnly();
                SqlServerPeopleTable ret = dao.queryByPk(1,
                        new DalHints());
                ret.setCityID(2000);
                dao.update(new DalHints(), ret);
                dao.insert(new DalHints(), pojo);

				return true;
            }
        };
        try {
            client.execute(command, new DalHints());
        } catch (Exception e) {
        }

        assertEquals(21, dao.queryByPk(2, new DalHints()).getCityID().intValue());
        assertEquals(6, dao.count(new DalHints()));
    }*/

    @Test
    public void testTransFail() throws Exception {
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                SqlServerPeopleTable pojo = new SqlServerPeopleTable();
                pojo.setPeopleID(2l);
                dao.delete(new DalHints(), pojo);
                SqlServerPeopleTable ret = dao.queryByPk(1,
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
        } catch (Exception e) {
        }

        assertEquals(21, dao.queryByPk(2, new DalHints()).getCityID().intValue());
        assertEquals(6, dao.count(new DalHints()));
    }

    @Test
    public void testTransCommandsPass() throws Exception {
        List<DalCommand> cmds = new LinkedList<DalCommand>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                SqlServerPeopleTable ret = dao.queryByPk(1,
                        new DalHints());
                ret.setCityID(1000);
                dao.update(new DalHints(), ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                SqlServerPeopleTable pojo = new SqlServerPeopleTable();
                pojo.setPeopleID(2l);
                dao.delete(new DalHints(), pojo);
                SqlServerPeopleTable ret = dao.queryByPk(3,
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

    /*@Test
    public void testTransCommandSetRollback() throws Exception {
        List<DalCommand> cmds = new LinkedList<DalCommand>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                SqlServerPeopleTable ret = dao.queryByPk(1,
                        new DalHints());
                ret.setCityID(1000);
                dao.update(new DalHints(), ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                SqlServerPeopleTable pojo = new SqlServerPeopleTable();
                pojo.setPeopleID(2l);
                dao.delete(new DalHints(), pojo);
//                DalTransactionManager.setRollbackOnly();
                SqlServerPeopleTable ret = dao.queryByPk(3,
                        new DalHints());
                ret.setCityID(2000);
                dao.update(new DalHints(), ret);
                dao.insert(new DalHints(), pojo);

				return true;
            }
        });
        try {
            client.execute(cmds, new DalHints());
        } catch (Exception e) {
        }
        assertEquals(20, dao.queryByPk(1, new DalHints()).getCityID().intValue());
        assertEquals(21, dao.queryByPk(2, new DalHints()).getCityID().intValue());
        assertEquals(22, dao.queryByPk(3, new DalHints()).getCityID().intValue());
        assertEquals(6, dao.count(new DalHints()));
    }*/

    @Test
    public void testTransCommandsFail() throws Exception {
        List<DalCommand> cmds = new LinkedList<DalCommand>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                SqlServerPeopleTable ret = dao.queryByPk(1,
                        new DalHints());
                ret.setCityID(1000);
                dao.update(new DalHints(), ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                SqlServerPeopleTable pojo = new SqlServerPeopleTable();
                pojo.setPeopleID(2l);
                dao.delete(new DalHints(), pojo);
                SqlServerPeopleTable ret = dao.queryByPk(3,
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
        } catch (Exception e) {
        }
        assertEquals(20, dao.queryByPk(1, new DalHints()).getCityID().intValue());
        assertEquals(21, dao.queryByPk(2, new DalHints()).getCityID().intValue());
        assertEquals(22, dao.queryByPk(3, new DalHints()).getCityID().intValue());
        assertEquals(6, dao.count(new DalHints()));
    }
}
