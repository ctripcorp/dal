package com.ctrip.platform.dal.tester;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ctrip.freeway.config.LogConfig;
import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.db.ConfigureServiceReader;
import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public class DirectClientDaoTest {
	private StatementParameters parameters = new StatementParameters();
	private DalHints hints = new DalHints();
	private String sql = "select [HotelID],[LatestBookTime],[UID]  from HotelLatestBookInfo hl with(nolock)  Join resource r with(nolock) on r.resource = hl.HotelID join city c (nolock) on c.city = r.city and c.city in (select city from city (nolock) where Country = 1) ";
	private String sql2 = "select * from Person";
	
	public void test() {
		try {
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
		try {
			DalClient client = DalClientFactory.getClient("dao_test");

			String delete = "delete from Person where id = 100";
			String insert = "insert into Person values(100, 'aaa', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";

			System.out.println("Executing" + delete);
			client.update(delete, parameters, hints);
			selectPerson(client);
			
			System.out.println("Executing" + insert);
			client.update(insert, parameters, hints);
			selectPerson(client);
			
			client.update(delete, parameters, hints);
			selectPerson(client);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testAutoIncrement() {
		try {
			DalClient client = DalClientFactory.getClient("dao_test");

			String delete = "delete from Person where id = ?";
			String insert = "insert into Person values(NULL, 'bbb', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";

			System.out.println("Executing" + insert);
			KeyHolder kh = new KeyHolder();
			client.update(insert, parameters, hints, kh);
			
			long id = kh.getKey().longValue();
			
			StatementParameters parameters = new StatementParameters();
			StatementParameter param  = StatementParameter.newBuilder().setDbType(DbType.Int32).setValue(id).setIndex(1).setName("").build();
			parameters.add(param);

			client.update(delete, parameters, hints);
			selectPerson(client);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testBatch() {
		try {
			DalClient client = DalClientFactory.getClient("dao_test");

			String delete = "delete from Person where id > 2000";
			String insert = "insert into Person values(NULL, 'bbb', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";
			String update = "update Person set name='abcde' where id > 2000";
			String[] sqls = new String[]{insert, insert, insert, update};

			System.out.println(client.batchUpdate(sqls, hints));

			client.update(delete, parameters, hints);
			selectPerson(client);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testBatch2() {
		try {
			DalClient client = DalClientFactory.getClient("dao_test");

			String insert = "insert into Person values(NULL, ?, ?, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";

			StatementParameters[] parameterList = new StatementParameters[3];
			
			for (int i = 0; i < parameterList.length; i++) {
				StatementParameters parameters = new StatementParameters();
				StatementParameter param;
				
				param = StatementParameter.newBuilder().setDbType(DbType.String).setValue("abcde" + i).setIndex(1).build();
				parameters.add(param);
				
				param  = StatementParameter.newBuilder().setDbType(DbType.Int32).setValue(i).setIndex(2).build();
				parameters.add(param);

				parameterList[i] = parameters;	
			}
			
			System.out.println(client.batchUpdate(insert, parameterList, hints));
			selectPerson(client);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testSP() {
		DalClient client = DalClientFactory.getClient("dao_test");
		
		try {
			int testId = 1000;
			StatementParameters parameters = new StatementParameters();
			
			parameters.set(1, Types.INTEGER, testId);
			// clean up
			String delete = "delete from Person where id = ?";
			client.update(delete, parameters, hints);
			
			String insert = "insert into Person values(?, 'aaa', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";
			client.update(insert, parameters, hints);
			//================

			parameters = new StatementParameters();
			parameters.set("userId", Types.INTEGER, testId);
			parameters.registerOut("pName", Types.VARCHAR);
			
			DalRowMapperExtractor<Map<String, Object>> extractor = new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper());
			parameters.add(StatementParameter.newBuilder().setResultsParameter(true).setResultSetExtractor(extractor).setName("result").build());
			parameters.add(StatementParameter.newBuilder().setResultsParameter(true).setName("count").build());

			System.out.println(client.call("call getPersonById(?, ?)", parameters, hints));
			
			// clean up
			parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, testId);
			delete = "delete from Person where id = ?";
			client.update(delete, parameters, hints);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void testSPInOut() {
		DalClient client = DalClientFactory.getClient("dao_test");
		
		try {
			int testId = 100;
			StatementParameters parameters = new StatementParameters();
			
			StatementParameter param  = StatementParameter.newBuilder().setDbType(DbType.String).setIndex(1).setName("version").setDirection(ParameterDirection.Output).build();
			parameters.add(param);

			param  = StatementParameter.newBuilder().setDbType(DbType.Int32).setValue(testId).setIndex(2).setName("increment").setDirection(ParameterDirection.InputOutput).build();
			parameters.add(param);

			DalRowMapperExtractor<Map<String, Object>> extractor = new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper());
			param = StatementParameter.newBuilder().setResultsParameter(true).setResultSetExtractor(extractor).setName("result").build();
			parameters.add(param);

			param  = StatementParameter.newBuilder().setResultsParameter(true).setName("count").build();
			parameters.add(param);

			System.out.println(client.call("call inOutTest(?, ?)", parameters, hints));
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testCommand() {
		try {
			DalClient client = DalClientFactory.getClient("dao_test");
			List<DalCommand> cmds = new LinkedList<DalCommand>();
			cmds.add(new DalCommand() {
				@Override
				public boolean execute(DalClient client) throws SQLException {
					String delete = "delete from Person where id > 2000";
					String insert = "insert into Person values(NULL, 'bbb', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";
					String update = "update Person set name='abcde' where id > 2000";
					String[] sqls = new String[]{insert, insert, insert, update};

					System.out.println(client.batchUpdate(sqls, hints));

					client.update(delete, parameters, hints);
					selectPerson(client);
					return true;
				}
			});
			
			cmds.add(new DalCommand() {
				@Override
				public boolean execute(DalClient client) throws SQLException {
					selectPerson(client);
					return false;
				}
			});

			cmds.add(new DalCommand() {
				@Override
				public boolean execute(DalClient client) throws SQLException {
					selectPerson(client);
					return true;
				}
			});

			client.execute(cmds, hints);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testConnectionException() {
		DalClient client = DalClientFactory.getClient("dao_test");
		
		try {
			client.query(sql, parameters, hints, new DalResultSetExtractor<Object>() {
				@Override
				public Object extract(ResultSet rs) throws SQLException {
					throw new RuntimeException("test");
				}
				
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void testTransactionException() {
		try {
			DalClient client = DalClientFactory.getClient("dao_test");
			List<DalCommand> cmds = new LinkedList<DalCommand>();

			cmds.add(new DalCommand() {
				@Override
				public boolean execute(DalClient client) throws SQLException {
					selectPerson(client);

					String insert = "insert into Person values(NULL, 'bbb', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";
					String update = "update Person set name='abcde' where id > 2000";
					String[] sqls = new String[]{insert, insert, insert, update};

					System.out.println(client.batchUpdate(sqls, hints));
					List<DalCommand> cmds = new LinkedList<DalCommand>();
					cmds.add(new DalCommand() {
						@Override
						public boolean execute(DalClient client) throws SQLException {
							String delete = "delete from xPerson where id > 2000";
							String insert = "insert into Person values(NULL, 'bbb', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";
							String update = "update Person set name='abcde' where id > 2000";
							String[] sqls = new String[]{insert, insert, insert, update};

							System.out.println(client.batchUpdate(sqls, hints));

							client.update(delete, parameters, hints);
							selectPerson(client);
							return true;
						}
					});

					client.execute(cmds, hints);
					return true;
				}
			});

			client.execute(cmds, hints);
		} catch (Exception e) {
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

	public void testType(String db, String table) {
		try {
			DalClient client = DalClientFactory.getClient(db);

			client.query("select * from " + table, parameters, hints, new ColumnTypeExtractor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
        LogConfig.setAppID("930201");
//      LogConfig.setLoggingServerIP("localhost");
        LogConfig.setLoggingServerIP("192.168.82.58");
        LogConfig.setLoggingServerPort("63100");

		Configuration.addResource("conf.properties");
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DalClientFactory.initDirectClientFactory(reader, "HtlProductdb", "dao_test");
		} catch (Exception e) {
			System.exit(0);
		}
		
		DirectClientDaoTest test = new DirectClientDaoTest();
		
		test.testType("dao_test", "ManyTypes");
//		test.test();
//		test.test2();
//		test.testAutoIncrement();
//		test.testBatch();
//		test.testBatch2();
//		test.testCommand();
		test.testSP();
//		test.testSPInOut();
//		test.testConnectionException();
//		test.testTransactionException();
		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}