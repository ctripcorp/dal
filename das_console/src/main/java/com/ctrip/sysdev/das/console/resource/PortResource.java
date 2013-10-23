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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.glassfish.jersey.server.JSONP;

import com.ctrip.sysdev.das.console.domain.Port;
import com.ctrip.sysdev.das.console.domain.Status;

@Resource
@Path("configure/port")
@Singleton
public class PortResource extends DalBaseResource {
	@Context
	private ServletContext sContext;

	@GET
	@JSONP(queryParam = "jsonpCallback")
	@Produces({ "application/x-javascript" })
	public Port getPort(@QueryParam("jsonpCallback") String callback) {
		Port port = new Port();
		Set<Integer> ports = new HashSet<Integer>();
		port.setPorts(ports);

		ZooKeeper zk = getZk();
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
	@Produces({ "application/json" })
	public Status addDb(@FormParam("number") String number) {
		System.out.printf("Add port: " +number);
		ZooKeeper zk = getZk();
		String portPath = "/dal/das/configure/port" + "/" + number;
		
		try {
			zk.create(portPath, number.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
		
	}
	
	@DELETE
	@Path("{number}")
	public void deleteDb(@PathParam("number") String number) {
		System.out.printf("Delete port: " +number);
		ZooKeeper zk = getZk();
		String portPath = "/dal/das/configure/port" + "/" + number;
		
		try {
			zk.delete(portPath, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}