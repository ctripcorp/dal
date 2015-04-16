package com.ctrip.platform.dal.tester.baseDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ctrip.framework.clogging.agent.config.LogConfig;
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
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.tester.ColumnTypeExtractor;

public class DirectClientDaoTest {
	private String sql = "SELECT * FROM [AbacusDB].[dbo].[AbacusPara]";
	private String sql2 = "select * from Person";
	
	public void test() {
		try {
			DalClient client = DalClientFactory.getClient("AbacusDB");
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();

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
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();

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
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();

			String delete = "delete from Person where id = ?";
			String insert = "insert into Person values(NULL, 'bbb', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00', NULL)";

			System.out.println("Executing" + insert);
			KeyHolder kh = new KeyHolder();
			client.update(insert, parameters, hints.setKeyHolder(kh));
			
			long id = kh.getKey().longValue();
			
			parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, id);

			client.update(delete, parameters, hints);
			selectPerson(client);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testBatch() {
		try {
			DalClient client = DalClientFactory.getClient("dao_test");
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();

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
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();

			String insert = "insert into Person values(NULL, ?, ?, 'aaaaa', 100, 1, '2012-05-01 10:10:00')";

			StatementParameters[] parameterList = new StatementParameters[3];
			
			for (int i = 0; i < parameterList.length; i++) {
				parameters = new StatementParameters();
				parameters.set(1, Types.VARCHAR, "abcde" + i);
				parameters.set(2, Types.INTEGER, i);
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
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();

		try {
			int testId = 1000;
			parameters = new StatementParameters();
			
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
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();

		try {
			int testId = 100;
			parameters = new StatementParameters();
			parameters.set("version", Types.VARCHAR, "version");
			parameters.set("increment", Types.INTEGER, testId);

			DalRowMapperExtractor<Map<String, Object>> extractor = new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper());
			parameters.setResultsParameter("result", extractor);
			parameters.setResultsParameter("count");

			System.out.println(client.call("call inOutTest(?, ?)", parameters, hints));
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testBatchSP() {
		DalClient client = DalClientFactory.getClient("dao_test");
		StatementParameters[] parametersList = new StatementParameters[3];
		StatementParameters parameters;
		DalHints hints = new DalHints();

		try {
			
			for (int i = 0; i < parametersList.length; i++) {
				parameters = new StatementParameters();
				parameters.set("v_address", Types.VARCHAR, "my address test");
				parameters.set("v_telephone", Types.VARCHAR, "12345678901");
				parameters.set("v_name", Types.VARCHAR, "my name");
				parameters.set("v_age", Types.INTEGER, 30);
				parameters.set("v_gender", Types.INTEGER, 1);
				parameters.set("v_birth", Types.TIMESTAMP, new Timestamp(System.currentTimeMillis()));
				parameters.set("v_PartmentID", Types.INTEGER, 3);
				parametersList[i] = parameters;	
			}

			int []result = client.batchCall("call insertPerson(?, ?,?, ?,?, ?,?)", parametersList, hints);

			System.out.println(result.length);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void testCommand() {
		try {
			DalClient client = DalClientFactory.getClient("dao_test");
			final StatementParameters parameters = new StatementParameters();
			final DalHints hints = new DalHints();

			DalCommand command = new DalCommand() {
				@Override
				public boolean execute(DalClient client) throws SQLException {
					String delete = "delete from Person where id > 2000";
					String insert = "insert into Person values(NULL, 'bbb', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00',1)";
					String update = "update Person set name='abcde' where id > 2000";
					String[] sqls = new String[]{delete, insert, insert, insert, update};

					System.out.println(client.batchUpdate(sqls, hints));

					client.update(delete, parameters, hints);
					selectPerson(client);
					return true;
				}
			};
			
			client.execute(command, hints);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testCommands() {
		try {
			DalClient client = DalClientFactory.getClient("dao_test");
			final StatementParameters parameters = new StatementParameters();
			final DalHints hints = new DalHints();

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
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();

		try {
			client.query(sql2, parameters, hints, new DalResultSetExtractor<Object>() {
				@Override
				public Object extract(ResultSet rs) throws SQLException {
					throw new RuntimeException("test");
				}
				
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void testDetectDistibutedTransaction() {
		try {
			DalClient daoTestClient = DalClientFactory.getClient("dao_test");
			
			final StatementParameters parameters = new StatementParameters();
			final DalHints hints = new DalHints();

			DalCommand command = new DalCommand() {
				@Override
				public boolean execute(DalClient client) throws SQLException {
					String delete = "delete from Person where id > 2000";
					String insert = "insert into Person values(NULL, 'bbb', 100, 'aaaaa', 100, 1, '2012-05-01 10:10:00',1)";
					String update = "update Person set name='abcde' where id > 2000";
					String[] sqls = new String[]{delete, insert, insert, insert, update};

					System.out.println(client.batchUpdate(sqls, hints));

					client.update(delete, parameters, hints);
					selectPerson(client);

					StatementParameters parameters = new StatementParameters();
					DalHints hints = new DalHints();
					DalClient abacusDBClient = DalClientFactory.getClient("AbacusDB");
					abacusDBClient.query(sql, parameters, hints, new DalScalarExtractor());
					return true;
				}
			};
			
			daoTestClient.execute(command, hints);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testTransactionException() {
		try {
			DalClient client = DalClientFactory.getClient("dao_test");
			final StatementParameters parameters = new StatementParameters();
			final DalHints hints = new DalHints();

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

					client.execute(cmds, hints);
					return true;
				}
			});

			client.execute(cmds, hints);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testSelect()
	{
		DalClient client = DalClientFactory.getClient("dao_test");
		try {	
			this.selectPerson(client);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void selectPerson(DalClient client) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();

		client.query(sql2, parameters, hints, new DalResultSetExtractor<List<Integer>>() {
			private boolean headerDisplayed;
			private int colCount;
			@Override
			public List<Integer> extract(ResultSet rs) throws SQLException {
				List<Integer> lts = new ArrayList<Integer>();
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
					lts.add(count);
				}
				System.out.println("Result count: " + count);
				return lts;
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
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();

			client.query("select * from " + table, parameters, hints, new ColumnTypeExtractor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testIsolationLevel() {
		try {
			// Test for simple connection
			DalClient client = DalClientFactory.getClient("dao_test");
			final DalHints hints = new DalHints();
			hints.setIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ);
			final StatementParameters parameters = new StatementParameters();

			client.query("select * from Person", parameters, hints, new ColumnTypeExtractor());
			
			
			//Test for transaction
			// this will pass
			List<DalCommand> cmds1 = new LinkedList<DalCommand>();
			cmds1.add(new DalCommand() {
				@Override
				public boolean execute(DalClient client) throws SQLException {
					selectPerson(client);
					return true;
				}
			});
			client.execute(cmds1, hints);

			// This will fail
			List<DalCommand> cmds = new LinkedList<DalCommand>();
			cmds.add(new DalCommand() {
				@Override
				public boolean execute(DalClient client) throws SQLException {
					selectPerson(client);

					selectPerson(client);
					List<DalCommand> cmds = new LinkedList<DalCommand>();
					cmds.add(new DalCommand() {
						@Override
						public boolean execute(DalClient client) throws SQLException {
							String update = "update Person set name='abcde' where id > 2000";
							String[] sqls = new String[]{update, update};

							System.out.println(client.batchUpdate(sqls, hints));

							client.update(update, parameters, hints);
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
	
	public void testDuplicateColumnName() throws SQLException
	{
		DalClient client = DalClientFactory.getClient("dao_test");
		final DalHints hints = new DalHints();
		final StatementParameters parameters = new StatementParameters();
		client.query("SELECT Per.ID as pId, Per.Name as pName, Part.Name as paName FROM Person AS Per JOIN Partment AS Part ON Per.PartmentID = Part.ID", 
				parameters, hints, new DalResultSetExtractor<Integer>(){

			@Override
			public Integer extract(ResultSet rs) throws SQLException {
				// TODO Auto-generated method stub
				while(rs.next())
				{
					ResultSetMetaData rsMeta = rs.getMetaData();
					for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
						System.out.println(rsMeta.getColumnLabel(i));
					}
					Object obj = rs.getObject("pId");
					Object obj0 = rs.getObject("paName");
					Object obj1 = rs.getObject(1);
					Object obj2 = rs.getObject(2);
					Object obj3 = rs.getObject(3);
					System.out.println(obj);
				}
				return null;
			}});
	}
	
	public void testCombinedAllInsertIntoOneClause(){
		
	}
	
	public static void main(String[] args) throws SQLException {
        LogConfig.setAppID("9302011");
//      LogConfig.setLoggingServerIP("localhost");
        LogConfig.setLoggingServerIP("192.168.82.58");
        LogConfig.setLoggingServerPort("63100");

//		Configuration.addResource("conf.properties");
//		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
//			DalClientFactory.initDirectClientFactory(reader, "dao_test");
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			System.exit(0);
		}
		
		DirectClientDaoTest test = new DirectClientDaoTest();
		
//		test.testBatchSP();
//		test.testCommand();
//		test.testIsolationLevel();
		
//		test.testDuplicateColumnName();
		//test.testType("dao_test", "ManyTypes");
//		test.testSP();
		test.test();
//		test.test2();
		/*test.testAutoIncrement();*/
		/*test.testBatch();
		test.testBatch2();/*
		test.testCommand();
		test.testSP();
		test.testSPInOut();
		test.testConnectionException();
		test.testTransactionException();*/
		test.testDetectDistibutedTransaction();
		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
