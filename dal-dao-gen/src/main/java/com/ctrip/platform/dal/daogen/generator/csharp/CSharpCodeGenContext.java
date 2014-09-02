package com.ctrip.platform.dal.daogen.generator.csharp;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlPojoHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpTableHost;
import com.ctrip.platform.dal.daogen.host.csharp.DatabaseHost;

public class CSharpCodeGenContext extends CodeGenContext {
	
	protected Map<String, DatabaseHost> _dbHosts = new ConcurrentHashMap<String, DatabaseHost>();
	protected Queue<CSharpFreeSqlHost> _freeSqlHosts = new ConcurrentLinkedQueue<CSharpFreeSqlHost>();
	protected Map<String, CSharpFreeSqlPojoHost> _freeSqlPojoHosts = new ConcurrentHashMap<String, CSharpFreeSqlPojoHost>();
	protected Set<String> _freeDaos = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	protected Set<String> _tableDaos = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	protected Set<String> _spDaos = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	protected Queue<CSharpTableHost> _tableViewHosts = new ConcurrentLinkedQueue<CSharpTableHost>();
	protected Queue<CSharpTableHost> _spHosts = new ConcurrentLinkedQueue<CSharpTableHost>();
	
	protected boolean newPojo = false;
	
	public static String regEx = null;
	public static Pattern inRegxPattern = null;
	
	static{
		 //regEx="in\\s(@\\w+)";
		 regEx = "(?i)In *(\\(?@\\w+\\)?)";
		 inRegxPattern = Pattern.compile(regEx);
	}
	
	public CSharpCodeGenContext(int projectId, boolean regenerate,
			Progress progress, Map<String, ?> hints) {
		super(projectId, regenerate, progress, hints);
	}

	public Map<String, DatabaseHost> get_dbHosts() {
		return _dbHosts;
	}

	public void set_dbHosts(Map<String, DatabaseHost> _dbHosts) {
		this._dbHosts = _dbHosts;
	}

	public Queue<CSharpFreeSqlHost> get_freeSqlHosts() {
		return _freeSqlHosts;
	}

	public void set_freeSqlHosts(Queue<CSharpFreeSqlHost> _freeSqlHosts) {
		this._freeSqlHosts = _freeSqlHosts;
	}

	public Map<String, CSharpFreeSqlPojoHost> get_freeSqlPojoHosts() {
		return _freeSqlPojoHosts;
	}

	public void set_freeSqlPojoHosts(
			Map<String, CSharpFreeSqlPojoHost> _freeSqlPojoHosts) {
		this._freeSqlPojoHosts = _freeSqlPojoHosts;
	}

	public Set<String> get_freeDaos() {
		return _freeDaos;
	}

	public void set_freeDaos(Set<String> _freeDaos) {
		this._freeDaos = _freeDaos;
	}

	public Set<String> get_tableDaos() {
		return _tableDaos;
	}

	public void set_tableDaos(Set<String> _tableDaos) {
		this._tableDaos = _tableDaos;
	}

	public Set<String> get_spDaos() {
		return _spDaos;
	}

	public void set_spDaos(Set<String> _spDaos) {
		this._spDaos = _spDaos;
	}

	public Queue<CSharpTableHost> get_tableViewHosts() {
		return _tableViewHosts;
	}

	public void set_tableViewHosts(Queue<CSharpTableHost> _tableViewHosts) {
		this._tableViewHosts = _tableViewHosts;
	}

	public Queue<CSharpTableHost> get_spHosts() {
		return _spHosts;
	}

	public void set_spHosts(Queue<CSharpTableHost> _spHosts) {
		this._spHosts = _spHosts;
	}

	public boolean isNewPojo() {
		return newPojo;
	}

	public void setNewPojo(boolean newPojo) {
		this.newPojo = newPojo;
	}

	public static String getRegEx() {
		return regEx;
	}

	public static void setRegEx(String regEx) {
		CSharpCodeGenContext.regEx = regEx;
	}

	public static Pattern getInRegxPattern() {
		return inRegxPattern;
	}

	public static void setInRegxPattern(Pattern inRegxPattern) {
		CSharpCodeGenContext.inRegxPattern = inRegxPattern;
	}

}













