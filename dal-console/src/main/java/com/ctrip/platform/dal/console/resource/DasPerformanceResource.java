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
import com.ctrip.platform.dal.console.domain.Performance;
import com.ctrip.platform.dal.console.domain.PerformanceHistory;
import com.ctrip.platform.dal.console.domain.PerformanceHistorySet;
import com.ctrip.platform.dal.console.domain.StringIdSet;

@Resource
@Path("monitor/performance")
@Singleton
public class DasPerformanceResource {
	@Context
	private ServletContext sContext;
	private ConcurrentHashMap<String, PerformanceHistorySet> store = new ConcurrentHashMap<String, PerformanceHistorySet>();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StringIdSet getPerformance() {
		StringIdSet ids = new StringIdSet();
		ids.setIds(store.keySet());
		return ids;
	}
	
	@GET
	@Path("{ip}")
	@Produces(MediaType.APPLICATION_JSON)
	public PerformanceHistorySet getPerformanceHistorySet(@PathParam("ip") String ip) {
		return store.get(ip);
	}

	@GET
	@Path("{ip}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public PerformanceHistory getPerformanceHistory(@PathParam("ip") String ip, @PathParam("id") String id) {
		PerformanceHistorySet hs = store.get(ip);
		if(hs == null)
			return null;
		return hs.getPerformanceHistory(id);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Status addPerformance(
			@FormParam("id") String id, 
			@FormParam("ip") String ip, 
			@FormParam("systemCpuUsage") double systemCpuUsage, 
			@FormParam("processCpuUsage") double processCpuUsage, 
			@FormParam("start") long start, 
			@FormParam("end") long end, 
			@FormParam("freeMemory") long freeMemory, 
			@FormParam("totalMemory") long totalMemory,
			@FormParam("sysTotalMemory") long sysTotalMemory,
			@FormParam("sysFreeMemory") long sysFreeMemory
			) {
		Performance p = new Performance();
		
		p.setSystemCpuUsage(systemCpuUsage);
		p.setProcessCpuUsage(processCpuUsage);
		p.setStart(start);
		p.setEnd(end);
		p.setFreeMemory(freeMemory);
		p.setTotalMemory(totalMemory);
		p.setSysFreeMemory(sysFreeMemory);
		p.setSysTotalMemory(sysTotalMemory);
		
		
		PerformanceHistorySet phs = store.get(ip);
		if(phs == null) {
			phs = new PerformanceHistorySet();
			PerformanceHistorySet oldPhs = store.putIfAbsent(ip, phs);
			if(oldPhs == null){
				phs.setIp(ip);
				phs.add(id, p);
			}else{
				oldPhs.add(id, p);
			}
		}else{
			phs.add(id, p);
		}
			
		return Status.OK;
	}
}
