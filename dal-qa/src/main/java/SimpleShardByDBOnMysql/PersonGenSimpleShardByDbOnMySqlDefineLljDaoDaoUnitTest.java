package SimpleShardByDBOnMysql;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.sql.SQLException;

import org.junit.*;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;


/**
 * JUnit test of PersonGenSimpleShardByDbOnMySqlDefineLljDaoDao class.
**/
public class PersonGenSimpleShardByDbOnMySqlDefineLljDaoDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByDBOnMysql";
	private static PersonGenSimpleShardByDbOnMySqlDefineLljDaoDao dao = null;
	private static DalClient client = null;
	private Integer getModel_Integer(DalHints hints) throws SQLException {
		try {
			return (Integer)hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	private List<PersonGenSimpleShardByDbOnMySqlDefineLljPojo> getModel_list(DalHints hints) throws SQLException {
		try {
			return (List<PersonGenSimpleShardByDbOnMySqlDefineLljPojo>)hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	 public class TestQueryResultCallback_Integer implements DalResultCallback {
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
	       
	        private AtomicReference<List<PersonGenSimpleShardByDbOnMySqlDefineLljPojo>> model_list = new AtomicReference<>(); 
	       
	        @Override
	        public <T> void onResult(T result) {
	        	
	        	model_list.set((List<PersonGenSimpleShardByDbOnMySqlDefineLljPojo>)result);
	        }
	        
	        public List<PersonGenSimpleShardByDbOnMySqlDefineLljPojo> get_list() {
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
		dao = new PersonGenSimpleShardByDbOnMySqlDefineLljDaoDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
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
		
		Integer Age = 26;// Test value here
		List<PersonGenSimpleShardByDbOnMySqlDefineLljPojo>	ret = dao.test_def_query(Age, hints.inAllShards());
		assertNull(ret);
		//ret = getModel_list(hints);//异步返回结果
		ret = callback.get_list();//异步回调
		assertEquals(4, ret.size());
	}
	
    //自定义，增删改，不支持异步
	@Test
	public void testtest_def_update() throws Exception {
		/*DalHints hints1 = new DalHints().asyncExecution();//异步返回结果
		Integer Age = 20;
	    int ret = dao.test_def_update(Age, hints1.inShard(0));
	    assertEquals(0, ret);
	    ret = getModel_Integer(hints1);//异步返回结果
	    assertEquals(1, ret);*/
	    
	    TestQueryResultCallback_Integer callback= new TestQueryResultCallback_Integer();//异步回调
	    DalHints hints2 = new DalHints().callbackWith(callback);//异步回调
	    Integer Age = 20;
	    int ret = dao.test_def_update(hints2.inShard(1));
		assertEquals(0, ret);
		ret = callback.get_Integer();//异步回调
		assertEquals(1, ret);
		
		//自定义SQL，增删改，由于不知用户输入具体情况，所以inAllShards不管用
	/*	Age = 26;// Test value here
		ret = dao.test_def_update(Age, new DalHints().inAllShards());
		assertEquals(4, ret);*/
	}

}