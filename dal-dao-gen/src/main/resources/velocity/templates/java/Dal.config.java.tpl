<dal name="$host.getName()">
	<databaseSets>
#foreach($databaseSet in $host.getDatabaseSet())
#if($databaseSet.hasShardingStrategy())
		<databaseSet name="$databaseSet.getName()" provider="$databaseSet.getProvider()"
             shardingStrategy="$databaseSet.getShardingStrategy()">
#else
		<databaseSet name="$databaseSet.getName()" provider="$databaseSet.getProvider()">
#end
#foreach($entry in $host.getDatabaseSetEntry($databaseSet.getId()))
            <add name="$entry.getName()" databaseType="$entry.getDatabaseType()" sharding="$entry.getSharding()" connectionString="${entry.getConnectionString()}"/>   
#end
		</databaseSet>
#end
	</databaseSets>
	<LogListener>
		<logger>com.ctrip.platform.dal.sql.logging.CtripDalLogger</logger>
	</LogListener>
	<ConnectionLocator>
		<settings>
			<dc>{$DBDataCenter}</dc>
			<connectionStringParser>com.ctrip.datasource.configure.CtripConnectionStringParser</connectionStringParser>
		</settings>
	</ConnectionLocator>
	<TaskFactory>
		<factory>com.ctrip.platform.dal.dao.CtripTaskFactory</factory>
	</TaskFactory>
</dal>