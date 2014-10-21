package com.ctrip.platform.appinternals.appinfo;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.platform.appinternals.helpers.Helper;

public class AppInfoBuilder {
	private AppInfo info;
	
	public AppInfoBuilder(AppInfo info){
		this.info = info;
	}
	
	public void setStartTime(){
		SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		this.info.setAppStartTime(tf.format(date));
	}
	
	public void setAppId(){
		this.info.setAppID(LogConfig.getAppID());
	}
	
	public void setDomain(String domain){
		this.info.setDomain(domain);
	}
	
	public void setPost(String port){
		this.info.setPort(port);
	}
	
	public void setAssemblyInfos(String libPath){
		List<AssemblyInfo> asms = new ArrayList<AssemblyInfo>();
		File dir = new File(libPath);
		AssemblyInfoBuilder builder = new AssemblyInfoBuilder();
		if(dir.isDirectory()){
			File[] jars = dir.listFiles();
			for (File jar : jars) {
				if(jar.isFile() && jar.getName().endsWith(".jar")){
					AssemblyInfo asm = builder.build(jar.getName());
					if(asm != null)
						asms.add(asm);
				}
			}
		}
		
		this.info.setAssemblyInfos(asms);
	}

	public void setVirtualDirectory(String dir){
		this.info.setVirtualDirectory(dir);
	}
	
	public void setPhyDirectory(String dir){
		this.info.setPhyDirectory(dir);
	}
	
	public  void setIPV4(){
		List<String> ips = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				if (!current.isUp() || current.isLoopback()
						|| current.isVirtual())
					continue;
				Enumeration<InetAddress> addresses = current.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress current_addr = addresses.nextElement();
					if (current_addr instanceof Inet4Address) {
						ips.add(current_addr.getHostAddress());
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.info.setIPV4(ips);
	}

	public void setHostName(){
		try {
			this.info.setSVR(InetAddress.getLocalHost().getHostName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setOS(){
		this.info.setOS(System.getProperty("os.name"));
	}
	
	public void setIs64BitOS(){
		this.info.setIs64BitOS(System.getProperty("os.arch").contains("64") ? "True" : "False");
	}
	
	public void setProcessorCount(){
		Runtime runtime = Runtime.getRuntime();
        this.info.setProcessorCount(Integer.toString(runtime.availableProcessors()));
	}

	public AppInfo getAppInfo(){
		return this.info;
	}
	
	public String getJsonAppInfo(){
		return Helper.toJSON(AppInfo.class, "appinfo", this.info);	
	}
}
