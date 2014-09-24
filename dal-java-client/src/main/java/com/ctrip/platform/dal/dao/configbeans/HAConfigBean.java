package com.ctrip.platform.dal.dao.configbeans;

import java.util.HashSet;
import java.util.Set;

import com.ctrip.platform.appinternals.annotations.BeanMeta;
import com.ctrip.platform.appinternals.configuration.ChangeEvent;
import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;

@BeanMeta(alias = "haconf")
public class HAConfigBean extends ConfigBeanBase{
	private boolean enable = false;
	private int retryCount = 1;
	private String sqlserverErrorCodes = "-2,233,845,846,847,1421,2,53,701,802,945,1204,1222";
	private String mysqlErrorCodes = "1043,1159,1161,1021,1037,1038,1039,1040,1041,1154,1158,1160,1189,1190,1205,1218,1219,1220";
	
	@BeanMeta(omit = true)
	private Set<Integer> sqlservercodes = new HashSet<Integer>();
	@BeanMeta(omit = true)
	private Set<Integer> mysqlcodes = new HashSet<Integer>();
	
	public HAConfigBean(){
		
		sqlservercodes = parseErrorCodes(this.sqlserverErrorCodes);
		mysqlcodes = parseErrorCodes(this.mysqlErrorCodes);
		
		this.addChangeEvent("sqlserverErrorCodes", new ChangeEvent() {		
			@Override
			public void callback(String oldVal, String newVal) {
				sqlservercodes = parseErrorCodes(newVal);
			}
		});
		
		this.addChangeEvent("mysqlErrorCodes", new ChangeEvent() {
			@Override
			public void callback(String oldVal, String newVal) {
				mysqlcodes = parseErrorCodes(newVal);
			}
		});
	}
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public String getSqlserverErrorCodes() {
		return sqlserverErrorCodes;
	}
	public void setSqlserverErrorCodes(String sqlserverErrorCodes) {
		this.sqlserverErrorCodes = sqlserverErrorCodes;
	}
	public String getMysqlErrorCodes() {
		return mysqlErrorCodes;
	}
	public void setMysqlErrorCodes(String mysqlErrorCodes) {
		this.mysqlErrorCodes = mysqlErrorCodes;
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
