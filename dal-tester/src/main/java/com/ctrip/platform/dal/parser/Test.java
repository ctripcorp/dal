package com.ctrip.platform.dal.parser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.ext.parser.EnteroctopusDao;

public class Test {

	public static void main(String[] args) throws Exception {
		DalClientFactory.initPrivateFactory();
		EnteroctopusDao dao = EnteroctopusDao.create(Person.class);
		
		List<Person> entities = new ArrayList<Person>();
		for(int i = 100; i > 0; i --){
			Person person = new Person();
			person.setAddress("tsh");
			person.setAge(15);
			person.setBirth(new Timestamp((new Date()).getTime()));
			person.setGender(1);
			person.setName("forest0" + i);
			person.setPartmentID(1);
			person.setTelephone("110");
			entities.add(person);
		}
		KeyHolder hk = dao.batchInsert(entities, false);
		
		for(Map<String, Object> ent : hk.getKeyList()){
			for(Map.Entry<String, Object> en : ent.entrySet())
			System.out.println(String.format("key=%s;value=%s", en.getKey(), en.getValue()));
		}
		
		List<Person> all = dao.queryAll();
		System.out.println(all.size());
		
		String condition = "Address like ?";
		
		List<Person> like = dao.queryByCondition(condition, "%tsh%");
		System.out.println(like.size());
		
	}

}
