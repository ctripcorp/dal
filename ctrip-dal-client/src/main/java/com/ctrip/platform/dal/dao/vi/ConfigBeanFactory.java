package com.ctrip.platform.dal.dao.vi;

import com.ctrip.framework.vi.component.ComponentManager;

public class ConfigBeanFactory {
	public static void init() {
		ComponentManager.register(HAConfigBean.class);
		ComponentManager.register(MarkdownConfigBean.class);
		ComponentManager.register(TimeoutMarkDownBean.class);
		
		ComponentManager.register(DatabaseSetConfig.class);
		ComponentManager.register(DataSourceConfig.class);
	}
}
