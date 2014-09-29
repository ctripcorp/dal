package com.ctrip.platform.dal.dao.markdown;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class TimeoutAutoMarkdown implements AutoMarkdown{
	private static Logger logger = LoggerFactory.getLogger(TimeoutAutoMarkdown.class);
	private Map<String, Data> data = new ConcurrentHashMap<String, Data>();
	private long latest = 0;
	
	/**
	 * This method will be invoked by one thread.
	 */
	@Override
	public void collectException(MarkKey mark) {
		if(!ConfigBeanFactory.getTimeoutMarkDownBean().isEnableTimeoutMarkDown() ||
				ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(mark.getName())){
			return;
		}
		if(!data.containsKey(mark.getName()))
			data.put(mark.getName(), new Data());
		Data dt = data.get(mark.getName());
		int hints = 0;
		int requests = 0;
		if(this.isHint(mark.getDbtype(), mark.getExType(), mark.getErrorCode())){
			hints = dt.incrementAndGetHints();
		}
		requests = dt.incrementAndGetRequestTimes();
		
		logger.debug("request: " + requests + ", hints: " + hints);
		
		if(hints >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorCountBaseLine()){
			ConfigBeanFactory.getMarkdownConfigBean().markdown(mark.getName());
			dt.clear();
			return;
		}
		if(requests >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorPercentBaseLine()){
			float percent = (hints + 0.0f) /requests;
			if(percent >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorPercent()){
				ConfigBeanFactory.getMarkdownConfigBean().markdown(mark.getName());
				dt.clear();
				return;
			}
		}
		
		this.removeOverdueData(mark.getName(), mark.getTime());
	}
	
	private void removeOverdueData(String key, long time){
		if(time > this.latest)
			this.latest = time;
		logger.debug("duration: " + (this.latest - System.currentTimeMillis()) + "ms");
		if(this.latest - System.currentTimeMillis() > 
			ConfigBeanFactory.getTimeoutMarkDownBean().getSamplingDuration() * 1000){
			this.data.get(key).clear();
		}
	}
	
	private boolean isHint(String dbType, Class<?> exType, int errorCode){
		if(dbType.equalsIgnoreCase("Microsoft SQL Server"))
			return ConfigBeanFactory.getTimeoutMarkDownBean().getSqlServerTimeoutMarkdownCodes().contains(errorCode);
		else{
			if(exType.toString().equalsIgnoreCase(MySQLTimeoutException.class.toString())){
				return true;
			}
		}
		return false;
	}

	private static class Data{
		private int requestTimes = 0;
		private int hints = 0;
		
		public int incrementAndGetRequestTimes(){
			return ++ this.requestTimes;
		}
		
		public int incrementAndGetHints(){
			return ++ this.hints;
		}
		
		public void clear(){
			this.requestTimes = 0;
			this.hints = 0;
		}
	}
}
