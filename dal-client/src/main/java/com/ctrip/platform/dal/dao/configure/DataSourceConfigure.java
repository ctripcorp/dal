package com.ctrip.platform.dal.dao.configure;

import java.util.Map;
import java.util.Properties;

public class DataSourceConfigure implements DatabasePoolConfigConstants {
    private String name;
	private Properties properties = new Properties();

    public DataSourceConfigure(String name){
        this.name = name;
    }
    
    public DataSourceConfigure(String name, Properties properties) {
        this(name);
        this.properties = properties;
    }
    
    public DataSourceConfigure(String name, Map<String, String> propertyMap) {
        this(name);
        merge(propertyMap);
    }
    
    public void merge(Properties properties) {
        for(Object keyObj: properties.keySet()) {
            String key = (String)keyObj;
            setProperty(key, properties.getProperty(key));
        }
    }
    
    public void merge(Map<String, String> propertyMap) {
        for(Map.Entry<String, String> entry: propertyMap.entrySet())
            properties.setProperty(entry.getKey(), entry.getValue());
    }
    
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
		return getProperty(USER_NAME);
	}
	
	public void setUserName(String userName) {
	    setProperty(USER_NAME, userName);
	}
	
	public String getPassword() {
	    return getProperty(PASSWORD);
	}
	
	public void setPassword(String password) {
	    setProperty(PASSWORD, password);
	}
	
	public String getConnectionUrl() {
	    return getProperty(CONNECTION_URL);
	}
	
	public void setConnectionUrl(String connectionUrl) {
	    setProperty(CONNECTION_URL, connectionUrl);
	}
	
	public String getDriverClass() {
	    return getProperty(DRIVER_CLASS_NAME);
	}
	
	public void setDriverClass(String driverClass) {
		setProperty(DRIVER_CLASS_NAME, driverClass);
	}
	
    public Properties getProperties() {
        return properties;
    }
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        return properties.containsKey(key) ? Integer.parseInt(getProperty(key)) : defaultValue;
    }
    
    public long getLongProperty(String key, long defaultValue) {
        return properties.containsKey(key) ? Long.parseLong(getProperty(key)) : defaultValue;
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return properties.containsKey(key) ? Boolean.parseBoolean(getProperty(key)) : defaultValue;
    }
}
