package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class TimeoutAutoMarkdown implements AutoMarkdown{
	
	private Map<String, Data> data = new ConcurrentHashMap<String, Data>();
	
	/**
	 * This method will be invoked by one thread.
	 */
	@Override
	public void collectException(MarkKey mark) {
		if(!ConfigBeanFactory.getTimeoutMarkDownBean().isEnableTimeoutMarkDown() ||
				ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(mark.getName()))
			return;
		if(!data.containsKey(mark.getName()))
			data.put(mark.getName(), new Data());
		Data dt = data.get(mark.getName());
		int hints = 0;
		int requests = 0;
		if(this.isHint(mark.getDbtype(), mark.getException())){
			hints = dt.incrementAndGetHints();
		}
		requests = dt.incrementAndGetRequestTimes();
		
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
	}
	
	private boolean isHint(String dbType, Throwable e){
		if(null == e)
			return false;
		if(dbType.equalsIgnoreCase("Microsoft SQL Server") && e instanceof SQLException)
			return ConfigBeanFactory.getTimeoutMarkDownBean().getSqlServerTimeoutMarkdownCodes().contains(((SQLException)e).getErrorCode());
		else{
			if(e.getClass().toString().equalsIgnoreCase(MySQLTimeoutException.class.toString())){
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
