package com.ctrip.platform.dal.dao.helper;

import org.junit.AfterClass;
import org.junit.Test;

public class DalSingleResultExtractorTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testExtract() {
		DalColumnMapRowMapper mapper = new DalColumnMapRowMapper();
		DalSingleResultExtractor test = new DalSingleResultExtractor(mapper, true);
		
		
	}

}
