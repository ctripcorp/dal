package com.ctrip.platform.appinternals.permission;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.appinternals.helpers.IPv4Util;

public class InnerAddress {
	private static Map<String, Rangle> innerIps = new HashMap<String, Rangle>();
	static{
		innerIps.put("10", new Rangle(IPv4Util.ipToInt("10.0.0.0"), IPv4Util.ipToInt("10.255.255.255")));
		innerIps.put("192", new Rangle(IPv4Util.ipToInt("192.168.0.0"), IPv4Util.ipToInt("192.168.255.255")));
		innerIps.put("172", new Rangle(IPv4Util.ipToInt("172.16.0.0"), IPv4Util.ipToInt("172.31.255.255")));
	}
	public static boolean isInner(String ip){
		if(ip == null || ip.isEmpty())
			return false;
		if(isLocal(ip))
			return true;
		try{
			String first = StringUtils.split(ip, ".")[0];
			Rangle rangle = innerIps.get(first);
			if(rangle == null)
				return false;
			Integer l = IPv4Util.ipToInt(ip);
			return rangle.getMinNum() <= l && rangle.getMaxNum() >= l;
		}catch(Exception e){}
		return false;
	}
	
	public static boolean isLocal(String ip){
		if(ip == null || ip.isEmpty())
			return false;
		return ip.equalsIgnoreCase("127.0.0.1") || ip.equalsIgnoreCase("::1");
	}
}
