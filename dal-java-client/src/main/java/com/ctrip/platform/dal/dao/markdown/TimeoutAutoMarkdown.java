package com.ctrip.platform.dal.dao.markdown;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.logging.markdown.MarkDownInfo;
import com.ctrip.platform.dal.logging.markdown.MarkDownPolicy;
import com.ctrip.platform.dal.logging.markdown.MarkDownReason;
import com.ctrip.platform.dal.sql.logging.Metrics;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class TimeoutAutoMarkdown implements AutoMarkdown{
	//private static Logger logger = LoggerFactory.getLogger(TimeoutAutoMarkdown.class);
	private Map<String, Data> data = new ConcurrentHashMap<String, Data>();
	private long latest = 0;
	
	/**
	 * This method will be invoked by one thread.
	 */
	@Override
	public void collectException(MarkContext mark) {
		if(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(mark.getName())){
			return;
		}
		MarkDownInfo info = new MarkDownInfo(mark.getName(), MarkDownPolicy.TIMEOUT,
				ConfigBeanFactory.getTimeoutMarkDownBean().getSamplingDuration());
		if(!data.containsKey(mark.getName()))
			data.put(mark.getName(), new Data());
		Data dt = data.get(mark.getName());
		if(isTimeOutHint(mark)){
			dt.incrementAndGetHints();
			info.setStatus("fail");
		}else{
			info.setStatus("total");
		}
		dt.incrementAndGetRequestTimes();

		if(dt.getHints() >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorCountBaseLine()){
			info.setReason(MarkDownReason.ERRORCOUNT);
			Metrics.report(info, dt.getRequestTimes());
			this.markdown(mark.getName(), dt);	
			return;
		}
		if(dt.getRequestTimes() >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorPercentBaseLine()){
			float percent = (dt.getHints() + 0.0f) /dt.getRequestTimes();
			if(percent >= ConfigBeanFactory.getTimeoutMarkDownBean().getErrorPercent()){
				info.setReason(MarkDownReason.ERRORPERCENT);
				Metrics.report(info, dt.getHints());
				this.markdown(mark.getName(), dt);
				return;
			}
		}
		this.removeOverdueData(mark.getName(), mark.getTime());
	}
	
	private void markdown(String key,Data dt){
		if(ConfigBeanFactory.getTimeoutMarkDownBean().isEnableTimeoutMarkDown()){
			//logger.info("########################Mark-Donw########################");
			ConfigBeanFactory.getMarkdownConfigBean().markdown(key);
		}
		dt.clear();
	}

	public static boolean isTimeOutHint(MarkContext mark){
		if(mark.getCost() >= ConfigBeanFactory.getTimeoutMarkDownBean().getMinTimeOut()){
			if(mark.getDbtype().equalsIgnoreCase("Microsoft SQL Server")){
				if(mark.getMsg().startsWith("The query has timed out") || mark.getMsg().startsWith("查询超时")){
					return true;
				}
			} else{
				if(mark.getExType().toString().equalsIgnoreCase(MySQLTimeoutException.class.toString())){
					return true;
				}
			}
		}
		return false;
	}
	
	private void removeOverdueData(String key, long time){
		if(time > this.latest)
			this.latest = time;
		if((System.currentTimeMillis() - this.latest) > 
			(ConfigBeanFactory.getTimeoutMarkDownBean().getSamplingDuration() * 1000)){
			//logger.info("data overdur, cleared.");
			this.data.get(key).clear();
		}
	}

	private static class Data{
		private int requestTimes = 0;
		private int hints = 0;
		
		public int incrementAndGetRequestTimes(){
			return this.requestTimes ++;
		}
		
		public int incrementAndGetHints(){
			return this.hints ++;
		}
		
		public int getRequestTimes(){
			return this.requestTimes;
		}
		
		public int getHints(){
			return this.hints;
		}
		
		public void clear(){
			this.requestTimes = 0;
			this.hints = 0;
		}
	}
}
