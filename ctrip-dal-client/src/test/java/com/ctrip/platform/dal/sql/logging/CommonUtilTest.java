package com.ctrip.platform.dal.sql.logging;

import junit.framework.Assert;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class CommonUtilTest {
	@Test
	public void testDesEncrypt() {
		Assert.assertEquals("VnsVK8ZdnkmTwqXTP+zi1g==", CommonUtil.desEncrypt("key1=value1"));
	}

	@Test
	public void testDesDecrypt() {
		Assert.assertEquals("key1=value1", CommonUtil.desDecrypt("VnsVK8ZdnkmTwqXTP+zi1g=="));
	}

	@Test
	public void testSetToString() {
		try {
			Set<String> set1 = new HashSet<>();
			set1.add("aa");
			set1.add("bb");
			set1.add("cc");
			String str1 = CommonUtil.setToOrderedString(set1);

			Set<String> set2 = new HashSet<>();
			set2.add("bb");
			set2.add("aa");
			set2.add("cc");
			String str2 = CommonUtil.setToOrderedString(set2);

			Set<String> set3 = new HashSet<>();
			set3.add("cc");
			set3.add("aa");
			set3.add("bb");
			String str3 = CommonUtil.setToOrderedString(set3);
			assertTrue(str1.equals(str2));
			assertTrue(str3.equals(str2));

			Set<String> set4 = new HashSet<>();
			set4.add("switch off");
			String str4 = CommonUtil.setToOrderedString(set4);
			assertTrue("switch off".equals(str4));

			String str5 = CommonUtil.setToOrderedString(null);
			assertTrue("empty tables".equals(str5));

			Set<String> set6 = new HashSet<>();
			set6.add("not supported");
			String str6 = CommonUtil.setToOrderedString(set6);
			assertTrue("not supported".equals(str6));
		} catch (Throwable e) {
			e.printStackTrace();
			fail();
		}
	}
}
