package com.ctrip.platform.dal.dao.configure;

/**
 * Created by lilj on 2018/9/25.
 */
public interface DalConnectionString {
     String getName();

     String getIPConnectionString();

     String getDomainConnectionString();

     DalConnectionStringConfigure getIPConnectionStringConfigure();

     DalConnectionStringConfigure getDomainConnectionStringConfigure();

     boolean equals(Object o);

     DalConnectionString clone();
}
