package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end
import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

/**
 * Auto-generated: JUnit test of ${host.getPojoClassName()}Dao class.
 * The required test data must be set before run these junit test methods.
**/
public class ${host.getPojoClassName()}DaoUnitTest {

	private static final String DATA_BASE = "${host.getDbName()}";

	private static ${host.getPojoClassName()}Dao dao = null;
	private static DalClient client = null;
	
	//The optional setup SQL, which will be executed on test begin.
	private static String[] setupsqls = null;
	//The optional cleanup SQL, which will be executed on test end.
	private static String[] cleanupsqls = null;
	
	//The required test data, which will be used for query test.
	private static ${host.getPojoClassName()}[] testdata = null;
	//The required test data, which will be used for insert/delete/update test
	private static ${host.getPojoClassName()}[] testdata2 = null; 
	
	static{
		try{
    		/**
    		* Initialize DalClientFactory.
    		* The Dal.config can be specified from class-path or local file path.
    		* One of follow three need to be enabled.
    		**/
    		//DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
    		DalClientFactory.initClientFactory(); // load from class-path Dal.config
    		//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
    		
    		client = DalClientFactory.getClient(DATA_BASE);
    		dao = new ${host.getPojoClassName()}Dao();
			
    		testdata = new ${host.getPojoClassName()}[3];
    		testdata2 = new ${host.getPojoClassName()}[3];
    		 
    		for(int i = 0; i < 3; i++){
				${host.getPojoClassName()} p1 = new ${host.getPojoClassName()}();
#foreach($field in ${host.getFields()})
    		 	p1.set${field.getCapitalizedName()}(${helper.getMockValForUnitTest($field,"i")});
#end
    		 	testdata[i] = p1;
    		 	
    		 	${host.getPojoClassName()} p2 = new ${host.getPojoClassName()}();
#foreach($field in ${host.getFields()})	
    		 	p2.set${field.getCapitalizedName()}(${helper.getMockValForUnitTest($field,"i+3")});
#end	
    		 	testdata2[i] = p2;
    		 	
    		 }
    		 
    		 //cleanupsqls = new String[]{"delete from ${host.getTableName()} where 1=0"};
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(null != setupsqls && setupsqls.length >= 0){
			client.batchUpdate(setupsqls, new DalHints());
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(null != cleanupsqls && cleanupsqls.length >= 0){
			client.batchUpdate(cleanupsqls, new DalHints());
		}
	}
	
	@Before
	public void setUp() throws Exception {
		//Setup the data
		if(null == testdata || testdata.length <= 0 ||
			null == testdata2 || testdata2.length <= 0)
			throw new Exception("Test data must be prepared.");
#if($host.isHasIdentity())
		KeyHolder keyHolder = new KeyHolder();
#end
		for(int i = 0; i < testdata.length; i++){
			dao.delete(new DalHints(), testdata[i]);
#if($host.isHasIdentity())
		    dao.insert(new DalHints(), keyHolder, testdata[i]);
			testdata[i].set${host.getPrimaryKeyName()}((${host.getPrimaryKeyType()})keyHolder.getKey(i));
#else
		    dao.insert(new DalHints(), testdata[i]);
#end	    
		}
		
		for(int i = 0; i<testdata2.length; i++){
			dao.delete(new DalHints(), testdata2[i]);
		}
	}

	@After
	public void tearDown() throws Exception {
		//Cleanup the data
		if(null == testdata || testdata.length <= 0 ||
			null == testdata2 || testdata2.length <= 0)
			throw new Exception("Test data must be prepared.");
		for(int i = 0; i < testdata.length; i++){
			dao.delete(new DalHints(), testdata[i]);
		}
		for(int i = 0; i<testdata2.length; i++){
			dao.delete(new DalHints(), testdata2[i]);
		}
	} 
	
#if($host.hasPk() && $host.generateAPI(1,2,3,13,14,15,22,23,24,34,35,36))
#if($host.isIntegerPk())
	@Test
	public void testQueryByPk() {
		${host.getPrimaryKeyType()} id = testdata[0].get${host.getPrimaryKeyName()}();
		try {
			${host.getPojoClassName()} ret = dao.queryByPk(id, new DalHints());
			//TODO: Verify the result.
			assertNotNull(ret);
			assertEquals(id, ret.get${host.getPrimaryKeyName()}());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
#else
	@Test
	public void testQueryByPk(){
		//Set the primary keys 
#foreach($p in $host.getPrimaryKeys())
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = testdata[0].get${p.getCapitalizedName}();
#end
	    try{
			${host.getPojoClassName()} pk = dao.queryByPk(${host.getPkParametersList()});
			//TODO: Verify the result.
			assertNotNull(pk);
#foreach($p in $host.getPrimaryKeys())
		    assertEquals(testdata[0].get${p.getCapitalizedName}(), pk.get${p.getCapitalizedName}());
#end
		}catch (SQLException e) {
			fail(e.getMessage());
		}
	}
#end
#end

#if($host.generateAPI(3,15,24,36))
	@Test
	public void testQueryByPkWithEnitity(){
		${host.getPojoClassName()} pk = testdata[0];
		try{
			pk = dao.queryByPk(pk, new DalHints());
			//TODO: Verify the result.
			assertNotNull(pk);
#foreach($p in $host.getPrimaryKeys())
		    assertEquals(testdata[0].get${p.getCapitalizedName()}(), pk.get${p.getCapitalizedName()}());
#end
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end

#if($host.generateAPI(4,16,25,37))
	@Test
	public void testCount(){
		int count = -1;
		try{
			count = dao.count(new DalHints());
			//TODO: Verify the result.
			assertTrue(count >= testdata.length);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end

#if($host.generateAPI(5,17,26,38))
	@Test
	public void testQueryByPage(){
		int pageNo = 1;
		int pageSize = 10;
		try{
			List<${host.getPojoClassName()}> pojos = dao.queryByPage(pageSize, pageNo, new DalHints());
			//TODO: Verify the result.
			if(testdata.length > 10){
				assertTrue(pojos.size() == 10);
			}else{
				assertTrue(pojos.size() >= testdata.length);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end

#if($host.generateAPI(6,18,27))
	@Test
	public void testGetAll(){
		try{
			List<${host.getPojoClassName()}> pojos = dao.getAll(new DalHints());
			int count = dao.count(new DalHints());
			//TODO: Verify the result.
			assertTrue(null != pojos && pojos.size() == count);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#if($host.getSpInsert().isExist())
#if($host.generateAPI(19))
	@Test
	public void testInsert(){
		 ${host.getPojoClassName()} daoPojo = testdata2[0];
		 try{
			int rows = dao.insert(new DalHints(), daoPojo);
#if($host.getSpInsert().isExist())
		    assertTrue(rows == 0);
#else
		    assertEquals(1, rows);
#end
		 }catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#if($host.getSpInsert().getType=="sp3" && $host.generateAPI(8,29,39))
	@Test
	public void testMultipleInsert(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
		try{
			int[] rows = dao.insert(new DalHints(), pojos);
			
			//TODO: Verify the result
			for(int i = 0; i < pojos.length; i++){
				ret = null;
				ret = dao.queryByPk(pojos[i], new DalHints());					
				assertTrue(null != ret);
			}
			assertTrue(null != rows && rows.length = pojos.size());
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#else
#if($host.generateAPI(7,28))
	@Test
	public void testMultipleInsert(){
		${host.getPojoClassName()}[] pojos = testdata2;
		try{
			int rows = dao.insert(new DalHints(), pojos);
			assertEquals(testdata2.length, rows);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end

#if($host.generateAPI(8,29))
	@Test
	public void testBatchInsert(){
		${host.getPojoClassName()}[] pojos = testdata2;
		try{
			int[] rows = dao.batchInsert(new DalHints(), pojos);
			
			//TODO: Verify the result
			assertTrue(null != rows && rows.length == pojos.length);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end

#if($host.generateAPI(9,30))
	@Test
	public void testMultipleInsertWithKeyHolder(){
#if($host.isHasIdentity())
		KeyHolder holder = new KeyHolder();
#end
		${host.getPojoClassName()}[] pojos = testdata2;
		try{
#if($host.isHasIdentity())
			int rows = dao.insert(new DalHints(), holder, pojos);
			assertEquals(testdata2.length, holder.size());
#else
		    int rows = dao.insert(new DalHints(), null, pojos);
#end
	        assertEquals(testdata2.length, rows);	
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#end

#if($host.getSpDelete().isExist())
#if($host.generateAPI(20))
	@Test
	public void testDelete(){
		 ${host.getPojoClassName()} daoPojo = testdata2[0];
		 ${host.getPojoClassName()} ret = null;
#if($host.isHasIdentity())
		 KeyHolder holder = new KeyHolder();
#end
		 try{
#if($host.isHasIdentity())
		    dao.insert(new DalHints(), holder, daoPojo);
			daoPojo.set${host.getPrimaryKeys().get(0).getCapitalizedName()}((${host.getPrimaryKeyType()})holder.getKey());
#else
		    dao.insert(new DalHints(), daoPojo);
#end
			ret = dao.queryByPk(daoPojo, new DalHints());
			assertTrue(null != ret);
			
			dao.delete(new DalHints(), daoPojo);
			
			//TODO: Verify the result
			ret = dao.queryByPk(daoPojo, new DalHints());		
			assertTrue(null == ret);
		 }catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
	
#if($host.getSpInsert().getType=="sp3" && $host.generateAPI(40))
	@Test
	public void testMultipleDelete(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
#if($host.isHasIdentity())
		KeyHolder holder = new KeyHolder();
#end
		try{
			for(int i = 0; i < pojos.length; i++){
				ret = null;
#if($host.isHasIdentity())
		        dao.insert(new DalHints(), holder, pojos[i]);
				pojos[i].set${host.getPrimaryKeys().get(0).getCapitalizedName()}(holder.getKey(i).intValue());
#else
		        dao.insert(new DalHints(), pojos[i]);
#end
				ret = dao.queryByPk(pojos[i], new DalHints());
				assertTrue(null != ret);
			}
			
			dao.delete(new DalHints(), pojos);
			
			//TODO: Verify the result
			for(int i = 0; i < pojos.length; i++){
				ret = null;
				ret = dao.queryByPk(pojos[i], new DalHints());
				assertTrue(null == ret);
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#else
#if($host.generateAPI(10,31))
	@Test
	public void testMultipleDelete(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
#if($host.isHasIdentity())
		KeyHolder holder = new KeyHolder();
#end
		try{
			
			for(int i = 0; i < pojos.length; i++){
				ret = null;
#if($host.isHasIdentity())
		        dao.insert(new DalHints(), holder, pojos[i]);
				pojos[i].set${host.getPrimaryKeys().get(0).getCapitalizedName()}(holder.getKey(i).intValue());
#else
		         dao.insert(new DalHints(), pojos[i]);
#end
				ret = dao.queryByPk(pojos[i], new DalHints());
				assertTrue(null != ret);
			}
			
			dao.delete(new DalHints(), pojos);
			
			//TODO: Verify the result
			for(int i = 0; i < pojos.length; i++){
				ret = null;
				ret = dao.queryByPk(pojos[i], new DalHints());
				assertTrue(null == ret);
			}
		}catch (SQLException e) {
			fail(e.getMessage());
		}
	}
#end

#if($host.generateAPI(11,32))
	@Test
	public void testBatchDelete(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
#if($host.isHasIdentity())
		KeyHolder holder = new KeyHolder();
#end
		try{
			for(int i = 0; i < pojos.length; i++){
				ret = null;
#if($host.isHasIdentity())
		        dao.insert(new DalHints(),holder, pojos[i]);
				pojos[i].set${host.getPrimaryKeys().get(0).getCapitalizedName()}(holder.getKey(i).intValue());
#else
		        dao.insert(new DalHints(), pojos[i]);
#end
				ret = dao.queryByPk(pojos[i], new DalHints());			
				assertTrue(null != ret);
			}
			
			int[] rows = dao.batchDelete(new DalHints(), pojos);
			
			//TODO: Verify the result
			for(int i = 0; i < pojos.length; i++){
				ret = null;
				ret = dao.queryByPk(pojos[i], new DalHints());
				assertTrue(null == ret);
			}

			assertTrue(null != rows && rows.length == pojos.length);
		}catch (SQLException e) {
			fail(e.getMessage());
		}
	}
#end
#end

#if($host.getSpUpdate().isExist())
#if($host.generateAPI(21))
	@Test
	public void testUpdate(){
		 ${host.getPojoClassName()} pojos = testdata2[0];
		 ${host.getPojoClassName()} ret = null;
		 try{
#if($host.isHasIdentity())
		    KeyHolder keyHolder = new KeyHolder();
			dao.insert(new DalHints(), keyHolder, pojos);
			pojos.set${host.getPrimaryKeyName()}((${host.getPrimaryKeyType()})keyHolder.getKey(0));
			
			ret = dao.queryByPk(pojos, new DalHints());
			assertNotNull(ret);
#else
			dao.insert(new DalHints(), pojos);
			ret = dao.queryByPk(pojos, new DalHints());
			assertTrue(null != ret);
#end
			//Change the values of pojos
			int row = dao.update(new DalHints(), pojos);
			assertTrue(row >= 0);
			
			ret = dao.queryByPk(pojos, new DalHints());
			//TODO: Verify the result
			assertTrue(null != ret);
			
		 }catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#else
#if($host.generateAPI(12,33,41))
	@Test
	public void testMultipleUpdate(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
#if($host.isHasIdentity())
		KeyHolder holder = new KeyHolder();
#end
		try{
			for(int i = 0; i < pojos.length; i++){
				ret = null;
    			
#if($host.isHasIdentity())
		        dao.insert(new DalHints(), holder, pojos[i]);
				pojos[i].set${host.getPrimaryKeys().get(0).getCapitalizedName()}(holder.getKey(i).intValue());
#else
		        dao.insert(new DalHints(), pojos[i]);
#end
				ret = dao.queryByPk(pojos[0], new DalHints());
				assertTrue(null != ret);
			}
			
			//TODO: Change the pojos
			dao.update(new DalHints(), pojos);
			
			for(int i = 0; i < pojos.length; i++){
				ret = null;
				ret = dao.queryByPk(pojos[0], new DalHints());
				
				//TODO: Verify the result
				assertTrue(null != ret);
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#end

#if($host.hasMethods())
#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")	
	/**
	 * Test ${method.getName()}: ${method.getComments()}
	**/
	@Test
	public void test${method.getName()}(){
#foreach($p in $method.getParameters())
		${p.getClassDisplayName()} ${p.getAlias()} = null;
#end
	    try{
#if($method.isSampleType())
#if($method.isReturnList())
		    List<${method.getPojoClassName()}> result = dao.${method.getName()}(${method.getParameterNames(null)});
#else
		    ${method.getPojoClassName()} result = dao.${method.getName()}(${method.getParameterNames(null)});
#end
#else
#if($method.isReturnList())
		    List<${host.getPojoClassName()}> result = dao.${method.getName()}(${method.getParameterNames(null)});
#else
		    ${host.getPojoClassName()} result = dao.${method.getName()}(${method.getParameterNames(null)});
#end
#end
	        //TODO: Verify the result
	        assertTrue(null != result);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
#else
	/**
	 * Test ${method.getName()}: ${method.getComments()}
	**/
	@Test
	public void test${method.getName()}(){
#foreach($p in $method.getParameters())
		    ${p.getClassDisplayName()} ${p.getAlias()} = null; //set you value here
#end
	       try{
			    int affectedRows = dao.${method.getName()}(${method.getParameterNames(null)});
				//TODO: Verify the result
				assertTrue(affectedRows > 0);
		   }catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#end
#end
}
