<Datasources>
#foreach($resource in $host.getResources())	
	<Datasource name="${resource.getName()}"
              testWhileIdle="true"
              testOnBorrow="false"
              testOnReturn="false"
              validationQuery="SELECT 1"
              validationInterval="30000"
              timeBetweenEvictionRunsMillis="5000"
              maxActive="100"
              minIdle="10"
              maxWait="30000"
              initialSize="10"
              removeAbandonedTimeout="60"
              removeAbandoned="true"
              logAbandoned="true"
              minEvictableIdleTimeMillis="30000"/>
#end			  
</Datasources>
