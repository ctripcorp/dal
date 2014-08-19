package com.ctrip.platform.dal.daogen;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.xross.tools.xunit.Context;

public class CodeGenContext implements Context {
	
	protected int projectId;
	protected boolean regenerate;
	protected Progress progress;
	protected Map<String,?> hints;
	protected String namespace;
	public static String generatePath;
	
	protected Queue<GenTaskBySqlBuilder> _sqlBuilders = new ConcurrentLinkedQueue<GenTaskBySqlBuilder>();
	protected DalConfigHost dalConfigHost;
	
	static{
		generatePath = Configuration.get("gen_code_path");
	}
	
	public CodeGenContext(int projectId, boolean regenerate, Progress progress,
			Map<String, ?> hints) {
		super();
		this.projectId = projectId;
		this.regenerate = regenerate;
		this.progress = progress;
		this.hints = hints;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public boolean isRegenerate() {
		return regenerate;
	}

	public void setRegenerate(boolean regenerate) {
		this.regenerate = regenerate;
	}

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public Map<String, ?> getHints() {
		return hints;
	}

	public void setHints(Map<String, ?> hints) {
		this.hints = hints;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public static String getGeneratePath() {
		return generatePath;
	}

	public static void setGeneratePath(String generatePath) {
		CodeGenContext.generatePath = generatePath;
	}

	public Queue<GenTaskBySqlBuilder> get_sqlBuilders() {
		return _sqlBuilders;
	}

	public void set_sqlBuilders(Queue<GenTaskBySqlBuilder> _sqlBuilders) {
		this._sqlBuilders = _sqlBuilders;
	}

	public DalConfigHost getDalConfigHost() {
		return dalConfigHost;
	}

	public void setDalConfigHost(DalConfigHost dalConfigHost) {
		this.dalConfigHost = dalConfigHost;
	}
	
}
