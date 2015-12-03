<?xml version="1.0" encoding="UTF-8"?>

<connectionStrings>
#foreach($entry in $host.getDatabaseSetEntryMap())
	<add name="${entry.getConnectionString()}" connectionString="${entry.getAllInOneConnectionString()}" />
#end
</connectionStrings>