package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
