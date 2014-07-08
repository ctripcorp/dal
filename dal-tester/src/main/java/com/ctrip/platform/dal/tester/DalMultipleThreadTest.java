package com.ctrip.platform.dal.tester;

import org.punit.convention.JUnitAnnotationConvention;
import org.punit.reporter.chart.OverviewReporter;
import org.punit.reporter.chart.image.ImageRender;
import org.punit.runner.ConcurrentRunner;
import org.punit.runner.SoloRunner;
import org.punit.watcher.MemoryWatcher;

import com.ctrip.platform.dal.dao.unittests.DalConcurrentMysqlTest;
import com.ctrip.platform.dal.dao.unittests.DalConcurrentSqlServerTest;
import com.ctrip.platform.dal.dao.unittests.DalDirectClientMySqlTest;
import com.ctrip.platform.dal.dao.unittests.DalDirectClientSqlServerTest;


public class DalMultipleThreadTest {
	
	public static void main(String[] args){
		SoloRunner runner = new SoloRunner();
		runner.addEventListener(new OverviewReporter(new ImageRender()));
		runner.methodRunner().addWatcher(new MemoryWatcher());
		runner.setConvention(new JUnitAnnotationConvention());
		
		runner.run(DalDirectClientMySqlTest.class);
		runner.run(DalDirectClientSqlServerTest.class);
		
		ConcurrentRunner crunner = new ConcurrentRunner(100);
		
		crunner.setConvention(new JUnitAnnotationConvention());
		crunner.addEventListener(new OverviewReporter(new ImageRender()));
		crunner.methodRunner().addWatcher(new MemoryWatcher());
		
		crunner.run(DalConcurrentSqlServerTest.class);
		crunner.run(DalConcurrentMysqlTest.class);
		System.exit(1);
	}
}
