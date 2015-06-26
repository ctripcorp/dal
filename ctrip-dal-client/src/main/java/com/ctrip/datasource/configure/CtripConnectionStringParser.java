package com.ctrip.datasource.configure;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.security.encryption.Crypto;

public class CtripConnectionStringParser implements ConnectionStringParser {

	private static final Log log = LogFactory.getLog(CtripConnectionStringParser.class);
	
	@Override
	public String decrypt(String dbname, String connStr) {
		if (connStr!=null && -1==connStr.indexOf(';')) { // connStr was encrypted
			try {
				return Crypto.getInstance().decrypt(connStr);
			} catch(Exception e) {
				log.error("decode " + dbname + " connectionString exception, msg:" + e.getMessage(), e);
				throw new RuntimeException("decode " + dbname + " connectionString exception, msg:" + e.getMessage(), e);
			}
		} else {
			return connStr;
		}
	}

}
