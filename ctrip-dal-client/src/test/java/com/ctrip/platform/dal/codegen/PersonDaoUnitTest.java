package com.ctrip.platform.dal.codegen;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;

/**
 * JUnit test of PersonDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class PersonDaoUnitTest {

	private static final String DATABASE_NAME_MYSQL = "MySqlSimpleShard";

	private static PersonDao dao = null;

    private final static String TABLE_NAME = "person";
    private final static int mod = 2;
    private final static int tableMod = 4;
    
    //Drop the the table
    private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME + "_%d";
    
    //Create the the table
    // Note that id is UNSIGNED int, which maps to Long in java when using rs.getObject()
    private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"_%d("
            + "PeopleID int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
            + "CityID int,"
            + "ProvinceID int,"
            + "CountryID int,"
            + "Name VARCHAR(64) not null, "
            + "DataChange_LastTime timestamp default CURRENT_TIMESTAMP)";
    
    private static DalClient clientMySql;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
        clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        DalHints hints = new DalHints();
        String[] sqls = null;
        // For SQL server
        hints = new DalHints();
        for(int i = 0; i < mod; i++) {
            for(int j = 0; j < tableMod; j++) {
                sqls = new String[] { 
                        String.format(DROP_TABLE_SQL_MYSQL_TPL, j), 
                        String.format(CREATE_TABLE_SQL_MYSQL_TPL, j)};
                clientMySql.batchUpdate(sqls, hints.inShard(i));
            }
        }
        dao = new PersonDao();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DalHints hints = new DalHints();
        String[] sqls = null;
        //For Sql Server
        hints = new DalHints();
        for(int i = 0; i < mod; i++) {
            sqls = new String[tableMod];
            for(int j = 0; j < tableMod; j++) {
                sqls[j] = String.format(DROP_TABLE_SQL_MYSQL_TPL, j);
            }
            clientMySql.batchUpdate(sqls, hints.inShard(i));
        }
    }	
	
	@Before
	public void setUp() throws Exception {
		tearDown();
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				List<Person> daoPojo = createPojos(i, j);
	
				try {
					dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<Person> createPojos(int countryID, int cityID) {
		List<Person> pl = new ArrayList<>();
		
		Person daoPojo;

		for (int i = 0; i < 4; i++) {
			daoPojo = new Person();
			daoPojo.setCountryID(countryID);
			daoPojo.setCityID(cityID);
			daoPojo.setName("Test");
			daoPojo.setPeopleID(i+1);
			pl.add(daoPojo);
		}
		
		return pl;
	}

	private Person createPojo(int countryID, int cityID, int id) {
		Person daoPojo;

		daoPojo = new Person();
		daoPojo.setCountryID(countryID);
		daoPojo.setCityID(cityID);
		daoPojo.setName("Test");
		daoPojo.setPeopleID(id);
		
		return daoPojo;
	}

	private void changePojo(Person daoPojo) {
		daoPojo.setName(daoPojo.getName() + " changed");
	}
	
	private void changePojos(List<Person> daoPojos) {
		for(Person daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(Person daoPojo) {
		assertEquals("Test changed", daoPojo.getName());
	}
	
	private void verifyPojos(List<Person> daoPojos) {
		for(Person daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				dao.delete(new DalHints().inShard(i).inTableShard(j), dao.queryAll(new DalHints().inShard(i).inTableShard(j)));
			}
		}
	} 
	
	@Test
	public void testCount() throws Exception {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				int affected = dao.count(new DalHints().inShard(i).inTableShard(j));
				assertEquals(4, affected);
			}
		}
	}
	
	@Test
	public void testCountAllShards() throws Exception {
		for(int j = 0; j < 4; j++) {
			int affected = dao.count(new DalHints().inAllShards().inTableShard(j));
			assertEquals(8, affected);
		}
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
	    
	    List<Person> daoPojos = createPojos(1, 1);

	    hints = new DalHints();
		int affected = dao.delete(hints, daoPojos.get(0)); 
		assertEquals(1, affected);
		
		hints = new DalHints().asyncExecution();
		affected = dao.delete(hints, daoPojos.get(1)); 
		assertEquals(0, affected);
		assertArrayEquals(new int[]{1}, hints.getIntArrayResult());
	}
	
	@Test
	public void testDelete2() throws Exception {
		// All shards normal
		DalHints hints = new DalHints();
		List<Person> daoPojos = dao.queryAll(new DalHints().inAllShards().inTableShard(1));
		int[] affected = dao.delete(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1},  affected);
		
		// In shard normal
		hints = new DalHints();
		daoPojos = dao.queryAll(hints.inShard(0).inTableShard(2));
		affected = dao.delete(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,},  affected);
		
		// In shard async
		hints = new DalHints();
		daoPojos = dao.queryAll(hints.inShard(1).inTableShard(2));
		hints = new DalHints();
		affected = dao.delete(hints.asyncExecution(), daoPojos);
		assertNull(affected);
		assertArrayEquals(new int[]{1,1,1,1,},  hints.getIntArrayResult());
		
		// All shards async
		hints = new DalHints();
		daoPojos = dao.queryAll(hints.inAllShards().inTableShard(3));
		hints = new DalHints();
		affected = dao.delete(hints.asyncExecution(), daoPojos);
		assertNull(affected);
		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1},  hints.getIntArrayResult());
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		//all shards
		DalHints hints = new DalHints();
		List<Person> daoPojos = dao.queryAll(hints.inAllShards().inTableShard(1));
		hints = new DalHints();
		int[] affected = dao.batchDelete(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1},  affected);
		
		//all shards async
		hints = new DalHints();
		daoPojos = dao.queryAll(hints.inAllShards().inTableShard(0));
		hints = new DalHints();
		affected = dao.batchDelete(hints.asyncExecution(), daoPojos);
		assertNull(affected);
		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1},  hints.getIntArrayResult());
		
		//shard
		hints = new DalHints();
		daoPojos = dao.queryAll(hints.inShard(0).inTableShard(2));
		hints = new DalHints();
		affected = dao.batchDelete(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1},  affected);

		//shard async
		hints = new DalHints();
		daoPojos = dao.queryAll(hints.inShard(1).inTableShard(2));
		hints = new DalHints();
		affected = dao.batchDelete(hints.asyncExecution(), daoPojos);
		assertNull(affected);
		assertArrayEquals(new int[]{1,1,1,1},  hints.getIntArrayResult());
	}
	
	@Test
	public void testQueryAll() throws Exception {
		DalHints hints;
		List<Person> list;
		// all shards
		hints = new DalHints();
		list = dao.queryAll(hints.inAllShards().inTableShard(1));
		assertEquals(8, list.size());
		
		// all shards async
		hints = new DalHints();
		list = dao.queryAll(hints.inAllShards().asyncExecution().inTableShard(1));
		assertNull(list);
		assertEquals(8, hints.getListResult().size());
		
		// in shard
		hints = new DalHints();
		list = dao.queryAll(hints.inShard(1).inTableShard(1));
		assertEquals(4, list.size());
		
		// in shard async
		hints = new DalHints();
		list = dao.queryAll(hints.inShard(1).asyncExecution().inTableShard(1));
		assertNull(list);
		assertEquals(4, hints.getListResult().size());
		
	}
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		List<Person> daoPojos;
		int affected;
		
		// normal
		daoPojos = createPojos(1, 1);
		hints = new DalHints();
		affected = dao.insert(hints, daoPojos.get(0));
		assertEquals(1, affected);
		hints = new DalHints();
		assertEquals(5,  dao.count(hints.inShard(1).inTableShard(1)));
		
		// override
		daoPojos = createPojos(1, 1);
		hints = new DalHints();
		affected = dao.insert(hints.inShard(0).inTableShard(3), daoPojos.get(0));
		assertEquals(1, affected);
		hints = new DalHints();
		assertEquals(5,  dao.count(hints.inShard(0).inTableShard(3)));
		
		// normal async
		daoPojos = createPojos(1, 2);
		hints = new DalHints();
		affected = dao.insert(hints.asyncExecution(), daoPojos.get(0));
		assertEquals(0, affected);
		assertEquals(1, hints.getIntResult());
		hints = new DalHints();
		assertEquals(5,  dao.count(hints.inShard(1).inTableShard(2)));
		
		// override async
		daoPojos = createPojos(1, 1);
		hints = new DalHints();
		affected = dao.insert(hints.inShard(1).inTableShard(3).asyncExecution(), daoPojos.get(0));
		assertEquals(0, affected);
		assertEquals(1, hints.getIntResult());
		hints = new DalHints();
		assertEquals(5,  dao.count(hints.inShard(1).inTableShard(3)));
	}
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints;
		List<Person> daoPojos;
		int[] affected;
		
		// normal
		daoPojos = createPojos(1, 1);
		hints = new DalHints();
		affected = dao.insert(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1},  affected);
		
		// normal override
		daoPojos = createPojos(1, 1);
		hints = new DalHints();
		affected = dao.insert(hints.inShard(0).inTableShard(3), daoPojos);
		assertArrayEquals(new int[]{1,1,1,1},  affected);

		// normal async
		daoPojos = createPojos(1, 1);
		hints = new DalHints();
		affected = dao.insert(hints.asyncExecution(), daoPojos);
		assertNull(affected);
		assertArrayEquals(new int[]{1,1,1,1},  hints.getIntArrayResult());
		
		// normal override async
		daoPojos = createPojos(1, 1);
		hints = new DalHints();
		affected = dao.insert(hints.inShard(0).inTableShard(3).asyncExecution(), daoPojos);
		assertNull(affected);
		assertArrayEquals(new int[]{1,1,1,1},  hints.getIntArrayResult());

	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints;
		Person daoPojo;
		int affected;
		KeyHolder keyHolder;
		
		keyHolder = new KeyHolder();
		daoPojo = createPojos(1, 1).get(0);
		hints = new DalHints();
		affected = dao.insert(hints, keyHolder, daoPojo);
		assertEquals(1, affected);
		assertEquals(1, keyHolder.size());
	}

    @Test
    public void testInsert3PkInsertBack() throws Exception {
        DalHints hints;
        Person daoPojo;
        int affected;
        KeyHolder keyHolder;

        keyHolder = new KeyHolder();
        daoPojo = createPojos(1, 1).get(0);
        hints = new DalHints();
        affected = dao.insert(hints, keyHolder, daoPojo);
        assertEquals(1, affected);
        assertEquals(1, keyHolder.size());
        Person p2 = dao.queryByPk(daoPojo, hints);
        assertEquals(daoPojo.getName(), p2.getName());
    }

	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<Person> daoPojos = createPojos(1, 1);
		int[] affected = dao.insert(hints, keyHolder, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,},  affected);
		assertEquals(4, keyHolder.size());
	}

    @Test
    public void testInsert4PkInsertBack() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<Person> daoPojos = createPojos(1, 1);
        int i = 0;
        for(Person p: daoPojos)
            p.setName("test" + i++);

        int[] affected = dao.insert(hints.setIdentityBack(), keyHolder, daoPojos);
        assertArrayEquals(new int[]{1,1,1,1,},  affected);
        assertEquals(4, keyHolder.size());
        for(Person p: daoPojos) {
            Person p2 = dao.queryByPk(p, hints);
            assertEquals(p.getName(), p2.getName());
        }
    }

	@Test
	public void testInsert4PkInsertBack2() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<Person> daoPojos = createPojos(1, 1);
		int i = 0;
		for(Person p: daoPojos) {
			p.setName("test" + i++);
			p.setPeopleID(null);
		}

		int[] affected = dao.insert(hints.setIdentityBack(), keyHolder, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,},  affected);
		assertEquals(4, keyHolder.size());
		for(Person p: daoPojos) {
			Person p2 = dao.queryByPk(p, hints);
			assertEquals(p.getName(), p2.getName());
		}
	}

	@Test
	public void testInsert4PkInsertBack3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<Person> daoPojos = createPojos(1, 1);
		int i = 0;
		for(Person p: daoPojos) {
			p.setName("test" + i++);
			p.setPeopleID(null);
		}

		int[] affected = dao.insert(hints.setIdentityBack().enableIdentityInsert(), keyHolder, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,},  affected);
		assertEquals(4, keyHolder.size());
		for(Person p: daoPojos) {
			Person p2 = dao.queryByPk(p, hints);
			assertEquals(p.getName(), p2.getName());
		}
	}

	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<Person> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(1));
		int[] affected = dao.batchInsert(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1}, affected);
	}
	
    @Test
    public void testInsertAllShard() throws Exception {
        DalHints hints = new DalHints();
        List<Person> daoPojos = dao.queryAll(new DalHints().inAllShards().inTableShard(1));
        daoPojos.addAll(dao.queryAll(new DalHints().inAllShards().inTableShard(2)));
        daoPojos.addAll(dao.queryAll(new DalHints().inAllShards().inTableShard(3)));
        daoPojos.addAll(dao.queryAll(new DalHints().inAllShards().inTableShard(0)));
        int[] affected = dao.batchInsert(hints.sequentialExecute(), daoPojos);
        assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,}, affected);
        
        // Async
        affected = dao.batchInsert(hints.sequentialExecute().asyncExecution(), daoPojos);
        assertNull(affected);
        assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,}, hints.getIntArrayResult());
    }
    
	@Test
	public void testCombinedInsert1() throws Exception {
		DalHints hints = new DalHints();
		List<Person> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(1));
		int affected = dao.combinedInsert(hints, daoPojos);
		assertEquals(4, affected);
	}

    @Test
    public void testCombinedInsert1PkInsertBack() throws Exception {
        DalHints hints = new DalHints();
        List<Person> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(1));
        KeyHolder keyHolder = new KeyHolder();
        int i = 0;
        for(Person p: daoPojos)
            p.setName("test" + i++);
        int affected = dao.combinedInsert(hints.setIdentityBack(), keyHolder, daoPojos);
        assertEquals(4, affected);
        for(Person p: daoPojos) {
            Person p2 = dao.queryByPk(p, hints.inShard(1).inTableShard(1));
            assertEquals(p.getName(), p2.getName());
        }
    }

	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<Person> daoPojos = dao.queryAll(new DalHints().inAllShards().inTableShard(1));
		int affected = dao.combinedInsert(hints, keyHolder, daoPojos);
		assertEquals(8, keyHolder.size());
		assertEquals(8, dao.count(new DalHints().inShard(0).inTableShard(1)));
		assertEquals(8, dao.count(new DalHints().inShard(1).inTableShard(1)));
	}

    @Test
    public void testCombinedInsert2PkInsertBack() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<Person> daoPojos = dao.queryAll(new DalHints().inAllShards().inTableShard(1));
        int i = 0;
        for(Person p: daoPojos)
            p.setName("test" + i++);
        dao.combinedInsert(hints.setIdentityBack(), keyHolder, daoPojos);
        for(Person p: daoPojos) {
            Person p2 = dao.queryByPk(p, hints.inTableShard(1));
            assertEquals(p.getName(), p2.getName());
        }
    }

	@Test
	public void testQueryAllByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 4;
		int pageNo = 1;
		for(int i=0; i< 9; i++)
			dao.insert(hints, createPojos(1, 1));
			
		List<Person> list = dao.queryAllByPage(1, pageSize, hints.inShard(1).inTableShard(1));
		assertEquals(4, list.size());
		
		list = dao.queryAllByPage(2, pageSize, hints.inShard(1).inTableShard(1));
		assertEquals(4, list.size());

		list = dao.queryAllByPage(9, pageSize, hints.inShard(1).inTableShard(1));
		assertEquals(4, list.size());
		
		list = dao.queryAllByPage(11, pageSize, hints.inShard(1).inTableShard(1));
		assertEquals(0, list.size());

		list = dao.queryAllByPage(10, pageSize, hints.inAllShards().inTableShard(1));
		assertEquals(4, list.size());

		list = dao.queryAllByPage(10, pageSize, hints.inAllShards().asyncExecution().inTableShard(1));
		assertNull(list);
		assertEquals(4, hints.getListResult().size());
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1;
		DalHints hints = new DalHints();
		Person affected = dao.queryByPk(id, hints.inShard(1).inTableShard(0));
		assertNotNull(affected);
		
		id = 1;
		hints = new DalHints();
		affected = dao.queryByPk(id, hints.inShard(1).inTableShard(0).asyncExecution());
		assertNull(affected);
		assertNotNull((Person)hints.getResult());

	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		Person pk = new Person();
		pk.setPeopleID(1);
		DalHints hints = new DalHints();
		Person affected = dao.queryByPk(pk, hints.inShard(0).inTableShard(3));
		assertNotNull(affected);
		
		hints = new DalHints();
		affected = dao.queryByPk(pk, hints.inShard(0).inTableShard(3).asyncExecution());
		assertNull(affected);
		assertNotNull((Person)hints.getResult());

	}
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		Person daoPojo = dao.queryByPk(createPojo(1, 1, 1), hints);
		changePojo(daoPojo);
		hints = new DalHints();
		int affected = dao.update(hints, daoPojo);
		assertEquals(1, affected);
		daoPojo = dao.queryByPk(createPojo(1,1,1), null);
		verifyPojo(daoPojo);

		// Async
		hints = new DalHints();
		daoPojo = dao.queryByPk(createPojo(1, 1, 2), hints);
		changePojo(daoPojo);
		hints = new DalHints();
		affected = dao.update(hints.asyncExecution(), daoPojo);
		assertEquals(0, affected);
		assertEquals(1, hints.getIntResult());
		daoPojo = dao.queryByPk(createPojo(1,1,2), null);
		verifyPojo(daoPojo);
	}
	
	@Test
	public void testUpdate2() throws Exception {
		DalHints hints = new DalHints();
		List<Person> daoPojos = dao.queryAll(hints.inShard(1).inTableShard(1));
		changePojos(daoPojos);
		hints = new DalHints();
		int[] affected = dao.update(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1},  affected);
		verifyPojos(dao.queryAll(new DalHints().inShard(1).inTableShard(1)));

		// Async
		hints = new DalHints();
		daoPojos = dao.queryAll(hints.inShard(1).inTableShard(2));
		changePojos(daoPojos);
		hints = new DalHints();
		affected = dao.update(hints.asyncExecution(), daoPojos);
		assertNull(affected);
		assertArrayEquals(new int[]{1,1,1,1},  hints.getIntArrayResult());
		verifyPojos(dao.queryAll(new DalHints().inShard(1).inTableShard(2)));

	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<Person> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(1));
		changePojos(daoPojos);
		hints = new DalHints();
		int[] affected = dao.batchUpdate(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1},  affected);
		verifyPojos(dao.queryAll(new DalHints().inShard(1).inTableShard(1)));
		
		// async
		hints = new DalHints();
		daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(2));
		changePojos(daoPojos);
		hints = new DalHints();
		affected = dao.batchUpdate(hints.asyncExecution(), daoPojos);
		assertNull(affected);
		assertArrayEquals(new int[]{1,1,1,1},  hints.getIntArrayResult());
		verifyPojos(dao.queryAll(new DalHints().inShard(1).inTableShard(2)));
	}
	
	@Test
	public void testdelete() throws Exception {
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(0);
		String name = "Test";
	    int ret = dao.delete(cityIds, name, new DalHints().inShard(1).inTableShard(0));
	    assertEquals(4, ret);
	    
	    // async
		cityIds = new ArrayList<>();
		cityIds.add(1);
		name = "Test";
		DalHints hints = new DalHints();
	    ret = dao.delete(cityIds, name, hints.inShard(1).inTableShard(1).asyncExecution());
	    assertEquals(0, ret);
	    assertEquals(4, hints.getIntResult());
	}
	
	@Test
	public void testinsert() throws Exception {
		Integer CityID = 3;
		String Name = "AAA";
		Integer ProvinceID = 4;
	    int ret = dao.insert(CityID, Name, ProvinceID, new DalHints().inShard(0).inTableShard(1));
	    assertEquals(1, ret);

	    // async
	    DalHints hints = new DalHints();
	    ret = dao.insert(CityID, Name, ProvinceID, hints.asyncExecution().inShard(0).inTableShard(1));
	    assertEquals(0, ret);
	    assertEquals(1, hints.getIntResult());
	    
	}
	
	@Test
	public void testupdate() throws Exception {
		List<Person> pojos = dao.queryAll(new DalHints().inShard(1).inTableShard(1));
		for(Person p: pojos)
			p.setProvinceID(4);
		
		dao.update(null, pojos);
		
		String Name = "Aaa";// Test value here
		Integer ProvinceID = 1;// Test value here
		List<Integer> cityIds = new ArrayList<>();// Test value here
		cityIds.add(1);
		cityIds.add(0);
		
		Integer proviinceId = 4;// Test value here
	    int ret = dao.update(Name, ProvinceID, cityIds, proviinceId, new DalHints().inAllShards().inTableShard(1));
	    assertEquals(4, ret);
	    
	    // async
	    pojos = dao.queryAll(new DalHints().inShard(1).inTableShard(2));
		for(Person p: pojos)
			p.setProvinceID(4);
		
		dao.update(null, pojos);
		
		Name = "Aaa";// Test value here
		ProvinceID = 1;// Test value here
		cityIds = new ArrayList<>();// Test value here
		cityIds.add(1);
		cityIds.add(0);
		cityIds.add(2);
		
		proviinceId = 4;// Test value here
		DalHints hints = new DalHints();
	    ret = dao.update(Name, ProvinceID, cityIds, proviinceId, hints.inAllShards().inTableShard(1).asyncExecution());
	    assertEquals(0, ret);
	    assertEquals(0, hints.getIntResult());
	    
	    ret = dao.update(Name, ProvinceID, cityIds, proviinceId, hints.inAllShards().inTableShard(2).asyncExecution());
	    assertEquals(0, ret);
	    assertEquals(4, hints.getIntResult());

	}
	
	@Test
	public void testfindFirst() throws Exception {
		List<Integer> cityIds = new ArrayList<>();// Test value here
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		String name = "Test";// Test value here
	    Person ret = dao.findFirst(cityIds, name, new DalHints().inAllShards().inTableShard(0));
	    assertNull(ret);
	    
	    ret = dao.findFirst(cityIds, name, new DalHints().inAllShards().inTableShard(1));
	    assertNotNull(ret);

	    ret = dao.findFirst(cityIds, name, new DalHints().inShard(1).inTableShard(2));
	    assertNotNull(ret);

	    ret = dao.findFirst(cityIds, name, new DalHints().inShard(0).inTableShard(3));
	    assertNotNull(ret);

	    DalHints hints = new DalHints();
	    ret = dao.findFirst(cityIds, name, hints.inShard(0).inTableShard(3).asyncExecution());
	    assertNull(ret);
	    assertNotNull(hints.getResult());
	}

	@Test
	public void testfindListByPage() throws Exception {
		List<Integer> ids = new ArrayList<>();
		ids.add(1);
		ids.add(0);
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		
		DalHints hints = new DalHints();
	    List<Person> ret = dao.findListByPage(ids, cityIds, 1, 10, hints.inAllShards().inTableShard(0));
	    assertEquals(0, ret.size());
	    
	    hints = new DalHints();
	    ret = dao.findListByPage(ids, cityIds, 1, 10, hints.inAllShards().inTableShard(1));
	    assertEquals(2, ret.size());
	    
	    hints = new DalHints();
	    ret = dao.findListByPage(ids, cityIds, 1, 10, hints.inAllShards().inTableShard(2));
	    assertEquals(2, ret.size());
	    
	    hints = new DalHints();
	    ret = dao.findListByPage(ids, cityIds, 1, 10, hints.inAllShards().inTableShard(3).asyncExecution());
	    assertNull(ret);
	    assertEquals(2, ((List)hints.getResult()).size());
	}

	@Test
	public void testfindList() throws Exception {
		List<Integer> peopleIds = new ArrayList<>();
		peopleIds.add(1);
		peopleIds.add(2);
		peopleIds.add(3);

		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
	    List<Person> ret = dao.findList(peopleIds, cityIds, new DalHints().inAllShards().inTableShard(1));
	    assertEquals(6, ret.size());
	    
	    ret = dao.findList(peopleIds, cityIds, new DalHints().inShard(1).inTableShard(1));
	    assertEquals(3, ret.size());
	    
	    ret = dao.findList(peopleIds, cityIds, new DalHints().inShard(0).inTableShard(1));
	    assertEquals(3, ret.size());

	    DalHints hints = new DalHints();
	    ret = dao.findList(peopleIds, cityIds, hints.inShard(0).inTableShard(1).asyncExecution());
	    assertNull(ret);
	    assertEquals(3, ((List)hints.getResult()).size());
	    
	}
	
	@Test
	public void testfindSingle() throws Exception {
		Person p = dao.queryByPk(1, new DalHints().inShard(1).inTableShard(1));
		p.setName("AAA");
		dao.update(null, p);
		
		String name = "AAA";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
	    Person ret = dao.findSingle(name, cityIds, new DalHints().inShard(1).inTableShard(1));
	    assertNotNull(ret);
	    
	    ret = dao.findSingle(name, cityIds, new DalHints().inAllShards().inTableShard(1));
	    assertNotNull(ret);
	    
	    ret = dao.findSingle(name, cityIds, new DalHints().inAllShards().inTableShard(0));
	    assertNull(ret);
	    
	    DalHints hints = new DalHints();
	    ret = dao.findSingle(name, cityIds, hints.inAllShards().inTableShard(0).asyncExecution());
	    assertNull(ret);
	    assertNull(hints.getResult());
	    
	    hints = new DalHints();
	    ret = dao.findSingle(name, cityIds, hints.inAllShards().inTableShard(1).asyncExecution());
	    assertNull(ret);
	    assertNotNull(hints.getResult());
	}
	
	@Test
	public void testfindFieldFirst() throws Exception {
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		String name = "Test";// Test value here
	    String ret = dao.findFieldFirst(cityIds, name, new DalHints().inAllShards().inTableShard(1));
	    assertEquals("Test", ret);
	    
	    ret = dao.findFieldFirst(cityIds, name, new DalHints().inAllShards().inTableShard(0));
	    assertNull(ret);
	    
	    DalHints hints = new DalHints();
	    ret = dao.findFieldFirst(cityIds, name, hints.inAllShards().inTableShard(1).asyncExecution());
	    assertNull(ret);
	    assertEquals("Test", hints.getResult());

	}

	@Test
	public void testfindFieldListByPage() throws Exception {
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);

		List<Integer> countryIds = new ArrayList<>();
		countryIds.add(0);
		countryIds.add(1);
		countryIds.add(2);
		countryIds.add(3);

		DalHints hints = new DalHints();
	    List<String> ret = dao.findFieldListByPage(cityIds, countryIds, 1, 10, hints.inShard(0).inTableShard(1));
	    assertEquals(4, ret.size());
	    
	    hints = new DalHints();
	    ret = dao.findFieldListByPage(cityIds, countryIds, 1, 10, hints.inAllShards().inTableShard(2));
	    assertEquals(8, ret.size());

	    hints = new DalHints();
	    ret = dao.findFieldListByPage(cityIds, countryIds, 1, 10, hints.shardBy("CountryID").inTableShard(2));
	    assertEquals(8, ret.size());
	    
	    hints = new DalHints();
	    ret = dao.findFieldListByPage(cityIds, countryIds, 1, 10, hints.shardBy("CountryID").inTableShard(2).asyncExecution());
	    assertNull(ret);
	    assertEquals(8, ((List)hints.getResult()).size());

	}

	@Test
	public void testfindFieldList() throws Exception {
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);

		List<Integer> countryIds = new ArrayList<>();
		countryIds.add(0);
		countryIds.add(1);
		countryIds.add(2);
		countryIds.add(3);

		DalHints hints = new DalHints();
	    List<String> ret = dao.findFieldList(cityIds, countryIds, hints.inShard(0).inTableShard(1));
	    assertEquals(4, ret.size());
	    
	    hints = new DalHints();
	    ret = dao.findFieldList(cityIds, countryIds, hints.inAllShards().inTableShard(1));
	    assertEquals(8, ret.size());
	    
	    hints = new DalHints();
	    ret = dao.findFieldList(cityIds, countryIds, hints.shardBy("CountryID").inTableShard(1));
	    assertEquals(8, ret.size());

	    hints = new DalHints();
	    ret = dao.findFieldListByPage(cityIds, countryIds, 1, 10, hints.shardBy("CountryID").inTableShard(2).asyncExecution());
	    assertNull(ret);
	    assertEquals(8, ((List)hints.getResult()).size());
	}

	@Test
	public void testfindFieldSingle() throws Exception {
		Person p = dao.queryByPk(1, new DalHints().inShard(1).inTableShard(1));
		p.setName("AAA");
		dao.update(null, p);
		
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		String name = "AAA";// Test value here
		
		DalHints hints = new DalHints();
	    Integer ret = dao.findFieldSingle(cityIds, name, hints.inAllShards().inTableShard(1));
	    assertEquals(1, ret.intValue());
	    
		hints = new DalHints();
	    ret = dao.findFieldSingle(cityIds, name, hints.inAllShards().inTableShard(1));
	    assertEquals(1, ret.intValue());
	    
		hints = new DalHints();
	    ret = dao.findFieldSingle(cityIds, name, hints.inAllShards().inTableShard(0));
	    assertNull(ret);
	    
		hints = new DalHints();
	    ret = dao.findFieldSingle(cityIds, name, hints.inAllShards().inTableShard(1).asyncExecution());
	    assertNull(ret);
	    assertEquals(1, hints.getIntResult());
	}
	
    @Test
    public void testCatTransaction() throws Exception {
        List<Integer> cityIds = new ArrayList<>();// Test value here
        cityIds.add(1);
        cityIds.add(2);
        cityIds.add(3);
        String name = "Test";// Test value here
        dao.findFirst(cityIds, name, new DalHints().inAllShards().inTableShard(0).asyncExecution());
        dao.findFirst(cityIds, name, new DalHints().inAllShards().inTableShard(0).asyncExecution().sequentialExecute());

        dao.findFirst(cityIds, name, new DalHints().inAllShards().inTableShard(0));
//        dao.findFirst(cityIds, name, new DalHints().inAllShards().inTableShard(0).sequentialExecute());
//        Thread.sleep(60*1000);
    }
    
    @Test
    public void testCatTransactionInDalCommand() throws Exception {
        DalCommand command = new DalCommand() {

            @Override
            public boolean execute(DalClient client) throws SQLException {
                List<Integer> cityIds = new ArrayList<>();// Test value here
                cityIds.add(1);
                cityIds.add(2);
                cityIds.add(3);
                String name = "Test";// Test value here
                dao.findFirst(cityIds, name, new DalHints().inShard(0).inTableShard(0).sequentialExecute());
                return false;
            }            
        };
        
        DalClientFactory.getClient(DATABASE_NAME_MYSQL).execute(command, new DalHints().inShard(0));
//        Thread.sleep(60*1000);
    }   
    
//    @Test
//    public void testKKKK() throws Exception {
//        DalClientFactory.shutdownFactory();
//        DalClientFactory.initClientFactory("e:\\dal.config");
//        DalClientFactory.shutdownFactory();
//    }
//
}
