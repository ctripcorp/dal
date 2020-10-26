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
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MgrRequestTask {
    private static final String selectValue = "ID=%sName=%sAge=%s";

    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private static Logger log = LoggerFactory.getLogger(Application.class);
    private int qps = 100;
    private int delay = 40;
    private SQLThread mySQLThread;
    private String clusterName = "dalservice2db_dalcluster";


    @Autowired
    private DalApplicationConfig dalApplicationConfig;
    @Autowired
    private DALServiceDao mySqlDao;


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
            Cat.logError("get qps from QConfig error", e);
        }

        try {
            mySQLThread = new MgrRequestTask.SQLThread(mySqlDao, delay, clusterName);
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
        mySQLThread.exit = true;
    }

    private void startTasks() {
        executor.submit(mySQLThread);
    }

    public void restart() throws Exception {
        cancelTasks();
        init();
    }

    public int getQps() {
        return qps;
    }

    private static class SQLThread extends Thread {
        public volatile boolean exit = false;
        private final DALServiceDao dao;
        private final long delay;
        private String clusterName;

        public SQLThread(DALServiceDao dao, long delay, String clusterName) {
            this.dao = dao;
            this.delay = delay;
            this.clusterName = clusterName;
        }

        @Override
        public void run() {
            DataSource dataSource = null;
            try {
                dataSource = new DalDataSourceFactory().getOrCreateDataSource(clusterName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!exit) {
                Transaction t = Cat.newTransaction("DAL.App.Task", dao.getDatabaseName());
                try {
                    try (Connection connection = dataSource.getConnection()){
                        try (Statement statement = connection.createStatement()){
                            statement.setQueryTimeout(1);
                            try (ResultSet resultSet = statement.executeQuery("select * from dalservicetable where ID = 10086")){
                                boolean result = explainSelect(resultSet);
                                if (!result) {
                                    boolean insertResult = statement.execute("insert into dalservicetable values (10086, 'mgrtest', 20);");
                                    Cat.logEvent("DalApplication", "MgrTest", Message.SUCCESS, String.valueOf(insertResult));
                                }

                            } catch (Exception e) {
                                Cat.logEvent("DalApplication", "MgrSelect", "fail", e.getMessage());
                            }
                        }
                        try (Statement statement = connection.createStatement()){
                            statement.setQueryTimeout(1);
                            Boolean flag = statement.execute("update dalservicetable set Age=" + (int)(Math.random() * 100) + " where ID=10086;");
                            Cat.logEvent("DalApplication", "MgrUpdate", Message.SUCCESS, String.valueOf(flag));
                        }catch (Exception e) {
                            Cat.logEvent("DalApplication", "MgrUpdate", "fail", e.getMessage());
                        }
                        t.setStatus(Transaction.SUCCESS);
                    } catch (Exception e) {
                        log.error(dao.getDatabaseName() + " error", e);
                        t.setStatus(e);
                    } finally {
                        t.complete();
                        try {
                            Thread.sleep(delay);
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                    Cat.logEvent("DalApplication", "MgrTest",  "fail", e.getMessage());
                }
            }
        }

        private boolean explainSelect(ResultSet resultSet) {
            try {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String name = resultSet.getString("Name");
                    int age = resultSet.getInt("Age");
                    Cat.logEvent("DalApplication", "MgrTest", Message.SUCCESS, String.format(selectValue, id, name, age));
                    return true;
                }
                return false;
            }catch (Exception e) {
                Cat.logEvent("DalApplication", "MgrTest", "fail", "no data");
                return false;
            }
        }
    }

}
