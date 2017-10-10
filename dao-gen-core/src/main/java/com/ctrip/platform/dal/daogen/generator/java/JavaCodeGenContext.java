package com.ctrip.platform.dal.daogen.generator.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.host.java.*;
import com.ctrip.platform.dal.daogen.utils.Configuration;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JavaCodeGenContext implements CodeGenContext {
    protected int projectId;
    private String projectName;
    protected boolean regenerate;
    private boolean ignoreApproveStatus;
    protected Progress progress;
    protected String namespace;
    public String generatePath;

    protected Queue<GenTaskBySqlBuilder> sqlBuilders = new ConcurrentLinkedQueue<>();
    protected DalConfigHost dalConfigHost;

    protected Queue<JavaTableHost> tableHosts = new ConcurrentLinkedQueue<>();
    protected Queue<ViewHost> viewHosts = new ConcurrentLinkedQueue<>();
    // <SpDbHost dbName, SpDbHost>
    protected Map<String, SpDbHost> spHostMaps = new ConcurrentHashMap<>();
    protected Queue<SpHost> spHosts = new ConcurrentLinkedQueue<>();
    protected ContextHost contextHost = new ContextHost();
    protected Queue<FreeSqlHost> freeSqlHosts = new ConcurrentLinkedQueue<>();
    // <JavaMethodHost pojoClassName, JavaMethodHost>
    protected Map<String, JavaMethodHost> freeSqlPojoHosts = new ConcurrentHashMap<>();

    public JavaCodeGenContext(int projectId, boolean regenerate, Progress progress) {
        this.projectId = projectId;
        this.regenerate = regenerate;
        this.progress = progress;
        this.generatePath = Configuration.get("gen_code_path");
    }

    public Queue<JavaTableHost> getTableHosts() {
        return tableHosts;
    }

    public void setTableHosts(Queue<JavaTableHost> tableHosts) {
        this.tableHosts = tableHosts;
    }

    public Queue<ViewHost> getViewHosts() {
        return viewHosts;
    }

    public void setViewHosts(Queue<ViewHost> viewHosts) {
        this.viewHosts = viewHosts;
    }

    public Map<String, SpDbHost> getSpHostMaps() {
        return spHostMaps;
    }

    public void setSpHostMaps(Map<String, SpDbHost> spHostMaps) {
        this.spHostMaps = spHostMaps;
    }

    public Queue<SpHost> getSpHosts() {
        return spHosts;
    }

    public void setSpHosts(Queue<SpHost> spHosts) {
        this.spHosts = spHosts;
    }

    public ContextHost getContextHost() {
        return contextHost;
    }

    public void setContextHost(ContextHost contextHost) {
        this.contextHost = contextHost;
    }

    public Queue<FreeSqlHost> getFreeSqlHosts() {
        return freeSqlHosts;
    }

    public void setFreeSqlHosts(Queue<FreeSqlHost> freeSqlHosts) {
        this.freeSqlHosts = freeSqlHosts;
    }

    public Map<String, JavaMethodHost> get_freeSqlPojoHosts() {
        return freeSqlPojoHosts;
    }

    public void setFreeSqlPojoHosts(Map<String, JavaMethodHost> freeSqlPojoHosts) {
        this.freeSqlPojoHosts = freeSqlPojoHosts;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getGeneratePath() {
        return generatePath;
    }

    public void setGeneratePath(String generatePath) {
        this.generatePath = generatePath;
    }

    public Queue<GenTaskBySqlBuilder> getSqlBuilders() {
        return sqlBuilders;
    }

    public void setSqlBuilders(Queue<GenTaskBySqlBuilder> sqlBuilders) {
        this.sqlBuilders = sqlBuilders;
    }

    public DalConfigHost getDalConfigHost() {
        return dalConfigHost;
    }

    public void setDalConfigHost(DalConfigHost dalConfigHost) {
        this.dalConfigHost = dalConfigHost;
    }

    public boolean isIgnoreApproveStatus() {
        return ignoreApproveStatus;
    }

    public void setIgnoreApproveStatus(boolean ignoreApproveStatus) {
        this.ignoreApproveStatus = ignoreApproveStatus;
    }

}
