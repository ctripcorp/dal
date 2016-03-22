<dal name="llj_shardbydb_java_mysql">
	<databaseSets>
		<databaseSet name="ShardColModShardByDBOnMysql" provider="mySqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=Age;mod=2;">
            <add name="CrossShardByModByMySql_1" databaseType="Master" sharding="1" connectionString="CrossShardByModByMySql_1"/>   
            <add name="CrossShardByModByMySql_0" databaseType="Master" sharding="0" connectionString="CrossShardByModByMySql_0"/>   
		</databaseSet>
	</databaseSets>
	<databaseSets>
		<databaseSet name="ShardColModShardByDBOnSqlserver" provider="sqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CityID;mod=2;">
            <add name="SimpleShardBySqlServer_0" databaseType="Master" sharding="0" connectionString="SimpleShardBySqlServer_0"/>   
            <add name="SimpleShardBySqlServer_1" databaseType="Master" sharding="1" connectionString="SimpleShardBySqlServer_1"/>   
		</databaseSet>
	</databaseSets>
	<databaseSets>
		<databaseSet name="ShardColModShardByDBTableOnMysql" provider="mySqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CityID;mod=2;tableColumns=Age;tableMod=2;separator=_;shardedTables=people">
            <add name="CrossShardByModByMySql_1" databaseType="Master" sharding="1" connectionString="CrossShardByModByMySql_1"/>   
            <add name="CrossShardByModByMySql_0" databaseType="Master" sharding="0" connectionString="CrossShardByModByMySql_0"/>   
		</databaseSet>
	</databaseSets>
	<databaseSets>
		<databaseSet name="ShardColModShardByTableOnMysql" provider="mySqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;tableColumns=Age;tableMod=2;separator=_;shardedTables=person">
            <add name="SimpleShardByTableByMySql_llj" databaseType="Master" sharding="" connectionString="SimpleShardByTableByMySql_llj"/>   
		</databaseSet>
	</databaseSets>
	<databaseSets>
		<databaseSet name="SimpleShardByDBOnMysql" provider="mySqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.SimpleShardHintStrategy;shardByDb=true">
            <add name="CrossShardByModByMySql_1" databaseType="Master" sharding="1" connectionString="CrossShardByModByMySql_1"/>   
            <add name="CrossShardByModByMySql_0" databaseType="Master" sharding="0" connectionString="CrossShardByModByMySql_0"/>   
		</databaseSet>
	</databaseSets>
	<databaseSets>
		<databaseSet name="SimpleShardByDBOnSqlserver" provider="sqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CityID;mod=2;">
            <add name="SimpleShardBySqlServer_0" databaseType="Master" sharding="0" connectionString="SimpleShardBySqlServer_0"/>   
            <add name="SimpleShardBySqlServer_1" databaseType="Master" sharding="1" connectionString="SimpleShardBySqlServer_1"/>   
		</databaseSet>
	</databaseSets>
	<databaseSets>
		<databaseSet name="SimpleShardByDBTableOnMysql" provider="mySqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.SimpleShardHintStrategy;shardByTable=true;shardByDb=true;shardedTables=people;separator=_">
            <add name="CrossShardByModByMySql_1" databaseType="Master" sharding="1" connectionString="CrossShardByModByMySql_1"/>   
            <add name="CrossShardByModByMySql_0" databaseType="Master" sharding="0" connectionString="CrossShardByModByMySql_0"/>   
		</databaseSet>
	</databaseSets>
	<databaseSets>
		<databaseSet name="SimpleShardByTableOnMySql" provider="mySqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.SimpleShardHintStrategy;shardByTable=true;shardedTables=person;separator=_">
            <add name="SimpleShardByTableByMySql_llj" databaseType="Master" sharding="" connectionString="SimpleShardByTableByMySql_llj"/>   
		</databaseSet>
		<databaseSet name="testHintsOfCodeGenByMysql" provider="mySqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=Age;mod=2;separator=_">
            <add name="CrossShardByModByMySql_1" databaseType="Master" sharding="1" connectionString="CrossShardByModByMySql_1"/>   
            <add name="CrossShardByModByMySql_0" databaseType="Master" sharding="0" connectionString="CrossShardByModByMySql_0"/>   
		</databaseSet>
		<databaseSet name="testHintsOfCodeGenBySqlserver" provider="sqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CityID;mod=2;separator=_">
            <add name="SimpleShardBySqlServer_0" databaseType="Master" sharding="0" connectionString="SimpleShardBySqlServer_0"/>   
            <add name="SimpleShardBySqlServer_1" databaseType="Master" sharding="1" connectionString="SimpleShardBySqlServer_1"/>   
		</databaseSet>
		<databaseSet name="AllTypesTest" provider="mySqlProvider">
            <add name="AllTypesTest_llj_0" databaseType="Master" sharding="" connectionString="AllTypesTest_llj_0"/>   
		</databaseSet>
		<databaseSet name="test_parser_Mysql" provider="mySqlProvider">
            <add name="CrossShardByModByMySql_0" databaseType="Master" sharding="" connectionString="CrossShardByModByMySql_0"/>   
		</databaseSet>
		<databaseSet name="test_parser_Sqlserver" provider="sqlProvider">
            <add name="SimpleShardBySqlServer_0" databaseType="Master" sharding="" connectionString="SimpleShardBySqlServer_0"/>   
		</databaseSet>
	</databaseSets>
	<LogListener>
		<logger>com.ctrip.platform.dal.sql.logging.CtripDalLogger</logger>
	</LogListener>
	<ConnectionLocator>
		<settings>
			<serviceAddress>{$CFX_DataSource_ServiceUrl}</serviceAddress>
			<dataSourceConfigureProvider>com.ctrip.datasource.titan.TitanProvider</dataSourceConfigureProvider>
		</settings>
	</ConnectionLocator>
	<TaskFactory>
		<factory>com.ctrip.platform.dal.dao.CtripTaskFactory</factory>
	</TaskFactory>
</dal>