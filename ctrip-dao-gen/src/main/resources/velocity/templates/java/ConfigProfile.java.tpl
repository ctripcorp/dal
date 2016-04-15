<?xml version="1.0" encoding="utf-8" ?>
<profile>
	<environments>
		<!--所有属性及XML节点名称均需要区分大小写 -->
		<!--name属性的命名可以随意进行，只要符合Windows的文件夹名规范即可，下文中的类似prd_iis7_net4的节点名称就是依据此属性而得来的，很多 
			同事会以target来命名，这是不太理想的 -->
		<!--target属性可以是dev,test等属性，是用来区分当前环境的 -->
		<!--iis属性可以是IIS6,IIS7或者ALL -->
		<!--net属性可以是NET2，NET4或者ALL -->
		<!--dataCenter属性，可以是如下值: sh,nt,sh2或者ALL，也可以是sh,nt,sh2中任意多个值的并集，以逗号分隔 -->
		<!--以上5个属性共同组成了一个元数据，可以唯一的标识一个环境 -->
		<add name="dev" target="dev" iis="ALL" net="ALL" dataCenter="sh" />
		<add name="fws" target="fws" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="fat" target="fat" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="lpt" target="lpt" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="uat_nt" target="uat_nt" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="prd" target="prd" iis="ALL" net="ALL" dataCenter="sh" />
	</environments>
	<!--environments节点有多少个子节点，则下文中必须出现同样数量的节点，且名称必须与上文中的name属性一致 -->
	<dev>
		<LoggingServer.V2.IP>collector.logging.fws.qa.nt.ctripcorp.com</LoggingServer.V2.IP>
		<LoggingServer.V2.Port>63100</LoggingServer.V2.Port>
		<appInternals.appInfoUrl>http://ws.fx.fws.qa.nt.ctripcorp.com/appinfo/appinfo/Send</appInternals.appInfoUrl>
		<appInternals.permissions.write>10.32.21.21</appInternals.permissions.write>
		<appInternals.permissions.read>10.32.21.21</appInternals.permissions.read>
		<CFX_DataSource_ServiceUrl></CFX_DataSource_ServiceUrl>
	</dev>
	<fat>
		<LoggingServer.V2.IP>collector.logging.fws.qa.nt.ctripcorp.com</LoggingServer.V2.IP>
		<LoggingServer.V2.Port>63100</LoggingServer.V2.Port>
		<appInternals.appInfoUrl>http://ws.fx.fws.qa.nt.ctripcorp.com/appinfo/appinfo/Send</appInternals.appInfoUrl>
		<appInternals.permissions.write>10.32.21.21</appInternals.permissions.write>
		<appInternals.permissions.read>10.32.21.21</appInternals.permissions.read>
		<CFX_DataSource_ServiceUrl>https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query</CFX_DataSource_ServiceUrl>
	</fat>
	<lpt>
		<LoggingServer.V2.IP>collector.logging.lpt.qa.nt.ctripcorp.com</LoggingServer.V2.IP>
		<LoggingServer.V2.Port>63100</LoggingServer.V2.Port>
		<appInternals.appInfoUrl>http://ws.fx.fws.qa.nt.ctripcorp.com/appinfo/appinfo/Send</appInternals.appInfoUrl>
		<appInternals.permissions.write>10.2.4.42</appInternals.permissions.write>
		<CFX_DataSource_ServiceUrl>https://ws.titan.lpt.qa.nt.ctripcorp.com/titanservice/query</CFX_DataSource_ServiceUrl>
	</lpt>
	<fws>
		<LoggingServer.V2.IP>collector.logging.fws.qa.nt.ctripcorp.com</LoggingServer.V2.IP>
		<LoggingServer.V2.Port>63100</LoggingServer.V2.Port>
		<appInternals.appInfoUrl>http://ws.fx.fws.qa.nt.ctripcorp.com/appinfo/appinfo/Send</appInternals.appInfoUrl>
		<appInternals.permissions.read>10.32.21.21</appInternals.permissions.read>
		<appInternals.permissions.write>10.32.21.21</appInternals.permissions.write>
		<CFX_DataSource_ServiceUrl>https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query</CFX_DataSource_ServiceUrl>
	</fws>
	<uat_nt>
		<LoggingServer.V2.IP>collector.logging.uat.qa.nt.ctripcorp.com</LoggingServer.V2.IP>
		<LoggingServer.V2.Port>63100</LoggingServer.V2.Port>
		<appInternals.permissions.write>10.2.24.129</appInternals.permissions.write>
		<CFX_DataSource_ServiceUrl>https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query</CFX_DataSource_ServiceUrl>
	</uat_nt>
	<prd>
		<LoggingServer.V2.IP>collector.logging.sh.ctriptravel.com</LoggingServer.V2.IP>
		<LoggingServer.V2.Port>63100</LoggingServer.V2.Port>
		<appInternals.permissions.write>192.168.79.237,192.168.79.236,192.168.79.235,192.168.79.234</appInternals.permissions.write>
		<CFX_DataSource_ServiceUrl>https://ws.titan.ctripcorp.com/titanservice/query</CFX_DataSource_ServiceUrl>
	</prd>
</profile>