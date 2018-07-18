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
	
	<databaseProviders>
        <add name="sqlProvider" type="Arch.Data.DbEngine.Providers.SqlDatabaseProvider,Arch.Data"/>
        <!--如果只用Sql Server,请注释掉下面这行，否则可能有运行时错误-->
        <add name="mySqlProvider" type="Arch.Data.MySqlProvider.MySqlDatabaseProvider,Arch.Data.MySqlProvider"/>
	</databaseProviders>
</dal>