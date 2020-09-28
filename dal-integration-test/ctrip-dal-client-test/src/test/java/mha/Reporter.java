package mha;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.CommonFileLoader;
import com.ctrip.platform.dal.dao.configure.Resource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author c7ch23en
 */
public class Reporter {

    private static final String PATH_FAILOVER_LOGS = "D:\\projects\\20200825\\";
    private static final String FILE_FAILOVER_CLUSTER_LIST = PATH_FAILOVER_LOGS + "_failover_clusters_";
    private static final String FILE_FAILOVER_REPORT = PATH_FAILOVER_LOGS + "_failover_report_";
    private static final String BASE_URL_FAILOVER_LOG_API = "http://10.9.118.7:8080/hamanager/clusterfailoverlog/dir=/var/log/masterha/";
    private static final String FAIL_TIME = "01:02:54";

    private static final Pattern REGEX_MASTER_DOWN_DETECTED = Pattern.compile("Connection failed 4 time[^\f\n]*\n[^\f\n]*(\\d{2}:\\d{2}:\\d{2})[^\f\n]*Master is not reachable from health checker");
    private static final Pattern REGEX_CONFIG_LOADED = Pattern.compile("Reading server configuration[^\f\n]*\n[^\f\n]*(\\d{2}:\\d{2}:\\d{2})[^\f\n]*GTID failover mode = 1");
    private static final Pattern REGEX_FAILOVER_STARTED = Pattern.compile("Configuration Check Phase[^\f\n]*\n[^\f\n]*\n[^\f\n]*(\\d{2}:\\d{2}:\\d{2})[^\f\n]*GTID failover mode = 1");
    private static final Pattern REGEX_VIP_SHUTDOWN = Pattern.compile("shut down cluster[^\f\n]*\n[^\f\n]*(\\d{2}:\\d{2}:\\d{2})[^\f\n]*done");
    private static final Pattern REGEX_VIP_SHUTDOWN_V2 = Pattern.compile("shut down clust[^\f\n]*\n[^\f\n]*\n[^\f\n]*(\\d{2}:\\d{2}:\\d{2})[^\f\n]*done");
    private static final Pattern REGEX_UPDATE_DAL_CLUSTERS = Pattern.compile("(\\d{2}:\\d{2}:\\d{2})[^\f\n]*update dal cluster");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommonFileLoader fileLoader = new CommonFileLoader();
    private final HttpExecutor httpExecutor = HttpExecutor.getInstance();

    @Test
    public void report() {
        Set<FailoverCluster> clusters = getFailoverClusters();
        logger.info("{} failover cluster(s) found", clusters.size());
        StringBuilder builder = new StringBuilder();
        Map<String, String> map = getDalClusterDetails();
        clusters.forEach(cluster -> {
            try {
                FailoverDetails details = getFailoverDetails(cluster);
                details.dalClustersUpdatedTime = map.get(cluster.name);
                builder.append(details.export()).append("\n");
                logger.info("[{}] Failover details parsed", cluster.name);
            } catch (Throwable t) {
                logger.warn("[{}] Errored parsing failover details", cluster.name, t);
            }
        });
        try {
            writeFile(FILE_FAILOVER_REPORT, builder.toString());
        } catch (IOException e) {
            logger.error("Errored writing report file: {}", FILE_FAILOVER_REPORT, e);
        }
    }

    private Set<FailoverCluster> getFailoverClusters() {
        Set<FailoverCluster> failoverClusters = new LinkedHashSet<>();
        Resource<String> failoverClustersFile = fileLoader.getResource(FILE_FAILOVER_CLUSTER_LIST);
        if (failoverClustersFile != null) {
            String content = failoverClustersFile.getContent();
            String[] clusters = content.split("\n");
            for (String cluster : clusters) {
                String[] meta = cluster.split("\t");
                if (meta.length == 2)
                    failoverClusters.add(new FailoverCluster(meta[0].trim().toLowerCase(), meta[1].trim().toLowerCase()));
            }
        }
        return failoverClusters;
    }

    private FailoverDetails getFailoverDetails(FailoverCluster cluster) {
        String log = getFailoverLog(cluster);
        FailoverDetails details = new FailoverDetails(cluster.name, FAIL_TIME);
        Matcher matcher = REGEX_MASTER_DOWN_DETECTED.matcher(log);
        if (matcher.find())
            details.masterDownDetectedTime = matcher.group(1);
        matcher = REGEX_CONFIG_LOADED.matcher(log);
        if (matcher.find())
            details.configLoadedTime = matcher.group(1);
        matcher = REGEX_FAILOVER_STARTED.matcher(log);
        if (matcher.find())
            details.failoverStartedTime = matcher.group(1);
        matcher = REGEX_VIP_SHUTDOWN.matcher(log);
        if (matcher.find())
            details.vipShutdownTime = matcher.group(1);
        else {
            matcher = REGEX_VIP_SHUTDOWN_V2.matcher(log);
            if (matcher.find())
                details.vipShutdownTime = matcher.group(1);
        }
        matcher = REGEX_UPDATE_DAL_CLUSTERS.matcher(log);
        if (matcher.find())
            details.updateDalClustersTime = matcher.group(1);
        return details;
    }

