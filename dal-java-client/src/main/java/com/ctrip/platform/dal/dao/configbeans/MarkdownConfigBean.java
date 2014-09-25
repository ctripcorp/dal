package com.ctrip.platform.dal.dao.configbeans;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.appinternals.annotations.BeanMeta;
import com.ctrip.platform.appinternals.configuration.ChangeEvent;
import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;

@BeanMeta(alias = "markdown")
public class MarkdownConfigBean extends ConfigBeanBase{
	@BeanMeta(alias = "Markdown")
	private volatile boolean markdown = false;
	
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
					//If the current mark down database doesn't contain the new value
					//The new value need to be marked up on auto mark down
					if(!marks.contains(token))
						MarkdownManager.markup(token);
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
	
	public boolean isMarkdown(String dbname){
		return this.isMarkdown() && this.marks.contains(dbname);
	}
	
	public boolean markdown(String dbname){
		if(!this.marks.contains(dbname)){
			this.marks.add(dbname);
			this.dbMarkdown = StringUtils.join(this.marks, ",");
		}			
		return this.marks.contains(dbname);
	}
}
