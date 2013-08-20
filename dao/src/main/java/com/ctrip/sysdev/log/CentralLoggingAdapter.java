package com.ctrip.sysdev.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.NotImplementedException;

import com.ctrip.freeway.config.ConfigManager;
import com.ctrip.freeway.config.LogConfig;
import com.ctrip.freeway.logging.ILog;
import com.ctrip.freeway.logging.LogManager;

public class CentralLoggingAdapter implements LogAdapter {

	private ILog logger = null;
	
	private static final Map<String, CentralLoggingAdapter> loggers = new HashMap<String, CentralLoggingAdapter>();
	
	private CentralLoggingAdapter(Class<?> c) {
		this.logger = LogManager.getLogger(c);
	}
	
	static{
		try {
			setUpLogger();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get logger of log4j according to the class 
	 * @param c
	 * @return
	 */
	public static CentralLoggingAdapter getLogger(Class<?> c){
		String className = c.getName();
		
		CentralLoggingAdapter logger = loggers.get(className);
		
		if(logger == null){
			synchronized (CentralLoggingAdapter.class) {
				logger = loggers.get(className);
				if (logger == null) {
					logger = new CentralLoggingAdapter(c);
					loggers.put(className, logger);
				}
			}
		}
		
		return logger;
	}
	
	/**
	 * Set up the log4j logger
	 * @throws IOException
	 */
	private static void setUpLogger() throws IOException {
//		String path = "Log4jConfig.properties";
//		Properties properties = new Properties();
//		properties.load(CentralLoggingAdapter.class.getClassLoader().getResourceAsStream(path));
		synchronized (CentralLoggingAdapter.class) {
			LogConfig.setAppID("989679");
			LogConfig.setLoggingServerIP("192.168.82.58");
			LogConfig.setLoggingServerPort("63100");
			loggers.clear();
		}
		CentralLoggingAdapter logger = CentralLoggingAdapter.getLogger(CentralLoggingAdapter.class);
		logger.info("Properties loaded: ");
	}

	public void fatal(String message) {
		this.logger.fatal(message);
	}

	public void error(String message) {
		this.logger.error(message);
	}

	public void warn(String message) {
		this.logger.warn(message);
	}

	public void info(String message) {
		this.logger.info(message);
	}

	public void debug(String message) {
		this.logger.debug(message);
	}

	public void trace(String message) {
		//this.logger.trace(message);
		throw new NotImplementedException();
	}
	
	public static void main(String[] args) {
		CentralLoggingAdapter adapter = CentralLoggingAdapter.getLogger(CentralLoggingAdapter.class);
		adapter.fatal("ok");
	}
}
