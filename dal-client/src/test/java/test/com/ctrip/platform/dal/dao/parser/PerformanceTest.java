package test.com.ctrip.platform.dal.dao.parser;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;

public class PerformanceTest {
	public static void main(String[] args) throws Exception{
		
		DalClientFactory.initClientFactory();
		DalClientFactory.warmUpConnections();
		jpa(1, "id < 1");
		int[] samples = new int[]{1,10,100,1000,2000,3000,4000,6000,8000,10000};
		for (int count : samples) {
			System.out.println(String.format("%1$s条insert, %1$s条query，%1$s条update（下面的每行数据代表一次完整的insert、query、update执行）", count));
			String queryWhere = "id < " + count;
			jpa(count, queryWhere);
			normal(count, queryWhere);	
			normal(count, queryWhere);	
			jpa(count, queryWhere);
			jpa(count, queryWhere);
			normal(count, queryWhere);	
		}
		System.exit(1);
	}
	
	public static void jpa(int insertCount, String queryWhere) throws SQLException{
		DalDefaultJpaPerformance performancer = new DalDefaultJpaPerformance(DatabaseCategory.MySql);
		performancer.dropAndCreateTable();
		long start = System.currentTimeMillis();
		performancer.randomInsert(insertCount);
		List<DalDefaultJpaPerformance.ClientTestModel> models = performancer.query(queryWhere);
		for (DalDefaultJpaPerformance.ClientTestModel model : models) {
			model.setAddress1("PIS");
			model.setAddress2("PIS");
			model.setAddress3("PIS");
			model.setAddress4("PIS");
			model.setAddress5("PIS");
			model.setAddress6("PIS");
			model.setAddress7("PIS");
			model.setAddress8("PIS");
			model.setAddress9("PIS");
			model.setAddress10("PIS");
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
			model.setAddress1("PIS");
			model.setAddress2("PIS");
			model.setAddress3("PIS");
			model.setAddress4("PIS");
			model.setAddress5("PIS");
			model.setAddress6("PIS");
			model.setAddress7("PIS");
			model.setAddress8("PIS");
			model.setAddress9("PIS");
			model.setAddress10("PIS");
		}
		performancer.update(models);
		System.out.println("normal time: " + (System.currentTimeMillis() - start) + "(ms)");
	}
}