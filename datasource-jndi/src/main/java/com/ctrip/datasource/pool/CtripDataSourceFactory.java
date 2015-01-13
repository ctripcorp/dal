package com.ctrip.datasource.pool;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;

import com.ctrip.datasource.AllInOneConfigParser;

public class CtripDataSourceFactory extends DataSourceFactory{
	
	private static final Log log = LogFactory.getLog(CtripDataSourceFactory.class);
	    	

	@Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?,?> environment) throws Exception {

        if ((obj == null) || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference) obj;
        Map<String,String[]> props = AllInOneConfigParser.newInstance().getDBAllInOneConfig();
        String dbKey=name.toString();
        if(dbKey==null){
        	log.error("No jndi name info in config file");
        	return null;
        }
        if(dbKey.indexOf('/')>0){
        	dbKey=dbKey.substring(dbKey.lastIndexOf('/'),dbKey.length()-1);
        }
        //String dbKey= ref.get(DB_KEY).getContent().toString();
        if (props.containsKey(dbKey)){
        	String[] prop=props.get(dbKey);
        	ref.add(new StringRefAddr(PROP_URL, prop[0]));
        	ref.add(new StringRefAddr(PROP_USERNAME, prop[1]));
        	ref.add(new StringRefAddr(PROP_PASSWORD, prop[2]));
        	ref.add(new StringRefAddr(PROP_DRIVERCLASSNAME, prop[3]));
        }else{
        	java.util.Iterator<String> s =props.keySet().iterator();
        	StringBuilder sb = new StringBuilder();
        	while(s.hasNext())
        		sb.append(s.next()).append(",");
        	log.debug("===All db key:"+sb.toString());
        	log.error("No db ["+dbKey+"]'s info in config file," + "Key size:"+props.size());
        	return null; 
        }
        
        return super.getObjectInstance(ref, name, nameCtx, environment);
      
    }
	

	
}
