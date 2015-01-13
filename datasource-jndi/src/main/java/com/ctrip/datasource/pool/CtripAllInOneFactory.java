package com.ctrip.datasource.pool;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.apache.catalina.deploy.ContextResource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.ResourceRef;
import org.apache.tomcat.util.modeler.Registry;

import com.ctrip.datasource.DataSourceConfigFactory;

public class CtripAllInOneFactory  implements ObjectFactory {
	
	private static final Log log = LogFactory.getLog(CtripAllInOneFactory.class);
	
	
	public Object getObjectInstance(Object obj,
		      Name name, Context nameCtx, Hashtable environment)
		      throws Exception {

		      // Acquire an instance of our specified bean class

		      // Customize the bean properties from our attributes
		      Reference ref = (Reference) obj;
		      Enumeration<RefAddr> addrs = ref.getAll();
		      while (addrs.hasMoreElements()) {
		          RefAddr addr = addrs.nextElement();
		          String typeName = addr.getType();
		          log.info("CtripAllInOneFactory typeName:"+typeName);
		          //String typeValue = (String) addr.getContent();
		      }
		      
		      CtripDataSourceFactory dataSourceFactory = new CtripDataSourceFactory();
		      
		      List<Map<String,String>> conns = new DataSourceConfigFactory().getDSConfig();
		      
				/*Context ic= new InitialContext();
				ic.createSubcontext("java:");
	            ic.createSubcontext("java:/comp");
	            ic.createSubcontext("java:/comp/env");
	            ic.createSubcontext("java:/comp/env/jdbc");*/
		      
		      String dbkey;
		     
		      
		      //NamingContextListener ncl = new NamingContextListener();
		      
		      for(Map<String,String> connKey : conns){
		    	  
		    	  dbkey = connKey.get("dbkey");
		    	  
			      ContextResource resource = new ContextResource();
			      
			      resource.setAuth("Container");
			      resource.setScope("Shareable");
			      resource.setDescription("");
			      resource.setName("jdbc/"+dbkey);
			      resource.setSingleton(true);
			      resource.setType("javax.sql.DataSource");
			      resource.setProperty("factory", "com.ctrip.datasource.pool.CtripDataSourceFactory");
			      
			      //ncl.addResource(resource);
			      
		    	  
			      Context ic = createSubcontexts(new InitialContext(),"java:/comp/env");
		    	  addResource(resource,ic);
		      
			      /*Reference refDs = new Reference("javax.sql.DataSource",CtripDataSourceFactory.class.getName(),null);
			      for (int i = 0; i < ALL_PROPERTIES.length; i++) {
			            String propertyName = ALL_PROPERTIES[i];
			            refDs.add(new StringRefAddr(propertyName,null));
			        }
			      dbkey = connKey.get("dbkey");
			      
			      log.info("===binding java:/comp/env/jdbc/"+dbkey);
			      
			      ic.bind("java:/comp/env/jdbc/"+dbkey, dataSourceFactory.getObjectInstance(refDs, new CompositeName(dbkey), nameCtx, environment));
			      
			      log.info("===binded java:/comp/env/jdbc/"+dbkey);*/
			  }

		      // Return the customized instance
		      return null;
	}
	
	private void addResource(ContextResource resource,Context envCtx) {

        // Create a reference to the resource.
        Reference ref = new ResourceRef
            (resource.getType(), resource.getDescription(),
             resource.getScope(), resource.getAuth(),
             resource.getSingleton());
        // Adding the additional parameters, if any
        Iterator<String> params = resource.listProperties();
        while (params.hasNext()) {
            String paramName = params.next();
            String paramValue = (String) resource.getProperty(paramName);
            StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
            ref.add(refAddr);
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("  Adding resource ref " 
                             + resource.getName() + "  " + ref);
            }
            createSubcontexts(envCtx, resource.getName());
            envCtx.bind(resource.getName(), ref);
        } catch (NamingException e) {
            log.error("naming.bindFailed", e);
        }

        if ("javax.sql.DataSource".equals(ref.getClassName()) &&
                resource.getSingleton()) {
            try {
		    	ObjectName on = new ObjectName("Catalina:type=DataSource,context=/site-demo,host=localhost,class=javax.sql.DataSource,name=\""+resource.getName()+"\"");
	            Object actualResource = envCtx.lookup(resource.getName());
	            Registry.getRegistry(null, null).registerComponent(actualResource, on, null);
            } catch (Exception e) {
                log.warn("naming.jmxRegistrationFailed", e);
            }
        }
        
    }
	
	/**
     * Create all intermediate subcontexts.
     */
    private Context createSubcontexts(Context ctx, String name)
        throws NamingException {
        javax.naming.Context currentContext = ctx;
        StringTokenizer tokenizer = new StringTokenizer(name, "/");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if ((!token.equals("")) && (tokenizer.hasMoreTokens())) {
                try {
                    currentContext = currentContext.createSubcontext(token);
                } catch (NamingException e) {
                    // Silent catch. Probably an object is already bound in
                    // the context.
                    currentContext =
                        (javax.naming.Context) currentContext.lookup(token);
                }
            }
        }
        
        return currentContext;
    }
	

	
}
