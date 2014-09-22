package com.ctrip.platform.appinternals.helpers;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

public class Helper {
	private static final String IPV4PATTERN = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])"
			+ "\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
	
	private static final String URLPATTERN = "https?://(.*)/appinternals/?(.*)";
	
	private static Pattern ippattern = null;
	private static Pattern urlpattern = null;
	
	private static XStream xmlStream = null;
	private static XStream jsonStream = null;
	
	static {
		ippattern = Pattern.compile(IPV4PATTERN);
		urlpattern = Pattern.compile(URLPATTERN);
		
		xmlStream = new XStream();
		jsonStream = new XStream(new JsonHierarchicalStreamDriver() {
		    public HierarchicalStreamWriter createWriter(Writer writer) {
		        return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
		    }
		});;
	}
	
	public static boolean validateIPV4(String ipaddr) {  
	    Matcher m = ippattern.matcher(ipaddr);  
	    return m.matches();    
	}
	
	public static boolean validateURL(String url){
		Matcher m = urlpattern.matcher(url);  
	    return m.matches();  
	}
	
	public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
            .append(Character.toTitleCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }
	
	public static <T> String toXML(Class<?> type, String alias, T model){
		xmlStream.setMode(XStream.NO_REFERENCES);
		xmlStream.autodetectAnnotations(true);
		xmlStream.alias(alias, type);
		return xmlStream.toXML(model);
	}
	
	public static <T> String toJSON(Class<?> type, String alias, T model){
		jsonStream.alias(alias, type);
		jsonStream.autodetectAnnotations(true);
		jsonStream.alias(alias, type);
		return jsonStream.toXML(model);
	}
}
