package com.ctrip.platform.dal.dao.markdown;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.dao.configbeans.TimeoutMarkDownBean;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class TimeoutDetector implements ErrorDetector{
	private Map<String, DetectorCounter> data = new ConcurrentHashMap<String, DetectorCounter>();
	private DalLogger logger;
	
	public TimeoutDetector() {
		this.logger = DalClientFactory.getDalLogger();
	}
	
	/**
	 * This method will be invoked by one thread.
	 */
	@Override
	public void detect(ErrorContext ctx) {	
		TimeoutMarkDownBean tmb = ConfigBeanFactory.getTimeoutMarkDownBean();
		long duration = tmb.getSamplingDuration() * 1000 + 10;
		if(!data.containsKey(ctx.getName()))
			data.put(ctx.getName(), new DetectorCounter(duration));
		DetectorCounter dt = data.get(ctx.getName());
		if(dt.getDuration() != duration){
			dt.reset(duration);
		}
		if(isTimeOutException(ctx)){
			dt.incrementErrors();
		}
		dt.incrementRequest();
		
		if(dt.getErrors() >= tmb.getErrorCountThreshold()){
			this.markdown(ctx.getName(), dt, MarkDownReason.ERRORCOUNT);
		}else if(dt.getRequestTimes() >= tmb.getErrorPercentReferCount()){
			float percent = (dt.getErrors() + 0.0f) /dt.getRequestTimes();
			if(percent >= tmb.getErrorPercentThreshold()){
				this.markdown(ctx.getName(), dt, MarkDownReason.ERRORPERCENT);
			}
		}
	}
	
	private void markdown(String key, DetectorCounter dc, MarkDownReason reason){
		if(ConfigBeanFactory.getTimeoutMarkDownBean().isEnableTimeoutMarkDown()){
			ConfigBeanFactory.getMarkdownConfigBean().markdown(key);
			logger.info(String.format("Database %s has been marked down automatically", key));
		}
		MarkDownInfo info = new MarkDownInfo(key, Version.getVersion(), MarkDownPolicy.TIMEOUT, dc.getDuration());
		
		info.setReason(reason);	
		info.setTotal(dc.getRequestTimes());
		info.setFail(dc.getErrors());
		logger.markdown(info);
		
		dc.reset();
	}
	
	public String toDebugInfo(String key) {
		return String.format("request:%s--hints:%s", 
				data.get(key).getRequestTimes(), data.get(key).getErrors());
	}

	public static boolean isTimeOutException(ErrorContext ctx){
		if(ctx.getDbCategory() == DatabaseCategory.SqlServer){
			if(ctx.getMsg().startsWith("The query has timed out") || ctx.getMsg().startsWith("查询超时")){
				return true;
			}
		} else{
			if(ctx.getExType().toString().equalsIgnoreCase(MySQLTimeoutException.class.toString())){
				return true;
			}
		}
		return false;
	}
}
