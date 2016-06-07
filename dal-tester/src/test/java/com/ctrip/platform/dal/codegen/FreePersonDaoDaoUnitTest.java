package com.ctrip.platform.dal.codegen;

import java.util.List;
import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of FreePersonDaoDao class.
**/
public class FreePersonDaoDaoUnitTest {

	private static final String DATA_BASE = "MySqlSimpleShard";
	//ShardColModShardStrategy;columns=CountryID;mod=2;tableColumns=CityID;tableMod=4;separator=_;shardedTables=person

	private static FreePersonDaoDao dao = null;
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
		dao = new FreePersonDaoDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	
	@Test
	public void testupdate() throws Exception {
		//String name = "";// Test value here
		//Integer cityId = 1;// Test value here
		//Integer countryId = 1;// Test value here
	    //int ret = dao.update(name, cityId, countryId, new DalHints());
	}
	
	@Test
	public void testfindFreeFirst() throws Exception {
		//String name = "";// Test value here
		//Integer cityIds = 1;// Test value here
		//FreeEntityPojo ret = dao.findFreeFirst(name, cityIds, new DalHints());
	}
	
	@Test
	public void testfindFreeList() throws Exception {
		//String name = "";// Test value here
		//Integer cityIds = 1;// Test value here
		//List<FreeEntityPojo> ret = dao.findFreeList(name, cityIds, new DalHints());
	}
	
	@Test
	public void testfindFreeListPage() throws Exception {
		//String name = "";// Test value here
		//Integer cityIds = 1;// Test value here
		//List<FreeEntityPojo> ret = dao.findFreeListPage(name, cityIds, 1, 10, new DalHints());
	}
	
	@Test
	public void testfindFreeSingle() throws Exception {
		//String name = "";// Test value here
		//Integer cityIds = 1;// Test value here
		//FreeEntityPojo ret = dao.findFreeSingle(name, cityIds, new DalHints());
	}
	
	@Test
	public void testfindFreeFieldFirst() throws Exception {
		//String name = "";// Test value here
		//Integer cityIds = 1;// Test value here
		//String ret = dao.findFreeFieldFirst(name, cityIds, new DalHints());
	}
	
	@Test
	public void testfindFreeFieldList() throws Exception {
		//String name = "";// Test value here
		//Integer cityIds = 1;// Test value here
		//List<String> ret = dao.findFreeFieldList(name, cityIds, new DalHints());
	}
	
	@Test
	public void testfindFreeFieldListPage() throws Exception {
		//String name = "";// Test value here
		//Integer cityIds = 1;// Test value here
		//List<String> ret = dao.findFreeFieldListPage(name, cityIds, 1, 10, new DalHints());
	}
	
	@Test
	public void testfindFreeFieldSingle() throws Exception {
		//String name = "";// Test value here
		//Integer cityIds = 1;// Test value here
		//String ret = dao.findFreeFieldSingle(name, cityIds, new DalHints());
	}

}
