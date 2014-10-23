package com.ctrip.platform.dal.dao.markdown;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.logging.markdown.MarkDownInfo;
import com.ctrip.platform.dal.logging.markdown.MarkDownPolicy;
import com.ctrip.platform.dal.logging.markdown.MarkDownReason;
import com.ctrip.platform.dal.sql.logging.Metrics;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class TimeoutDetector implements ErrorDetector{
	private static Logger logger = LoggerFactory.getLogger(TimeoutDetector.class);
	private Map<String, DetectorCounter> data = new ConcurrentHashMap<String, DetectorCounter>();
	
	static{
		ConfigBeanFactory.getTimeoutMarkDownBean().setMinTimeOut(1);
	}
	
	/**
	 * This method will be invoked by one thread.
	 */
	@Override
	public void detect(ErrorContext ctx) {	
		long duration = ConfigBeanFactory.getTimeoutMarkDownBean().getSamplingDuration() * 1000;
		if(!data.containsKey(ctx.getName()))
			data.put(ctx.getName(), new DetectorCounter(duration));
		DetectorCounter dt = data.get(ctx.getName());
		if(isTimeOutException(ctx)){
			dt.incrementHints();
		}
		dt.incrementRequest();
		
		if(dt.getHints() >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorCountBaseLine()){
			this.markdown(ctx.getName(), dt, MarkDownReason.ERRORCOUNT);
		}else if(dt.getRequestTimes() >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorPercentBaseLine()){
			float percent = (dt.getHints() + 0.0f) /dt.getRequestTimes();
			if(percent >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorPercent()){
				this.markdown(ctx.getName(), dt, MarkDownReason.ERRORPERCENT);
			}
		}
	}
	
	private void markdown(String key, DetectorCounter dc, MarkDownReason reason){
		if(ConfigBeanFactory.getTimeoutMarkDownBean().isEnableTimeoutMarkDown()){
			logger.info("Mark-Donw: " + MarkupManager.getMarkupInfo(key));
			ConfigBeanFactory.getMarkdownConfigBean().markdown(key);
		}
		MarkDownInfo info = new MarkDownInfo(key, MarkDownPolicy.TIMEOUT, dc.getDuration());
		
		info.setReason(reason);	
		info.setStatus("Total");
		Metrics.report(info, dc.getRequestTimes());
		info.setStatus("Fail");
		Metrics.report(info, dc.getHints());
	}
	
	public String toDebugInfo(String key) {
		return String.format("request:%s--hints:%s", 
				data.get(key).getRequestTimes(), data.get(key).getHints());
	}

	public static boolean isTimeOutException(ErrorContext ctx){
		if(ctx.getCost() >= ConfigBeanFactory.getTimeoutMarkDownBean().getMinTimeOut() * 1000){
			if(ctx.getDbCategory() == DatabaseCategory.SqlServer){
				if(ctx.getMsg().startsWith("The query has timed out") || ctx.getMsg().startsWith("查询超时")){
					return true;
				}
			} else{
				if(ctx.getExType().toString().equalsIgnoreCase(MySQLTimeoutException.class.toString())){
					return true;
				}
			}
		}
		return false;
	}
}
