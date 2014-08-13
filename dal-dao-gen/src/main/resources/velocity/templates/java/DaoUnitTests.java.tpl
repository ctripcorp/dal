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
		for(int i = 0; i < testdata.length; i++){
			dao.delete(new DalHints(), testdata[i]);
			dao.insert(new DalHints(), testdata[i]);
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
	
#if($host.hasPk())
#if($host.isIntegerPk())
	@Test
	public void testQueryByPk() {
		Integer id = null;
#foreach( $field in ${host.getFields()} )
#if($field.isPrimary())
		id = testdata[0].get${field.getCapitalizedName()}();
#end
#end
		try {
			${host.getPojoClassName()} ret = dao.queryByPk(id, new DalHints());
			//TODO: Verify the result.
			assertTrue(null != ret);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
#else
	@Test
	public void testQueryByPk(){
		//Set the primary keys 
#foreach($p in $host.getPrimaryKeys())
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = null;
#end
	    try{
			${host.getPojoClassName()} pk = dao.queryByPk(${host.getPkParametersList()});
			//TODO: Verify the result.
			assertTrue(null != pk);
		}catch (SQLException e) {
			fail(e.getMessage());
		}
	}
#end
#end

	@Test
	public void testQueryByPkWithEnitity(){
		${host.getPojoClassName()} pk = testdata[0];
		try{
			pk = dao.queryByPk(pk, new DalHints());
			//TODO: Verify the result.
			assertTrue(null != pk);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
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
#if($host.getSpInsert().isExist())
	@Test
	public void testInsert(){
		 ${host.getPojoClassName()} daoPojo = testdata2[0];
		 ${host.getPojoClassName()} ret = null;
		 try{
			dao.insert(new DalHints(), daoPojo);
			
			//TODO: Verify the result.
			ret = dao.queryByPk(daoPojo, new DalHints());
			assertTrue(ret != null);
		 }catch (SQLException e) {
			e.printStackTrace();
		}
	}
#if($host.getSpInsert().getType=="sp3")
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
	@Test
	public void testMultipleInsert(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
		try{
			dao.insert(new DalHints(), pojos);
			//TODO: Verify the result
			for(int i = 0; i < pojos.length; i++){
				ret = null;
				ret = dao.queryByPk(pojos[i], new DalHints());					
				assertTrue(null != ret);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
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
	
	@Test
	public void testMultipleInsertWithKeyHolder(){
		KeyHolder holder = new KeyHolder();
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
		try{
			dao.insert(new DalHints(), holder, pojos);
			
			//TODO: Verify the result
			for(int i = 0; i < pojos.length; i++){
				ret = null;
				ret = dao.queryByPk(pojos[i], new DalHints());
				assertTrue(null != ret);
			}
			assertTrue(holder.getKeyList().size() == pojos.length);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end

#if($host.getSpDelete().isExist())
	@Test
	public void testDelete(){
		 ${host.getPojoClassName()} daoPojo = testdata2[0];
		 ${host.getPojoClassName()} ret = null;
		 try{
			dao.insert(new DalHints(), daoPojo);
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
	
#if($host.getSpInsert().getType=="sp3")
	@Test
	public void testMultipleDelete(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
		try{
			for(int i = 0; i < pojos.length; i++){
				ret = null;
    			dao.insert(new DalHints(), pojos[i]);
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
	@Test
	public void testMultipleDelete(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
		try{
			
			for(int i = 0; i < pojos.length; i++){
				ret = null;
    			dao.insert(new DalHints(), pojos[i]);
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
	
	@Test
	public void testBatchDelete(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
		try{
			for(int i = 0; i < pojos.length; i++){
				ret = null;
    			dao.insert(new DalHints(), pojos[i]);
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

#if($host.getSpUpdate().isExist())
	@Test
	public void testUpdate(){
		 ${host.getPojoClassName()} pojos = testdata2[0];
		 ${host.getPojoClassName()} ret = null;
		 try{
			dao.insert(new DalHints(), pojos);

			ret = dao.queryByPk(pojos, new DalHints());
			assertTrue(null != ret);
			
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
#else
	@Test
	public void testMultipleUpdate(){
		${host.getPojoClassName()}[] pojos = testdata2;
		${host.getPojoClassName()} ret = null;
		try{
			for(int i = 0; i < pojos.length; i++){
				ret = null;
    			dao.insert(new DalHints(), pojos[i]);
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
