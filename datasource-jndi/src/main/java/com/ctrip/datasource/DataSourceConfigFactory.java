package com.ctrip.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class DataSourceConfigFactory {
	
	private static final Log log = LogFactory.getLog(DataSourceConfigFactory.class);
			
	private final static String DAL_CONFIG_FILE="Dal.config";
	
	private static final Pattern dbLinePattern = Pattern.compile(" connectionString=\"([^\"]+)\"");
	
	private List<Map<String,String>> conns = new ArrayList<Map<String,String>>();
	
	public static void main(String[] args){
		List<Map<String,String>> conns =new DataSourceConfigFactory().getDSConfig();
		System.out.println(conns.size());
		System.out.println(conns.get(0).get("dbkey"));
	}
	
	public DataSourceConfigFactory(){
		init();
	}
	
	public List<Map<String,String>> getDSConfig(){
		return conns;
	}
	
	private void init(){
		try {
			parseDALConfig(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(DAL_CONFIG_FILE)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("No dal config file error, msg:"+e.getMessage(), e);
		}
	}
	
	private void parseDALConfig(Reader reader){
		//Map<String, String[]> props = new HashMap<String, String[]>();
		BufferedReader br = null;
		try {
			//br = new BufferedReader(new FileReader(DB_CONFIG_FILE));
			br = new BufferedReader(reader);
			// StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			Matcher matcher;
			Map<String,String> connMap;
			while (line != null) {
				if(line.trim().length()==0 || line.trim().startsWith("<!--")){
					line = br.readLine();
					continue;
				}
				// sb.append(line);
				matcher = dbLinePattern.matcher(line);
				if (matcher.find()) {
					connMap = new HashMap<String,String>();
					connMap.put("dbkey",matcher.group(1));
					conns.add(connMap);
				}

				line = br.readLine();
			}
			// String everything = sb.toString();
			// props.put("product_select", new
			// String[]{"jdbc:mysql://192.168.80.60/product?useUnicode=true&characterEncoding=utf-8","product","123456"});
			//return props;
		} catch (IOException e) {
			log.error("Read dal config file error, msg:"+e.getMessage(), e);
			//throw e;
		} catch (Exception e) {
			log.error("Init dal conns props error, msg:"+e.getMessage(), e);
			//throw e;
		} finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error("close Dal config file IO error", e);
				}
			}
		}
	}
}
