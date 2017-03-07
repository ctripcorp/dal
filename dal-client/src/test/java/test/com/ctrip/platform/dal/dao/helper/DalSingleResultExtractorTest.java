package test.com.ctrip.platform.dal.dao.helper;

import org.junit.AfterClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalSingleResultExtractor;

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
