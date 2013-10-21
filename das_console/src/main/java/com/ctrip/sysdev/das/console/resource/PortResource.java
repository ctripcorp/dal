package com.ctrip.sysdev.das.console.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.zookeeper.ZooKeeper;

import com.ctrip.sysdev.das.console.domain.Port;

@Resource
@Path("configure/port")
@Singleton
public class PortResource {
	@Context
	private ServletContext sContext;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Port getPort() {
		Port port = new Port();
		Set<Integer> ports = new HashSet<Integer>();
		port.setPorts(ports);

		ZooKeeper zk = (ZooKeeper)sContext.getAttribute("com.ctrip.sysdev.das.console.zk");
		try {
			List<String> portNumberList = zk.getChildren("/dal/das/configure/port", false);
			for(String number: portNumberList)
				ports.add(new Integer(number));
		} catch (Exception e) {
			e.printStackTrace();
		}
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