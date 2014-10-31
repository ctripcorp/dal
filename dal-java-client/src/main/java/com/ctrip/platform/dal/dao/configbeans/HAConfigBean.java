package com.ctrip.platform.dal.dao.configbeans;

import java.util.HashSet;
import java.util.Set;

import com.ctrip.platform.appinternals.annotations.BeanMeta;
import com.ctrip.platform.appinternals.configuration.ChangeEvent;
import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;

@BeanMeta(alias = "arch-data-common-bean-habean")
public class HAConfigBean extends ConfigBeanBase{
	@BeanMeta(alias = "HAEnabled")
	private volatile boolean enable = false;
	@BeanMeta(alias = "RetryTimes")
	private volatile int retryCount = 1;
	@BeanMeta(alias = "SqlServerHAErrorCodes")
	private String sqlserverErrorCodes = "-2,233,845,846,847,1421,2,53,701,802,945,1204,1222";
	@BeanMeta(alias = "MySqlHAErrorCodes")
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
			public void before(Object oldVal, String newVal) throws Exception {
				// TODO Auto-generated method stub	
			}

			@Override
			public void end(Object oldVal, String newVal) throws Exception {
				sqlservercodes = parseErrorCodes(newVal);
			}
		});
		
		this.addChangeEvent("mysqlErrorCodes", new ChangeEvent() {
			@Override
			public void before(Object oldVal, String newVal) throws Exception {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void end(Object oldVal, String newVal) throws Exception {
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
