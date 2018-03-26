package test.${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end
import ${host.getPackageName()}.dao.${host.getClassName()}Dao;
import ${host.getPackageName()}.entity.*;

import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of ${host.getClassName()}Dao class.
**/
public class ${host.getClassName()}DaoUnitTest {

	private static final String DATA_BASE = "${host.getDbSetName()}";
	private static ${host.getClassName()}Dao dao = null;
	private static DalClient client = null;
	
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
		dao = new ${host.getClassName()}Dao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
#foreach($method in $host.getMethods())
#if($method.getCrud_type() != "select")
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
	    //int ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##实体类型且返回First
#if($method.isReturnFirst() && !$method.isSampleType())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
		//${method.getPojoClassName()} ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##实体类型并且返回值为List
#if($method.isReturnList() && !$method.isSampleType())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
		//List<${method.getPojoClassName()}> ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##实体类型且返回Single
#if($method.isReturnSingle() && !$method.isSampleType())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
		//${method.getPojoClassName()} ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##简单类型且返回值为First
#if($method.isSampleType() && $method.isReturnFirst())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
		//${method.getPojoClassName()} ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##简单类型并且返回值是List
#if($method.isSampleType() && $method.isReturnList())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
		//List<${method.getPojoClassName()}> ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##简单类型并且返回值为Single
#if($method.isSampleType() && $method.isReturnSingle())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
		//${method.getPojoClassName()} ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end

}