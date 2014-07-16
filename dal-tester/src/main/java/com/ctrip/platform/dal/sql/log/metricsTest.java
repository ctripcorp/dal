package com.ctrip.platform.dal.sql.log;
import java.io.IOException;

import org.junit.runner.JUnitCore;

import com.ctrip.platform.dal.dao.unittests.DalDirectClientMySqlTest;


public class metricsTest {
	public static void main(String[] args) throws IOException, InterruptedException{
		int c = 0;
		while(true){
			for(int i = 0; i < 1000; i++){
				new JUnitCore().run(DalDirectClientMySqlTest.class);
				//MetricsLogger.report("testDao", "testMethod", i%100, "success", i);	
				System.out.println(c++);
			}
			Thread.sleep(1000);
		}
	}
}
