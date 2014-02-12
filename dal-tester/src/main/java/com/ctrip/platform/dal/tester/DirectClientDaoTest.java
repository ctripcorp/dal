package com.ctrip.platform.dal.tester;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.db.ConfigureServiceReader;
import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DirectClientDaoTest {
	private StatementParameters parameters = new StatementParameters();
	private DalHints hints = new DalHints();
	private String sql = "select [HotelID],[LatestBookTime],[UID]  from HotelLatestBookInfo hl with(nolock)  Join resource r with(nolock) on r.resource = hl.HotelID join city c (nolock) on c.city = r.city and c.city in (select city from city (nolock) where Country = 1) ";
	private String sql2 = "select * from Person";
	
	public void test() {
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DalClientFactory.initDirectClientFactory(reader, "HtlProductdb", "dao_test");
			DalClient client = DalClientFactory.getClient("HtlProductdb");

			client.query(sql, parameters, hints, new DalResultSetExtractor<Object>() {
				@Override
				public Object extract(ResultSet rs) throws SQLException {
					while(rs.next()){
						rs.getString(1);
					}
					return null;
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test2() {
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DalClientFactory.initDirectClientFactory(reader, "dao_test");
			DalClient client = DalClientFactory.getClient("dao_test");

			selectPerson(client);
			
			String insert = "insert into Person values(100, 'aaa', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";
			System.out.println("Executing" + insert);
			client.update(insert, parameters, hints);
			selectPerson(client);
			
			String delete = "delete from Person where id = 100";
			System.out.println("Executing" + delete);
			client.update(delete, parameters, hints);
			selectPerson(client);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testSP() {
		DalClient client = DalClientFactory.getClient("dao_test");
		StatementParameter.Builder builder = StatementParameter.newBuilder();
		builder.setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(false);
		StatementParameter instance = builder.build();
		System.out.println(instance.build2SqlParameters().toByteArray());
		StatementParameters parameters = new StatementParameters();
		parameters.add(instance);
		
		try {
			String insert = "insert into Person values(100, 'aaa', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";
			client.update(insert, parameters, hints);
			client.call("call getPersonById(?)", parameters, hints);
			
			String delete = "delete from Person where id = 100";
			client.update(delete, parameters, hints);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void selectPerson(DalClient client) throws SQLException {
		client.query(sql2, parameters, hints, new DalResultSetExtractor<Object>() {
			private boolean headerDisplayed;
			private int colCount;
			@Override
			public Object extract(ResultSet rs) throws SQLException {
				if(!headerDisplayed) {
					ResultSetMetaData meta = rs.getMetaData();
					colCount = meta.getColumnCount();
					for(int i = 1; i <= colCount; i++){
						System.out.print(meta.getColumnName(i) + "\t");
					}
					System.out.println("\n==========");
					headerDisplayed = true;
				}
				
				int count = 0;
				while(rs.next()){
					for(int i = 1; i <= colCount; i++)
						System.out.print(rs.getString(i) + "\t");
					System.out.println();
					count++;
				}
				System.out.println("Result count: " + count);
				return null;
			}
			
		});
		
		
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
		test.testSP();
		System.exit(0);
	}
}