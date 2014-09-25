package com.ctrip.platform.dal.dao.configbeans;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.appinternals.annotations.BeanMeta;
import com.ctrip.platform.appinternals.configuration.ChangeEvent;
import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.dal.dao.DalClientFactory;

@BeanMeta(alias = "markdown")
public class MarkdownConfigBean extends ConfigBeanBase{
	@BeanMeta(alias = "Markdown")
	private boolean markdown = false;
	
	@BeanMeta(alias = "MarkDownDB")
	private String dbMarkdown = "";
	
	private String alldbs = "";
	
	@BeanMeta(omit = true)
	private Set<String> marks = new HashSet<String>();
	
	//TODO: How to get all db names
	public MarkdownConfigBean(){
		this.addChangeEvent("dbMarkdown", new ChangeEvent() {			
			
			@Override
			public void before(Object oldVal, String newVal) throws Exception {
				String[] tokens = newVal.split(",");
				for (String token : tokens) {
					if(!getAlldbs().contains(token))
						throw new Exception(String.format(
								"The specified database[%s] are not included, pls check", token));
				}
			}

			@Override
			public void end(Object oldVal, String newVal) throws Exception {
				Set<String> temp = new HashSet<String>();
				if(newVal == null || newVal.isEmpty())
					marks = temp;
				String[] tokens = newVal.split(",");
				for (String token : tokens) {
					temp.add(token);
				}
				marks = temp;
			}
		});
	}
	
	public boolean isMarkdown() {
		return markdown;
	}

	public void setMarkdown(boolean markdown) {
		this.markdown = markdown;
	}

	public String getDbMarkdown() {
		return dbMarkdown;
	}

	public void setDbMarkdown(String dbMarkdown) {
		this.dbMarkdown = dbMarkdown;
	}

	public String getAlldbs() {
		if(this.alldbs.isEmpty())
			this.alldbs = StringUtils.join(DalClientFactory.getAllDB(), ",");
		return this.alldbs;
	}
	
	public Set<String> getMarks(){
		return this.marks;
	}
}
