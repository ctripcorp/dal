package com.ctrip.platform.dal.dao.client;

import java.util.Map;

import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;

/**
 * Used when logger is disabled
 * @author jhhe
 *
 */
public class NullLogger implements DalLogger {

	@Override
	public void initialize(Map<String, String> settings) {
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

	@Override
	public String getAppID() {
		return "999999";
	}
}