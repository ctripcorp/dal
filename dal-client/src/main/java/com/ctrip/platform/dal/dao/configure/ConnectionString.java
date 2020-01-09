package com.ctrip.platform.dal.dao.configure;


import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;

public class ConnectionString implements DalConnectionString{
    private ConnectionStringParser parser;

    private String name;
    private String ipConnectionString;
    private String domainConnectionString;

    public ConnectionString(String name, String ipConnectionString, String domainConnectionString) {
        this.parser = ConnectionStringParser.getInstance();
        this.name = name;
        this.ipConnectionString = ipConnectionString;
        this.domainConnectionString = domainConnectionString;
    }

    public String getName() {
        return name;
    }

    public String getIPConnectionString() {
        return ipConnectionString;
    }

    public String getDomainConnectionString() {
        return domainConnectionString;
    }

    public DalConnectionStringConfigure getIPConnectionStringConfigure() {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return parser.parse(keyName, ipConnectionString);
    }

    public DalConnectionStringConfigure getDomainConnectionStringConfigure() {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return parser.parse(keyName, domainConnectionString);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (!(o instanceof DalConnectionString))
            return false;
        DalConnectionString connectionString = (ConnectionString) o;
        return name.equals(connectionString.getName())
                && ipConnectionString.equals(connectionString.getIPConnectionString())
                && domainConnectionString.equals(connectionString.getDomainConnectionString());
    }

    public synchronized ConnectionString clone() {
        return new ConnectionString(name, ipConnectionString, domainConnectionString);
    }
}
