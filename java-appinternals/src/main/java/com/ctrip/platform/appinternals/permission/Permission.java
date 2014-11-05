package com.ctrip.platform.appinternals.permission;

import java.util.HashMap;
import java.util.Map;

public class Permission {
	private static Permission instance = null;
	
	private Map<String, IPVisitor> users;
	
	private Permission(){
		this.users = new HashMap<String, IPVisitor>();
	}
	
	public static Permission getInstance(){
		if(null == instance)
			instance = new Permission();
		return instance;
	}
	
	public boolean hasRead(String ip){
		return true;
		/*if(isLocal(ip))
			return true;
		return this.users != null && this.users.containsKey(ip) ? this.users.get(ip).isRead() : false;*/
	}
	
	public boolean hasWrite(String ip){
		if(isLocal(ip))
			return true;
		return this.users != null && this.users.containsKey(ip) ? this.users.get(ip).isWrite() : false;
	}
	
	private boolean isLocal(String ip){
		return ip != null && 
				(ip.equalsIgnoreCase("127.0.0.1") || ip.equalsIgnoreCase("0:0:0:0:0:0:0:1"));
	}
	public void addUser(String ip, int permission){
		if(!this.users.containsKey(ip)){
			this.users.put(ip, new IPVisitor());
		}
		IPVisitor user = this.users.get(ip);
		user.setIp(ip);
		if(1 == permission){
			user.setWrite(true);
			user.setRead(true);
		}else{
			user.setWrite(false);
			user.setRead(true);
		}
	}
}
