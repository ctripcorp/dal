package com.ctrip.sysdev.das.console.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.zookeeper.ZooKeeper;
import org.glassfish.jersey.server.JSONP;

import com.ctrip.sysdev.das.console.domain.Controller;

@Resource
@Path("instance/controller")
@Singleton
public class ControllerResource extends DalBaseResource {
	@Context
	private ServletContext sContext;
	
	@GET
	@JSONP(queryParam = "jsonpCallback")
	@Produces("application/x-javascript")
	public Controller getController() {
		Controller controller = new Controller();
		Set<String> ips = new HashSet<String>();
		controller.setIps(ips);

		ZooKeeper zk = getZk();
		try {
			List<String> ipList = zk.getChildren("/dal/das/instance/controller", false);
			for(String ip: ipList)
				ips.add(ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return controller;
	}

	@DELETE
	@Path("{name}")
	public void deleteNode(@PathParam("name") String name) {
		System.out.printf("Delete node: " + name);
		ZooKeeper zk = getZk();
		String controllerPath = "/dal/das/instance/controller" + "/" + name;
		try {
			zk.delete(controllerPath, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
