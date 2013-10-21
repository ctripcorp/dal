package com.ctrip.sysdev.das.console.resource;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ctrip.sysdev.das.console.domain.Port;

@Resource
@Path("configure/port")
@Singleton
public class PortResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Port getPort() {
		Port port = new Port();
		Set<Integer> ports = new HashSet<Integer>();
		ports.add(new Integer(7001));
		ports.add(new Integer(7002));
		ports.add(new Integer(7003));
		port.setPorts(ports);
		return port;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void addDb(@FormParam("number") String number) {
		System.out.printf("Add port: " +number);
	}
	
	@DELETE
	@Path("{name}")
	public void deleteDb(@PathParam("number") String number) {
		System.out.printf("Delete port: " +number);
	}
}