package com.ctrip.platform.dal.dao.markdown;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.status.TimeoutMarkdown;

public class TimeoutDetector implements ErrorDetector{
	private Map<String, DetectorCounter> data = new ConcurrentHashMap<String, DetectorCounter>();
	
	/**
	 * This method will be invoked by one thread.
	 */
	@Override
	public void detect(ErrorContext ctx) {	
		TimeoutMarkdown tmb = DalStatusManager.getTimeoutMarkdown();

		if(!tmb.isEnabled())
			return;

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
		MarkDownInfo info = new MarkDownInfo(key, Version.getVersion(), MarkDownPolicy.TIMEOUT, dc.getDuration());
		
		info.setReason(reason);	
		info.setTotal(dc.getRequestTimes());
		info.setFail(dc.getErrors());
		
		MarkdownManager.autoMarkdown(info);
		dc.reset();
	}
	
	public String toDebugInfo(String key) {
		return String.format("request:%s--hints:%s", 
				data.get(key).getRequestTimes(), data.get(key).getErrors());
	}

	public static boolean isTimeOutException(ErrorContext ctx){
		return ctx.getDbCategory().isTimeOutException(ctx);
	}
}
