<?xml version="1.0" encoding="UTF-8"?>
<!-- If deploying this as an exploded war (a directory) please make sure to specify a docBase attribute to the context, pointing
to the root directory of the application. -->
<Context docBase="$host.getDocBase()" path="$host.getPath()">
#foreach($resource in $host.getResources())
	<Resource name="$resource.getName()"
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
<Context>