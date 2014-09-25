package com.ctrip.platform.dal.dao.markdown;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

/**
 * Manual mark down
 */
public class ManualMarkdown {
	public boolean isMarkdown(String key){
		return ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(key);
	}
	public void markown(String key){
		ConfigBeanFactory.getMarkdownConfigBean().markdown(key);
	}
}
