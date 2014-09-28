package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class TimeoutAutoMarkdown implements AutoMarkdown{
	
	private Map<String, Tuple<AtomicInteger, AtomicInteger>> data = 
			new ConcurrentHashMap<String, Tuple<AtomicInteger, AtomicInteger>>();
	
	/**
	 * This method will be invoked by one thread.
	 */
	@Override
	public void collectException(Mark mark) {
		if(!ConfigBeanFactory.getTimeoutMarkDownBean().isEnableTimeoutMarkDown() ||
				ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(mark.getName()))
			return;
		if(!data.containsKey(mark.getName()))
			data.put(mark.getName(), new Tuple<AtomicInteger, AtomicInteger>(
					new AtomicInteger(0), new AtomicInteger(0)));
		Tuple<AtomicInteger, AtomicInteger> dt = data.get(mark.getName());
		int hints = 0;
		int requests = 0;
		if(this.isHint(mark.getDbtype(), mark.getException())){
			hints = dt.getItem1().incrementAndGet();
		}
		requests = dt.getItem2().incrementAndGet();
		
		if(hints >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorCountBaseLine()){
			ConfigBeanFactory.getMarkdownConfigBean().markdown(mark.getName());
			data.clear();
			return;
		}
		if(requests >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorPercentBaseLine()){
			float percent = (hints + 0.0f) /requests;
			if(percent >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorPercent()){
				ConfigBeanFactory.getMarkdownConfigBean().markdown(mark.getName());
				data.clear();
				return;
			}
		}
	}
	
	private boolean isHint(String dbType, Throwable e){
		if(null == e)
			return false;
		if(dbType.equalsIgnoreCase("Microsoft SQL Server"))
			return e instanceof SQLException && ((SQLException)e).getErrorCode() == -2;
		else{
			if(e.getClass().toString().equalsIgnoreCase(MySQLTimeoutException.class.toString())){
				return true;
			}
		}
		return false;
	}
}
