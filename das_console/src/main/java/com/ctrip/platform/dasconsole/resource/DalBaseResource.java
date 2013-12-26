package com.ctrip.platform.dasconsole.resource;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.apache.zookeeper.ZooKeeper;

@Resource
public class DalBaseResource {
	@Context
	private ServletContext sContext;

	public ZooKeeper getZk() {
		return (ZooKeeper) sContext.getAttribute("com.ctrip.sysdev.das.console.zk");
	}
	
	public void deleteNodeNested(String path) throws Exception {
		ZooKeeper zk = getZk();
		List<String> children = zk.getChildren(path, false);
		if(children!= null){
			for(String c: children){
				deleteNodeNested(path + "/" + c);
			}
		}
		zk.delete(path, -1);
	}
}
