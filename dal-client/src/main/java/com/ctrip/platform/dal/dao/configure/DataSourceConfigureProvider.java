package com.ctrip.platform.dal.dao.configure;

import java.util.Set;


/**
 * This interface is used by connection locator to provide connection configure.
 * The assumption is different company may have different way of storing connection info
 * for safety reason.
 * The imterface extends DalComponent to receive configuration in connection locater settings
 * @author jhhe
 *
 */
public interface DataSourceConfigureProvider extends DalComponent {
	
	/**
	 * Declare which databases we want to use.
	 * @param dbNames
	 */
	void setup(Set<String> dbNames);
	
	/**
	 * Return null if no such config is found
	 * @param dbName
	 * @param option addition option for setup connection URL
	 * @return
	 */
	DataSourceConfigure getDataSourceConfigure(String dbName);
}
