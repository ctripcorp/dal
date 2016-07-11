package com.ctrip.platform.dal.dao.status;

import java.util.HashSet;
import java.util.Set;

public class TimeoutMarkdown extends BaseStatus implements TimeoutMarkdownMBean {
	private volatile boolean enableTimeoutMarkDown;

    private volatile int samplingDuration = 120;

	private volatile int timeoutThreshold = 60;

    private volatile int errorCountThreshold = 300;

    private volatile float errorPercentThreshold = 0.5f;

	private volatile int errorPercentReferCount = 400;
	
    private volatile String mySqlErrorCodes = "0";

    private volatile String sqlServerErrorCodes = "-2";
    
    private volatile Set<Integer> mysqlTimeoutMarkdownCodes = new HashSet<Integer>();

    private volatile Set<Integer> sqlServerTimeoutMarkdownCodes = new HashSet<Integer>();
	
	public boolean isEnabled() {
		return enableTimeoutMarkDown;
	}
	public void setEnabled(boolean enabled) {
		this.enableTimeoutMarkDown = enabled;
		changed();
	}

	public int getSamplingDuration() {
		return samplingDuration;
	}
	public void setSamplingDuration(int samplingDuration) {
		this.samplingDuration = samplingDuration;
		changed();
	}

	public int getErrorCountThreshold() {
		return errorCountThreshold;
	}
	public void setErrorCountThreshold(int errorCountBaseLine) {
		this.errorCountThreshold = errorCountBaseLine;
		changed();
	}

	public float getErrorPercentThreshold() {
		return errorPercentThreshold;
	}
	public void setErrorPercentThreshold(float errorPercent) {
		this.errorPercentThreshold = errorPercent;
		changed();
	}

	public String getMySqlErrorCodes() {
		return mySqlErrorCodes;
	}
	public void setMySqlErrorCodes(String mySqlErrorCodes) {
		mysqlTimeoutMarkdownCodes = parseErrorCodes(mySqlErrorCodes);
		this.mySqlErrorCodes = mySqlErrorCodes;
		changed();
	}

	public int getErrorPercentReferCount() {
		return errorPercentReferCount;
	}
	public void setErrorPercentReferCount(int errorPercentBaseLine) {
		this.errorPercentReferCount = errorPercentBaseLine;
		changed();
	}

	public String getSqlServerErrorCodes() {
		return sqlServerErrorCodes;
	}
	public void setSqlServerErrorCodes(String sqlServerErrorCodes) {
		sqlServerTimeoutMarkdownCodes = parseErrorCodes(sqlServerErrorCodes);
		this.sqlServerErrorCodes = sqlServerErrorCodes;
		changed();
	}

	public int getTimeoutThreshold() {
		return timeoutThreshold;
	}
	public void setTimeoutThreshold(int minTimeOut) {
		this.timeoutThreshold = minTimeOut;
		changed();
	}

	public Set<Integer> getMysqlTimeoutMarkdownCodes() {
		return mysqlTimeoutMarkdownCodes;
	}
	public Set<Integer> getSqlServerTimeoutMarkdownCodes() {
		return sqlServerTimeoutMarkdownCodes;
	}	
	
	private Set<Integer> parseErrorCodes(String codes){
		Set<Integer> temp = new HashSet<Integer>();
		if(codes == null || codes.isEmpty())
			return temp;
		String[] tokens = codes.split(",");
		for (String token : tokens) {
			temp.add(Integer.valueOf(token));
		}
		return temp;
	}
}
