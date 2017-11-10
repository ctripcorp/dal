package com.ctrip.platform.dal.dao.client;

import java.util.Set;


/**
 * You can provide your own request level context info by suclassing this class
 * 
 * @author jhhe
 */
public class LogContext {
    private static String execludedPackageSpace = "com.ctrip.platform.dal.dao.";
    private boolean singleTask;
    private boolean seqencialExecution;
    private Set<String> shards;

    public boolean isSingleTask() {
        return singleTask;
    }

    public void setSingleTask(boolean singleTask) {
        this.singleTask = singleTask;
    }

    public boolean isSeqencialExecution() {
        return seqencialExecution;
    }

    public void setSeqencialExecution(boolean seqencialExecution) {
        this.seqencialExecution = seqencialExecution;
    }

    public Set<String> getShards() {
        return shards;
    }

    public void setShards(Set<String> shards) {
        singleTask = false;
        this.shards = shards;
    }

    public static String getRequestCaller(){
        StackTraceElement[] callers = Thread.currentThread().getStackTrace();
        for (int i = 4; i < callers.length; i++) {
            StackTraceElement caller = callers[i];
            if (caller.getClassName().startsWith(execludedPackageSpace))
                continue;
            
            return caller.getClassName() + "." + caller.getMethodName(); 
        }

        return "unknow";
    }
    
    private String caller;

    public void setCaller(String caller) {
        this.caller = caller;
    }
    
    public String getCaller() {
        return caller;
    }
}
