<?xml version="1.0" encoding="UTF-8"?>

<connectionStrings Version="dev">
#foreach($entry in $host.getDatabaseSetEntryMap().values())
	<add name="${entry.getConnectionString()}" connectionString="${entry.getAllInOneConnectionString()}" providerName="${entry.getProviderName()}"/>
#end
</connectionStrings>