<Datasources>
#foreach($resource in $host.getResources())	
	<Datasource name="${resource.getName()}"
              testWhileIdle="false"
              testOnBorrow="false"
              testOnReturn="false"
              validationQuery="SELECT 1"
              validationInterval="30000"
              timeBetweenEvictionRunsMillis="5000"
              maxActive="100"
              minIdle="0"
              maxWait="10000"
              maxAge="30000"
              initialSize="1"
              removeAbandonedTimeout="60"
              removeAbandoned="true"
              logAbandoned="true"
#if (${resource.isOptionAppend()})
              minEvictableIdleTimeMillis="30000"
              connectionProperties="sendTimeAsDateTime=false;sendStringParametersAsUnicode=false"/>
#else
              minEvictableIdleTimeMillis="30000"
              connectionProperties="rewriteBatchedStatements=true;allowMultiQueries=true"/>
#end
#end			  
</Datasources>
