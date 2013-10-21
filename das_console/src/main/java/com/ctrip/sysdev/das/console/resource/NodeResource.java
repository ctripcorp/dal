package com.ctrip.sysdev.das.console.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ctrip.sysdev.das.console.domain.Node;
import com.ctrip.sysdev.das.console.domain.NodeSetting;

@Resource
@Path("configure/node")
@Singleton
public class NodeResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Node> getNode() {
		List<Node> nodeList = new ArrayList<Node>();
		Node node = new Node();
		node.setName("aaa");
		NodeSetting setting = new NodeSetting();
		setting.setDirectory("direc");
		setting.setMaxHeapSize("123");
		setting.setStartingHeapSize("25");
		node.setSetting(setting);
		nodeList.add(node);
		return nodeList;
	}
	
	@GET
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public NodeSetting getDbSetting() {
		NodeSetting setting = new NodeSetting();
		setting.setDirectory("direc");
		setting.setMaxHeapSize("123");
		setting.setStartingHeapSize("25");
		return setting;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void addDb(@FormParam("name") String name, @FormParam("directory") String directory, @FormParam("maxHeapSize") String maxHeapSize, @FormParam("startingHeapSize") String startingHeapSize) {
		System.out.printf("add node: " +name);
	}
	
	@PUT
	@Path("{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateDB(@PathParam("name") String name, @FormParam("directory") String directory, @FormParam("maxHeapSize") String maxHeapSize, @FormParam("startingHeapSize") String startingHeapSize) {
		System.out.printf("Update node: " +name);
	}
	
	@DELETE
	@Path("{name}")
	public void deleteDb(@PathParam("name") String name) {
		System.out.printf("Delete node: " +name);
	}
}
