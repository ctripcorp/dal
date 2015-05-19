package com.ctrip.platform.dal.parser;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;

public class PerformanceTest {
	public static void main(String[] args) throws Exception{
		
		DalClientFactory.initClientFactory();
		int count = 100;
		String queryWhere = "id < " + count;
		normal(count, queryWhere);	
		jpa(count, queryWhere);
		normal(count, queryWhere);	
		jpa(count, queryWhere);
		System.exit(1);
	}
	
	public static void jpa(int insertCount, String queryWhere) throws SQLException{
		DalDefaultJpaPerformance performancer = new DalDefaultJpaPerformance(DatabaseCategory.MySql, "dao_test");
		performancer.dropAndCreateTable();
		long start = System.currentTimeMillis();
		
		performancer.randomInsert(insertCount);
		
		List<DalDefaultJpaPerformance.ClientTestModel> models = performancer.query(queryWhere);
		
		for (DalDefaultJpaPerformance.ClientTestModel model : models) {
			model.setAddress("PIS");
		}
		performancer.update(models);
		System.out.println("jpa time: " + (System.currentTimeMillis() - start) + "(ms)");
	}
	
	public static void normal(int insertCount, String queryWhere) throws SQLException{
		NormalParserPerformance performancer = new NormalParserPerformance();
		performancer.dropAndCreateTable();
		long start = System.currentTimeMillis();
		
		performancer.randomInsert(insertCount);
		
		List<NormalParserPerformance.ClientTestModel> models = performancer.query(queryWhere);
		
		for (NormalParserPerformance.ClientTestModel model : models) {
			model.setAddress("PIS");
		}
		performancer.update(models);
		System.out.println("normal time: " + (System.currentTimeMillis() - start) + "(ms)");
	}
}