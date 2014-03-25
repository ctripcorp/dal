package com.ctrip.platform.dal.datasource;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class LocalDataSourceFactory implements ObjectFactory
{
	  private static final Log log = LogFactory.getLog(LocalDataSourceFactory.class);
	  private final Map<String, String[]> props = AllInOneConfigParser.newInstance().getDBAllInOneConfig();
	  private final Context ic = null;
	  
	  public static void main(String[] args)
	  {
	    try
	    {
	      Name name = new CompositeName("UserDB_Select");
	      Object o = new LocalDataSourceFactory().getObjectInstance(null, name, new InitialContext(), null);
	      System.out.println(o);
	      o = new LocalDataSourceFactory().getObjectInstance(null, name, new InitialContext(), null);
	      System.out.println(o);
	    }
	    catch (InvalidNameException e)
	    {
	      e.printStackTrace();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  }
	  
	  public LocalDataSourceFactory()
	  {
	    initJndiContext();
	  }
	  
	  public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
	    throws Exception
	  {
	    Object ds = null;
	    try
	    {
	      ds = nameCtx.lookup("java:/comp/env/jdbc/" + name.get(0));
	    }
	    catch (NameNotFoundException e)
	    {
	      ds = createDataSource(name.get(0));
	      this.ic.bind("java:/comp/env/jdbc/" + name.get(0), ds);
	    }
	    return ds;
	  }
	  
	  public void initJndiContext()
	  {
	    try
	    {
	      System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
	      System.setProperty("java.naming.factory.url.pkgs", "org.apache.naming");
	      
	      Context ic = new InitialContext();
	      ic.createSubcontext("java:");
	      ic.createSubcontext("java:/comp");
	      ic.createSubcontext("java:/comp/env");
	      ic.createSubcontext("java:/comp/env/jdbc");
	    }
	    catch (NamingException e)
	    {
	      e.printStackTrace();
	    }
	  }
	  
	  private javax.sql.DataSource createDataSource(String name)
	  {
	    PoolProperties p = new PoolProperties();
	    
	    String[] prop = (String[])this.props.get(name);
	    p.setUrl(prop[0]);
	    p.setUsername(prop[1]);
	    p.setPassword(prop[2]);
	    p.setDriverClassName(prop[3]);
	    p.setJmxEnabled(true);
	    p.setTestWhileIdle(false);
	    p.setTestOnBorrow(true);
	    p.setValidationQuery("SELECT 1");
	    p.setTestOnReturn(false);
	    p.setValidationInterval(30000L);
	    p.setTimeBetweenEvictionRunsMillis(30000);
	    p.setMaxActive(100);
	    p.setInitialSize(10);
	    p.setMaxWait(10000);
	    p.setRemoveAbandonedTimeout(60);
	    p.setMinEvictableIdleTimeMillis(30000);
	    p.setMinIdle(10);
	    p.setLogAbandoned(true);
	    p.setRemoveAbandoned(true);
	    p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
	    

	    org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(p);
	    try
	    {
	      ds.createPool();
	      return ds;
	    }
	    catch (SQLException e)
	    {
	      log.error("Creating DataSource error:" + e.getMessage(), e);
	    }
	    return null;
	  }
	}

