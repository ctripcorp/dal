package com.ctrip.platform.dal.service.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.service.jmx.DasServerInfoMBean;
import com.ctrip.platform.dal.service.jmx.ServerInfoMXBean;
import com.google.inject.Binder;
import com.google.inject.Module;

public class CommonModule implements Module {

	private static final Logger logger = LoggerFactory
			.getLogger(CommonModule.class);

	@Override
	public void configure(Binder binder) {
		// jmx
		binder.bind(ServerInfoMXBean.class).to(DasServerInfoMBean.class)
				.asEagerSingleton();
		// db connection pool
//		binder.bind(DataSourceWrapper.class).to(DruidDataSourceWrapper.class)
//				.asEagerSingleton();

		logger.info("CommonModule loaded");
	}
}
