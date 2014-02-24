package com.ctrip.platform.dal.console.resource;

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
import javax.ws.rs.core.MediaType;

import org.apache.zookeeper.ZooKeeper;

import com.ctrip.platform.dal.console.common.Status;
import com.ctrip.platform.dal.console.domain.Controller;

@Resource
@Path("instance/controller")
@Singleton
public class ControllerResource extends DalBaseResource {
	@Context
	private ServletContext sContext;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
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
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteNode(@PathParam("name") String name) {
		System.out.printf("Delete node: " + name);
		ZooKeeper zk = getZk();
		String controllerPath = "/dal/das/instance/controller" + "/" + name;
		try {
			zk.delete(controllerPath, -1);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
	}
}
