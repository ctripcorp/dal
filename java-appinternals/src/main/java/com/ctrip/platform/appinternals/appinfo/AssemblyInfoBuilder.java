package com.ctrip.platform.appinternals.appinfo;

public class AssemblyInfoBuilder {
	public AssemblyInfo build(String jarName){
		if(jarName == null || jarName.isEmpty() || !jarName.endsWith(".jar"))
			return null;
		AssemblyInfo info = new AssemblyInfo();
		int des = 0, index = 0;
		do{
			index = jarName.indexOf("-", index + 1);
			if(index != -1){
				des = index;
			}
		}while(index != -1);
		
		if(des > 0){
			info.setName(jarName.substring(0, des));
			if(jarName.length() > des + 5){
				info.setVersion(jarName.substring(des + 1, jarName.length() - 4));
			}
		}
		return info;
	}
}
