package com.ctrip.platform.dal.dao.configure;


import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
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

    private static final String PORT_SPLIT = ",";
    public static final String DBURL_SQLSERVER = "jdbc:sqlserver://%s:%s;DatabaseName=%s";
    public static final String DBURL_MYSQL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_PORT = "3306";
    public static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
    public static final String DRIVER_SQLSERVRE = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final Pattern ipPortPatternInMySQLURL=Pattern.compile("");
    private static final Pattern ipPortPatternInSQLServerURL=Pattern.compile("");
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
        String port=null;
        matcher = dburlPattern.matcher(connectionString);
        boolean isSqlServer;
        if (matcher.find()) {
            String[] dburls = matcher.group(2).split(PORT_SPLIT);
            dbhost = dburls[0];
            if (dburls.length == 2) {// is sqlserver
                isSqlServer = true;
                port=dburls[1];
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
                    port=matcher.group(2);
//                    url = String.format(DBURL_MYSQL, dbhost, matcher.group(2), dbname, charset);
                } else {
                    port=DEFAULT_PORT;
//                    url = String.format(DBURL_MYSQL, dbhost, DEFAULT_PORT, dbname, charset);
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
        config.setUserName(userName);
        config.setPassword(password);
        config.setDriverClass(driverClass);
        config.setVersion(version);
        config.setHostName(dbhost);
        config.setPort(Integer.parseInt(port));

        return config;
    }


    public static String parseHostNameFromURL(String url){
       return null;
    }

    public static String parsePortFromURL(String url){
        return null;
    }

}
