package com.ctrip.platform.appinternals.appinfo;

import java.util.List;

public class AppInfo {
    private static String AppID = "";
    private String Domain = "";
    private String Port = "";
    private String AppStartTime = "";
    private String Is64BitProcess = "";
    private String VirtualDirectory = "";
    private String PhyDirectory = "";
    private String BuildingTime = "";
    private String ReleaseTime = "";
    public List<String> IPV4;
    private String PipelineMode = "";
    private String DotnetFramework = "";
    private String ServerCulture = "";
    private String AppPoolName = "";
    private String SVR = "";
    private String OS = "";
    private String ProcessorCount = "";
    private String SystemStartTime = "";
    private String Is64BitOS = "";
    private String PhyMemory = "";
    private String SystemPageSize = "";
    private List<AssemblyInfo> AssemblyInfos;
    
    public static String getAppID() {
		return AppID;
	}
	public static void setAppID(String appID) {
		AppID = appID;
	}

	public String getDomain() {
		return Domain;
	}
	public void setDomain(String domain) {
		Domain = domain;
	}
	public String getPort() {
		return Port;
	}
	public void setPort(String port) {
		Port = port;
	}
	public String getAppStartTime() {
		return AppStartTime;
	}
	public void setAppStartTime(String appStartTime) {
		AppStartTime = appStartTime;
	}
	public String getIs64BitProcess() {
		return Is64BitProcess;
	}
	public void setIs64BitProcess(String is64BitProcess) {
		Is64BitProcess = is64BitProcess;
	}
	public String getVirtualDirectory() {
		return VirtualDirectory;
	}
	public void setVirtualDirectory(String virtualDirectory) {
		VirtualDirectory = virtualDirectory;
	}
	public String getPhyDirectory() {
		return PhyDirectory;
	}
	public void setPhyDirectory(String phyDirectory) {
		PhyDirectory = phyDirectory;
	}
	public String getBuildingTime() {
		return BuildingTime;
	}
	public void setBuildingTime(String buildingTime) {
		BuildingTime = buildingTime;
	}
	public String getReleaseTime() {
		return ReleaseTime;
	}
	public void setReleaseTime(String releaseTime) {
		ReleaseTime = releaseTime;
	}
	public List<String> getIPV4() {
		return IPV4;
	}
	public void setIPV4(List<String> iPV4) {
		IPV4 = iPV4;
	}
	public String getPipelineMode() {
		return PipelineMode;
	}
	public void setPipelineMode(String pipelineMode) {
		PipelineMode = pipelineMode;
	}
	public String getDotnetFramework() {
		return DotnetFramework;
	}
	public void setDotnetFramework(String dotnetFramework) {
		DotnetFramework = dotnetFramework;
	}
	public String getServerCulture() {
		return ServerCulture;
	}
	public void setServerCulture(String serverCulture) {
		ServerCulture = serverCulture;
	}
	public String getAppPoolName() {
		return AppPoolName;
	}
	public void setAppPoolName(String appPoolName) {
		AppPoolName = appPoolName;
	}
	public String getSVR() {
		return SVR;
	}
	public void setSVR(String sVR) {
		SVR = sVR;
	}
	public String getOS() {
		return OS;
	}
	public void setOS(String oS) {
		OS = oS;
	}
	public String getProcessorCount() {
		return ProcessorCount;
	}
	public void setProcessorCount(String processorCount) {
		ProcessorCount = processorCount;
	}
	public String getSystemStartTime() {
		return SystemStartTime;
	}
	public void setSystemStartTime(String systemStartTime) {
		SystemStartTime = systemStartTime;
	}
	public String getIs64BitOS() {
		return Is64BitOS;
	}
	public void setIs64BitOS(String is64BitOS) {
		Is64BitOS = is64BitOS;
	}
	public String getPhyMemory() {
		return PhyMemory;
	}
	public void setPhyMemory(String phyMemory) {
		PhyMemory = phyMemory;
	}
	public String getSystemPageSize() {
		return SystemPageSize;
	}
	public void setSystemPageSize(String systemPageSize) {
		SystemPageSize = systemPageSize;
	}
	public List<AssemblyInfo> getAssemblyInfos() {
		return AssemblyInfos;
	}
	public void setAssemblyInfos(List<AssemblyInfo> assemblyInfos) {
		AssemblyInfos = assemblyInfos;
	}
}
