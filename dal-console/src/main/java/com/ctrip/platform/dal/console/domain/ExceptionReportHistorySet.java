package com.ctrip.platform.dal.console.domain;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionReportHistorySet {
	private String ip;
	
	private ConcurrentHashMap<String, ExceptionReportHistory> exceptionReportHistoryMap = new ConcurrentHashMap<String, ExceptionReportHistory>();

	public ExceptionReportHistory[] getExceptionReportHistoryList() {
		Collection<ExceptionReportHistory> v = exceptionReportHistoryMap.values();
		return v.toArray(new ExceptionReportHistory[v.size()]);
	}

	public void setExceptionReportHistoryList(ExceptionReportHistory[] v) {
		;
	}

	public ExceptionReportHistory getExceptionReportHistory(String id) {
		return exceptionReportHistoryMap.get(id);
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void add(String id, ExceptionReport p) {
		ExceptionReportHistory history = exceptionReportHistoryMap.get(id);
		if(history == null){
			history = new ExceptionReportHistory();
			ExceptionReportHistory oldHistory =  exceptionReportHistoryMap.putIfAbsent(id, history);
			if(oldHistory == null){
				history.setPort(new Integer(id));
				history.add(p);
			}else
				oldHistory.add(p);
		}else{
			history.add(p);
		}
	}
}
