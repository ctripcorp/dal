package com.ctrip.platform.dal.application.service;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.application.Application;
import com.ctrip.platform.dal.application.Config.DalApplicationConfig;
import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class MgrRequestTask {
    private static final String selectSQL = "select * from dalservicetable limit 1;";
    private static final String insertSQL = "insert into dalservicetable values ('insert', 10);";
    private static final String updateSQL = "update dalservicetable set Age=11 where ID=1;";
    private static final String deleteSQL = "delete from dalservicetable where ID=2;";

    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private static Logger log = LoggerFactory.getLogger(Application.class);
    private int qps = 100;
    private int delay = 40;
    private SQLThread selectSQLThread;
    private SQLThread insertSQLThread;
    private SQLThread updateSQLThread;
    private SQLThread deleteSQLThread;
    private String clusterName = "dalservice2db_dalcluster";


    @Autowired
    private DalApplicationConfig dalApplicationConfig;


    @PostConstruct
    private void init() throws Exception {
        try {
            String qpsCfg = dalApplicationConfig.getQPS();
            if (qpsCfg != null)
                qps = Integer.parseInt(qpsCfg);
            String cluster = dalApplicationConfig.getClusterName();
            if (!StringUtils.isTrimmedEmpty(cluster))
                this.clusterName = cluster;
            delay = (1000 / qps) * 4;
        } catch (Exception e) {
            Cat.logError("get qps or clusterName from QConfig error", e);
        }

        DataSource dataSource = new DalDataSourceFactory().getOrCreateDataSource(clusterName);

        try {
            selectSQLThread = new SQLThread(delay, dataSource) {
                @Override
                void execute(Statement statement) throws SQLException {
                    Cat.logEvent("DalApplication", "mgrTest", Message.SUCCESS, "execute select");
                    statement.executeQuery(selectSQL);
                }
            };
            insertSQLThread = new SQLThread(delay, dataSource) {
                @Override
                void execute(Statement statement) throws SQLException {
                    Cat.logEvent("DalApplication", "mgrTest", Message.SUCCESS, "execute select");
                    statement.execute(insertSQL);
                }
            };
            updateSQLThread = new SQLThread(delay, dataSource) {
                @Override
                void execute(Statement statement) throws SQLException {
                    Cat.logEvent("DalApplication", "mgrTest", Message.SUCCESS, "execute update");
                    statement.execute(updateSQL);
                }
            };
            deleteSQLThread = new SQLThread(delay, dataSource) {
                @Override
                void execute(Statement statement) throws SQLException {
                    Cat.logEvent("DalApplication", "mgrTest", Message.SUCCESS, "execute select");
                    statement.execute(deleteSQL);
                }
            };
            startTasks();
            Cat.logEvent("DalApplication", "ConfigChanged", Message.SUCCESS, String.format("executor start with qps %s", getQps()));
        } catch (Exception e) {
            log.error("DALRequestTask init error", e);
        }
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        executor.shutdownNow();
    }

    public void cancelTasks() {
        selectSQLThread.exit = true;
        updateSQLThread.exit = true;
        insertSQLThread.exit = true;
        deleteSQLThread.exit = true;
    }

    private void startTasks() {
        executor.submit(selectSQLThread);
        executor.submit(updateSQLThread);
        executor.submit(insertSQLThread);
        executor.submit(deleteSQLThread);
    }

    public void restart() throws Exception {
        cancelTasks();
        init();
    }

    public int getQps() {
        return qps;
    }

    private static abstract class SQLThread extends Thread {
        public volatile boolean exit = false;
        private final long delay;
        private DataSource dataSource;

        public SQLThread(long delay, DataSource dataSource) {
            this.delay = delay;
            this.dataSource = dataSource;
        }

        @Override
        public void run() {
            while (!exit) {
                Transaction out = Cat.newTransaction("DAL.App.Task", "DalMgrTest");
                Cat.logEvent("DalApplication", "mgrTest", Message.SUCCESS, "getConnectionStart");
                Transaction in = Cat.newTransaction("DAL.App.Task.in", "getConnection");
                try (Connection connection = dataSource.getConnection()){
                    Cat.logEvent("MGR.getConnection", connection.getMetaData().getURL(), Message.SUCCESS, "getConnectionEnd");
                    in.setStatus(Transaction.SUCCESS);
                    in.complete();
                    try (Statement statement = connection.createStatement()){
                        statement.setQueryTimeout(1);
                        Cat.logEvent("DalApplication", "mgrTest", Message.SUCCESS, "executeSQLStart");
                        try {
                            execute(statement);
                            Cat.logEvent("DalApplication", "mgrTest", Message.SUCCESS, "execute SQL finish");
                        } catch (Exception e) {
                            Cat.logEvent("DalApplication", "mgrTest", "fail", "execute failed");
                        }
                    } catch (Exception e ) {
                        Cat.logEvent("DalApplication", "mgrTest", "fail", "get statement failed");
                    }
                    out.setStatus(Transaction.SUCCESS);
                } catch (Exception e) {
                    log.error("DalMgrTest error", e);
                    Cat.logEvent("MGR.getConnection", "exception", "fail", "get connection failed");
                    out.setStatus(e);
                } finally {
                    out.complete();
                    try {
                        Thread.sleep(delay);
                    } catch (Exception e) {
                    }
                }
            }
        }

        abstract void execute(Statement statement) throws SQLException;
    }

}
