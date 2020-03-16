package com.ctrip.datasource.configure;

import com.ctrip.datasource.util.entity.ClusterNodeInfo;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.dao.configure.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlApiConnectionStringParser {
    private static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
    private static final String MGR_URL_TEMPLATE = "jdbc:mysql:replication://%s/%s?useUnicode=true&characterEncoding=%s";
    private static final String MGR_HOST_PORT_TEMPLATE = "address=(type=master)(protocol=tcp)(host=%s)(port=%s)";
    private static final String COM_SPLIT = ",";
    private static final String ONLINE = "online";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String MYSQL_API_CLUSTER_TYPE_MGR = "mgr";
    private static final String MYSQL_API_CLUSTER_TYPE_MHA = "mha";
    private static final String MYSQL_MASTER = "master";

    private static final Pattern dbnamePattern =
            Pattern.compile("(database|initial\\scatalog)=([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern dbcharsetPattern = Pattern.compile("(charset)=([^;]+)", Pattern.CASE_INSENSITIVE);

    private static MysqlApiConnectionStringParser parser = null;

    public synchronized static MysqlApiConnectionStringParser getInstance() {
        if (parser == null) {
            parser = new MysqlApiConnectionStringParser();
        }
        return parser;
    }

    public DataSourceConfigure parser(String dbName, MysqlApiConnectionStringInfo info, String token,
                                               DBModel dbModel) throws UnsupportedEncodingException {
        if (info == null || info.getClusternodeinfolist().size() == 0) {
            return null;
        }
        String connectionString = info.getConnectionstring();
        DataSourceConfigure configure = parserConnectionString(dbName, connectionString, token);

        if (DBModel.MGR.equals(dbModel)) {
            String database = null;
            Matcher matcher = dbnamePattern.matcher(connectionString);
            if (matcher.find()) {
                database = matcher.group(2);
            }
            String charset = null;
            matcher = dbcharsetPattern.matcher(connectionString);
            if (matcher.find()) {
                charset = matcher.group(2);
            } else {
                charset = DEFAULT_ENCODING;
            }

            String ipAndPortString = "";
            String masterIp = null;
            String port = null;
            int masterCount = 0;
            List<ClusterNodeInfo> clusterNodeInfoList = info.getClusternodeinfolist();

            for (ClusterNodeInfo clusterNodeInfo : clusterNodeInfoList) {
                if (checkClusterNodeInfo(clusterNodeInfo)) {
                    ipAndPortString += String.format(MGR_HOST_PORT_TEMPLATE, clusterNodeInfo.getIp_business(),
                            clusterNodeInfo.getDns_port()) + COM_SPLIT;
                    if (MYSQL_MASTER.equalsIgnoreCase(clusterNodeInfo.getRole())) {
                        masterIp = clusterNodeInfo.getIp_business();
                        port = String.valueOf(clusterNodeInfo.getDns_port());
                        ++ masterCount;
                    }
                }
            }

            if (info.getClustertype().equalsIgnoreCase(MYSQL_API_CLUSTER_TYPE_MHA) && masterCount == 1 && StringUtils.isNotBlank(masterIp)
                    && StringUtils.isNotBlank(port)) {
                String ipDirectConnectUrl = ConnectionStringParser.replaceHostAndPort(configure.getConnectionUrl(), masterIp, port);
                configure.setConnectionUrl(ipDirectConnectUrl);
                return configure;
            }

            else if (info.getClustertype().equalsIgnoreCase(MYSQL_API_CLUSTER_TYPE_MGR)) {
                ipAndPortString = ipAndPortString.substring(0, ipAndPortString.length() - 1);
                String mgrUrl = String.format(MGR_URL_TEMPLATE, ipAndPortString, database, charset);
                configure.setConnectionUrl(mgrUrl);
                return configure;
            }
        }
        else if (DBModel.STANDALONE.equals(dbModel)) {
            return configure;
        }
        return null;
    }

    private boolean checkClusterNodeInfo(ClusterNodeInfo c) {
        return ONLINE.equalsIgnoreCase(c.getStatus()) && StringUtils.isNotBlank(c.getIp_business()) && c.getDns_port() != 0;
    }

    private DataSourceConfigure parserConnectionString(String dbName, String connectionString, String token) throws UnsupportedEncodingException {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        DalConnectionStringConfigure stringConfigure = ConnectionStringParser.getInstance().parse(dbName, connectionString);
        dataSourceConfigure.setName(dbName);
        dataSourceConfigure.setUserName(stringConfigure.getUserName());
        dataSourceConfigure.setPassword(decrypt(stringConfigure.getPassword(), token));
        dataSourceConfigure.setConnectionUrl(stringConfigure.getConnectionUrl());
        dataSourceConfigure.setDriverClass(DRIVER_MYSQL);
        return dataSourceConfigure;
    }

    private String decrypt(String password, String token) throws UnsupportedEncodingException {
        String decodePassword = new String(Base64.decodeBase64(password), "utf-8");
        StringBuilder sb = new StringBuilder(decodePassword);
        sb.reverse();
        return sb.substring(0, sb.toString().indexOf(token));
    }
}