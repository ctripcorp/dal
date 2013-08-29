package com.ctrip.sysdev.das.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DalService;
import com.ctrip.sysdev.das.commons.DataSourceWrapper;
import com.ctrip.sysdev.das.dataSource.DruidDataSourceWrapper;
import com.ctrip.sysdev.das.jmx.DasServerInfoMBean;
import com.ctrip.sysdev.das.jmx.ServerInfoMXBean;
import com.ctrip.sysdev.das.serde.MsgPackSerDe;
import com.ctrip.sysdev.das.serde.impl.ChunkSerDe;
import com.ctrip.sysdev.das.serde.impl.RequestSerDe;
import com.ctrip.sysdev.das.serde.impl.ResponseSerDe;
import com.ctrip.sysdev.das.server.DalServer;
import com.ctrip.sysdev.das.service.DalServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class CommonModule implements Module {

	private static final Logger logger = LoggerFactory
			.getLogger(CommonModule.class);

	@Override
	public void configure(Binder binder) {
		// jmx
		binder.bind(ServerInfoMXBean.class).to(DasServerInfoMBean.class)
				.asEagerSingleton();
		// db connection pool
		binder.bind(DataSourceWrapper.class).to(DruidDataSourceWrapper.class)
				.asEagerSingleton();
		// serde
		binder.bind(MsgPackSerDe.class)
				.annotatedWith(Names.named("RequestSerDe"))
				.to(RequestSerDe.class).in(Scopes.SINGLETON);
		binder.bind(MsgPackSerDe.class)
				.annotatedWith(Names.named("ResponseSerDe"))
				.to(ResponseSerDe.class).in(Scopes.SINGLETON);
		binder.bind(MsgPackSerDe.class)
				.annotatedWith(Names.named("ChunkSerDe")).to(ChunkSerDe.class)
				.in(Scopes.SINGLETON);
		// dalservice
		binder.bind(DalService.class).to(DalServiceImpl.class);
		binder.bind(Service.class).to(DalServer.class);

		logger.info("CommonModule loaded");
	}
}
