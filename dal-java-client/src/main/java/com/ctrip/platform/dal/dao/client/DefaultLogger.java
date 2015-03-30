package com.ctrip.platform.dal.dao.client;

import java.util.Map;

import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;

/**
 * Empty logger when log is disabled or you want to quick start your DAL project without
 * a specified logger
 * @author jhhe
 *
 */
public class DefaultLogger implements DalLogger {

	@Override
	public void initLogger(Map<String, String> settings) {
	}

	@Override
	public void info(String desc) {
	}

	@Override
	public void warn(String desc) {
	}

	@Override
	public void error(String desc, Throwable e) {
	}

	@Override
	public void getConnectionFailed(String logicDb, Throwable e) {
	}

	@Override
	public LogEntry createLogEntry() {
		return new LogEntry();
	}

	@Override
	public void start(LogEntry entry) {
	}

	@Override
	public void success(LogEntry entry, int count) {
	}

	@Override
	public void fail(LogEntry entry, Throwable e) {
	}

	@Override
	public void markdown(MarkDownInfo markdown) {
	}

	@Override
	public void markup(MarkupInfo markup) {
	}

	@Override
	public void shutdown() {
	}
}
