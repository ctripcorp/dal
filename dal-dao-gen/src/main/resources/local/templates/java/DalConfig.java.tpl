<dal name="$host.getName()">
	<databaseSets>
#foreach($databaseSet in $host.getDatabaseSet())
		<databaseSet name="$databaseSet.getName()" provider="$databaseSet.getProvider()"
             shardStrategy="$databaseSet.getShardingStrategy()">
#foreach($entry in $host.getDatabaseSetEntry($databaseSet.getId()))
            <add name="$entry.getName()" databaseType="$entry.getDatabaseType()" sharding="$entry.getSharding()" connectionString="$entry.getConnectionString()"/>   
#end
		</databaseSet>
#end
	</databaseSets>
</dal>