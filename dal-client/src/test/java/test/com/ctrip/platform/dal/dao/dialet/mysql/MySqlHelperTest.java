package test.com.ctrip.platform.dal.dao.dialet.mysql;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.dialect.DalMySqlHelper;

public class MySqlHelperTest {
	private final static String DATABASE_NAME_MYSQL = PersonParser.DATABASE_NAME;
	
	private final static String TABLE_NAME = PersonParser.TABLE_NAME;
	
	private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"("
			+ "ID int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "Address VARCHAR(64), "
			+ "Telephone VARCHAR(64), "
			+ "Name VARCHAR(64), "
			+ "Age int,"
			+ "Gender int,"
			+ "Birth timestamp default CURRENT_TIMESTAMP,"
			+ "PartmentID int)";
	
	private static DalClient clientMySql;
	
	static {
		try {
			DalClientFactory.initClientFactory();
			clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() {
		DalHints hints = new DalHints();
		String[] sqls = new String[]{DROP_TABLE_SQL_MYSQL_TPL, CREATE_TABLE_SQL_MYSQL_TPL};
		try {
			clientMySql.batchUpdate(sqls, hints);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[]{DROP_TABLE_SQL_MYSQL_TPL};
		clientMySql.batchUpdate(sqls, hints);
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		List<Person> plist = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			Person p = new Person();
			p.setID(i+1);
			p.setName("forest");
			plist.add(p);
		}
		client.insert(hints, plist);
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		clientMySql.update(sql, parameters, hints);
	}

	private static DalMySqlHelper<Person> helper = null;
	private static DalTableDao<Person> client = null;
	private static PersonParser parser = null;
	private static DalHints hints = null;
	
	static{
		try {
			DalClientFactory.initClientFactory();
//			DalLogger.setSimplifyLogging(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		hints = new DalHints();
		parser = new PersonParser();
		client = new DalTableDao<Person>(parser);
		helper = new DalMySqlHelper<Person>(parser);
	}
	
	@Test
	public void replaceTest() throws SQLException {
		int count = 3;
		List<Person> persons = new ArrayList<>();
		for(int i = 1; i <= count; i++){
		    Person pojo1 = new Person();
		    pojo1.setID(i);
		    pojo1.setName("forest" + i);
		    pojo1.setBirth(new Timestamp(System.currentTimeMillis()));
		    persons.add(pojo1);
		}
		
		client.delete(hints, persons);
		KeyHolder holder = new KeyHolder();
		
		helper.replace(holder, hints, persons);
		
		List<Map<String, Object>> generateKeys = holder.getKeyList();
		assertTrue(generateKeys.size() == 3);
		assertTrue(generateKeys.get(0).containsKey("GENERATED_KEY"));
		
		persons.get(1).setName("jack1");
		helper.replace(holder, new DalHints(), persons);	
		Person rep = client.queryByPk(2, hints);
		assertTrue(rep.getName().equals("jack1"));
		
		client.delete(hints, persons);
	}
	
	@Test
	public void replaceOneTest() throws SQLException {
		Person pojo1 = new Person();
	    pojo1.setID(1);
	    pojo1.setName("forest" + 1);
	    pojo1.setBirth(new Timestamp(System.currentTimeMillis()));
		
		client.delete(hints, pojo1);
		KeyHolder holder = new KeyHolder();
		
		helper.replace(holder, hints, pojo1);
		
		List<Map<String, Object>> generateKeys = holder.getKeyList();
		assertTrue(generateKeys.size() == 1);
		assertTrue(generateKeys.get(0).containsKey("GENERATED_KEY"));
		
		pojo1.setName("jack1");
		helper.replace(holder, new DalHints(), pojo1);	
		Person rep = client.queryByPk(1, hints);
		assertTrue(rep.getName().equals("jack1"));
		
		client.delete(hints, pojo1);
	}
	
	@Test
	public void multipleInsert() throws SQLException {
		int count = 3;
		List<Person> persons = new ArrayList<>();
		for(int i = 1; i <= count; i++){
		    Person pojo1 = new Person();
		    pojo1.setID(i);
		    pojo1.setName("forest" + i);
		    pojo1.setBirth(new Timestamp(System.currentTimeMillis()));
		    persons.add(pojo1);
		}

		KeyHolder holder = new KeyHolder();
		client.combinedInsert(hints, holder, persons);
		
		List<Map<String, Object>> generateKeys = holder.getKeyList();
		assertTrue(generateKeys.size() == 3);
		assertTrue(generateKeys.get(0).containsKey("GENERATED_KEY"));
		
		for(Map<String, Object> genk : generateKeys){
			Number id = (Number) genk.get("GENERATED_KEY");
			Person person = new Person();
			person.setID(id.intValue());
			client.delete(hints, person);
		}
	}
}