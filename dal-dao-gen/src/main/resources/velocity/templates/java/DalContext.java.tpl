<Context docBase="$host.getDocBase()" path="$host.getPath()" crossContext="true" reloadable="true">
#foreach($resource in $host.getResources())
	<Resource name="${resource.getName()}{$DBDataCenter}"
                   auth="$resource.getAuth()"
                   type="$resource.getType()"
                   factory="$resource.getFactory()"
                   testWhileIdle="$resource.isTestWhileIdle()"
                   testOnBorrow="$resource.isTestOnBorrow()"
                   testOnReturn="$resource.isTestOnReturn()"
                   validationQuery="$resource.getValidationQuery()"
                   validationInterval="$resource.getValidationInterval()"
                   timeBetweenEvictionRunsMillis="$resource.getTimeBetweenEvictionRunsMillis()"
                   maxActive="$resource.getMaxActive()"
                   minIdle="$resource.getMinIdle()"
                   maxWait="$resource.getMaxWait()"
                   initialSize="$resource.getInitialSize()"
                   removeAbandonedTimeout="$resource.getRemoveAbandonedTimeout()"
                   removeAbandoned="$resource.isRemoveAbandoned()"
                   logAbandoned="$resource.isLogAbandoned()"
                   minEvictableIdleTimeMillis="$resource.getMinEvictableIdleTimeMillis()"
                   jmxEnabled="$resource.isJmxEnabled()"
                   jdbcInterceptors="$resource.getJdbcInterceptors()"/>
#end
</Context>