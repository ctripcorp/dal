package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end
import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

public class ${host.getPojoClassName()}DaoUnitTest {

	private static ${host.getPojoClassName()}Dao dao = null;
	private static ${host.getPojoClassName()}[] testdata = null; 
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
		
		dao = new ${host.getPojoClassName()}Dao();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Setup the data
		if(null == testdata || testdata.length <= 0)
			throw new Exception("Test data must be prepared.");
		for(int i = 0; i < testdata.length; i++)
			dao.insert(new DalHints(), testdata[i]);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//Setup the data
		if(null == testdata || testdata.length <= 0)
			throw new Exception("Test data must be prepared.");
		for(int i = 0; i < testdata.length; i++)
			dao.delete(new DalHints(), testdata[i]);
	}
	
#if($host.hasPk())
#if($host.isIntegerPk())
	@Test
	public void testQueryByPk() {
		Integer id = null; //Set the existed value of id.
		try {
			dao.queryByPk(id, new DalHints());
			fail("Not implement"); //Verify the result
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
			assertTrue(null != pk);
		}catch (SQLException e) {
			fail(e.getMessage());
		}
	}
#end
#end

	@Test
	public void testQueryByPkWithEnitity(){
		${host.getPojoClassName()} pk = new ${host.getPojoClassName()}();
		//Set the primary keys of pk
		try{
			pk = dao.queryByPk(pk, new DalHints());
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
			assertTrue(count != -1);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testQueryByPage(){
		int pageNo = 1;
		int pageSize = 10;
		try{
			List<${host.getPojoClassName()}> pojos = dao.queryByPage(pageNo, pageSize, new DalHints());
			assertTrue(null != pojos);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetAll(){
		try{
			List<${host.getPojoClassName()}> pojos = dao.getAll(new DalHints());
			assertTrue(null != pojos);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#if($host.getSpInsert().isExist())
	@Test
	public void testInsert(){
		 ${host.getPojoClassName()} daoPojo = new ${host.getPojoClassName()}();
		 try{
			int row = dao.insert(new DalHints(), daoPojo);
			assertTrue(row > 0);
		 }catch (SQLException e) {
			e.printStackTrace();
		}
	}
#if($host.getSpInsert().getType=="sp3")
	@Test
	public void testMultipleInsert(){
		${host.getPojoClassName()}[] pojos = null;
		try{
			int[] rows = dao.insert(new DalHints(), pojos);
			assertTrue(null != rows);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#else
	@Test
	public void testMultipleInsert(){
		${host.getPojoClassName()}[] pojos = null;
		try{
			dao.insert(new DalHints(), pojos);
			assertTrue(true);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBatchInsert(){
		${host.getPojoClassName()}[] pojos = null;
		try{
			int[] rows = dao.batchInsert(new DalHints(), pojos);
			assertTrue(null != rows);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMultipleInsertWithKeyHolder(){
		KeyHolder holder = new KeyHolder();
		${host.getPojoClassName()}[] pojos = null;
		try{
			dao.insert(new DalHints(), holder, pojos);
			assertTrue(true);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end

#if($host.getSpDelete().isExist())
	@Test
	public void testDelete(){
		 ${host.getPojoClassName()} daoPojo = new ${host.getPojoClassName()}();
		 try{
			int row = dao.delete(new DalHints(), daoPojo);
			assertTrue(row > 0);
		 }catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
#if($host.getSpInsert().getType=="sp3")
	@Test
	public void testMultipleDelete(){
		${host.getPojoClassName()}[] pojos = null;
		try{
			int[] rows = dao.delete(new DalHints(), pojos);
			assertTrue(null != rows);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#else
	@Test
	public void testMultipleDelete(){
		${host.getPojoClassName()}[] pojos = null;
		try{
			dao.delete(new DalHints(), pojos);
			assertTrue(true);
		}catch (SQLException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testBatchDelete(){
		${host.getPojoClassName()}[] pojos = null;
		try{
			int[] rows = dao.batchDelete(new DalHints(), pojos);
			assertTrue(null != rows);
		}catch (SQLException e) {
			fail(e.getMessage());
		}
	}
#end

#if($host.getSpUpdate().isExist())
	@Test
	public void testUpdate(){
		 ${host.getPojoClassName()} daoPojo = new ${host.getPojoClassName()}();
		 try{
			int row = dao.update(new DalHints(), daoPojo);
			assertTrue(row > 0);
		 }catch (SQLException e) {
			e.printStackTrace();
		}
	}
#else
	@Test
	public void testMultipleUpdate(){
		${host.getPojoClassName()}[] pojos = null;
		try{
			dao.update(new DalHints(), pojos);
			assertTrue(true);
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
				assertTrue(affectedRows > 0);
		   }catch (SQLException e) {
			e.printStackTrace();
		}
	}
#end
#end
#end
}
