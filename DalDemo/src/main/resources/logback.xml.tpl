<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<pattern>[%5p][%d{MM-dd HH:mm:ss.SSS}][%t][%c:%L]-%m%n</pattern>
		</encoder>
	</appender>
	<appender name="CentralLogging"
		class="com.ctrip.framework.clogging.agent.appender.CLoggingAppender">
		<appId>930201</appId>
		<serverIp>{$LoggingServer.V2.IP}</serverIp>
		<serverPort>{$LoggingServer.V2.Port}</serverPort>
	</appender>
	<root level="INFO">
		<appender-ref ref="CentralLogging" />
		<appender-ref ref="stdout" />
	</root>
</configuration>