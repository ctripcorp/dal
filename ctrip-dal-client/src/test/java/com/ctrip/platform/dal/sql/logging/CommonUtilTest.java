package com.ctrip.platform.dal.sql.logging;

import junit.framework.Assert;

import org.junit.Test;

public class CommonUtilTest {
	@Test
	public void testDesEncrypt() {
		Assert.assertEquals("VnsVK8ZdnkmTwqXTP+zi1g==", CommonUtil.desEncrypt("key1=value1"));
	}

	@Test
	public void testDesDecrypt() {
		Assert.assertEquals("key1=value1", CommonUtil.desDecrypt("VnsVK8ZdnkmTwqXTP+zi1g=="));
	}
}
