package com.ctrip.platform.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.datasource.titan.TitanProvider.LogEntry;
import com.ctrip.framework.vi.IgniteManager.SimpleLogger;
import com.ctrip.framework.vi.annotation.Ignite;
import com.ctrip.framework.vi.ignite.AbstractCtripIgnitePlugin;
import com.ctrip.platform.dal.dao.DalClientFactory;

@Ignite(id = "fx.dal.ignite", type = Ignite.PluginType.Component)
public class DalIgnite extends AbstractCtripIgnitePlugin {
	private Map<String, String> configs = new HashMap<>();

	@Override
	public Map<String, String> coreConfigs() {
		return configs;
	}

	@Override
	public String helpUrl() {
		return "http://conf.ctripcorp.com/display/FRAM/Java+Client+FAQ";
	}

	@Override
	public boolean warmUP(SimpleLogger logger) {
		try {
			DalClientFactory.initClientFactory();
			
			configs.putAll(TitanProvider.config);
			log(logger);
			
			DalClientFactory.warmUpConnections();
			return true;
		} catch (Throwable e) {
			logger.error("Fail", e);
			return false;
		}
	}
	
	@Override
	public boolean selfCheck(SimpleLogger logger) {
		return true;
	}
	
	private void log(SimpleLogger logger) {
		List<LogEntry> startUpLog = new ArrayList<>(TitanProvider.startUpLog);
		for(LogEntry e: startUpLog) {
			switch (e.type) {
			case LogEntry.INFO:
				logger.info(e.msg);
				break;
			case LogEntry.WARN:
				logger.warn(e.msg);
				break;
			case LogEntry.ERROR:
				logger.error(e.msg);
				break;
			case LogEntry.ERROR2:
				logger.error(e.msg, e.e);
				break;
			default:
				break;
			}
		}
	}
}