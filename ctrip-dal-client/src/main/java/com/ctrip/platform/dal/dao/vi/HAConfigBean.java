package com.ctrip.platform.dal.dao.vi;

import com.ctrip.framework.vi.annotation.ComponentStatus;
import com.ctrip.framework.vi.annotation.FieldInfo;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.status.HAStatus;

@ComponentStatus(id="arch-data-common-bean-haconfigbean",name="arch-data-common-bean-haconfigbean",description = "DAL HA Config")
public class HAConfigBean extends BaseConfigBean {
	@FieldInfo(name = "HAEnabled", description = "HAEnabled")
	private boolean enable;
	
	@FieldInfo(name = "RetryTimes", description = "RetryTimes")
	private int retryCount;
	
	@FieldInfo(name = "SqlServerHAErrorCodes", description = "SqlServerHAErrorCodes")
	private String sqlserverErrorCodes;
	
	@FieldInfo(name = "MySqlHAErrorCodes", description = "MySqlHAErrorCodes")
	private String mysqlErrorCodes;
	
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

	@Override
	protected void register() {
		DalStatusManager.getHaStatus().addNotificationListener(this, null, null);
	}

	@Override
	protected void refresh() {
		HAStatus ha = DalStatusManager.getHaStatus();
		enable = ha.isEnabled();
		retryCount = ha.getRetryCount();
		sqlserverErrorCodes = ha.getSqlserverErrorCodes();
		mysqlErrorCodes = ha.getMysqlErrorCodes();
	}
}
