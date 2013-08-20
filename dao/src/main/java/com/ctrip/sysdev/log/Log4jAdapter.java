package com.ctrip.sysdev.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jAdapter implements LogAdapter {
	
	private Logger logger = null;
	
	private static final Map<String, Log4jAdapter> loggers = new HashMap<String, Log4jAdapter>();
	
	private Log4jAdapter(Class<?> c) {
		this.logger = Logger.getLogger(c);
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
	public static Log4jAdapter getLogger(Class<?> c){
		String className = c.getName();
		
		Log4jAdapter logger = loggers.get(className);
		
		if(logger == null){
			synchronized (Log4jAdapter.class) {
				logger = loggers.get(className);
				if (logger == null) {
					logger = new Log4jAdapter(c);
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
		String path = "Log4jConfig.properties";
		Properties properties = new Properties();
		properties.load(Log4jAdapter.class.getClassLoader().getResourceAsStream(path));
		synchronized (Log4jAdapter.class) {
			PropertyConfigurator.configure(properties);
			loggers.clear();
		}
		Log4jAdapter logger = Log4jAdapter.getLogger(Log4jAdapter.class);
		logger.info("Properties loaded: " + path);
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
		this.logger.trace(message);
	}
	
	public static void main(String[] args) {
		Log4jAdapter adapter = Log4jAdapter.getLogger(Log4jAdapter.class);
		adapter.fatal("ok");
	}

}
