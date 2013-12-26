package com.ctrip.platform.dasconsole.resource;

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

import com.ctrip.platform.dasconsole.common.Status;
import com.ctrip.platform.dasconsole.domain.StringIdSet;
import com.ctrip.platform.dasconsole.domain.TimeCost;
import com.ctrip.platform.dasconsole.domain.TimeCostEntry;
import com.ctrip.platform.dasconsole.domain.TimeCostStatistics;

@Resource
@Path("monitor/timeCosts")
@Singleton
public class DalTimeCostsResource extends DalBaseResource {
	@Context
	private ServletContext sContext;
	public static ConcurrentHashMap<String, TimeCost> store = new ConcurrentHashMap<String, TimeCost>();
	private TimeCostStatistics statistics = DalTimeCostStatisticsResource.statistics;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StringIdSet getTimeCosts() {
		StringIdSet ids = new StringIdSet();
		ids.setIds(store.keySet());
		return ids;
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public TimeCost getTimeCost(@PathParam("id") String id) {
		TimeCost tc = store.get(id);
		return tc;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Status addTimeCost(@FormParam("values") String values) {
		String[] entries = values.split(";");
		for(String rawEntry: entries){
			String[] parts = rawEntry.split(":");
			long cost = Long.parseLong(parts[2]);
			TimeCostEntry entry = new TimeCostEntry(getFullName(parts[1], cost), cost);
			String id = parts[0];
			TimeCost tc = store.get(id);
			if(tc == null) {
				tc = new TimeCost(id);
				TimeCost oldTc = store.putIfAbsent(id, tc);
				if(oldTc != null)
					oldTc.add(entry);
				else
					tc.add(entry);
			}else
				tc.add(entry);
		}
		return Status.OK;
	}
	
	//id:state:cost  decodeRequest=dr, dbTime=dt encodeResponseTime=er
	private static final String dr = "dr";
	private static final String decodeRequest = "decodeRequest";
	
	private static final String dt = "dt";
	private static final String dbTime = "dbTime";
	
	private static final String er = "er";
	private static final String encodeResponseTime = "encodeResponseTime";
	
	private String getFullName(String shortName, long delta) {
		if(shortName.equals(dr)){
			statistics.incTotalDecodeCost(delta);
			return decodeRequest;
		}
		
		if(shortName.equals(dt)){
			statistics.incTotalDBCost(delta);
			return dbTime;
		}
		
		if(shortName.equals(er)){
			statistics.incTotalEncodeCost(delta);
			return encodeResponseTime;
		}
		
		return shortName;
	}
}
