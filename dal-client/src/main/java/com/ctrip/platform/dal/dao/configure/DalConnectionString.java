package com.ctrip.platform.dal.dao.configure;

/**
 * Created by lilj on 2018/9/25.
 */
public interface DalConnectionString {
     String getName();

     String getIPConnectionString();

     String getDomainConnectionString();

     ConnectionStringConfigure getIPConnectionStringConfigure();

     ConnectionStringConfigure getDomainConnectionStringConfigure();

     boolean equals(Object o);
}
