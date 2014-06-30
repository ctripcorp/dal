package test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;

public class DataSourceUtilTest {
	
	@Test
	public void getConn1() throws SQLException{
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < 1; i++) {
			Connection conn = DataSourceUtil.getConnection("AccHotelDB_SELECT_3");
			System.out.println("catalog is " + conn.getCatalog());
			conn.close();
		}
		
		System.out.println("cost time is " + (System.currentTimeMillis() - start) + "ms");
	}
	
	@Test
	public void getConn2() throws SQLException{
		long start = System.currentTimeMillis();
		
		String address = "192.168.83.132";
		String port = "3306"; 
		String userName = "root";  
		String password = "platform_2014";  
		String driverClass = DatabaseType.MySQL.getValue(); 
		
		for (int i = 0; i < 1; i++) {
			Connection conn = DataSourceUtil.getConnection(address, port, userName, password, driverClass);
			System.out.println("catalog is " + conn.getCatalog());
			conn.close();
		}
		
		System.out.println("mysql cost time is " + (System.currentTimeMillis() - start) + "ms");
	}
	
	@Test
	public void getConn3() throws SQLException{
		long start = System.currentTimeMillis();
		
		String address = "devdb.dev.sh.ctriptravel.com"; 
		String port = "28747"; 
		String userName = "uws_AllInOneKey_dev";  
		String password = "!QAZ@WSX1qaz2wsx";  
		String driverClass = DatabaseType.SQLServer.getValue(); 
		
		for (int i = 0; i < 1; i++) {
			Connection conn = DataSourceUtil.getConnection(address, port, userName, password, driverClass);
			System.out.println("catalog is " + conn.getCatalog());
			conn.close();
		}
		
		System.out.println("sqlserver cost time is " + (System.currentTimeMillis() - start) + "ms");
	}

}
