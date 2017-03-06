package test.com.ctrip.platform.dal.dao.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;

public class DalConfigureFactoryTest {

	@Test
	public void testLoad() {
		// Test load from dal.xml
		try {
			assertNotNull(DalConfigureFactory.load());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 
	}

}
