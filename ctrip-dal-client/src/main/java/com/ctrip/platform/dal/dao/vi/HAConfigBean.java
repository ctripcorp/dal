package com.ctrip.platform.dal.dao.vi;

import com.ctrip.framework.vi.annotation.ComponentStatus;
import com.ctrip.framework.vi.annotation.FieldInfo;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.status.HAStatus;

@ComponentStatus(id="com.ctrip.dal.client.HAConfigBean",name="com.ctrip.dal.client.HAConfigBean",description = "DAL HA Config")
public class HAConfigBean extends BaseConfigBean {
	@FieldInfo(name = "HAEnabled", description = "HAEnabled")
	private boolean enable;
	
	@FieldInfo(name = "RetryTimes", description = "RetryTimes")
	private int retryCount;
	
	@FieldInfo(name = "SqlServerHAErrorCodes", description = "SqlServerHAErrorCodes")
	private String sqlserverErrorCodes;
	
	@FieldInfo(name = "MySqlHAErrorCodes", description = "MySqlHAErrorCodes")
	private String mysqlErrorCodes;

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
