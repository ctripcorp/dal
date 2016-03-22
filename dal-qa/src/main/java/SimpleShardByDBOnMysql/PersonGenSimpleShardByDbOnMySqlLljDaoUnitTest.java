package SimpleShardByDBOnMysql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.sql.SQLException;

import org.junit.*;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;



/**
 * JUnit test of PersonGenSimpleShardByDbOnMySqlLljDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class PersonGenSimpleShardByDbOnMySqlLljDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByDBOnMysql";

	private static DalClient client = null;
	private static PersonGenSimpleShardByDbOnMySqlLljDao dao = null;
	
	private Integer getModel_Integer(DalHints hints) throws SQLException {
		try {
			return (Integer)hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	private List<PersonGenSimpleShardByDbOnMySqlLlj> getModel_list(DalHints hints) throws SQLException {
		try {
			return (List<PersonGenSimpleShardByDbOnMySqlLlj>)hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	private int[] getModel_ArrayInt(DalHints hints) throws SQLException {
		try {
			return (int[])hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	private PersonGenSimpleShardByDbOnMySqlLlj getModel_Pojo(DalHints hints) throws SQLException {
		try {
			return (PersonGenSimpleShardByDbOnMySqlLlj)hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	 private class TestQueryResultCallback_Integer implements DalResultCallback {
	        private AtomicReference<Integer> model_Integer = new AtomicReference<>();
	       
	        @Override
	        public <T> void onResult(T result) {
	        	model_Integer.set((Integer)result);
	        }
	         
	        public Integer get_Integer() {
	            while(model_Integer.get() == null)
	                try {
	                    Thread.sleep(1);
	                } catch (Exception e) {
	                    return null;
	                }
	            return model_Integer.get();
	        }
	     
	        @Override
	        public void onError(Throwable e) {
	            // TODO Auto-generated method stub
	             
	        }
	    }
	 
	 public class TestQueryResultCallback_list implements DalResultCallback {
	       
	        private AtomicReference<List<PersonGenSimpleShardByDbOnMySqlLlj>> model_list = new AtomicReference<>(); 
	       
	        @Override
	        public <T> void onResult(T result) {
	        	
	        	model_list.set((List<PersonGenSimpleShardByDbOnMySqlLlj>)result);
	        }
	        
	        public List<PersonGenSimpleShardByDbOnMySqlLlj> get_list() {
	            while(model_list.get() == null)
	                try {
	                    Thread.sleep(1);
	                } catch (Exception e) {
	                    return null;
	                }
	            return model_list.get();
	        }
	        @Override
	        public void onError(Throwable e) {
	            // TODO Auto-generated method stub
	             
	        }
	    }
	 public class TestQueryResultCallback_ArrayInt implements DalResultCallback {
	       
	        private AtomicReference<int[]> model_ArrayInt = new AtomicReference<>(); 
	       
	        @Override
	        public <T> void onResult(T result) {
	        	
	        	model_ArrayInt.set((int[])result);
	        }
	        
	        public int[] get_ArrayInt() {
	            while(model_ArrayInt.get() == null)
	                try {
	                    Thread.sleep(1);
	                } catch (Exception e) {
	                    return null;
	                }
	            return model_ArrayInt.get();
	        }
	        @Override
	        public void onError(Throwable e) {
	            // TODO Auto-generated method stub
	             
	        }
	    }

	 public class TestQueryResultCallback_Pojo implements DalResultCallback {
	       
	        private AtomicReference<PersonGenSimpleShardByDbOnMySqlLlj> model_Pojo = new AtomicReference<>(); 
	       
	        @Override
	        public <T> void onResult(T result) {
	        	
	        	model_Pojo.set((PersonGenSimpleShardByDbOnMySqlLlj)result);
	        }
	        
	        public PersonGenSimpleShardByDbOnMySqlLlj get_Pojo() {
	            while(model_Pojo.get() == null)
	                try {
	                    Thread.sleep(1);
	                } catch (Exception e) {
	                    return null;
	                }
	            return model_Pojo.get();
	        }
	        @Override
	        public void onError(Throwable e) {
	            // TODO Auto-generated method stub
	             
	        }
	    }
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
		dao = new PersonGenSimpleShardByDbOnMySqlLljDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
	
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));
		
		KeyHolder keyHolder = new KeyHolder();
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos1 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(3);
		
		for(int i=0;i<3;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setAge(i+20);
			daoPojo.setName("Initial_Shard_0"+i);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(0), keyHolder, daoPojos1);
		
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos2 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(3);
		for(int i=0;i<3;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setAge(i+30);
			daoPojo.setName("Initial_Shard_1"+i);
			daoPojos2.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(1), keyHolder, daoPojos2);
	}

	@After
	public void tearDown() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));
	} 
	
	//调用的是baseClient,不支持异步和跨shards
	@Test
	public void testCount() throws Exception {
		DalHints hints1 = new DalHints();
		hints1.inShard(0);
		int ret = dao.count(hints1);
		assertEquals(3, ret);
		
 
		DalHints hints2 = new DalHints();
		hints2.inShard(1);
		ret = dao.count(hints2);
		assertEquals(3, ret);
	}
	
	@Test
	public void testDelete1() throws Exception {
		DalHints hints = new DalHints();
		PersonGenSimpleShardByDbOnMySqlLlj daoPojo = null;
		int ret = dao.delete(hints, daoPojo); 
	}
	
	@Test
	public void testDelete2() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();//异步返回结果
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos1 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(3);
		for(int i=0;i<3;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+20);
			daoPojo.setName("Initial_Shard_0"+i);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(0);
		int[] affected = dao.delete(hints1, daoPojos1);
		assertNull(affected);
		affected = getModel_ArrayInt(hints1);//异步返回结果
		assertEquals(3, affected.length);
		
		
		TestQueryResultCallback_ArrayInt callback= new TestQueryResultCallback_ArrayInt();//异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);//异步回调
		hints2.inShard(1);
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos2 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(3);
		for(int i=0;i<3;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+30);
			daoPojo.setName("Initial_Shard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.delete(hints2, daoPojos2);
		assertNull(affected);
		affected =  callback.get_ArrayInt();//异步回调
		assertEquals(3, affected.length);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();//异步返回结果
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos1 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(3);
		for(int i=0;i<3;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setAge(i+20);
			daoPojo.setName("Initial_Shard_0"+i);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(0);
		int[] affected = dao.batchDelete(hints1, daoPojos1);
		assertNull(affected);
		affected = getModel_ArrayInt(hints1);//异步返回结果
		assertEquals(3, affected.length);
		
		TestQueryResultCallback_ArrayInt callback= new TestQueryResultCallback_ArrayInt();//异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);//异步回调
		hints2.inShard(1);
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos2 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(3);
		for(int i=0;i<3;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setAge(i+30);
			daoPojo.setName("Initial_Shard_1"+i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchDelete(hints2, daoPojos2);
		assertNull(affected);
		affected =  callback.get_ArrayInt();//异步回调
		assertEquals(3, affected.length);
	}
	
	//调用的是baseClient,不支持异步和跨shards
	@Test
	public void testGetAll() throws Exception {
		DalHints hints1 = new DalHints();
		hints1.inShard(1);
		List<PersonGenSimpleShardByDbOnMySqlLlj> list = dao.getAll(hints1);
		assertEquals(3,list.size());
		
	
		DalHints hints2 = new DalHints();//异步回调
		hints2.inShard(0);
		list = dao.getAll(hints2);
		assertEquals(3,list.size());
	}
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		hints.inShard(0);
		PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
		daoPojo.setAge(12);
		daoPojo.setName("Angela");
		int affected = dao.insert(hints, daoPojo);
		assertEquals(1,affected);
		hints.inShard(1);
		daoPojo.setAge(23);
		daoPojo.setName("Lily");
		affected = dao.insert(hints, daoPojo);
		assertEquals(1,affected);
	}
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
		daoPojo.setAge(26);
		daoPojo.setName("Ochirly");
		hints.inShard(1);
		int affected = dao.insert(hints, keyHolder, daoPojo);
		assertEquals(1,affected);
		assertEquals(4l,keyHolder.getKey());
	}
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();//异步返回结果
		KeyHolder keyHolder = new KeyHolder();
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(3);
		for(int i=0;i<3;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setAge(i+50);
			daoPojo.setName("Belle");
			daoPojos.add(daoPojo);
		}
		hints1.inShard(1);
		int[] affected = dao.insert(hints1, keyHolder, daoPojos);
		assertNull(affected);
		affected = getModel_ArrayInt(hints1);//异步返回结果
		assertEquals(3,affected.length);
		
		TestQueryResultCallback_ArrayInt callback= new TestQueryResultCallback_ArrayInt();//异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);//异步回调
		hints2.inShard(0);
		affected = dao.insert(hints2, keyHolder, daoPojos);
		assertNull(affected);
		affected =  callback.get_ArrayInt();//异步回调
		assertEquals(3,affected.length);
		
	}
	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}
	
	@Test
	public void testCombinedInsert1() throws Exception {
		DalHints hints = new DalHints();
		hints.inShard(1);
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(2);
		
		PersonGenSimpleShardByDbOnMySqlLlj  daoPojos1=new PersonGenSimpleShardByDbOnMySqlLlj();
		daoPojos1.setAge(35);
		daoPojos1.setName("Bella");
		daoPojos.add(daoPojos1);
		
		PersonGenSimpleShardByDbOnMySqlLlj  daoPojos2=new PersonGenSimpleShardByDbOnMySqlLlj();
		daoPojos2.setAge(47);
		daoPojos2.setName("May");
		daoPojos.add(daoPojos2);
		
		int affected = dao.combinedInsert(hints, daoPojos);
		assertEquals(2,affected);
	}
	
	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();//异步返回结果
		KeyHolder keyHolder = new KeyHolder();
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(3);
		for(int i=0;i<3;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setAge(i+20);
			daoPojo.setName("Saturday");
			daoPojos.add(daoPojo);
		}
		hints1.inShard(0);
		int affected = dao.combinedInsert(hints1, keyHolder, daoPojos);
		assertEquals(0, affected);
		affected = getModel_Integer(hints1);//异步返回结果
		assertEquals(3,affected);
		
		TestQueryResultCallback_Integer callback= new TestQueryResultCallback_Integer();//异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);//异步回调
		hints2.inShard(1);
		affected = dao.combinedInsert(hints2, keyHolder, daoPojos);
		assertEquals(0, affected);
		affected =  callback.get_Integer();//异步回调
		assertEquals(3,affected);
	
	}
	
	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
		hints.inShard(0);
		int pageSize = 1;
		int pageNo = 3;
		List<PersonGenSimpleShardByDbOnMySqlLlj> list = dao.queryByPage(pageSize, pageNo, hints);
		assertEquals(1, list.size());
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1l;
		DalHints hints1 = new DalHints().asyncExecution();//异步返回结果
		hints1.inShard(0);
		PersonGenSimpleShardByDbOnMySqlLlj ret = dao.queryByPk(id, hints1);
		assertNull(ret);
		ret = getModel_Pojo(hints1);//异步返回结果
		assertEquals("Initial_Shard_00", ret.getName());
		
		id = 2l;
		TestQueryResultCallback_Pojo callback= new TestQueryResultCallback_Pojo();//异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);//异步回调
		hints2.inShard(1);
		ret = dao.queryByPk(id, hints2);
		assertNull(ret);
		ret =  callback.get_Pojo();//异步回调
		assertEquals("Initial_Shard_11", ret.getName());
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		PersonGenSimpleShardByDbOnMySqlLlj pk = new PersonGenSimpleShardByDbOnMySqlLlj();
		pk.setID(2);
		DalHints hints = new DalHints();
		
		hints.inShard(0);
		PersonGenSimpleShardByDbOnMySqlLlj ret = dao.queryByPk(pk, hints);
		assertEquals("Initial_Shard_01", ret.getName());
		
		hints.inShard(1);
		ret = dao.queryByPk(pk, hints);
		assertEquals("Initial_Shard_11", ret.getName());
	}
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		PersonGenSimpleShardByDbOnMySqlLlj daoPojo = null;
		int ret = dao.update(hints, daoPojo);
	}
	
	@Test
	public void testUpdate2() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();//异步返回结果
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos1 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(2);
		for(int i=0;i<2;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+80);
			daoPojo.setName("UpdateShard1"+i);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(1);
		int[] ret = dao.update(hints1, daoPojos1);
		assertNull(ret);
		ret = getModel_ArrayInt(hints1);//异步返回结果
		assertEquals(2, ret.length);
		
		Number id1 = 1l;
		PersonGenSimpleShardByDbOnMySqlLlj ret1 = dao.queryByPk(id1, new DalHints().inShard(1));
		assertEquals("UpdateShard10", ret1.getName());
		Number id2 = 2l;
		PersonGenSimpleShardByDbOnMySqlLlj ret2 = dao.queryByPk(id2, new DalHints().inShard(1));
		assertEquals("UpdateShard11", ret2.getName());
		
		TestQueryResultCallback_ArrayInt callback= new TestQueryResultCallback_ArrayInt();//异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);//异步回调
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos2 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(2);
		for(int i=0;i<2;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+80);
			daoPojo.setName("UpdateShard0"+i);
			daoPojos2.add(daoPojo);
		}
		hints2.inShard(0);
		ret = dao.update(hints2, daoPojos2);
		assertNull(ret);
		ret =  callback.get_ArrayInt();//异步回调
		assertEquals(2,ret.length);
		
		Number id3 = 1l;
		PersonGenSimpleShardByDbOnMySqlLlj ret3 = dao.queryByPk(id3, new DalHints().inShard(0));
		assertEquals("UpdateShard00", ret3.getName());
		Number id4 = 2l;
		PersonGenSimpleShardByDbOnMySqlLlj ret4 = dao.queryByPk(id4, new DalHints().inShard(0));
		assertEquals("UpdateShard01", ret4.getName());
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();//异步返回结果
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos1 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(2);
		for(int i=0;i<2;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+80);
			daoPojo.setName("UpdateShard1"+i);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(1);
		int[] ret = dao.batchUpdate(hints1, daoPojos1);
		assertNull(ret);
		ret = getModel_ArrayInt(hints1);//异步返回结果
		assertEquals(2, ret.length);
		
		Number id1 = 1l;
		PersonGenSimpleShardByDbOnMySqlLlj ret1 = dao.queryByPk(id1, new DalHints().inShard(1));
		assertEquals("UpdateShard10", ret1.getName());
		Number id2 = 2l;
		PersonGenSimpleShardByDbOnMySqlLlj ret2 = dao.queryByPk(id2, new DalHints().inShard(1));
		assertEquals("UpdateShard11", ret2.getName());
		
		TestQueryResultCallback_ArrayInt callback= new TestQueryResultCallback_ArrayInt();//异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);//异步回调
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos2 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(2);
		for(int i=0;i<2;i++)
		{
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setID(i+1);
			daoPojo.setAge(i+80);
			daoPojo.setName("UpdateShard0"+i);
			daoPojos2.add(daoPojo);
		}
		hints2.inShard(0);
		ret = dao.batchUpdate(hints2, daoPojos2);
		assertNull(ret);
		ret =  callback.get_ArrayInt();//异步回调
		assertEquals(2,ret.length);
	
		Number id3 = 1l;
		PersonGenSimpleShardByDbOnMySqlLlj ret3 = dao.queryByPk(id3, new DalHints().inShard(0));
		assertEquals("UpdateShard00", ret3.getName());
		Number id4 = 2l;
		PersonGenSimpleShardByDbOnMySqlLlj ret4 = dao.queryByPk(id4, new DalHints().inShard(0));
		assertEquals("UpdateShard01", ret4.getName());
	}

	//构建SQL，新增
	@Test
	public void testtest_build_insert() throws Exception {
	/*	DalHints hints1 = new DalHints();
		DalHints hints2 = new DalHints();*/
	//	DalHints hints3 = new DalHints().asyncExecution();//异步返回结果
		TestQueryResultCallback_Integer callback= new TestQueryResultCallback_Integer();//异步回调
		DalHints hints3 = new DalHints().callbackWith(callback);//异步回调
		
		Integer Age = 26;// Test value here
		String Name = "KarryAll";
	/*	String Name = "Karry0";
		hints1.inShard(0);
	    int ret = dao.test_build_insert(Name, Age, hints1);
	    assertEquals(1, ret);
	    
	    Name = "Karry1";
	    hints2.inShard(1);
	    ret = dao.test_build_insert(Name, Age, hints2);
	    assertEquals(1, ret);*/
			
	    int ret = dao.test_build_insert(Name, Age, hints3.inAllShards());
	    assertEquals(0, ret);
	   // ret = getModel(hints3);//异步返回结果
	    ret = callback.get_Integer();//异步回调
	    assertEquals(2, ret);
	}
	
	
	//构建SQL，查询
	@Test
	public void testtest_build_query() throws Exception {
		Integer Age = 20;// Test value here
	 /*   List<PersonGenSimpleShardByDbOnMySqlLlj> ret = dao.test_build_query(Age, new DalHints().inShard(0));
	    assertEquals(2, ret.size());
	    
	    ret = dao.test_build_query(Age, new DalHints().inShard(1));
	    assertEquals(3, ret.size());*/
	    
		//DalHints hints = new DalHints().asyncExecution();//异步返回结果
		
		TestQueryResultCallback_list callback= new TestQueryResultCallback_list();//异步回调
		DalHints hints = new DalHints().callbackWith(callback);//异步回调
		
		List<PersonGenSimpleShardByDbOnMySqlLlj> ret = dao.test_build_query(Age, hints.inAllShards());
		assertNull(ret);
	//	ret = getModel_list(hints);//异步返回结果
		ret = callback.get_list();//异步回调
		assertEquals(1, ret.size());
	    
	}
	
	
	
	//构建SQL，更新
	@Test
	public void testtest_build_update() throws Exception {
		/*String Name = "AppleUpdate";// Test value here
		Integer Age = 23;// Test value here
	    int ret = dao.test_build_update(Name, Age, new DalHints().inShard(0));
	    assertEquals(1, ret);
	    
	    Name = "BlueberryUpdate";// Test value here
		Age = 52;// Test value here
	    ret = dao.test_build_update(Name, Age, new DalHints().inShard(1));
	    assertEquals(1, ret);*/
	
		//DalHints hints = new DalHints().asyncExecution();//异步返回结果
		TestQueryResultCallback_Integer callback= new TestQueryResultCallback_Integer();//异步回调
		DalHints hints = new DalHints().callbackWith(callback);//异步回调
		
		String  Name = "callback";
		Integer Age = 30;
		
		int ret = dao.test_build_update(Name, Age, hints.inAllShards());
		assertEquals(0, ret);
		//ret = getModel_Integer(hints);//异步返回结果
		 ret = callback.get_Integer();//异步回调
	    assertEquals(1, ret);
	    
	}
	
	//构建SQL，删除
	@Test
	public void testtest_build_delete() throws Exception {
	/*	Integer param1 = 23;// Test value here
	    int ret = dao.test_build_delete(param1, new DalHints().inShard(0));
	    assertEquals(1, ret);
	    
	    param1 = 52;// Test value here
	    ret = dao.test_build_delete(param1, new DalHints().inShard(1));
	    assertEquals(1, ret);*/
	    
		//DalHints hints = new DalHints().asyncExecution();//异步返回结果
		TestQueryResultCallback_Integer callback= new TestQueryResultCallback_Integer();//异步回调
		DalHints hints = new DalHints().callbackWith(callback);//异步回调
		
		Integer  param1 = 31;// Test value here
	
		int   ret = dao.test_build_delete(param1, hints.inAllShards());
		assertEquals(0, ret);
		//ret = getModel_Integer(hints);//异步返回结果
		 ret = callback.get_Integer();//异步回调
	    assertEquals(1, ret);
	}
	

	//自定义，查询
		@Test
		public void testtest_def_query() throws Exception {
		/*	Integer Age = 20;// Test value here
			List<PersonGenSimpleShardByDbOnMySqlDefineLljPojo> ret = dao.test_def_query(Age, new DalHints().inShard(0));
			assertEquals(2, ret.size());
			
			Age = 23;// Test value here
			ret = dao.test_def_query(Age, new DalHints().inShard(1));
			assertEquals(4, ret.size());*/
			
			//DalHints hints = new DalHints().asyncExecution();//异步返回结果
			
			TestQueryResultCallback_list callback= new TestQueryResultCallback_list();//异步回调
			DalHints hints = new DalHints().callbackWith(callback);//异步回调
			
			Integer Age = 22;// Test value here
			List<PersonGenSimpleShardByDbOnMySqlLlj> ret = dao.test_def_query(Age, hints.inAllShards());
			assertNull(ret);
			//ret = getModel_list(hints);//异步返回结果
			ret = callback.get_list();//异步回调
			assertEquals(1, ret.size());
		}
		
	    //自定义，增删改，不支持异步
		@Test
		public void testtest_def_update() throws Exception {
		    int ret1 = dao.test_def_update(new DalHints().inShard(0));
		    assertEquals(0,ret1);
		    
		    int ret2 = dao.test_def_update(new DalHints().inShard(1));
		    assertEquals(0,ret2);
		}
}
