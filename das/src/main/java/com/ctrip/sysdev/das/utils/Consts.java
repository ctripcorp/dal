package com.ctrip.sysdev.das.utils;

public class Consts {

	//For Data Access Service client
	public static final String serverAddr = "0.0.0.0";
	public static final int serverPort = 9000;
	public static final int protocolVersion = 1;
	public static final int databaseId = 32;
	public static final String credential = "user=link;password=snow";

	//For mysql DB Client
//	public static final String connectionString = "jdbc:mysql://192.168.83.132:3306/dao_test";
//	public static final String user = "root";
//	public static final String password = "123456";
	
	//For SQL Server DB Client
	public static final String connectionString = "jdbc:sqlserver://testdb.dev.sh.ctriptravel.com:28747;DatabaseName=GeneratorDAO;integratedSecurity=true";
	public static final String user = "";
	public static final String password = "";

}
