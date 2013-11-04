package com.ctrip.sysdev.das.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.utils.Configuration;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class EnvConstsModul implements Module {

	private static final Logger logger = LoggerFactory
			.getLogger(EnvConstsModul.class);

	@Override
	public void configure(Binder binder) {
		binder.bindConstant().annotatedWith(Names.named("InetHost"))
				.to(Configuration.get("serverAddr"));
		binder.bindConstant().annotatedWith(Names.named("InetPort"))
				.to(Configuration.getInt("serverPort"));

//		binder.bindConstant().annotatedWith(Names.named("driverClass"))
//				.to(Configuration.get("driverClass"));
//		binder.bindConstant().annotatedWith(Names.named("jdbcUrl"))
//				.to(Configuration.get("jdbcUrl"));
//		binder.bindConstant().annotatedWith(Names.named("user"))
//				.to(Configuration.get("user"));
//		binder.bindConstant().annotatedWith(Names.named("password"))
//				.to(Configuration.get("password"));
		binder.bindConstant().annotatedWith(Names.named("initialSize"))
				.to(Configuration.getInt("initialSize"));
		binder.bindConstant().annotatedWith(Names.named("maxActive"))
				.to(Configuration.getInt("maxActive"));
		binder.bindConstant().annotatedWith(Names.named("minIdle"))
				.to(Configuration.getInt("minIdle"));
		binder.bindConstant().annotatedWith(Names.named("maxWait"))
				.to(Configuration.getInt("maxWait"));
		binder.bindConstant()
				.annotatedWith(Names.named("timeBetweenEvictionRunsMillis"))
				.to(Configuration.getInt("timeBetweenEvictionRunsMillis"));
		binder.bindConstant()
				.annotatedWith(Names.named("minEvictableIdleTimeMillis"))
				.to(Configuration.getInt("minEvictableIdleTimeMillis"));
		binder.bindConstant().annotatedWith(Names.named("validationQuery"))
				.to(Configuration.get("validationQuery"));

		logger.info("EnvConstsModul loaded");
	}

}
