package com.ctrip.platform.dal.dao.status;

public interface TimeoutMarkdownMBean {

	boolean isEnabled();

	void setEnabled(boolean enabled);

	int getSamplingDuration();

	void setSamplingDuration(int samplingDuration);

	int getErrorCountThreshold();

	void setErrorCountThreshold(int errorCountBaseLine);

	float getErrorPercentThreshold();

	void setErrorPercentThreshold(float errorPercent);

	String getMySqlErrorCodes();

	void setMySqlErrorCodes(String mySqlErrorCodes);

	int getErrorPercentReferCount();

	void setErrorPercentReferCount(int errorPercentBaseLine);

	String getSqlServerErrorCodes();

	void setSqlServerErrorCodes(String sqlServerErrorCodes);

	int getTimeoutThreshold();

	void setTimeoutThreshold(int minTimeOut);

}