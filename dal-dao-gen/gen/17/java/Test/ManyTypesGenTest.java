package com.ctrip.shard;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.*;

public class ManyTypesGenDaoTest {
	public static void main(String[] args) {
		
		try {
			// Initialize DalClientFactory
			DalClientFactory.initClientFactory("dao_test");
			ManyTypesGenDao dao = new ManyTypesGenDao();
		
			ManyTypesGen pk = dao.queryByPk(null);// you value here
			
			pk = dao.queryByPk(pk);
			List<ManyTypesGen> pojos = dao.queryByPage(pk, 100, 0);
			
			// test insert
			ManyTypesGen pojo1 = new ManyTypesGen();
			ManyTypesGen pojo2 = new ManyTypesGen();
			ManyTypesGen pojo3 = new ManyTypesGen();
			pojos.add(pojo1, pojo2, pojo3);
			
			KeyHolder keyHolder = new KeyHolder();
			ManyTypesGen pojo4 = new ManyTypesGen();
			ManyTypesGen pojo5 = new ManyTypesGen();
			ManyTypesGen pojo6 = new ManyTypesGen();
			dao.insert(keyHolder, pojo4, pojo5, pojo6);
			
			dao.delete(pojo1, pojo2, pojo3);
			dao.update(pojo4, pojo5, pojo6);
			Map<String, ?> result = dao.callManyTypesGen(param);

			// Test additional customized method
			int affectedRows = 0;
			List<ManyTypesGen> results = null;
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}