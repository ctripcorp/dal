package com.ctrip.platform.dal.console.resource;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ctrip.platform.dal.console.common.Status;
import com.ctrip.platform.dal.console.domain.ExceptionReport;
import com.ctrip.platform.dal.console.domain.ExceptionReportHistory;
import com.ctrip.platform.dal.console.domain.ExceptionReportHistorySet;
import com.ctrip.platform.dal.console.domain.StringIdSet;

@Resource
@Path("monitor/exceptions")
@Singleton
public class DasExceptionResource {
	@Context
	private ServletContext sContext;
	private ConcurrentHashMap<String, ExceptionReportHistorySet> store = new ConcurrentHashMap<String, ExceptionReportHistorySet>();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StringIdSet getExceptionReport() {
		StringIdSet ids = new StringIdSet();
		ids.setIds(store.keySet());
		return ids;
	}
	
	@GET
	@Path("{ip}")
	@Produces(MediaType.APPLICATION_JSON)
	public ExceptionReportHistorySet getExceptionReportHistorySet(@PathParam("ip") String ip) {
		return store.get(ip);
	}

	@GET
	@Path("{ip}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ExceptionReportHistory getExceptionReportHistory(@PathParam("ip") String ip, @PathParam("id") String id) {
		ExceptionReportHistorySet hs = store.get(ip);
		if(hs == null)
			return null;
		return hs.getExceptionReportHistory(id);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Status addExceptionReport(
			@FormParam("id") String id, 
			@FormParam("ip") String ip,
			@FormParam("reqId") String reqId,
			@FormParam("ts") long timeStamp,
			@FormParam("msg") String message
			) {
		ExceptionReport er = new ExceptionReport();
		
		er.setMessage(message);
		er.setReqId(reqId == null ? "" : reqId);
		er.setTimeStamp(timeStamp);
		
		ExceptionReportHistorySet erhs = store.get(ip);
		if(erhs == null) {
			erhs = new ExceptionReportHistorySet();
			ExceptionReportHistorySet oldPhs = store.putIfAbsent(ip, erhs);
			if(oldPhs == null){
				erhs.setIp(ip);
				erhs.add(id, er);
			}else{
				oldPhs.add(id, er);
			}
		}else{
			erhs.add(id, er);
		}
			
		return Status.OK;
	}
}
