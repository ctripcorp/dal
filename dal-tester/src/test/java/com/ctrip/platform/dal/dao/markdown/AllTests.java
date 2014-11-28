package com.ctrip.platform.dal.dao.markdown;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	MarkdownAndUpIntergration.class,
	DetectorCounterTest.class,
	TimeoutDetectorTest.class,
	AutoMarkdownTest.class,
	AutoMarkupTest.class,
	MarkupProcedureTest.class,
	ManualMarkDownTest.class,
	TimeBucketCounterTest.class,
	AsyncMarkupManagerTest.class})
public class AllTests {

}
