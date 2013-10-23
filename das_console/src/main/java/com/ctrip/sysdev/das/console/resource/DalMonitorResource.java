package com.ctrip.sysdev.das.console.resource;

import java.util.Map;
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

import org.glassfish.jersey.server.JSONP;

import com.ctrip.sysdev.das.console.domain.TimeCost;
import com.ctrip.sysdev.das.console.domain.TimeCostIdList;

@Resource
@Path("monitor/timeCosts")
@Singleton
public class DalMonitorResource extends DalBaseResource {
	@Context
	private ServletContext sContext;
	private ConcurrentHashMap<String, TimeCost> store = new ConcurrentHashMap<String, TimeCost>();
	
	@GET
	@JSONP(queryParam = "jsonpCallback")
	@Produces("application/x-javascript")
	public TimeCostIdList getTimeCosts() {
		TimeCostIdList ids = new TimeCostIdList();
		ids.setIds(store.keySet());
		return ids;
	}
	
	@GET
	@Path("{id}")
	@JSONP(queryParam = "jsonpCallback")
	@Produces("application/x-javascript")
	public TimeCost getTimeCost(@PathParam("id") String id) {
		TimeCost tc = store.get(id);
		return tc;
	}

	@POST
	public void addTimeCost(@FormParam("id") String id, @FormParam("timeCost") String timeCost) {
		// TimeCost looks like: name0:value0;name1:value1
		TimeCost tc = new TimeCost(id, timeCost);
		TimeCost oldTc = store.get(id);
		tc.merge(oldTc);
		store.put(id, tc);
	}
}
