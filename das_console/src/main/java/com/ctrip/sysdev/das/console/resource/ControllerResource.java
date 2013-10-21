package com.ctrip.sysdev.das.console.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.zookeeper.ZooKeeper;

import com.ctrip.sysdev.das.console.domain.Controller;

@Resource
@Path("instance/controller")
@Singleton
public class ControllerResource {
	@Context
	private ServletContext sContext;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Controller getController() {
		Controller controller = new Controller();
		Set<String> ips = new HashSet<String>();
		controller.setIps(ips);

		ZooKeeper zk = (ZooKeeper)sContext.getAttribute("com.ctrip.sysdev.das.console.zk");
		try {
			List<String> ipList = zk.getChildren("/dal/das/instance/controller", false);
			for(String ip: ipList)
				ips.add(ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return controller;
	}

}
