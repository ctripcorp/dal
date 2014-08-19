package com.ctrip.platform.dal.daogen.generator.java;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.host.java.ContextHost;
import com.ctrip.platform.dal.daogen.host.java.FreeSqlHost;
import com.ctrip.platform.dal.daogen.host.java.JavaMethodHost;
import com.ctrip.platform.dal.daogen.host.java.JavaTableHost;
import com.ctrip.platform.dal.daogen.host.java.SpDbHost;
import com.ctrip.platform.dal.daogen.host.java.SpHost;
import com.ctrip.platform.dal.daogen.host.java.ViewHost;

public class JavaCodeGenContext extends CodeGenContext{
	
	protected Queue<JavaTableHost> _tableHosts = new ConcurrentLinkedQueue<JavaTableHost>();
	protected Queue<ViewHost> _viewHosts = new ConcurrentLinkedQueue<ViewHost>();
	protected Map<String, SpDbHost> _spHostMaps = new ConcurrentHashMap<String, SpDbHost>();
	protected Queue<SpHost> _spHosts = new ConcurrentLinkedQueue<SpHost>();
	protected ContextHost contextHost = new ContextHost();
	protected Queue<FreeSqlHost> _freeSqlHosts = new ConcurrentLinkedQueue<FreeSqlHost>();
	protected Map<String, JavaMethodHost> _freeSqlPojoHosts = new ConcurrentHashMap<String, JavaMethodHost>();
	
	public JavaCodeGenContext(int projectId, boolean regenerate,
			Progress progress) {
		super(projectId, regenerate, progress, null);
	}

	public Queue<JavaTableHost> get_tableHosts() {
		return _tableHosts;
	}

	public void set_tableHosts(Queue<JavaTableHost> _tableHosts) {
		this._tableHosts = _tableHosts;
	}

	public Queue<ViewHost> get_viewHosts() {
		return _viewHosts;
	}

	public void set_viewHosts(Queue<ViewHost> _viewHosts) {
		this._viewHosts = _viewHosts;
	}

	public Map<String, SpDbHost> get_spHostMaps() {
		return _spHostMaps;
	}

	public void set_spHostMaps(Map<String, SpDbHost> _spHostMaps) {
		this._spHostMaps = _spHostMaps;
	}

	public Queue<SpHost> get_spHosts() {
		return _spHosts;
	}

	public void set_spHosts(Queue<SpHost> _spHosts) {
		this._spHosts = _spHosts;
	}

	public ContextHost getContextHost() {
		return contextHost;
	}

	public void setContextHost(ContextHost contextHost) {
		this.contextHost = contextHost;
	}

	public Queue<FreeSqlHost> get_freeSqlHosts() {
		return _freeSqlHosts;
	}

	public void set_freeSqlHosts(Queue<FreeSqlHost> _freeSqlHosts) {
		this._freeSqlHosts = _freeSqlHosts;
	}

	public Map<String, JavaMethodHost> get_freeSqlPojoHosts() {
		return _freeSqlPojoHosts;
	}

	public void set_freeSqlPojoHosts(Map<String, JavaMethodHost> _freeSqlPojoHosts) {
		this._freeSqlPojoHosts = _freeSqlPojoHosts;
	}

}













