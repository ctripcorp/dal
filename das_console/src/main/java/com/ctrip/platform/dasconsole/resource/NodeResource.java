package com.ctrip.platform.dasconsole.resource;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.ctrip.platform.dasconsole.common.Status;
import com.ctrip.platform.dasconsole.domain.Node;
import com.ctrip.platform.dasconsole.domain.NodeSetting;

@Resource
@Path("configure/node")
@Singleton
public class NodeResource extends DalBaseResource {
	@Context
	private ServletContext sContext;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Node> getNode() {
		List<Node> nodeList = new ArrayList<Node>();
		ZooKeeper zk = getZk();
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
	@Produces(MediaType.APPLICATION_JSON)
	public NodeSetting getNodeSetting(@PathParam("name") String name) {
		NodeSetting setting = new NodeSetting();
		ZooKeeper zk = getZk();
		try {
			String nodePath = "/dal/das/configure/node" + "/" + name;
			setting.setDirectory(new String(zk.getData(nodePath+ "/directory", false, null)));
			setting.setMaxHeapSize(new String(zk.getData(nodePath+ "/maxHeapSize", false, null)));
			setting.setStartingHeapSize(new String(zk.getData(nodePath+ "/startingHeapSize", false, null)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setting;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addNode(@FormParam("name") String name,
			@FormParam("directory") String directory,
			@FormParam("maxHeapSize") String maxHeapSize,
			@FormParam("startingHeapSize") String startingHeapSize) {
		System.out.printf("Add node: " + name);
		ZooKeeper zk = getZk();
		String nodePath = "/dal/das/configure/node" + "/" + name;
		
		try {
			zk.create(nodePath, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
			zk.create(nodePath + "/directory", directory.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(nodePath + "/maxHeapSize", maxHeapSize.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(nodePath + "/startingHeapSize", startingHeapSize.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
	}

	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Status updateNode(@PathParam("name") String name,
			@FormParam("directory") String directory,
			@FormParam("maxHeapSize") String maxHeapSize,
			@FormParam("startingHeapSize") String startingHeapSize) {
		System.out.printf("Update node: " + name);
		ZooKeeper zk = getZk();
		String nodePath = "/dal/das/configure/node" + "/" + name;
		
		try {
			zk.setData(nodePath + "/directory", directory.getBytes(), -1);
			zk.setData(nodePath + "/maxHeapSize", maxHeapSize.getBytes(), -1);
			zk.setData(nodePath + "/startingHeapSize", startingHeapSize.getBytes(), -1);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
	}

	@DELETE
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteNode(@PathParam("name") String name) {
		System.out.printf("Delete node: " + name);
		String nodePath = "/dal/das/configure/node" + "/" + name;
		try {
			deleteNodeNested(nodePath);
			return Status.OK;
		} catch (Exception e) {
			e.printStackTrace();
			return Status.ERROR;
		}
	}
}
