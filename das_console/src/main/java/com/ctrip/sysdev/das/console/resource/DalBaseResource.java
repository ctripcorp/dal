package com.ctrip.sysdev.das.console.resource;

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
}
