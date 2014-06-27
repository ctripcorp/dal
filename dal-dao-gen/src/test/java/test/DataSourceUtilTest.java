package test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;

public class DataSourceUtilTest {
	
	@Test
	public void getConn() throws SQLException{
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < 1; i++) {
			Connection conn = DataSourceUtil.getConnection("AccHotelDB_SELECT_3");
			System.out.println("catalog is " + conn.getCatalog());
			conn.close();
		}
		
		System.out.println("cost time is " + (System.currentTimeMillis() - start) + "ms");
	}

}
