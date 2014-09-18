package com.ctrip.platform.appinternals.serialization;

import java.util.Collection;

import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.appinternals.configuration.ConfigName;

public class JSONSerializer extends Serializer{

	@Override
	public String serializer(ConfigBeanBase bean) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		String token = "";
		for (ConfigName pname : bean.getFieldNames()) {
			String val = bean.get(pname.getName());
			if(val == null){
				token += "\"" + pname.getName() + "\":null,";
			}else{
				token += "\"" + pname.getName() + "\":\"" + val + "\",";
			}
		}
		if(!token.isEmpty())
			sb.append(token.substring(0, token.length()-1));
		sb.append("}");
		
		return sb.toString();
	}

	@Override
	public String serializer(Collection<ConfigBeanBase> beans) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"Name\":\"Beans\",\"Components\":[");
		for (ConfigBeanBase bean : beans) {
			sb.append("{");
			sb.append("\"Name\":" + "\"" + bean.getBeanInfo().getName() + "\",")
				.append("\"Url\":"+ "\"" + this.appPath + bean.getBeanInfo().getUrl() + "&amp;format=json" + "\"");
			if(bean.getBeanInfo().getLastModifyTime()!=null){
				sb.append(",\"LastModifyTime\":" + bean.getBeanInfo().getLastModifyTime() + "\"");
			}
			sb.append("}");
		}
		sb.append("]");
		sb.append("}");
		return sb.toString();
	}

}
