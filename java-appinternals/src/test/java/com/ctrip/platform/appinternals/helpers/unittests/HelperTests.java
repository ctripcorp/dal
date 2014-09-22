package com.ctrip.platform.appinternals.helpers.unittests;

import java.util.Date;

import org.junit.Test;

import com.ctrip.platform.appinternals.helpers.Helper;
import com.ctrip.platform.appinternals.models.BeanView;

public class HelperTests {

	@Test
	public void testToXml() {
		BeanView view = new BeanView();
		view.setName("javaconfigbean");
		view.setUrl("http://localhost:24886/appinternals/configurations/beans/appinternalstest-configbeans-javaconfigbean?format=xml&action=view");
		view.setLastModifyTime(new Date(123));
		
		System.out.println(Helper.toXML(BeanView.class, "Component", view));
	}
	
	@Test
	public void testToJson() {
		BeanView view = new BeanView();
		view.setName("javaconfigbean");
		view.setUrl("http://localhost:24886/appinternals/configurations/beans/appinternalstest-configbeans-javaconfigbean?format=xml&action=view");
		view.setLastModifyTime(new Date(123));
		
		System.out.println(Helper.toJSON(BeanView.class, "Component", view));
	}
}
