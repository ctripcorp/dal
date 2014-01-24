package com.ctrip.platform.dasconsole.resource;

import java.util.ArrayList;
import java.util.Collections;
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

import com.ctrip.platform.dal.common.to.DasWorker;
import com.ctrip.platform.dasconsole.common.Status;
import com.ctrip.platform.dasconsole.domain.Port;
import com.ctrip.platform.dasconsole.domain.Worker;

@Resource
@Path("instance")
@Singleton
public class WorkerResource extends DalBaseResource {
	@Context
	private ServletContext sContext;
	
	@GET
	@Path("worker")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Worker> getWorkers() {
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
				worker.setPorts(port);
				workerList.add(worker);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workerList;
	}

	@GET
	@Path("dbNode/{logicDbName}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DasWorker> getWorkersByLogicDb(@PathParam("logicDbName") String logicDbName) {
		try {
			return getFactory().getDasWorkerAccessor().listByLogicDB(logicDbName);
		} catch (Exception e) {
			logger.error("error during get worker by logic DB: " + logicDbName, e);
			return Collections.emptyList();
		}
	}

	@GET
	@Path("dbGroupNode/{logicDbGroupName}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DasWorker> getWorkersByLogicDbGroup(@PathParam("logicDbGroupName") String logicDbGroupName) {
		try {
			return getFactory().getDasWorkerAccessor().listByLogicDBGroup(logicDbGroupName);
		} catch (Exception e) {
			logger.error("Error during get worker by logic DB: " + logicDbGroupName, e);
			return Collections.emptyList();
		}
	}

	@DELETE
	@Path("worker/{name}/{number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteNode(@PathParam("name") String name, @PathParam("number") String number) {
		System.out.printf("Delete node: " + name);
		try {
			getFactory().getDasWorkerAccessor().unregister(name, Integer.parseInt(number));
			return Status.OK;
		} catch (Exception e) {
			logger.error(String.format("Error during delete worker for %s,port: %s ", name, number), e);
			return Status.ERROR;
		}
	}
	
	@DELETE
	@Path("dbNode/{logicDbName}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteWorkerByLogicDb(@PathParam("logicDbName") String logicDbName, @PathParam("name") String name) {
		System.out.printf("Delete node: " + name);
		try {
			getFactory().getDasWorkerAccessor().unregisterByLogicDB(name, logicDbName);
			return Status.OK;
		} catch (Exception e) {
			logger.error(String.format("Error during delete worker %s for logic DB %s", name, logicDbName), e);
			return Status.ERROR;
		}
	}

	@DELETE
	@Path("dbGroupNode/{logicDbGroupName}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteWorkerByLogicDbGroup(@PathParam("logicDbGroupName") String logicDbGroupName, @PathParam("name") String name) {
		System.out.printf("Delete node: " + name);
		try {
			getFactory().getDasWorkerAccessor().unregisterByLogicDB(name, logicDbGroupName);
			return Status.OK;
		} catch (Exception e) {
			logger.error(String.format("Error during delete worker %s for DB group %s", name, logicDbGroupName), e);
			return Status.ERROR;
		}
	}
}
