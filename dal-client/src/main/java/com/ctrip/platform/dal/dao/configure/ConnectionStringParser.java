package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionStringParser {
    private static ConnectionStringParser parser = null;

    public synchronized static ConnectionStringParser getInstance() {
        if (parser == null) {
            parser = new ConnectionStringParser();
        }
        return parser;
    }

    private static final Pattern dburlPattern =
            Pattern.compile("(data\\ssource|server|address|addr|network)=([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern dbuserPattern = Pattern.compile("(uid|user\\sid)=([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern dbpasswdPattern = Pattern.compile("(password|pwd)=([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern dbnamePattern =
            Pattern.compile("(database|initial\\scatalog)=([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern dbcharsetPattern = Pattern.compile("(charset)=([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern dbportPattern = Pattern.compile("(port)=([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern versionPattern = Pattern.compile("(version)=([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern connectTimeoutPattern = Pattern.compile("(connectTimeout)=([^;]+)", Pattern.CASE_INSENSITIVE);

    private static final String PORT_SPLIT = ",";
    public static final String MYSQL_URL_PREFIX="jdbc:mysql://";
    public static final String SQLSERVER_URL_PREFIX="jdbc:sqlserver://";
    public static final String REPLICATION_MYSQL_URL_PREFIX = "jdbc:mysql:replication://";
    public static final String DBURL_SQLSERVER = SQLSERVER_URL_PREFIX+"%s:%s;DatabaseName=%s";
    public static final String DBURL_MYSQL = MYSQL_URL_PREFIX+"%s:%s/%s?useUnicode=true&characterEncoding=%s";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_PORT = "3306";
    public static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
    public static final String DRIVER_SQLSERVRE = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final int DEFAULT_CONNECT_TIMEOUT = 1200;


    private static final Pattern hostPortPatternInMySQLURL = Pattern.compile("(jdbc:mysql://)([[^\\f\\n\\r\\t\\v=/]:]+):([^/]+)");
    private static final Pattern complexHostPatternInMySQLURL = Pattern.compile("(\\(host|,host)=([^\\),]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern complexPortPatternInMySQLURL = Pattern.compile("(\\(port|,port)=([^\\),]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern hostPortPatternInSQLServerURL = Pattern.compile("(jdbc:sqlserver://)([\\S:]+):([^;]+)");

    private static final Pattern urlReplacePatternInMySQLURL = Pattern.compile("jdbc:mysql://([^/]+)");
    private static final Pattern urlReplacePatternInMySQLMgrURL = Pattern.compile("jdbc:mysql:replication://([^/]+)");
    private static final Pattern urlReplacePatternInSQLServerURL = Pattern.compile("jdbc:sqlserver://([^;]+)");

    /**
     * parse "Data Source=127.0.0.1,28747;UID=sa;password=sa;database=test;"
     *
     * @return DataSourceConfigure
     */
    public DalConnectionStringConfigure parse(String name, String connectionString) {
        DataSourceConfigure config = new DataSourceConfigure();
        if (connectionString == null)
            return config;

        String version = null;
        Matcher matcher = versionPattern.matcher(connectionString);
        if (matcher.find()) {
            version = matcher.group(2);
        }

        String dbname = null;
        matcher = dbnamePattern.matcher(connectionString);
        if (matcher.find()) {
            dbname = matcher.group(2);
        }

        String dbhost = null;
        String url = null;
        String charset = null;
        String driverClass = null;
        String port = null;
        matcher = dburlPattern.matcher(connectionString);
        boolean isSqlServer;
        if (matcher.find()) {
            String[] dburls = matcher.group(2).split(PORT_SPLIT);
            dbhost = dburls[0];
            if (dburls.length == 2) {// is sqlserver
                isSqlServer = true;
                port = dburls[1];
                url = String.format(DBURL_SQLSERVER, dbhost, port, dbname);
            } else {// should be mysql
                isSqlServer = false;
                matcher = dbcharsetPattern.matcher(connectionString);
                if (matcher.find()) {
                    charset = matcher.group(2);
                } else {
                    charset = DEFAULT_ENCODING;
                }
                matcher = dbportPattern.matcher(connectionString);
                if (matcher.find()) {
                    port = matcher.group(2);
                } else {
                    port = DEFAULT_PORT;
                }
                url = String.format(DBURL_MYSQL, dbhost, port, dbname, charset);
            }

            driverClass = isSqlServer ? DRIVER_SQLSERVRE : DRIVER_MYSQL;
        } else {
            throw new RuntimeException("The format of connection string is incorrect for " + name);
        }

        String userName = null;
        matcher = dbuserPattern.matcher(connectionString);
        if (matcher.find()) {
            userName = matcher.group(2);
        }

        String password = null;
        matcher = dbpasswdPattern.matcher(connectionString);
        if (matcher.find()) {
            password = matcher.group(2);
        }

        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        config.setName(keyName);
        config.setConnectionUrl(url);
        config.setUserName(userName != null ? userName : "");
        config.setPassword(password != null ? password : "");
        config.setDriverClass(driverClass);
        config.setVersion(version);
        config.setHostName(dbhost);

        return config;
    }

    public static String replaceHostAndPort(String url, String newHost, String newPort) {
        Matcher matcher = null;
        if (url.toLowerCase().startsWith(MYSQL_URL_PREFIX)) {
            matcher = urlReplacePatternInMySQLURL.matcher(url);
            if (matcher.find())
                url = matcher.replaceFirst(String.format("jdbc:mysql://%s:%s", newHost, newPort));
        }

        else if (url.toLowerCase().startsWith(REPLICATION_MYSQL_URL_PREFIX)) {
            matcher = urlReplacePatternInMySQLMgrURL.matcher(url);
            if (matcher.find()) {
                url = matcher.replaceFirst(String.format("jdbc:mysql://%s:%s", newHost, newPort));
            }
        }

        else if (url.toLowerCase().startsWith(SQLSERVER_URL_PREFIX)) {
            matcher = urlReplacePatternInSQLServerURL.matcher(url);
            if (matcher.find())
                url = matcher.replaceFirst(String.format("jdbc:sqlserver://%s:%s", newHost, newPort));
        }
        return url;
    }

    public static HostAndPort parseHostPortFromURL(String url) {
        if (StringUtils.isEmpty(url)) {
            return new HostAndPort();
        }

        if (url.toLowerCase().startsWith(MYSQL_URL_PREFIX)) {
            String host = null;
            Integer port = null;

            // jdbc:mysql://host:port/db
            Matcher matcher = hostPortPatternInMySQLURL.matcher(url);
            if (matcher.find()) {
                host = matcher.group(2);
                port = parseInt(matcher.group(3));
            }

            if (host == null && port == null) {
                // jdbc:mysql://address=(host=host)(port=port)/db
                // jdbc:mysql://(host=host,port=port)/db
                matcher = complexHostPatternInMySQLURL.matcher(url);
                if (matcher.find())
                    host = matcher.group(2);
                matcher = complexPortPatternInMySQLURL.matcher(url);
                if (matcher.find())
                    port = parseInt(matcher.group(2));
            }
            return new HostAndPort(url, host, port);
        }

        if (url.toLowerCase().startsWith(SQLSERVER_URL_PREFIX)) {
            Matcher matcher = hostPortPatternInSQLServerURL.matcher(url);
            if (matcher.find())
                return new HostAndPort(url, matcher.group(2), parseInt(matcher.group(3)));
        }

        return new HostAndPort(url);
    }

    public static long parseConnectTimeout(String connectionProperties) {
        long connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        if (StringUtils.isEmpty(connectionProperties)) {
            return connectTimeout;
        }

        Matcher matcher = connectTimeoutPattern.matcher(connectionProperties);
        if (matcher.find()) {
            String connectTimeoutString = matcher.group(2);
            connectTimeout = Long.parseLong(connectTimeoutString);
        }

        return connectTimeout;
    }

    private static Integer parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Throwable t) {
            return null;
        }
    }
}
