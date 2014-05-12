package com.ctrip.platform.dal.parser;

import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
//import com.ctrip.platform.dal.parser.NormalParserPerformance.ClientTestModel;
import com.ctrip.platform.dal.parser.DalDefaultJpaPerformance.ClientTestModel;

public class PerformanceTest {
	public static void main(String[] args) throws Exception{
		
		DalClientFactory.initPrivateFactory();
		
		//NormalParserPerformance performancer = new NormalParserPerformance();
		DalDefaultJpaPerformance performancer = new DalDefaultJpaPerformance(DatabaseCategory.MySql, "dao_test");
		performancer.dropAndCreateTable();
		performancer.randomInsert(1000);
		List<ClientTestModel> models = performancer.query("id<100");
		System.out.println(models.size());
		for (ClientTestModel model : models) {
			model.setAddress("PIS");
		}
		performancer.update(models);
		
		System.exit(1);
	}
}
