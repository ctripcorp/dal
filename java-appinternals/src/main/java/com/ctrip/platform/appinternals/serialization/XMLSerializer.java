package com.ctrip.platform.appinternals.serialization;

import java.util.Collection;

import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.appinternals.configuration.ConfigName;

public class XMLSerializer extends Serializer{
	
	@Override
	public String serializer(ConfigBeanBase bean) throws Exception {
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<" + bean.getBeanInfo().getName() + ">");
		for (ConfigName pname : bean.getFieldNames()) {
			String val = bean.get(pname.getName());
			if(val != null){
				sb.append("<" + pname.getName() + ">")
					.append(val)
					.append("</" + pname.getName() + ">");
			}else{
				sb.append("<" + pname.getName() + ">")
				.append("</" + pname.getName() + ">");
			}
		}
		sb.append("</" + bean.getBeanInfo().getName() + ">");
		return sb.toString();
	}

	@Override
	public String serializer(Collection<ConfigBeanBase> beans) throws Exception{
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<ComponentCollection>")
			.append("<Name>Beans</Name>")
			.append("<Components>");
		for (ConfigBeanBase bean : beans) {
			sb.append("<Component>");
			sb.append("<Name>")
				.append(bean.getBeanInfo().getName())
				.append("</Name>");
			sb.append("<Url>")
				.append(this.appPath + bean.getBeanInfo().getUrl() + "&amp;format=xml")
				.append("</Url>");
			if(bean.getBeanInfo().getLastModifyTime() != null){
				sb.append("<LastModifyTime>")
					.append(bean.getBeanInfo().getLastModifyTime().toString())
					.append("</LastModifyTime>");
			}
			sb.append("</Component>");
		}
		sb.append("</Components>")
			.append("</ComponentCollection>");
		return sb.toString();
	}
}
