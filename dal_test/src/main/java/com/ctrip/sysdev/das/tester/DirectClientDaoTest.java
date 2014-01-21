package com.ctrip.sysdev.das.tester;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dao.client.Client;
import com.ctrip.platform.dao.client.DirectClientFactory;
import com.ctrip.platform.dao.param.StatementParameter;
import com.ctrip.sysdev.das.common.cfg.DasConfigureService;
import com.ctrip.sysdev.das.common.db.ConfigureServiceReader;
import com.ctrip.sysdev.das.common.db.DasConfigureReader;
import com.ctrip.sysdev.das.common.util.Configuration;

public class DirectClientDaoTest {
	private List<StatementParameter> parameters = Collections.EMPTY_LIST;
	private Map keywordParameters = Collections.EMPTY_MAP;
	private String sql = "select [HotelID],[LatestBookTime],[UID]  from HotelLatestBookInfo hl with(nolock)  Join resource r with(nolock) on r.resource = hl.HotelID join city c (nolock) on c.city = r.city and c.city in (select city from city (nolock) where Country = 1) ";
	private String sql2 = "select * from Person";
	
	public void test() {
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DirectClientFactory factory = new DirectClientFactory(reader, "HtlProductdb");
			Client client = factory.getClient();

			ResultSet rs = client.fetch(sql, parameters, keywordParameters);
			int count = 0;
			try {
				while(rs.next()){
					rs.getString(1);
					count++;
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
			
			System.err.println("Result count: " + count);
			
			try {
				rs.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
			
			client.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test2() {
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DirectClientFactory factory = new DirectClientFactory(reader, "dao_test");
			Client client = factory.getClient();

			selectPerson(client);
			
			String insert = "insert into Person values(100, 'aaa', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";
			System.out.println("Executing" + insert);
			client.execute(insert, parameters, keywordParameters);
			selectPerson(client);
			
			String delete = "delete from Person where id = 100";
			System.out.println("Executing" + delete);
			client.execute(delete, parameters, keywordParameters);
			selectPerson(client);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void selectPerson(Client client) throws SQLException {
		ResultSet rs = client.fetch(sql2, parameters, keywordParameters);
		ResultSetMetaData meta = rs.getMetaData();
		int colCount = meta.getColumnCount();
		for(int i = 1; i <= colCount; i++){
			System.out.print(meta.getColumnName(i) + "\t");
		}
		System.out.println("\n==========");
		int count = 0;
		try {
			while(rs.next()){
				for(int i = 1; i <= colCount; i++)
					System.out.print(rs.getString(i) + "\t");
				System.out.println();
				count++;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		
		System.out.println("Result count: " + count);
		
		try {
			rs.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		
		client.closeConnection();
		
		System.out.println("Press anykey to continue...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Configuration.addResource("conf.properties");
		DirectClientDaoTest test = new DirectClientDaoTest();
		test.test();
		test.test2();
		System.exit(0);
	}
}
