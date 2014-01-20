package com.ctrip.sysdev.das.tester;

import java.io.File;
import java.sql.ResultSet;
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

public class DirectClientDaoTest {
	private List<StatementParameter> parameters = Collections.EMPTY_LIST;
	private Map keywordParameters = Collections.EMPTY_MAP;
	private String sql = "select [HotelID],[LatestBookTime],[UID]  from HotelLatestBookInfo hl with(nolock)  Join resource r with(nolock) on r.resource = hl.HotelID join city c (nolock) on c.city = r.city and c.city in (select city from city (nolock) where Country = 1) ";
	
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

	public static void main(String[] args) {
		new DirectClientDaoTest().test();
		System.exit(0);
	}
}
