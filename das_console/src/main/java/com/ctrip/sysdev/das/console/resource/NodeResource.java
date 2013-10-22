package com.ctrip.sysdev.das.console.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.zookeeper.ZooKeeper;
import org.glassfish.jersey.server.JSONP;

import com.ctrip.sysdev.das.console.domain.Node;
import com.ctrip.sysdev.das.console.domain.NodeSetting;

@Resource
@Path("configure/node")
@Singleton
public class NodeResource {
	@Context
	private ServletContext sContext;

	@GET
	@JSONP(queryParam = "jsonpCallback")
	@Produces("application/x-javascript")
	public List<Node> getNode() {
		List<Node> nodeList = new ArrayList<Node>();
		ZooKeeper zk = (ZooKeeper) sContext.getAttribute("com.ctrip.sysdev.das.console.zk");
		try {
			List<String> nodeNameList = zk.getChildren("/dal/das/configure/node", false);
			for (String nodeName : nodeNameList) {
				String nodePath = "/dal/das/configure/node" + "/" + nodeName;
				Node node = new Node();
				node.setName(nodeName);
				NodeSetting setting = new NodeSetting();
				setting.setDirectory(new String(zk.getData(nodePath+ "/directory", false, null)));
				setting.setMaxHeapSize(new String(zk.getData(nodePath+ "/maxHeapSize", false, null)));
				setting.setStartingHeapSize(new String(zk.getData(nodePath+ "/startingHeapSize", false, null)));
				node.setSetting(setting);
				nodeList.add(node);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeList;
	}

	@GET
	@Path("{name}")
	@JSONP(queryParam = "jsonpCallback")
	@Produces("application/x-javascript")
	public NodeSetting getNodeSetting() {
		NodeSetting setting = new NodeSetting();
		setting.setDirectory("direc");
		setting.setMaxHeapSize("123");
		setting.setStartingHeapSize("25");
		return setting;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void addNode(@FormParam("name") String name,
			@FormParam("directory") String directory,
			@FormParam("maxHeapSize") String maxHeapSize,
			@FormParam("startingHeapSize") String startingHeapSize) {
		System.out.printf("add node: " + name);
	}

	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateNode(@PathParam("name") String name,
			@FormParam("directory") String directory,
			@FormParam("maxHeapSize") String maxHeapSize,
			@FormParam("startingHeapSize") String startingHeapSize) {
		System.out.printf("Update node: " + name);
	}

	@DELETE
	@Path("{name}")
	public void deleteNode(@PathParam("name") String name) {
		System.out.printf("Delete node: " + name);
	}
}
