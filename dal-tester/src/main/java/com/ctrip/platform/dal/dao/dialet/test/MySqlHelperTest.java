package com.ctrip.platform.dal.dao.dialet.test;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.dialect.DalMySqlHelper;
import com.ctrip.platform.dal.sql.logging.DalLogger;

public class MySqlHelperTest {

	private static DalMySqlHelper<Person> helper = null;
	private static DalTableDao<Person> client = null;
	private static PersonParser parser = null;
	private static DalHints hints = null;
	
	static{
		try {
			DalClientFactory.initClientFactory();
			DalLogger.setSimplifyLogging(true);
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
		Person[] persons = new Person[count];
		for(int i = 1; i <= count; i++){
		    Person pojo1 = new Person();
		    pojo1.setID(i);
		    pojo1.setName("forest" + i);
		    pojo1.setBirth(new Timestamp(System.currentTimeMillis()));
		    persons[i-1] = pojo1;
		}
		
		client.delete(hints, persons);
		KeyHolder holder = new KeyHolder();
		
		helper.replace(holder, hints, persons);
		
		List<Map<String, Object>> generateKeys = holder.getKeyList();
		assertTrue(generateKeys.size() == 3);
		assertTrue(generateKeys.get(0).containsKey("GENERATED_KEY"));
		
		persons[1].setName("jack1");
		helper.replace(holder, new DalHints(), persons);	
		Person rep = client.queryByPk(2, hints);
		assertTrue(rep.getName().equals("jack1"));
		
		client.delete(hints, persons);
	}
	
	@Test
	public void multipleInsert() throws SQLException {
		int count = 3;
		Person[] persons = new Person[count];
		for(int i = 1; i <= count; i++){
		    Person pojo1 = new Person();
		    pojo1.setID(i);
		    pojo1.setName("forest" + i);
		    pojo1.setBirth(new Timestamp(System.currentTimeMillis()));
		    persons[i-1] = pojo1;
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