<dal name="com.dal.test">
	<databaseSets>
		<databaseSet name="SqlServerSimpleShard" provider="sqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CityID,tableIndex;mod=2;">
            <add name="daltestS1db" databaseType="Master" sharding="1" connectionString="daltestS1db"/>   
            <add name="daltestS0db" databaseType="Master" sharding="0" connectionString="daltestS0db"/>   
		</databaseSet>
		<databaseSet name="MultiThreadingTest" provider="sqlProvider">
			<add name="MultiThreadingTest" databaseType="Master" sharding="" connectionString="MultiThreadingTest"/>   
		</databaseSet>
	</databaseSets>
	<LogListener enabled="true">
		<logger>com.ctrip.platform.dal.sql.logging.CtripDalLogger</logger>
        <settings>
			<encrypt>false</encrypt>
			<simplified>true</simplified>
        </settings>
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
