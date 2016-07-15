package com.ctrip.platform.dal.dao.vi;

import com.ctrip.framework.vi.annotation.ComponentStatus;
import com.ctrip.framework.vi.annotation.FieldInfo;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.status.TimeoutMarkdown;

@ComponentStatus(id="com.ctrip.dal.client.TimeoutMarkDownBean",name="com.ctrip.dal.client.TimeoutMarkDownBean",description = "DAL Timeout Markdown config")
public class TimeoutMarkDownBean extends BaseConfigBean {
	@FieldInfo(name = "EnableTimeoutMarkDown", description = "EnableTimeoutMarkDown")
	private boolean enableTimeoutMarkDown;

	@FieldInfo(name = "SamplingDuration", description = "SamplingDuration")
    private int samplingDuration;

	@FieldInfo(name = "TimeoutThreshold", description = "TimeoutThreshold")
	private int timeoutThreshold;

	@FieldInfo(name = "ErrorCountThreshold", description = "ErrorCountThreshold")
    private int errorCountThreshold;

	@FieldInfo(name = "ErrorPercentThreshold", description = "ErrorPercentThreshold")
    private float errorPercentThreshold;

	@FieldInfo(name = "ErrorPercentReferCount", description = "ErrorPercentReferCount")
	private int errorPercentReferCount;
	
	@FieldInfo(name = "MySqlErrorCodes", description = "MySqlErrorCodes")
    private String mySqlErrorCodes;

	@FieldInfo(name = "SqlServerErrorCodes", description = "SqlServerErrorCodes")
    private String sqlServerErrorCodes;

	public boolean isEnableTimeoutMarkDown() {
		return enableTimeoutMarkDown;
	}
	public void setEnableTimeoutMarkDown(boolean enableTimeoutMarkDown) {
		this.enableTimeoutMarkDown = enableTimeoutMarkDown;
	}
	public int getSamplingDuration() {
		return samplingDuration;
	}
	public void setSamplingDuration(int samplingDuration) {
		this.samplingDuration = samplingDuration;
	}
	public int getErrorCountThreshold() {
		return errorCountThreshold;
	}
	public void setErrorCountThreshold(int errorCountBaseLine) {
		this.errorCountThreshold = errorCountBaseLine;
	}
	public float getErrorPercentThreshold() {
		return errorPercentThreshold;
	}
	public void setErrorPercentThreshold(float errorPercent) {
		this.errorPercentThreshold = errorPercent;
	}
	public String getMySqlErrorCodes() {
		return mySqlErrorCodes;
	}
	public void setMySqlErrorCodes(String mySqlErrorCodes) {
		this.mySqlErrorCodes = mySqlErrorCodes;
	}
	public int getErrorPercentReferCount() {
		return errorPercentReferCount;
	}
	public void setErrorPercentReferCount(int errorPercentBaseLine) {
		this.errorPercentReferCount = errorPercentBaseLine;
	}
	public String getSqlServerErrorCodes() {
		return sqlServerErrorCodes;
	}
	public void setSqlServerErrorCodes(String sqlServerErrorCodes) {
		this.sqlServerErrorCodes = sqlServerErrorCodes;
	}
	public int getTimeoutThreshold() {
		return timeoutThreshold;
	}
	public void setTimeoutThreshold(int minTimeOut) {
		this.timeoutThreshold = minTimeOut;
	}

	@Override
	protected void register() {
		DalStatusManager.getTimeoutMarkdown().addNotificationListener(this, null, null);
	}

	@Override
	protected void refresh() {
		TimeoutMarkdown tm = DalStatusManager.getTimeoutMarkdown();
		enableTimeoutMarkDown = tm.isEnabled();
		samplingDuration = tm.getSamplingDuration();
		timeoutThreshold = tm.getTimeoutThreshold();
		errorCountThreshold = tm.getErrorCountThreshold();
		errorPercentThreshold = tm.getErrorPercentThreshold();
		errorPercentReferCount = tm.getErrorPercentReferCount();
		mySqlErrorCodes = tm.getMySqlErrorCodes();
		sqlServerErrorCodes = tm.getSqlServerErrorCodes();
	}
}
