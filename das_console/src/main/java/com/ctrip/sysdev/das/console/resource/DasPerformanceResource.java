package com.ctrip.sysdev.das.console.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

import com.ctrip.sysdev.das.console.domain.Performance;
import com.ctrip.sysdev.das.console.domain.PerformanceHistory;
import com.ctrip.sysdev.das.console.domain.PerformanceHistorySet;
import com.ctrip.sysdev.das.console.domain.Status;

@Resource
@Path("monitor/performance")
@Singleton
public class DasPerformanceResource {
	@Context
	private ServletContext sContext;
	private ConcurrentHashMap<String, PerformanceHistorySet> store = new ConcurrentHashMap<String, PerformanceHistorySet>();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> getPerformance() {
		return store.keySet();
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
		return store.get(ip).getPerformanceHistory(id);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Status addPerformance(
			@FormParam("id") String id, 
			@FormParam("ip") String ip, 
			@FormParam("systemCpuUsage") double systemCpuUsage, 
			@FormParam("processCpuUsage") double processCpuUsage, 
			@FormParam("freeMemory") long freeMemory, 
			@FormParam("totalMemory") long totalMemory
			) {
		Performance p = new Performance();
		
		p.setIp(ip);
		p.setId(id);
		p.setSystemCpuUsage(systemCpuUsage);
		p.setProcessCpuUsage(processCpuUsage);
		p.setFreeMemory(freeMemory);
		p.setTotalMemory(totalMemory);
		
		PerformanceHistorySet phs = store.get(ip);
		if(phs == null) {
			phs = new PerformanceHistorySet();
			PerformanceHistorySet oldPhs = store.putIfAbsent(ip, phs);
			if(oldPhs == null){
				phs.add(p);
			}else{
				oldPhs.add(p);
			}
		}
			
		return Status.OK;
	}
}
