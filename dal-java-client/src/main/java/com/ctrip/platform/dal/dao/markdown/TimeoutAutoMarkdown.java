package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class TimeoutAutoMarkdown implements AutoMarkdown{
	
	private Map<String, Tuple<AtomicInteger, AtomicInteger>> data = 
			new ConcurrentHashMap<String, Tuple<AtomicInteger, AtomicInteger>>();
	
	@Override
	public boolean collectException(String key, Throwable e) {
		if(!data.containsKey(key))
			data.put(key, new Tuple<AtomicInteger, AtomicInteger>(
					new AtomicInteger(0), new AtomicInteger(0)));
		Tuple<AtomicInteger, AtomicInteger> dt = data.get(key);
		boolean isHinted = false;
		if(this.isHint(e)){
			dt.getItem1().incrementAndGet();
			isHinted = true;
		}
		dt.getItem2().incrementAndGet();
		return isHinted;
	}

	@Override
	public boolean isMarkdown(String key) {
		Tuple<AtomicInteger, AtomicInteger> dt = data.get(key);
		boolean ret = false;
		if(null != dt){		
			float hints = dt.getItem1().get();
			float requestTimes = dt.getItem2().get();
			float percent = requestTimes >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorCountBaseLine() ?
					hints/requestTimes : 0;
			ret = hints >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorCountBaseLine() || 
					percent >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorPercent();
		}
		return ret;
	}

	@Override
	public void markup(String key) {
		Tuple<AtomicInteger, AtomicInteger> dt = data.get(key);
		if(null != dt){
			dt.getItem1().set(0);
			dt.getItem2().set(0);
		}
	}
	
	private boolean isHint(Throwable e){
		if(null == e)
			return false;
		if(e instanceof SQLException && ((SQLException)e).getErrorCode() == -2)
			return true;
		return false;
	}
}
