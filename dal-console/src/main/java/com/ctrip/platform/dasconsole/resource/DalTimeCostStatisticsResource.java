package com.ctrip.platform.dasconsole.resource;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ctrip.platform.dasconsole.domain.TimeCostStatistics;
import com.ctrip.platform.dasconsole.domain.TimeCostStatisticsTO;


@Resource
@Path("monitor/timeCostsStatistics")
@Singleton
public class DalTimeCostStatisticsResource extends DalBaseResource {
	@Context
	private ServletContext sContext;

	public static TimeCostStatistics statistics = new TimeCostStatistics();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TimeCostStatisticsTO getTimeCosts() {
		return statistics.getSnapshot(DalTimeCostsResource.store.size());
	}
}