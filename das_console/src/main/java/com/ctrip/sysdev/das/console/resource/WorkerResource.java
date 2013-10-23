package com.ctrip.sysdev.das.console.resource;

import java.util.ArrayList;
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
import org.glassfish.jersey.server.JSONP;

import com.ctrip.sysdev.das.console.domain.Port;
import com.ctrip.sysdev.das.console.domain.Worker;

@Resource
@Path("instance/worker")
@Singleton
public class WorkerResource extends DalBaseResource {
	@Context
	private ServletContext sContext;
	
	@GET
	@JSONP(queryParam = "jsonpCallback")
	@Produces("application/x-javascript")
	public List<Worker> getWorkerInstance() {
		List<Worker> workerList = new ArrayList<Worker>();
		ZooKeeper zk = getZk();
		try {
			List<String> workerNameList = zk.getChildren("/dal/das/instance/worker", false);
			for(String workerName: workerNameList) {
				String workerPath = "/dal/das/instance/worker" + "/" + workerName;
				Worker worker = new Worker();
				worker.setIp(workerName);
				Port port = new Port();
				Set<Integer> ports = new HashSet<Integer>();
				port.setPorts(ports);
				List<String> portNumberList = zk.getChildren(workerPath, false);
				for(String number: portNumberList)
					ports.add(new Integer(number));
				workerList.add(worker);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workerList;
	}

	@DELETE
	@Path("{name}/{number}")
	public void deleteNode(@PathParam("name") String name, @PathParam("number") String number) {
		System.out.printf("Delete node: " + name);
		ZooKeeper zk = getZk();
		String workerPath = "/dal/das/instance/worker" + "/" + name + "/" + number;
		try {
			zk.delete(workerPath, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
