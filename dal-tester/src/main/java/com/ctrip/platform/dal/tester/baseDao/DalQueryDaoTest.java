package com.ctrip.platform.dal.tester.baseDao;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.ctrip.freeway.config.LogConfig;
import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.db.ConfigureServiceReader;
import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowCallback;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalQueryDaoTest {
	private StatementParameters parameters = new StatementParameters();
	private DalHints hints = new DalHints();
	private String sqlList = "select [HotelID],[LatestBookTime],[UID]  from HotelLatestBookInfo hl with(nolock)  Join resource r with(nolock) on r.resource = hl.HotelID join city c (nolock) on c.city = r.city and c.city in (select city from city (nolock) where Country = 1) ";
	private String sqlObject = "select [HotelID],[LatestBookTime],[UID]  from HotelLatestBookInfo hl with(nolock)  where hl.HotelID = ?";
	private String sql2 = "select * from Person";
	
	public void testQueryMapperForList() {
		try {
			DalQueryDao dao = new DalQueryDao("HtlProductdb");
			List<Integer> result = dao.query(sqlList, parameters, hints, new DalRowMapper<Integer>() {
				@Override
				public Integer map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt("HotelID");
				}
			});
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testQueryCallbackForList() {
		try {
			DalQueryDao dao = new DalQueryDao("HtlProductdb");
			dao.query(sqlList, parameters, hints, new DalRowCallback() {
				@Override
				public void process(ResultSet rs) throws SQLException {
					System.out.println(rs.getObject(1));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testQueryForObject() {
		DalQueryDao dao = new DalQueryDao("HtlProductdb");
		
		Integer id;
		// This will fail
		try {
			id = dao.queryForObject(sqlList, parameters, hints, new DalRowMapper<Integer>() {
				@Override
				public Integer map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt("HotelID");
				}
			});
			System.out.println(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// This will pass
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 73657);

			id = dao.queryForObject(sqlObject, parameters, hints, new DalRowMapper<Integer>() {
				@Override
				public Integer map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt("HotelID");
				}
			});
			System.out.println(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// This will fail
		try {
			dao.queryForObject(sqlList, parameters, hints, Integer.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// This will pass
		try {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.INTEGER, 73657);

			dao.queryForObject(sqlObject, parameters, hints, Integer.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testRange() {
		try {
			DalQueryDao dao = new DalQueryDao("HtlProductdb");
			Integer id = dao.queryFisrt(sqlList, parameters, hints, new DalRowMapper<Integer>() {
				@Override
				public Integer map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt("HotelID");
				}
			});
			System.out.println(id);
			
			List<Integer> result = dao.queryTop(sqlList, parameters, hints, new DalRowMapper<Integer>() {
				@Override
				public Integer map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt("HotelID");
				}
			}, 5);
			System.out.println(result);
			
			result = dao.queryFrom(sqlList, parameters, hints, new DalRowMapper<Integer>() {
				@Override
				public Integer map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt("HotelID");
				}
			}, 3, 5);
			System.out.println(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
        LogConfig.setAppID("9302011");
        LogConfig.setLoggingServerIP("192.168.82.58");
        LogConfig.setLoggingServerPort("63100");

		Configuration.addResource("conf.properties");
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DalClientFactory.initDirectClientFactory(reader, "HtlProductdb", "dao_test");
		} catch (Exception e) {
			System.exit(0);
		}
		
		DalQueryDaoTest test = new DalQueryDaoTest();
		
		
		test.testQueryMapperForList();
		test.testQueryCallbackForList();
		test.testQueryForObject();
		test.testRange();
		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
