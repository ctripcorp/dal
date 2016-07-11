<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://java.sun.com/xml/ns/javaee"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
	id="WebApp_ID" version="3.0">
	<context-param>
		<param-name>com.ctrip.platform.dal.dao.DalWarmUp</param-name>
		<param-value>true</param-value>
	</context-param>
	<display-name>DalTest</display-name>
	<servlet>
		<description></description>
		<display-name>DalDaoTest</display-name>
		<servlet-name>DalDaoTest</servlet-name>
		<!--<servlet-class>com.dal.sqlserver.test.PeoplePortal</servlet-class>-->
		<servlet-class>com.dal.sqlserver.test.XunitPeoplePortal</servlet-class>
	</servlet>
	<servlet-mapping>
	  <servlet-name>DalDaoTest</servlet-name>
	  <url-pattern>/PeoplePortal/*</url-pattern>
	</servlet-mapping>
	<session-config>
		<session-timeout>40</session-timeout>
	</session-config>
	<welcome-file-list>
		<welcome-file>index.html</welcome-file>
	</welcome-file-list>
	<listener>
		<listener-class>com.ctrip.platform.dal.dao.helper.DalClientFactoryListener</listener-class>
	</listener>
	<!-- <listener>
		<listener-class>com.ctrip.framework.vi.VIServletContextListener</listener-class>
	</listener> -->
</web-app>