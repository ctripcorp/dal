package com.ctrip.platform.appinternals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ctrip.platform.appinternals.serialization.HTMLSerializer;
import com.ctrip.platform.appinternals.serialization.JSONSerializer;
import com.ctrip.platform.appinternals.serialization.Serializer;
import com.ctrip.platform.appinternals.serialization.XMLSerializer;

public class AppInternalsContext {
	private String applicationPath;
	private String requestUrl;
	private String remoteip;
	private String localPath;
	private Map<String, String> parameters = new HashMap<String, String>();;
	private List<String> segments = new ArrayList<String>();
	private Serializer serializer = null;
	private AppResponse content = new AppResponse();
	
	public AppInternalsContext(HttpServletRequest request){
		this.remoteip = request.getRemoteAddr();
		this.localPath = request.getRequestURI();
		this.requestUrl = request.getRequestURL().toString();
		applicationPath = requestUrl.substring(0, requestUrl.indexOf("appinternals") -1);
		String[] pices = this.localPath.replaceFirst("/", "")
				.replace('\\', '/').split("/");
		boolean bg = false;
		for (int i = 0; i < pices.length; i++) {
			if("appinternals".equalsIgnoreCase(pices[i])){
				bg = true;
			}
			if(bg){
				this.segments.add(pices[i]);
			}
		}
		this.parseQueryString(request.getQueryString());
		this.parseFromat();
		if(this.parameters.containsKey("mip")){
			this.remoteip = this.parameters.get("mip");
		}
	}

	public String getRemoteip() {
		return remoteip;
	}

	public String getLocalPath() {
		return localPath;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}
	
	public List<String> getSegments() {
		return segments;
	}
	
	public String getApplicationPath() {
		return applicationPath;
	}
	
	public Serializer getSerializer() {
		return serializer;
	}
	
	public AppResponse getContent() {
		return this.content;
	}

	public String getCategory(String parent){
        boolean flag = false;
        for (String segement : this.segments) {
			if(flag)
				return segement;
			if(segement.equalsIgnoreCase(parent))
				flag = true;
		}
		return "";
	}

	private void parseQueryString(String qs){
		if(qs == null || qs.isEmpty())
			return;
		String[] tokens = qs.trim().split("&");
		String[] parameter = null;
		for (String token : tokens) {
			parameter = token.split("=");
			if(parameter.length == 2){
				this.parameters.put(parameter[0], parameter[1]);
			}else{
				this.parameters.put(parameter[0], null);
			}
		}
	}
	
	private void parseFromat(){
		if(this.parameters.containsKey("format")){
			String format = this.parameters.get("format");
			if(null != format){
				if(format.equalsIgnoreCase("xml")){
					this.serializer = new XMLSerializer();
					content.setContextType("application/xml");
				}else if(format.equalsIgnoreCase("html")){
					this.serializer = new HTMLSerializer();
				}else{
					this.serializer = new JSONSerializer();
				}
			}else{
				this.serializer = new JSONSerializer();
			}
		}else{
			this.serializer = new JSONSerializer();
		}
		this.serializer.setAppPath(this.applicationPath);
	}
}
