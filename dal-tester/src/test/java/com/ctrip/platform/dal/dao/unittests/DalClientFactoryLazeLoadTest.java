package com.ctrip.platform.dal.dao.unittests;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class DalClientFactoryLazeLoadTest {
	@Test
	public void testLazeLoad(){
		Assert.assertNotNull(DalClientFactory.getClient("dao_test"));
	}
}
