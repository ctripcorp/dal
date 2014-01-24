package com.ctrip.platform.dasconsole.domain;

import java.util.ArrayList;
import java.util.List;

public class ExceptionReportHistory {
	private Integer port;
	
	private List<ExceptionReport> exceptionReportHistory = new ArrayList<ExceptionReport>();

	public List<ExceptionReport> getExceptionReportHistory() {
		return exceptionReportHistory;
	}

	public void setExceptionReportHistory(List<ExceptionReport> ExceptionReportHistory) {
		this.exceptionReportHistory = ExceptionReportHistory;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void add(ExceptionReport p) {
		exceptionReportHistory.add(p);
//		synchronized(exceptionReportHistory) {
//			if(exceptionReportHistory.size() == 0)
//				exceptionReportHistory.add(p);
//			else
//				exceptionReportHistory.set(0, p);
//		}
	}
	
	public ExceptionReportHistory getSub(long start, long end) {
		return this;
	}
}
