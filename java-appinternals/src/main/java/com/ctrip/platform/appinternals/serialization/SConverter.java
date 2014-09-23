package com.ctrip.platform.appinternals.serialization;

import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.appinternals.configuration.ConfigName;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class SConverter implements Converter{

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return type.getSuperclass().equals(ConfigBeanBase.class);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		ConfigBeanBase bean = (ConfigBeanBase)source;
		if(bean.getBeanInfo().getAlias() != null)
			writer.addAttribute("alias", bean.getBeanInfo().getAlias());
		for (ConfigName cname : bean.getFieldNames()) {
			writer.startNode(cname.getName());
			if(cname.getAlias()!=null)
				writer.addAttribute("alias", cname.getAlias());
			try {
				writer.setValue(bean.get(cname.getName()));
			} catch (Exception e) {
				writer.setValue(e.getMessage());
			}
			writer.endNode();
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		// TODO Auto-generated method stub
		return null;
	}

}
