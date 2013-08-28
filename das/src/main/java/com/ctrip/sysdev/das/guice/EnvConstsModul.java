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
		logger.info("EnvConstsModul loaded");
	}

}
