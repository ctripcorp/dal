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
		<add name="test" target="test" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="fws" target="fws" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="fat" target="fat" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="lpt" target="lpt" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="uat" target="uat" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="uat_nt" target="uat_nt" iis="ALL" net="ALL" dataCenter="ALL" />
		<add name="prd" target="prd" iis="ALL" net="ALL" dataCenter="sh" />
	</environments>
	<!--environments节点有多少个子节点，则下文中必须出现同样数量的节点，且名称必须与上文中的name属性一致 -->
	<fat>
		<DBDataCenter></DBDataCenter>
	</fat>
	<lpt>
		<DBDataCenter></DBDataCenter>
	</lpt>
	<dev>
		<DBDataCenter></DBDataCenter>
	</dev>
	<test>
		<DBDataCenter></DBDataCenter>
	</test>
	<fws>
		<DBDataCenter></DBDataCenter>
	</fws>
	<uat>
		<DBDataCenter></DBDataCenter>
	</uat>
	<uat_nt>
		<DBDataCenter>_NT</DBDataCenter>
	</uat_nt>
	<prd>
		<DBDataCenter>_SH</DBDataCenter>
	</prd>
</profile>