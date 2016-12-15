<dal name="com.dal.test">
	<databaseSets>
		<databaseSet name="SqlServerSimpleShard" provider="sqlProvider"
             shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CityID,tableIndex;mod=2;">
            <add name="daltestS1db" databaseType="Master" sharding="1" connectionString="SimpleShard_1"/>   
            <add name="daltestS0db" databaseType="Master" sharding="0" connectionString="SimpleShard_0"/>   
		</databaseSet>
		<databaseSet name="MultiThreadingTest" provider="sqlProvider">
			<add name="MultiThreadingTest" databaseType="Master" sharding="" connectionString="MultiThreadingTest"/>   
		</databaseSet>
	    <databaseSet name="MySqlSimpleShard" provider="mySqlProvider"
        	shardingStrategy="class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CountryID;mod=2;tableColumns=CityID;tableMod=4;separator=_;shardedTables=person">
	    	<add name="dal_shard_0" databaseType="Master" sharding="0" connectionString="dal_shard_0"/>   
	    	<add name="dal_shard_1" databaseType="Master" sharding="1" connectionString="dal_shard_1"/>   
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