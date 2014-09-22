package com.ctrip.platform.appinternals.serialization;

import java.util.Collection;

import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.appinternals.helpers.Helper;
import com.ctrip.platform.appinternals.models.BeanContainer;
import com.ctrip.platform.appinternals.models.BeanView;

public class JSONSerializer extends Serializer{

	@Override
	public String serializer(ConfigBeanBase bean) throws Exception {
		return Helper.toJSON(bean.getClass(), bean.getBeanInfo().getName(), bean);
	}

	@Override
	public String serializer(Collection<ConfigBeanBase> beans) throws Exception {
		BeanContainer container = new BeanContainer();
		container.setName("Beans");
		for (ConfigBeanBase bean : beans) {
			BeanView bv = new BeanView();
			bv.setName(bean.getBeanInfo().getName());
			bv.setUrl(this.appPath + bean.getBeanInfo().getUrl() + "&format=json");
			bv.setLastModifyTime(bean.getBeanInfo().getLastModifyTime());
			container.getBeans().add(bv);
		}
		return Helper.toJSON(container.getClass(), "ComponentCollection", container);
	}

}
