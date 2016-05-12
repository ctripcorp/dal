package com.dal.sqlserver.test.control;

import java.net.URLDecoder;
import java.sql.Connection;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.sql.logging.CommonUtil;
import com.dal.sqlserver.test.PeopleDao;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class ManagementAndTimeout implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		DalHints hints = context.getHints().timeout(1);
		PeopleDao dao = context.getDao();
		
		DalClient client = DalClientFactory.getClient("MultiThreadingTest");
		Object value = null;
		try {
			switch (context.getAction()) {
			case "timeout":
				value = client.query("SELECT * FROM dal_client_test_big WHERE address like '%TIMEOUT%' ORDER BY address", new StatementParameters(), hints, new DalRowMapperExtractor<>(new DalColumnMapRowMapper()));
				break;
			case "nontimeout":
				value = client.query("SELECT TOP 10 * FROM dal_client_test_big", new StatementParameters(), hints, new DalRowMapperExtractor<>(new DalColumnMapRowMapper()));
				break;
			case "decrypt":
				value = decrypt(context);
				break;
			case "checkConnection":
				value = checkConnection(context);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			context.handle(e);
		}
		
		context.setResponsValue(value);
	}
	
	private String decrypt(WebContext context) throws Exception {
		String rawValue = context.get("value");
		String str = CommonUtil.desDecrypt(rawValue);
		if(rawValue.equals(str))
			str = CommonUtil.desDecrypt(URLDecoder.decode(rawValue));

		return rawValue.equals(str) ?
				"Decrypt faild for:<br/>" + rawValue:
					"Original Content is:<br/>" + str;
	}
	
	private String checkConnection(WebContext context) throws Exception {
		String keyName = context.get("keyName");
		String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";

		DalDataSourceFactory dl = new DalDataSourceFactory();
		Connection conn = dl.createDataSource(keyName, fws, "12233").getConnection();
		conn.close();
		return "Connect to: " + keyName + " success";
	}
}
