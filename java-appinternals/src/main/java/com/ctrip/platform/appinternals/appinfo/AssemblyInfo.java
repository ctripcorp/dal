package com.ctrip.platform.appinternals.appinfo;

public class AssemblyInfo {
    private String Name = "";
    private String Version = "";
    private String FileVersion = "";
    private String Culture = "";
    private String PublicKeyToken = "";
    
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getVersion() {
		return Version;
	}
	public void setVersion(String version) {
		Version = version;
	}
	public String getFileVersion() {
		return FileVersion;
	}
	public void setFileVersion(String fileVersion) {
		FileVersion = fileVersion;
	}
	public String getCulture() {
		return Culture;
	}
	public void setCulture(String culture) {
		Culture = culture;
	}
	public String getPublicKeyToken() {
		return PublicKeyToken;
	}
	public void setPublicKeyToken(String publicKeyToken) {
		PublicKeyToken = publicKeyToken;
	}
}
