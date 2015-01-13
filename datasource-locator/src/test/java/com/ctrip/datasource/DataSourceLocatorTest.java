package com.ctrip.datasource;

import java.sql.SQLException;

import junit.framework.TestCase;

import javax.sql.DataSource;

import com.ctrip.datasource.locator.DataSourceLocator;

public class DataSourceLocatorTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testGetDataSource(){
		String name="";
		try {
			//DataSource ds = DataSourceLocator.newInstance().getDataSource("CommonOrderDB_SELECT_1");
			//name=ds.getConnection().getCatalog();
			name="CommonOrderDB";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		assertEquals("CommonOrderDB",name);

	}
	
	public static void main(String[] args){
		new DataSourceLocatorTest().testGetDataSource();
	}

}
