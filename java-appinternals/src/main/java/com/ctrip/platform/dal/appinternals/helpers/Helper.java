package com.ctrip.platform.dal.appinternals.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
	private static final String IPV4PATTERN = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])"
			+ "\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
	
	private static final String URLPATTERN = "https?://(.*)/appinternals/?(.*)";
	
	private static Pattern ippattern = null;
	private static Pattern urlpattern = null;
	
	static {
		ippattern = Pattern.compile(IPV4PATTERN);
		urlpattern = Pattern.compile(URLPATTERN);
	}
	
	public static boolean validateIPV4(String ipaddr) {  
	    Matcher m = ippattern.matcher(ipaddr);  
	    return m.matches();    
	}
	
	public static boolean validateURL(String url){
		Matcher m = urlpattern.matcher(url);  
	    return m.matches();  
	}
}