    private String getFailoverLog(FailoverCluster cluster) {
        String failoverLog = getFailoverLogFromLocalFile(cluster);
        if (failoverLog == null)
            failoverLog = getFailoverLogFromApi(cluster);
        if (failoverLog == null) {
            String msg = String.format("[%s] Failover log not found", cluster.name);
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        return failoverLog;
    }

    private String getFailoverLogFromLocalFile(FailoverCluster cluster) {
        String[] possibleFileNames = cluster.getPossibleLogFileNames();
        for (String possibleFileName : possibleFileNames) {
            Resource<String> localFile = fileLoader.getResource(PATH_FAILOVER_LOGS + possibleFileName);
            if (localFile != null && !StringUtils.isTrimmedEmpty(localFile.getContent()))
                return localFile.getContent();
        }
        return null;
    }

    private String getFailoverLogFromApi(FailoverCluster cluster) {
        String[] possibleFileNames = cluster.getPossibleLogFileNames();
        for (String possibleFileName : possibleFileNames) {
            String url = String.format("%s%s/%s", BASE_URL_FAILOVER_LOG_API, cluster.name, possibleFileName);
            try {
                String failoverLog = httpExecutor.executeGet(url, null, 5000);
                if (!StringUtils.isTrimmedEmpty(failoverLog)) {
                    failoverLog = failoverLog.replaceAll("<br />", "\n");
                    try {
                        writeFile(PATH_FAILOVER_LOGS + possibleFileName, failoverLog);
                    } catch (IOException e) {
                        logger.warn("[{}] Errored writing log file: {}", cluster.name, possibleFileName);
                    }
                    return failoverLog;
                }
            } catch (IOException e) {
                logger.warn("[{}] Errored getting failover log from api", cluster.name);
            }
        }
        return null;
    }

    private void writeFile(String fullPath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(fullPath)) {
            writer.write(content);
        }
    }

    private static class FailoverCluster {
        static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String name;
        String completedTime;

        FailoverCluster(String name, String completedTime) {
            this.name = name;
            this.completedTime = completedTime;
        }

        String[] getPossibleLogFileNames() {
            String[] names = new String[3];
            names[0] = format(completedTime);
            LocalDateTime time = LocalDateTime.parse(completedTime, TIME_FORMATTER);
            names[1] = format(time.minusSeconds(1).format(TIME_FORMATTER));
            names[2] = format(time.plusSeconds(1).format(TIME_FORMATTER));
            return names;
        }

        String format(String timestamp) {
            return String.format("%s.log.%s", name, timestamp
                    .replace('-', '_')
                    .replace(' ', '_')
                    .replace(':', '_'));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FailoverCluster that = (FailoverCluster) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "FailoverCluster{" +
                    "name='" + name + '\'' +
                    ", completedTime='" + completedTime + '\'' +
                    '}';
        }
    }

    static class FailoverDetails {
        String clusterName;
        String failTime;
        String masterDownDetectedTime;
        String configLoadedTime;
        String failoverStartedTime;
        String vipShutdownTime;
        String updateDalClustersTime;
        String dalClustersUpdatedTime;

        FailoverDetails(String clusterName, String failTime) {
            this.clusterName = clusterName;
            this.failTime = failTime;
        }

        String export() {
            return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", clusterName,
                    normalize(failTime), normalize(masterDownDetectedTime), normalize(configLoadedTime),
                    normalize(failoverStartedTime), normalize(vipShutdownTime), normalize(updateDalClustersTime),
                    normalize(dalClustersUpdatedTime));
        }

        String normalize(String time) {
            return StringUtils.isTrimmedEmpty(time) ? "-" : time;
        }
    }

    private Map<String, String> getDalClusterDetails() {
        Resource<String> report = fileLoader.getResource(PATH_FAILOVER_LOGS + "_dalcluster_finalsuccess_");
        String content = report.getContent();
        Map<String, String> map = new HashMap<>();
        String[] clusters = content.split("\n");
        for (String cluster : clusters) {
            String[] meta = cluster.split("\t");
            map.put(meta[1].toLowerCase(), meta[2].toLowerCase());
        }
        return map;
    }

}
