package com.ctrip.platform.dal.tester.manyTypes;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.ctrip.freeway.config.LogConfig;
import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.db.ConfigureServiceReader;
import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public class ManyuTypesTest {
	private static StatementParameters parameters = new StatementParameters();
	private static DalHints hints = new DalHints();

	public static void main(String[] args) {
        LogConfig.setAppID("929143");
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

		try {
			DalHints hints = new DalHints();
			
			DalTableDao<Manytypes> dao = new DalTableDao<Manytypes>(new DalManytypesParser());
			Manytypes p = new Manytypes();
//			ret = p.setNameAndAddress(name,address);
			
			p.setBigIntCol(1L);
			p.setBinaryCol("BinaryCol".getBytes());
			dao.insert(hints, p, p, p);
			
			KeyHolder kh = new KeyHolder();
			dao.insert(hints, kh, p, p, p);
			List<Map<String, Object>> k = kh.getKeyList();
			
			p.setId((Integer)k.get(0).get(DalTableDao.GENERATED_KEY));
			
			p.setBitCol(true);
			dao.update(hints, p);
			
			List<Manytypes> mts = dao.query("id > 0", parameters, hints);
			
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}
