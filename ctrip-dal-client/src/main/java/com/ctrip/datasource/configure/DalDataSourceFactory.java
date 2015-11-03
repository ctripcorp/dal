package com.ctrip.datasource.configure;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;

public class DalDataSourceFactory {
	private TitanProvider provider = new TitanProvider();

	/**
	 * In case user has clog or cat configured
	 * @param allInOneKey
	 * @param svcUrl
	 * @return
	 * @throws Exception
	 */
	public DataSource createDataSource(String allInOneKey, String svcUrl) throws Exception {
		return createDataSource(allInOneKey, svcUrl, TitanProvider.getPreConfiguredAppId());
	}
	
	/**
	 * In case user has no clog or cat configured
	 * @param allInOneKey
	 * @param svcUrl
	 * @param appid
	 * @return
	 * @throws Exception
	 */
	public DataSource createDataSource(String allInOneKey, String svcUrl, String appid) throws Exception {
		provider.setSvcUrl(svcUrl);
		provider.setAppid(appid);

		Set<String> dbNames = new HashSet<>();
		dbNames.add(allInOneKey);
		provider.setup(dbNames);
		
		DataSourceLocator loc = new DataSourceLocator(provider);
		return loc.getDataSource(allInOneKey);
	}
}
