package com.ctrip.platform.dal.daogen.generator.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlPojoHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpTableHost;
import com.ctrip.platform.dal.daogen.host.csharp.DatabaseHost;
import com.ctrip.platform.dal.daogen.utils.Configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

public class CSharpCodeGenContext implements CodeGenContext {
    private int projectId;
    private String projectName;
    private boolean regenerate;
    private boolean ignoreApproveStatus;
    private Progress progress;
    private Map<String, ?> hints;
    private String namespace;
    public String generatePath;

    private Queue<GenTaskBySqlBuilder> sqlBuilders = new ConcurrentLinkedQueue<>();
    private DalConfigHost dalConfigHost;

    // <DatabaseHost db_name, DatabaseHost>
    private Map<String, DatabaseHost> dbHosts = new ConcurrentHashMap<>();
    private Queue<CSharpFreeSqlHost> freeSqlHosts = new ConcurrentLinkedQueue<>();
    // <CSharpFreeSqlPojoHost pojo_name, CSharpFreeSqlPojoHost>
    private Map<String, CSharpFreeSqlPojoHost> freeSqlPojoHosts = new ConcurrentHashMap<>();
    // GenTaskByFreeSql class_name
    private Set<String> freeDaos = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    // PojoClassName of Table and View
    private Set<String> tableDaos = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    // PojoClassName of SP
    private Set<String> spDaos = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private Queue<CSharpTableHost> tableViewHosts = new ConcurrentLinkedQueue<>();
    private Queue<CSharpTableHost> spHosts = new ConcurrentLinkedQueue<>();

    private boolean newPojo = false;

    public static String regEx = null;
    public static Pattern inRegxPattern = null;

    static {
        // regEx="in\\s(@\\w+)";
        regEx = "(?i)In *(\\(?@\\w+\\)?)";
        inRegxPattern = Pattern.compile(regEx);
    }

    public CSharpCodeGenContext(int projectId, boolean regenerate, Progress progress, Map<String, ?> hints) {
        this.projectId = projectId;
        this.regenerate = regenerate;
        this.progress = progress;
        this.hints = hints;
        this.generatePath = Configuration.get("gen_code_path");
    }

    public Map<String, DatabaseHost> getDbHosts() {
        return dbHosts;
    }

    public void setDbHosts(Map<String, DatabaseHost> dbHosts) {
        this.dbHosts = dbHosts;
    }

    public Queue<CSharpFreeSqlHost> getFreeSqlHosts() {
        return freeSqlHosts;
    }

    public void setFreeSqlHosts(Queue<CSharpFreeSqlHost> freeSqlHosts) {
        this.freeSqlHosts = freeSqlHosts;
    }

    public Map<String, CSharpFreeSqlPojoHost> getFreeSqlPojoHosts() {
        return freeSqlPojoHosts;
    }

    public void set_freeSqlPojoHosts(Map<String, CSharpFreeSqlPojoHost> freeSqlPojoHosts) {
        this.freeSqlPojoHosts = freeSqlPojoHosts;
    }

    public Set<String> getFreeDaos() {
        return freeDaos;
    }

    public void setFreeDaos(Set<String> freeDaos) {
        this.freeDaos = freeDaos;
    }

    public Set<String> getTableDaos() {
        return tableDaos;
    }

    public void setTableDaos(Set<String> tableDaos) {
        this.tableDaos = tableDaos;
    }

    public Set<String> getSpDaos() {
        return spDaos;
    }

    public void setSpDaos(Set<String> spDaos) {
        this.spDaos = spDaos;
    }

    public Queue<CSharpTableHost> getTableViewHosts() {
        return tableViewHosts;
    }

    public void setTableViewHosts(Queue<CSharpTableHost> tableViewHosts) {
        this.tableViewHosts = tableViewHosts;
    }

    public Queue<CSharpTableHost> getSpHosts() {
        return spHosts;
    }

    public void setSpHosts(Queue<CSharpTableHost> spHosts) {
        this.spHosts = spHosts;
    }

    public boolean isNewPojo() {
        return newPojo;
    }

    public void setNewPojo(boolean newPojo) {
        this.newPojo = newPojo;
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
