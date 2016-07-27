package com.ctrip.platform.dal.dao.status;

import java.util.HashSet;
import java.util.Set;

public class HAStatus extends BaseStatus implements HAStatusMBean {

	private volatile boolean enabled = false;

	private volatile int retryCount = 1;

	private volatile String sqlserverErrorCodes = "-2,233,845,846,847,1421,2,53,701,802,945,1204,1222";

	private volatile String mysqlErrorCodes = "1043,1159,1161,1021,1037,1038,1039,1040,1041,1154,1158,1160,1189,1190,1205,1218,1219,1220";
	
	private volatile Set<Integer> sqlservercodes = new HashSet<Integer>();

	private volatile Set<Integer> mysqlcodes = new HashSet<Integer>();
	
	public HAStatus() {
		sqlservercodes = parseErrorCodes(sqlserverErrorCodes);
		mysqlcodes = parseErrorCodes(mysqlErrorCodes);
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		changed();
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
		changed();
	}
	
	public String getSqlserverErrorCodes() {
		return sqlserverErrorCodes;
	}
	
	public void setSqlserverErrorCodes(String sqlserverErrorCodes) {
		sqlservercodes = parseErrorCodes(sqlserverErrorCodes);
		this.sqlserverErrorCodes = sqlserverErrorCodes;
		changed();
	}

	public String getMysqlErrorCodes() {
		return mysqlErrorCodes;
	}
	
	public void setMysqlErrorCodes(String mysqlErrorCodes) {
		mysqlcodes = parseErrorCodes(mysqlErrorCodes);
		this.mysqlErrorCodes = mysqlErrorCodes;
		changed();
	}

	public Set<Integer> getSqlservercodes() {
		return sqlservercodes;
	}

	public Set<Integer> getMysqlcodes() {
		return mysqlcodes;
	}
	
	private Set<Integer> parseErrorCodes(String codes){
		Set<Integer> temp = new HashSet<Integer>();
		String[] tokens = codes.split(",");
		for (String code : tokens) {
			temp.add(Integer.valueOf(code));
		}
		return temp;
	}
}
