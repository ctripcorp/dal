package com.ctrip.datasource.configure;

import com.ctrip.datasource.util.entity.ClusterNodeInfo;
import com.ctrip.datasource.util.entity.VariableConnectionStringInfo;
import com.ctrip.platform.dal.dao.configure.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableConnectionStringParser {
    private static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
    private static final String MGR_URL_TEMPLATE = "jdbc:mysql:replication://%s/%s?useUnicode=true&characterEncoding=%s";
    private static final String MGR_HOST_PORT_TEMPLATE = "address=(type=%s)(protocol=tcp)(host=%s)(port=%s)";
    private static final String COM_SPLIT = ",";
    private static final String ONLINE = "online";
    private static final String MYSQL_SLAVE = "slave";
    private static final String MYSQL_MASTER = "master";
    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final Pattern dbnamePattern =
            Pattern.compile("(database|initial\\scatalog)=([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern dbcharsetPattern = Pattern.compile("(charset)=([^;]+)", Pattern.CASE_INSENSITIVE);

    public static DalConnectionStringConfigure parser(String dbName, VariableConnectionStringInfo info, String token) throws UnsupportedEncodingException {
        if (info == null) {
            return null;
        }
        String connectionString = info.getConnectionstring();
        DataSourceConfigure configure = parserConnectionString(dbName, connectionString, token);
        List<ClusterNodeInfo> clusterNodeInfoList = info.getClusternodeinfolist();
        if (clusterNodeInfoList == null || clusterNodeInfoList.size() <= 1) {
            return configure;
        }
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
        for (ClusterNodeInfo clusterNodeInfo : clusterNodeInfoList) {
            if (checkClusterNodeInfo(clusterNodeInfo)) {
                ipAndPortString += String.format(MGR_HOST_PORT_TEMPLATE, getMysqlRole(clusterNodeInfo.getRole()), clusterNodeInfo.getIp_business(),
                        clusterNodeInfo.getDns_port()) + COM_SPLIT;
            }
        }
        ipAndPortString = ipAndPortString.substring(0, ipAndPortString.length() - 1);
        String mgrUrl = String.format(MGR_URL_TEMPLATE, ipAndPortString, database, charset);
        configure.setConnectionUrl(mgrUrl);
        return configure;
    }

    private static boolean checkClusterNodeInfo(ClusterNodeInfo c) {
        return ONLINE.equalsIgnoreCase(c.getStatus()) && StringUtils.isNotBlank(c.getIp_business()) && c.getDns_port() != 0;
    }

    private static DataSourceConfigure parserConnectionString(String dbName, String connectionString, String token) throws UnsupportedEncodingException {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        ConnectionStringParser parser = ConnectionStringParser.getInstance();
        DalConnectionStringConfigure stringConfigure = parser.parse(dbName, connectionString);
        dataSourceConfigure.setName(dbName);
        dataSourceConfigure.setUserName(stringConfigure.getUserName());
        dataSourceConfigure.setPassword(decrypt(stringConfigure.getPassword(), token));
        dataSourceConfigure.setConnectionUrl(stringConfigure.getConnectionUrl());
        dataSourceConfigure.setDriverClass(DRIVER_MYSQL);
        return dataSourceConfigure;
    }

    private static String decrypt(String password, String token) throws UnsupportedEncodingException {
        String decodePassword = new String(Base64.decodeBase64(password), "utf-8");
        StringBuilder sb = new StringBuilder(decodePassword);
        sb.reverse();
        return sb.substring(0, sb.toString().indexOf(token));
    }

    private static String getMysqlRole(String roleString) {
        return roleString.contains(MYSQL_MASTER) ? MYSQL_MASTER : MYSQL_SLAVE;
    }
}
