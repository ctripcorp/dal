package com.ctrip.platform.dal.sql.logging;

import java.util.Map;

import com.ctrip.platform.dal.dao.client.LogContext;
import com.dianping.cat.message.ForkedTransaction;
import com.dianping.cat.message.Transaction;

public class CtripLogContext extends LogContext {
    private Transaction requestTransaction;
    private Transaction corssShardTasksTransaction;
    private Transaction singleTaskTransaction;
    private Map<String, ForkedTransaction> taskTransactions;

    public Transaction getRequestTransaction() {
        return requestTransaction;
    }

    public void setRequestTransaction(Transaction requestTransaction) {
        this.requestTransaction = requestTransaction;
    }

    public Transaction getCorssShardTasksTransaction() {
        return corssShardTasksTransaction;
    }

    public Transaction getSingleTaskTransaction() {
        return singleTaskTransaction;
    }

    public void setSingleTaskTransaction(Transaction singleTaskTransaction) {
        this.singleTaskTransaction = singleTaskTransaction;
    }

    public void setCorssShardTasksTransaction(Transaction tasksTransaction) {
        this.corssShardTasksTransaction = tasksTransaction;
    }

    public Map<String, ForkedTransaction> getTaskTransactions() {
        return taskTransactions;
    }

    public void setTaskTransactions(Map<String, ForkedTransaction> taskTransactions) {
        this.taskTransactions = taskTransactions;
    }

}
