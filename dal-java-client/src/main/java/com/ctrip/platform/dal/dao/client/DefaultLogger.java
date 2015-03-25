package com.ctrip.platform.dal.dao.client;

import java.util.Map;

import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;

public class DefaultLogger implements DalLogger {

	@Override
	public void initLogger(Map<String, String> settings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String desc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String desc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String desc, Throwable e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getConnectionFailed(String logicDb, Throwable e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LogEntry createLogEntry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start(LogEntry entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void success(LogEntry entry, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fail(LogEntry entry, Throwable e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markdown(MarkDownInfo markdown) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markup(MarkupInfo markup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
