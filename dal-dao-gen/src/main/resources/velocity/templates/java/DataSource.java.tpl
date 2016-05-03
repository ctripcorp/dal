<Datasources>
#foreach($resource in $host.getResources())	
	<Datasource name="${resource.getName()}"
              userName="${resource.getUserName()}"
              password="${resource.getPassword()}"
              connectionUrl="${resource.getConnectionUrl()}"
              driverClassName="${resource.getDriverClassName()}"
              testWhileIdle="false"
              testOnBorrow="false"
              testOnReturn="false"
              validationQuery="SELECT 1"
              validationInterval="30000"
              timeBetweenEvictionRunsMillis="30000"
              maxActive="100"
              minIdle="1"
              maxWait="10000"
              initialSize="1"
              removeAbandonedTimeout="60"
              removeAbandoned="true"
              logAbandoned="true"
#if (${resource.isOptionAppend()})
              minEvictableIdleTimeMillis="30000"
              option="sendStringParametersAsUnicode=false"/>
#else
              minEvictableIdleTimeMillis="30000"/>
#end
#end			  
</Datasources>
